package com.neocaptainnemo.testing.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.neocaptainnemo.testing.R;
import com.neocaptainnemo.testing.app.App;
import com.neocaptainnemo.testing.databinding.ActivityMainBinding;
import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;
import com.neocaptainnemo.testing.service.AddressFormatter;
import com.neocaptainnemo.testing.service.DistanceFormatter;
import com.neocaptainnemo.testing.ui.list.IListView;
import com.neocaptainnemo.testing.ui.list.ListFragment;
import com.neocaptainnemo.testing.ui.map.GoogleMapsFragment;
import com.neocaptainnemo.testing.ui.map.IMapView;
import com.neocaptainnemo.testing.ui.settings.SettingsFragment;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        IView, IMapView.OnGotViewPort, IMapView.OnAtmClicked,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener,
        IListView.OnAtmSelected {

    private static final int LOCATION_PERMISSION = 1;
    @Inject
    Presenter presenter;
    @Inject
    AddressFormatter addressFormatter;
    @Inject
    DistanceFormatter distanceFormatter;
    private Tab openedTab;
    private ActivityMainBinding binding;
    private boolean semiTransparentToolBar;
    private BottomSheetBehavior<NestedScrollView> atmDetailsBottomSheet;
    private AtmNode selectedAtm;
    private GoogleApiClient googleApiClient;
    private Location location;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).getAppComponent().inject(this);
        presenter.setView(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);
        binding.navigate.setOnClickListener(view -> {
            if (selectedAtm != null) {

                NavigationDecisionDialogFragment.instance(selectedAtm).show(getSupportFragmentManager(),
                        NavigationDecisionDialogFragment.TAG);
            }
        });


        atmDetailsBottomSheet = BottomSheetBehavior.from(binding.atmDetails);
        atmDetailsBottomSheet.setPeekHeight(0);
        binding.navigate.setScaleX(0);
        binding.navigate.setScaleY(0);
        binding.zoomFurther.setScaleX(0);
        binding.zoomFurther.setScaleY(0);
        atmDetailsBottomSheet.setHideable(true);
        binding.navigate.setVisibility(View.INVISIBLE);

        atmDetailsBottomSheet.setBottomSheetCallback(new BottomSheetBehaviour());

        if (savedInstanceState == null) {
            semiTransparentToolBar = true;
            openedTab = Tab.MAP;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map_container, GoogleMapsFragment.instance(), GoogleMapsFragment.TAG)
                    .commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.list_container, ListFragment.instance(), ListFragment.TAG)
                    .commit();


            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, SettingsFragment.instance(), SettingsFragment.TAG)
                    .commit();

            binding.listContainer.setVisibility(View.GONE);
            binding.settingsContainer.setVisibility(View.GONE);

            int accessFineLocation = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {


                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);
                }

            } else {
                getMyLocation();
            }
        }
    }

    private void getMyLocation() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        googleApiClient.connect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar, menu);

        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setQueryHint(getString(R.string.search_hint));

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search),
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        searchView.setIconified(false);
                        atmDetailsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        searchView.setQuery("", true);
                        return true;
                    }
                });

        searchView.setOnCloseListener(() -> {
            searchView.setIconified(false);
            return true;
        });

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        IMapView mapView = getMap();

        if (mapView != null) {
            mapView.setMyLocation(location);
        }

        IListView listView = ListFragment.onStack(getSupportFragmentManager());

        if (listView != null) {
            listView.setMyLocation(location);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        //do nothing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //do nothing
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IMapView mapView = getMap();

                    if (mapView != null) {
                        mapView.enableMyLocation();
                    }

                    getMyLocation();

                } else {

                    IMapView mapView = getMap();

                    if (mapView != null) {
                        mapView.disableMyLocation();
                    }
                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps:
                showMap();
                return true;

            case R.id.action_list:
                showList();
                return true;

            case R.id.action_settings:
                showSettings();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (atmDetailsBottomSheet.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            atmDetailsBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (openedTab != Tab.MAP) {
            showMap();
        } else {
            super.onBackPressed();
        }
    }

    private void animateColorChange(int from, int to) {
        int colorFrom = ContextCompat.getColor(this, from);
        int colorTo = ContextCompat.getColor(this, to);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        colorAnimation.addUpdateListener(animator -> binding.toolbar.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    @Override
    public void onGotViewPort(@NonNull ViewPort viewPort) {
        presenter.fetchAtms(viewPort);
        hideZoomInFurther();
    }

    @Override
    public void onZoomFurther() {
        showZoomInFurther();
    }

    @Override
    public void onGotAtms(@NonNull List<AtmNode> atmNodes) {

        IMapView mapView = getMap();

        if (mapView != null) {
            mapView.clearMap();
            mapView.showAtms(atmNodes);
        }

        IListView listView = ListFragment.onStack(getSupportFragmentManager());

        if (listView != null) {
            listView.showAtms(atmNodes);
        }
    }

    @Override
    public void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progress.setVisibility(View.GONE);
    }

    @Override
    public void showZoomInFurther() {
        binding.zoomFurther.setVisibility(View.VISIBLE);
        binding.zoomFurther
                .animate()
                .scaleX(1.f)
                .scaleY(1.f)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setListener(null)
                .start();
    }

    @Override
    public void hideZoomInFurther() {
        binding.zoomFurther
                .animate()
                .scaleX(0.f)
                .scaleY(0.f)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.zoomFurther.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @Override
    public void showMap() {
        if (!semiTransparentToolBar) {
            animateColorChange(R.color.colorPrimary, R.color.colorPrimarySemi);
        }
        semiTransparentToolBar = true;

        ViewCompat.setElevation(binding.appbar, getResources().getDimension(R.dimen.app_bar_elevation));

        binding.listContainer.setVisibility(View.GONE);
        binding.settingsContainer.setVisibility(View.GONE);
        openedTab = Tab.MAP;
    }

    @Override
    public void showList() {
        binding.zoomFurther.setVisibility(View.GONE);
        if (semiTransparentToolBar) {
            animateColorChange(android.R.color.transparent, R.color.colorPrimary);
        }
        semiTransparentToolBar = false;
        ViewCompat.setElevation(binding.appbar, getResources().getDimension(R.dimen.app_bar_elevation));


        binding.listContainer.setVisibility(View.VISIBLE);
        binding.settingsContainer.setVisibility(View.GONE);
        openedTab = Tab.LIST;
    }

    @Override
    public void showSettings() {
        binding.zoomFurther.setVisibility(View.GONE);
        if (semiTransparentToolBar) {
            animateColorChange(android.R.color.transparent, R.color.colorPrimary);
        }
        semiTransparentToolBar = false;
        ViewCompat.setElevation(binding.appbar, getResources().getDimension(R.dimen.app_bar_elevation));

        binding.listContainer.setVisibility(View.GONE);
        binding.settingsContainer.setVisibility(View.VISIBLE);
        openedTab = Tab.SETTINGS;
    }

    @Override
    public void onAtmClicked(@NonNull AtmNode atmNode) {

        selectedAtm = atmNode;

        binding.atmName.setText(atmNode.getTags().getName());
        atmDetailsBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);

        String address = addressFormatter.format(atmNode);

        if (address.isEmpty()) {
            binding.address.setText(R.string.no_address);
        } else {
            binding.address.setText(address);
        }

        if (location != null) {
            binding.distance.setVisibility(View.VISIBLE);

            double distance = SphericalUtil.computeDistanceBetween(new LatLng(atmNode.getLat(), atmNode.getLon()),
                    new LatLng(location.getLatitude(), location.getLongitude()));

            String fromYou = getString(R.string.distance_m_from_you, distanceFormatter.format(distance));
            binding.distance.setText(fromYou);
        } else {
            binding.distance.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String str) {
        presenter.setFilter(str);
        return true;
    }

    @Override
    public void onAtmSelected(@NonNull AtmNode atmNode) {
        IMapView mapView = getMap();

        if (mapView != null) {
            mapView.selectAtm(atmNode);
            showMap();
        }
    }

    private IMapView getMap() {
        IMapView mapView = GoogleMapsFragment.onStack(getSupportFragmentManager());

        if (mapView != null) {
            return mapView;
        }

        return null;
    }

    enum Tab {MAP, LIST, SETTINGS}

    private class BottomSheetBehaviour extends BottomSheetBehavior.BottomSheetCallback {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                IMapView mapView = getMap();

                if (mapView != null) {
                    mapView.clearSelectedMarker();
                }

            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navigate.animate().scaleX(1).scaleY(1)
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                binding.navigate.setVisibility(View.VISIBLE);
                            }
                        })

                        .start();
            } else {
                binding.navigate.animate().scaleX(0).scaleY(0)
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                binding.navigate.setVisibility(View.INVISIBLE);
                            }
                        })
                        .start();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            //do nothing
        }

    }

}
