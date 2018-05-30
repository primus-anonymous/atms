package com.neocaptainnemo.atms.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
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
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.ui.IAtmsView

class GoogleMapsFragment : Fragment(), IMapView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ClusterManager.OnClusterClickListener<AtmNode> {

    private var viewPortListener: IMapView.MapDelegate? = null
    private var atmListener: IAtmsView.OnAtmSelected? = null
    private var map: GoogleMap? = null
    private var selectedAtm: AtmNode? = null
    private var selectedMarker: Marker? = null
    private var locationSet = false

    private lateinit var clusterManager: ClusterManager<AtmNode>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is IMapView.MapDelegate) {
            viewPortListener = context
        }
        if (context is IAtmsView.OnAtmSelected) {
            atmListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewPortListener = null
        atmListener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_google_maps, container, false)

    override fun onMapReady(googleMap: GoogleMap) {

        clusterManager = ClusterManager(context, googleMap)
        clusterManager.renderer = AtmRenderer(context!!, googleMap, clusterManager)
        clusterManager.setOnClusterClickListener(this)
        map = googleMap
        map!!.setOnMarkerClickListener(this)

        map!!.setPadding(0, resources.getDimension(R.dimen.app_bar_height).toInt(), 0, 0)

        googleMap.setOnCameraIdleListener {

            clusterManager.cluster()

            val bounds = googleMap.projection.visibleRegion.latLngBounds
            val startLng = bounds.southwest.longitude
            val startLat = bounds.southwest.latitude

            val endLng = bounds.northeast.longitude
            val endLat = bounds.northeast.latitude

            val viewPort = ViewPort(startLng, startLat, endLng, endLat)

            val zoomLevel = googleMap.cameraPosition.zoom

            if (zoomLevel > minZoomLevel) {
                if (viewPortListener != null) {
                    viewPortListener!!.onGotViewPort(viewPort)
                }
            } else {
                if (viewPortListener != null) {
                    viewPortListener!!.onZoomFurther()
                }
            }
        }

        val accessFineLocation = ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            map!!.isMyLocationEnabled = true
        }
    }

    override fun showAtms(atmNodes: List<AtmNode>) {
        clusterManager.addItems(atmNodes)
        clusterManager.cluster()
    }

    override fun clear() {
        clusterManager.clearItems()
        selectedMarker = null
    }

    override fun enableMyLocation() {
        if (map != null) {
            map!!.isMyLocationEnabled = true
        }
    }

    override fun disableMyLocation() {
        if (map != null) {
            map!!.isMyLocationEnabled = false
        }
    }

    override fun selectAtm(atmNode: AtmNode) {
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

    override fun clearSelectedMarker() {
        selectedAtm = null
        if (selectedMarker != null) {
            selectedMarker!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm))
            selectedMarker = null
        }

    }

    override fun setMyLocation(location: Location) {
        if (!locationSet && map != null) {
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
                    targetZoom))
            locationSet = true
        }
    }

    override fun moveCameraToAtm(atmNode: AtmNode) {
        if (map != null) {
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(atmNode.lat, atmNode.lon),
                    targetZoom))
        }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        val res = clusterManager.onMarkerClick(marker)

        if (!res && atmListener != null && marker.tag != null) {
            atmListener!!.onAtmSelected((marker.tag as AtmNode?)!!, GoogleMapsFragment.tag)

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

        map!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

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
        private const val minZoomLevel = 12
        private const val targetZoom = 15.0f


        fun instance(): Fragment {
            return GoogleMapsFragment()
        }

        fun onStack(manager: FragmentManager): IMapView? {
            val fragment = manager.findFragmentByTag(tag)
            return if (fragment is IMapView) {
                fragment
            } else null
        }
    }
}