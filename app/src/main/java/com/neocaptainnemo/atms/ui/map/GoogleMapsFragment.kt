package com.neocaptainnemo.atms.ui.map

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.neocaptainnemo.atms.Optional
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.ui.MainViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GoogleMapsFragment : Fragment(), GoogleMap.OnMarkerClickListener,
        ClusterManager.OnClusterClickListener<AtmNode>, GoogleMap.OnCameraIdleListener {

    private lateinit var map: GoogleMap
    private var selectedAtm: AtmNode? = null
    private var locationSet = false

    private lateinit var clusterManager: ClusterManager<AtmNode>

    private lateinit var rxGoogleMap: RxGoogleMap

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    private val compositeDisposable = CompositeDisposable()


    override fun onAttach(context: Context?) {

        daggerInject()

        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        rxGoogleMap = RxGoogleMap(mapFragment)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_google_maps, container, false)


    override fun onCameraIdle() {

        clusterManager.cluster()

        val zoomLevel = map.cameraPosition.zoom

        viewModel.zoom = zoomLevel

        val bounds = map.projection.visibleRegion.latLngBounds

        val southWest = bounds.southwest

        val startLng = southWest.longitude
        val startLat = southWest.latitude

        val northeast = bounds.northeast

        val endLng = northeast.longitude
        val endLat = northeast.latitude

        val viewPort = ViewPort(startLng, startLat, endLng, endLat)

        viewModel.viewPort = viewPort

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        val dataSrc = Observable.combineLatest(viewModel.atms(), viewModel.selectedAtmObservable,
                BiFunction<List<AtmNode>, Optional<AtmNode>, Pair<List<AtmNode>, Optional<AtmNode>>> { atms, selected ->

                    Pair(atms, selected)
                })

        compositeDisposable.add(rxGoogleMap.map()
                .doOnNext {

                    map = it

                    if (!::clusterManager.isInitialized) {
                        clusterManager = ClusterManager(context, it)
                        clusterManager.renderer = AtmRenderer(context!!, it, clusterManager)
                        clusterManager.setOnClusterClickListener(this)

                    }
                    it.setOnMarkerClickListener(this)

                    it.setPadding(0, resources.getDimension(R.dimen.map_padding_top).toInt(), 0, 0)

                    it.setOnCameraIdleListener(this)


                }.flatMap { dataSrc }
                .subscribe {

                    selectedAtm = it.second.value

                    clusterManager.clearItems()
                    clusterManager.addItems(it.first)

                    clusterManager.cluster()

                })


        compositeDisposable.add(Observable.combineLatest(rxGoogleMap.map(), viewModel.selectedAtmObservable,
                BiFunction<GoogleMap, Optional<AtmNode>, Pair<GoogleMap, Optional<AtmNode>>> { map, selectedAtm ->
                    Pair(map, selectedAtm)
                }).subscribe {

            if (it.second.isNotNull()) {

                val atmNode = it.second.safeValue()

                it.first.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(atmNode.lat, atmNode.lon),
                        MainViewModel.targetZoom))
            }
        })

        compositeDisposable.add(Observable.combineLatest(rxGoogleMap.map(), viewModel.locationObservable,
                BiFunction<GoogleMap, Optional<Location>, Pair<GoogleMap, Optional<Location>>> { map, location ->
                    Pair(map, location)
                }).subscribe {

            if (it.second.isNotNull()) {

                if (!locationSet) {

                    val location = it.second.safeValue()

                    it.first.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
                            MainViewModel.targetZoom))
                    locationSet = true
                }
            }
        })


        compositeDisposable.add(Observable.combineLatest(rxGoogleMap.map(), viewModel.locationPermissionObservable,
                BiFunction<GoogleMap, Boolean, Pair<GoogleMap, Boolean>> { map, enabled ->
                    Pair(map, enabled)
                }).subscribe {

            it.first.isMyLocationEnabled = it.second

        })

    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val res = clusterManager.onMarkerClick(marker)

        if (!res && marker.tag != null) {

            viewModel.selectAtm((marker.tag as AtmNode?)!!)
            return true

        }
        return true
    }


    override fun onClusterClick(cluster: Cluster<AtmNode>): Boolean {
        val builder = LatLngBounds.builder()
        for (item in cluster.items) {
            builder.include(item.position)
        }
        val bounds = builder.build()

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        return true
    }


    inner class AtmRenderer internal constructor(context: Context, map: GoogleMap, clusterManager: ClusterManager<AtmNode>)
        : DefaultClusterRenderer<AtmNode>(context, map, clusterManager) {

        override fun onClusterItemRendered(clusterItem: AtmNode, marker: Marker) {
            marker.tag = clusterItem
            if (clusterItem == selectedAtm) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected))
            } else {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm))
            }
            super.onClusterItemRendered(clusterItem, marker)
        }
    }

    companion object {

        const val tag = "GoogleMapsFragment"

        fun instance(): Fragment = GoogleMapsFragment()
    }
}
