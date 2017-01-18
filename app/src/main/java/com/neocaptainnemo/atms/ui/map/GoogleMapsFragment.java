package com.neocaptainnemo.atms.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.neocaptainnemo.atms.R;
import com.neocaptainnemo.atms.databinding.FragmentGoogleMapsBinding;
import com.neocaptainnemo.atms.model.AtmNode;
import com.neocaptainnemo.atms.model.ViewPort;

import java.util.List;

public class GoogleMapsFragment extends Fragment implements IMapView, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, ClusterManager.OnClusterClickListener<AtmNode> {

    public static final String TAG = "GoogleMapsFragment";
    private static final int MIN_ZOOM_LEVEL = 12;
    private static final float TARGET_ZOOM = 15.0f;
    private MapDelegate viewPortListener;
    private OnAtmSelected atmListener;
    private GoogleMap map;
    private AtmNode selectedAtm;
    private Marker selectedMarker;
    private boolean locationSet;

    private ClusterManager<AtmNode> clusterManager;


    public static Fragment instance() {
        return new GoogleMapsFragment();
    }

    public static IMapView onStack(@NonNull FragmentManager manager) {
        Fragment fragment = manager.findFragmentByTag(TAG);
        if (fragment instanceof IMapView) {
            return (IMapView) fragment;
        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapDelegate) {
            viewPortListener = (MapDelegate) context;
        }
        if (context instanceof OnAtmSelected) {
            atmListener = (OnAtmSelected) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewPortListener = null;
        atmListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentGoogleMapsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_google_maps, container, false);
        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setRenderer(new AtmRenderer(getContext(), googleMap, clusterManager));
        clusterManager.setOnClusterClickListener(this);
        map = googleMap;
        map.setOnMarkerClickListener(this);

        map.setPadding(0, (int) getResources().getDimension(R.dimen.app_bar_height), 0, 0);

        googleMap.setOnCameraIdleListener(() -> {

            clusterManager.cluster();

            LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
            double startLng = bounds.southwest.longitude;
            double startLat = bounds.southwest.latitude;

            double endLng = bounds.northeast.longitude;
            double endLat = bounds.northeast.latitude;

            ViewPort viewPort = new ViewPort(startLng, startLat, endLng, endLat);

            float zoomLevel = googleMap.getCameraPosition().zoom;

            if (zoomLevel > MIN_ZOOM_LEVEL) {
                if (viewPortListener != null) {
                    viewPortListener.onGotViewPort(viewPort);
                }
            } else {
                if (viewPortListener != null) {
                    viewPortListener.onZoomFurther();
                }
            }
        });

        int accessFineLocation = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void showAtms(@NonNull List<AtmNode> atmNodes) {
        if (clusterManager != null) {
            clusterManager.addItems(atmNodes);
            clusterManager.cluster();
        }
    }

    @Override
    public void clear() {
        if (clusterManager != null) {
            clusterManager.clearItems();
            selectedMarker = null;
        }
    }

    @Override
    public void enableMyLocation() {
        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void disableMyLocation() {
        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    public void selectAtm(@NonNull AtmNode atmNode) {
        if (clusterManager != null) {
            selectedAtm = atmNode;

            for (Marker marker : clusterManager.getMarkerCollection().getMarkers()) {
                if (marker.getTag() != null && marker.getTag() instanceof AtmNode) {
                    AtmNode atm = (AtmNode) marker.getTag();
                    if (atm.equals(selectedAtm)) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected));
                    }
                }
            }
        }
    }

    @Override
    public void clearSelectedMarker() {
        selectedAtm = null;
        if (selectedMarker != null) {
            selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm));
            selectedMarker = null;
        }

    }

    @Override
    public void setMyLocation(Location location) {
        if (location != null && !locationSet && map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                    TARGET_ZOOM));
            locationSet = true;
        }
    }

    @Override
    public void moveCameraToAtm(@NonNull AtmNode atmNode) {
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(atmNode.getLat(), atmNode.getLon()),
                    TARGET_ZOOM));
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        boolean res = clusterManager.onMarkerClick(marker);

        if (!res && atmListener != null && marker.getTag() != null) {
            atmListener.onAtmSelected((AtmNode) marker.getTag(), TAG);

            if (selectedMarker != null) {
                selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm));
            }

            selectedMarker = marker;
            selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected));
            return true;

        }
        return true;
    }


    @Override
    public boolean onClusterClick(Cluster<AtmNode> cluster) {

        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        final LatLngBounds bounds = builder.build();

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        return true;
    }


    private class AtmRenderer extends DefaultClusterRenderer<AtmNode> {

        AtmRenderer(Context context, GoogleMap map, ClusterManager<AtmNode> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(AtmNode item, MarkerOptions markerOptions) {
            if (selectedAtm != null && selectedAtm.getId() == item.getId()) {
                markerOptions.position(new LatLng(item.getLat(), item.getLon()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm_selected));

            } else {
                markerOptions.position(new LatLng(item.getLat(), item.getLon()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_atm));
            }
        }

        @Override
        protected void onClusterItemRendered(AtmNode clusterItem, Marker marker) {
            marker.setTag(clusterItem);
            if (clusterItem.equals(selectedAtm)) {
                selectedMarker = marker;
            }
            super.onClusterItemRendered(clusterItem, marker);
        }


    }
}