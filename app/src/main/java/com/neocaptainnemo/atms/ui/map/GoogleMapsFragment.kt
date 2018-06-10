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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.ui.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class GoogleMapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        ClusterManager.OnClusterClickListener<AtmNode>, GoogleMap.OnCameraIdleListener {

    private var map: GoogleMap? = null
    private var selectedAtm: AtmNode? = null
    private var selectedMarker: Marker? = null
    private var locationSet = false

    private lateinit var clusterManager: ClusterManager<AtmNode>

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

        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_google_maps, container, false)

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        clusterManager = ClusterManager(context, googleMap)
        clusterManager.renderer = AtmRenderer(context!!, googleMap, clusterManager)
        clusterManager.setOnClusterClickListener(this)
        map = googleMap

        googleMap.setOnMarkerClickListener(this)

        googleMap.setPadding(0, 0, 0, 0)

        googleMap.setOnCameraIdleListener(this)

        googleMap.isMyLocationEnabled = viewModel.locationPermission

    }

    override fun onCameraIdle() {

        if (map == null) return

        clusterManager.cluster()

        val zoomLevel = map!!.cameraPosition.zoom

        viewModel.zoom = zoomLevel


        val bounds = map!!.projection.visibleRegion.latLngBounds

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

        compositeDisposable.add(viewModel.atms().subscribe({

            clusterManager.clearItems()
            clusterManager.addItems(it)
            clusterManager.cluster()

        }, {
            //do nothing
        }))

        compositeDisposable.add(viewModel.selectedAtmObservable.subscribe {
            clearSelectedMarker()

            if (it.isNotNull()) {
                selectAtm(it.value!!)
                moveCameraToAtm(it.value)
            }
        })

        compositeDisposable.add(viewModel.locationObservable.subscribe {
            if (it.isNotNull()) {
                setMyLocation(it.value!!)
            }
        })

        compositeDisposable.add(viewModel.locationPermissionObservable.subscribe {
            map?.isMyLocationEnabled = it
        })
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()

        map?.isMyLocationEnabled = false

    }

    private fun selectAtm(atmNode: AtmNode) {
        selectedAtm = atmNode

        for (marker in clusterManager.markerCollection.markers) {
            if (marker.tag != null && marker.tag is AtmNode) {
                val atm = marker.tag as AtmNode
                if (atm == selectedAtm) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected))
                }
            }
        }
    }

    private fun clearSelectedMarker() {
        selectedAtm = null
        selectedMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm))
        selectedMarker = null

    }

    private fun setMyLocation(location: Location) {
        if (!locationSet) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
                    MainViewModel.targetZoom))
            locationSet = true
        }
    }

    private fun moveCameraToAtm(atmNode: AtmNode) =
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(atmNode.lat, atmNode.lon),
                    MainViewModel.targetZoom))


    override fun onMarkerClick(marker: Marker): Boolean {
        val res = clusterManager.onMarkerClick(marker)

        if (!res && marker.tag != null) {

            viewModel.selectAtm((marker.tag as AtmNode?)!!)

            if (selectedMarker != null) {
                selectedMarker!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm))
            }

            selectedMarker = marker
            selectedMarker!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected))
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

        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        return true
    }


    inner class AtmRenderer internal constructor(context: Context, map: GoogleMap, clusterManager: ClusterManager<AtmNode>) : DefaultClusterRenderer<AtmNode>(context, map, clusterManager) {

        override fun onBeforeClusterItemRendered(item: AtmNode, markerOptions: MarkerOptions) {
            if (selectedAtm != null && selectedAtm!!.id == item.id) {
                markerOptions.position(LatLng(item.lat, item.lon))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected))

            } else {
                markerOptions.position(LatLng(item.lat, item.lon))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm))
            }
        }

        override fun onClusterItemRendered(clusterItem: AtmNode, marker: Marker) {
            marker.tag = clusterItem
            if (clusterItem == selectedAtm) {
                selectedMarker = marker
            }
            super.onClusterItemRendered(clusterItem, marker)
        }


    }

    companion object {

        const val tag = "GoogleMapsFragment"

        fun instance(): Fragment = GoogleMapsFragment()
    }
}