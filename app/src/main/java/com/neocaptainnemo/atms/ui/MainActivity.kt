package com.neocaptainnemo.atms.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.neocaptainnemo.atms.Optional
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.MapType
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.service.AddressFormatter
import com.neocaptainnemo.atms.service.DistanceFormatter
import com.neocaptainnemo.atms.service.ISettings
import com.neocaptainnemo.atms.ui.list.ListFragment
import com.neocaptainnemo.atms.ui.map.GoogleMapsFragment
import com.neocaptainnemo.atms.ui.map.YandexMapsFragment
import com.neocaptainnemo.atms.ui.settings.SettingsFragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener {

    @Inject
    lateinit var addressFormatter: AddressFormatter

    @Inject
    lateinit var distanceFormatter: DistanceFormatter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var settings: ISettings

    private lateinit var viewModel: MainViewModel

    private lateinit var atmDetailsBottomSheet: BottomSheetBehavior<NestedScrollView>

    private var selectedAtm: AtmNode? = null

    private lateinit var locationRequest: LocationRequest

    private val compositeDisposable = CompositeDisposable()

    private lateinit var tabDisposable: Disposable

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation ?: return
            viewModel.setLocation(location)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        daggerInject()

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener(this)
        navigate.setOnClickListener { _ ->
            if (selectedAtm != null) {

                NavigationDecisionDialogFragment.instance(selectedAtm!!).show(supportFragmentManager,
                        NavigationDecisionDialogFragment.tag)
            }
        }


        atmDetailsBottomSheet = BottomSheetBehavior.from(atmDetails)
        atmDetailsBottomSheet.peekHeight = 0
        navigate.scaleX = 0f
        navigate.scaleY = 0f
        zoomFurther.scaleX = 0f
        zoomFurther.scaleY = 0f
        atmDetailsBottomSheet.isHideable = true
        navigate.visibility = View.INVISIBLE

        atmDetailsBottomSheet.setBottomSheetCallback(BottomSheetBehaviour())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.smallestDisplacement = 100.0f
        locationRequest.interval = 7 * 1000

        val accessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AllowNavigationDialog.instance().show(supportFragmentManager, AllowNavigationDialog.tag)
            } else {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermission)
            }
        } else {
            viewModel.locationPermission = true
        }


        tabDisposable = viewModel.tabObservable.subscribe {

            when (it) {
                Tab.MAP -> showMap()
                Tab.LIST -> showList()
                Tab.SETTINGS -> showSettings()
            }
        }

        floatingSearchView.setOnQueryChangeListener { oldQuery, newQuery ->

            if (oldQuery != newQuery) {
                viewModel.searchQuery = newQuery
            }

        }

        if (intent.hasExtra(test)) {

            viewModel.viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            locationPermission -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    viewModel.locationPermission = true

                    fusedLocationClient.lastLocation.addOnCompleteListener {
                        viewModel.setLocation(it.result)
                    }

                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                } else {
                    viewModel.locationPermission = false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val accessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            viewModel.locationPermission = true
        } else {
            viewModel.locationPermission = false
            viewModel.clearLocation()
        }

        compositeDisposable.add(viewModel.progressObservable.subscribe {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })

        compositeDisposable.add(viewModel.zoomInFurtherObservable.subscribe {
            if (it) showZoomInFurther() else hideZoomInFurther()
        })

        compositeDisposable.add(viewModel.errorObservable.subscribe {
            showError(getString(it))
        })

        compositeDisposable.add(viewModel.searchVisibilityObservable.subscribe {
            floatingSearchView.visibility = if (it) View.VISIBLE else View.GONE
        })


        compositeDisposable.add(Observable.combineLatest(viewModel.selectedAtmObservable, viewModel.locationObservable,
                BiFunction<Optional<AtmNode>, Optional<Location>, Pair<Optional<AtmNode>, Optional<Location>>> { t1, t2 ->
                    Pair(t1, t2)
                }).subscribe {


            if (it.first.isNotNull()) {

                viewModel.tab = Tab.MAP

                onAtmSelected(it.first.safeValue())

                selectedAtm = it.first.safeValue()

                updateDistance(it.first.safeValue(), it.second.value)
            }

        })


    }

    override fun onStop() {
        super.onStop()

        fusedLocationClient.removeLocationUpdates(locationCallback)

        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        tabDisposable.dispose()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_maps -> {
                viewModel.tab = Tab.MAP
                return true
            }

            R.id.action_list -> {
                viewModel.tab = Tab.LIST
                return true
            }

            R.id.action_settings -> {
                viewModel.tab = Tab.SETTINGS
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        when {
            atmDetailsBottomSheet.state != BottomSheetBehavior.STATE_COLLAPSED -> atmDetailsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED)
            viewModel.tab !== Tab.MAP -> viewModel.tab = Tab.MAP
            else -> super.onBackPressed()
        }
    }


    private fun showError(errorMsg: String) =
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()


    private fun showZoomInFurther() {
        zoomFurther.visibility = View.VISIBLE
        zoomFurther
                .animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(null)
                .start()
    }

    private fun hideZoomInFurther() {
        zoomFurther
                .animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        zoomFurther.visibility = View.GONE
                    }
                })
                .start()
    }

    private fun showMap() {

        val ft = supportFragmentManager.beginTransaction()

        val mapType = settings.mapType

        if (mapType == MapType.GOOGLE) {

            val yandexFragment = supportFragmentManager.findFragmentByTag(YandexMapsFragment.tag)

            if (yandexFragment != null) {
                ft.remove(yandexFragment)
            }

            val mapFragment = supportFragmentManager.findFragmentByTag(GoogleMapsFragment.tag)

            if (mapFragment == null) {
                ft.add(R.id.container, GoogleMapsFragment.instance(), GoogleMapsFragment.tag)
            } else if (mapFragment.isHidden) {
                ft.show(mapFragment)
            }

        } else {

            val googleFragment = supportFragmentManager.findFragmentByTag(GoogleMapsFragment.tag)

            if (googleFragment != null) {
                ft.remove(googleFragment)
            }

            val mapFragment = supportFragmentManager.findFragmentByTag(YandexMapsFragment.tag)

            if (mapFragment == null) {
                ft.add(R.id.container, YandexMapsFragment.instance(), YandexMapsFragment.tag)
            } else if (mapFragment.isHidden) {
                ft.show(mapFragment)
            }
        }


        val listFragment = supportFragmentManager.findFragmentByTag(ListFragment.tag)

        if (listFragment != null && !listFragment.isHidden) {
            ft.hide(listFragment)
        }

        val settingsFragment = supportFragmentManager.findFragmentByTag(SettingsFragment.tag)

        if (settingsFragment != null && !settingsFragment.isHidden) {
            ft.hide(settingsFragment)
        }

        ft.commit()
    }

    private fun showList() {

        val ft = supportFragmentManager.beginTransaction()

        val listFragment = supportFragmentManager.findFragmentByTag(ListFragment.tag)

        if (listFragment == null) {
            ft.add(R.id.container, ListFragment.instance(), ListFragment.tag)
        } else if (listFragment.isHidden) {
            ft.show(listFragment)

        }

        val yandexMap = supportFragmentManager.findFragmentByTag(YandexMapsFragment.tag)

        if (yandexMap != null && !yandexMap.isHidden) {
            ft.hide(yandexMap)
        }

        val googleMap = supportFragmentManager.findFragmentByTag(GoogleMapsFragment.tag)

        if (googleMap != null && !googleMap.isHidden) {
            ft.hide(googleMap)
        }


        val settingsFragment = supportFragmentManager.findFragmentByTag(SettingsFragment.tag)

        if (settingsFragment != null && !settingsFragment.isHidden) {
            ft.hide(settingsFragment)
        }

        ft.commit()
    }

    private fun showSettings() {

        val ft = supportFragmentManager.beginTransaction()

        val settingsFragment = supportFragmentManager.findFragmentByTag(SettingsFragment.tag)

        if (settingsFragment == null) {
            ft.add(R.id.container, SettingsFragment.instance(), SettingsFragment.tag)
        } else if (settingsFragment.isHidden) {
            ft.show(settingsFragment)
        }

        val yandexMap = supportFragmentManager.findFragmentByTag(YandexMapsFragment.tag)

        if (yandexMap != null && !yandexMap.isHidden) {
            ft.hide(yandexMap)
        }

        val googleMap = supportFragmentManager.findFragmentByTag(GoogleMapsFragment.tag)

        if (googleMap != null && !googleMap.isHidden) {
            ft.hide(googleMap)
        }

        val listFragment = supportFragmentManager.findFragmentByTag(ListFragment.tag)

        if (listFragment != null && !listFragment.isHidden) {
            ft.hide(listFragment)
        }

        ft.commit()
    }

    override fun onQueryTextSubmit(str: String): Boolean = false

    override fun onQueryTextChange(str: String): Boolean {
        viewModel.searchQuery = str
        return true
    }

    private fun onAtmSelected(atmNode: AtmNode) {

        atmName.text = atmNode.tags!!.name
        atmDetailsBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        val address = addressFormatter.format(atmNode)

        if (address.isEmpty()) {
            atmAddress.setText(R.string.no_address)
        } else {
            atmAddress.text = address
        }

    }

    private fun updateDistance(atmNode: AtmNode, location: Location?) {
        if (location != null) {

            val calculatedDistance = SphericalUtil.computeDistanceBetween(LatLng(atmNode.lat, atmNode.lon),
                    LatLng(location.latitude, location.longitude))

            val fromYou = getString(R.string.distance_m_from_you, distanceFormatter.format(calculatedDistance))
            bottomSheetAtmDistance.text = fromYou
        } else {
            bottomSheetAtmDistance.setText(R.string.location_disabled)
        }
    }


    private inner class BottomSheetBehaviour : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {

                    viewModel.clearAtm()
                }
                BottomSheetBehavior.STATE_EXPANDED -> navigate.animate().scaleX(1f).scaleY(1f)
                        .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator) {
                                super.onAnimationStart(animation)
                                navigate.visibility = View.VISIBLE
                            }
                        })

                        .start()
                else -> navigate.animate().scaleX(0f).scaleY(0f)
                        .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                navigate.visibility = View.INVISIBLE
                            }
                        })
                        .start()
            }

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            //do nothing
        }

    }

    companion object {

        const val test = "test"
        const val locationPermission = 1
    }

}
