package com.neocaptainnemo.atms.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.app.App
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.service.AddressFormatter
import com.neocaptainnemo.atms.service.DistanceFormatter
import com.neocaptainnemo.atms.ui.list.ListFragment
import com.neocaptainnemo.atms.ui.map.GoogleMapsFragment
import com.neocaptainnemo.atms.ui.map.IMapView
import com.neocaptainnemo.atms.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, IView,
        IMapView.MapDelegate, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SearchView.OnQueryTextListener, IAtmsView.OnAtmSelected {

    @Inject
    lateinit var presenter: Presenter
    @Inject
    lateinit var addressFormatter: AddressFormatter
    @Inject
    lateinit var distanceFormatter: DistanceFormatter

    private var semiTransparentToolBar = false
    private var atmDetailsBottomSheet: BottomSheetBehavior<NestedScrollView>? = null

    private var selectedAtm: AtmNode? = null

    private var googleApiClient: GoogleApiClient? = null

    private var location: Location? = null

    private var searchView: SearchView? = null
    private val locationListener = LocationListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        (application as App).appComponent!!.inject(this)
        presenter.setView(this)

        setSupportActionBar(toolbar)

        bottomNavigation.setOnNavigationItemSelectedListener(this)
        navigate.setOnClickListener { _ ->
            if (selectedAtm != null) {

                NavigationDecisionDialogFragment.instance(selectedAtm!!).show(supportFragmentManager,
                        NavigationDecisionDialogFragment.TAG)
            }
        }


        atmDetailsBottomSheet = BottomSheetBehavior.from(atmDetails)
        atmDetailsBottomSheet!!.peekHeight = 0
        navigate.scaleX = 0f
        navigate.scaleY = 0f
        zoomFurther.scaleX = 0f
        zoomFurther.scaleY = 0f
        atmDetailsBottomSheet!!.isHideable = true
        navigate.visibility = View.INVISIBLE

        atmDetailsBottomSheet!!.setBottomSheetCallback(BottomSheetBehaviour())


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
        }

        semiTransparentToolBar = true

        if (savedInstanceState == null) {

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.map_container, GoogleMapsFragment.instance(), GoogleMapsFragment.tag)
                    .commit()

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.listContainer, ListFragment.instance(), ListFragment.tag)
                    .commit()


            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settingsContainer, SettingsFragment.instance(), SettingsFragment.TAG)
                    .commit()

            presenter.openedTab = Presenter.Tab.MAP

        } else {
            val openedTab = savedInstanceState.getSerializable("tab") as Presenter.Tab
            presenter.openedTab = openedTab
        }

        if (intent.hasExtra(test)) {
            presenter.fetchAtms(ViewPort(1.0, 1.0, 1.0, 1.0))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("tab", presenter.openedTab)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.app_bar, menu)

        searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView!!.queryHint = getString(R.string.search_hint)

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search),
                object : MenuItemCompat.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        searchView!!.isIconified = false
                        atmDetailsBottomSheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        searchView!!.setQuery("", true)
                        return true
                    }
                })

        searchView!!.setOnCloseListener {
            searchView!!.isIconified = false
            true
        }

        searchView!!.setOnQueryTextListener(this)

        return true
    }

    override fun onConnected(bundle: Bundle?) {

        val accessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {

            location = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient)

            var view: IAtmsView? = GoogleMapsFragment.onStack(supportFragmentManager)

            if (view != null && location != null) {
                view.setMyLocation(location!!)
            }

            view = ListFragment.onStack(supportFragmentManager)

            if (view != null && location != null) {
                view.setMyLocation(location!!)
            }

            val request = LocationRequest.create()

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request,
                    locationListener)
        }
    }

    override fun onConnectionSuspended(i: Int) {
        //do nothing
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //do nothing
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            locationPermission -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val mapView = GoogleMapsFragment.onStack(supportFragmentManager)

                    mapView?.enableMyLocation()

                    googleApiClient!!.connect()

                } else {

                    val mapView = GoogleMapsFragment.onStack(supportFragmentManager)

                    mapView?.disableMyLocation()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()

        val accessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            googleApiClient!!.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()

        if (googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener)
            googleApiClient!!.disconnect()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_maps -> {
                presenter.openedTab = Presenter.Tab.MAP
                return true
            }

            R.id.action_list -> {
                presenter.openedTab = Presenter.Tab.LIST
                return true
            }

            R.id.action_settings -> {
                presenter.openedTab = Presenter.Tab.SETTINGS
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        when {
            atmDetailsBottomSheet!!.state != BottomSheetBehavior.STATE_COLLAPSED -> atmDetailsBottomSheet!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            presenter.openedTab !== Presenter.Tab.MAP -> presenter.openedTab = Presenter.Tab.MAP
            else -> super.onBackPressed()
        }
    }


    override fun onGotViewPort(viewPort: ViewPort) {
        presenter!!.fetchAtms(viewPort)
        hideZoomInFurther()
    }

    override fun onZoomFurther() {
        showZoomInFurther()
    }

    override fun onGotAtms(atmNodes: List<AtmNode>) {

        var view: IAtmsView? = GoogleMapsFragment.onStack(supportFragmentManager)

        if (view != null) {
            view.clear()
            view.showAtms(atmNodes)
        }

        view = ListFragment.onStack(supportFragmentManager)

        if (view != null) {
            view.clear()
            view.showAtms(atmNodes)
        }
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun showError(errorMsg: String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }

    override fun showZoomInFurther() {
        zoomFurther.visibility = View.VISIBLE
        zoomFurther
                .animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(resources.getInteger(android.R.integer.config_mediumAnimTime).toLong())
                .setListener(null)
                .start()
    }

    override fun hideZoomInFurther() {
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

    override fun showMap() {
        if (!semiTransparentToolBar) {
            animateColorChange(R.color.colorPrimary, R.color.colorPrimarySemi)
        }
        semiTransparentToolBar = true

        ViewCompat.setElevation(appbar, resources.getDimension(R.dimen.app_bar_elevation))

        listContainer.visibility = View.GONE
        settingsContainer.visibility = View.GONE
    }

    override fun showList() {
        zoomFurther.visibility = View.GONE
        if (semiTransparentToolBar) {
            animateColorChange(android.R.color.transparent, R.color.colorPrimary)
        }
        semiTransparentToolBar = false
        ViewCompat.setElevation(appbar, resources.getDimension(R.dimen.app_bar_elevation))


        listContainer.visibility = View.VISIBLE
        settingsContainer.visibility = View.GONE
    }

    override fun showSettings() {
        zoomFurther.visibility = View.GONE
        if (semiTransparentToolBar) {
            animateColorChange(android.R.color.transparent, R.color.colorPrimary)
        }
        semiTransparentToolBar = false
        ViewCompat.setElevation(appbar, resources.getDimension(R.dimen.app_bar_elevation))

        listContainer.visibility = View.GONE
        settingsContainer.visibility = View.VISIBLE
    }

    override fun onQueryTextSubmit(str: String): Boolean {
        return false
    }

    override fun onQueryTextChange(str: String): Boolean {
        presenter!!.setFilter(str)
        return true
    }

    override fun onAtmSelected(atmNode: AtmNode, src: String) {

        val mapView = GoogleMapsFragment.onStack(supportFragmentManager)

        if (mapView != null) {
            mapView.selectAtm(atmNode)
            if (ListFragment.tag == src) {
                mapView.moveCameraToAtm(atmNode)
            }
            showMap()
        }

        selectedAtm = atmNode

        atmName.text = atmNode.tags!!.name
        atmDetailsBottomSheet!!.state = BottomSheetBehavior.STATE_EXPANDED

        val address = addressFormatter!!.format(atmNode)

        if (address.isEmpty()) {
            atmAddress.setText(R.string.no_address)
        } else {
            atmAddress.text = address
        }

        updateDistance()

    }

    private fun updateDistance() {
        if (selectedAtm != null && location != null) {

            val calcualtedDistance = SphericalUtil.computeDistanceBetween(LatLng(selectedAtm!!.lat, selectedAtm!!.lon),
                    LatLng(location!!.latitude, location!!.longitude))

            val fromYou = getString(R.string.distance_m_from_you, distanceFormatter.format(calcualtedDistance))
            distance.text = fromYou
        } else {
            distance.setText(R.string.location_disabled)
        }
    }

    private fun animateColorChange(from: Int, to: Int) {
        val colorFrom = ContextCompat.getColor(this, from)
        val colorTo = ContextCompat.getColor(this, to)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        colorAnimation.addUpdateListener { animator -> toolbar.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
    }


    private inner class LocationListener : com.google.android.gms.location.LocationListener {

        override fun onLocationChanged(location: Location) {

            this@MainActivity.location = location

            val view = ListFragment.onStack(supportFragmentManager)

            view?.setMyLocation(location)

            updateDistance()

        }
    }

    private inner class BottomSheetBehaviour : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                val mapView = GoogleMapsFragment.onStack(supportFragmentManager)

                mapView?.clearSelectedMarker()

            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                navigate.animate().scaleX(1f).scaleY(1f)
                        .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator) {
                                super.onAnimationStart(animation)
                                navigate.visibility = View.VISIBLE
                            }
                        })

                        .start()
            } else {
                navigate.animate().scaleX(0f).scaleY(0f)
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
