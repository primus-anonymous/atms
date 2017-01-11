package com.neocaptainnemo.testing.ui.map;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neocaptainnemo.testing.R;
import com.neocaptainnemo.testing.databinding.FragmentYandexMapsBinding;
import com.neocaptainnemo.testing.model.AtmNode;

import java.util.List;

import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;

public class YandexMapsFragment extends Fragment implements IMapView, OnMapListener {

    public static final String TAG = "YandexMapsFragment";

    private FragmentYandexMapsBinding binding;
    private OnGotViewPort viewPortListener;

    public static Fragment instance() {
        return new YandexMapsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGotViewPort) {
            viewPortListener = (OnGotViewPort) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewPortListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.map.getMapController().addMapListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_yandex_maps, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void showAtms(List<AtmNode> atmNodes) {

    }

    @Override
    public void clearMap() {

    }

    @Override
    public void enableMyLocation() {

    }

    @Override
    public void disableMyLocation() {

    }

    @Override
    public void clearSelectedMarker() {

    }

    @Override
    public void setMyLocation(Location location) {

    }

    @Override
    public void selectAtm(@NonNull AtmNode atmNode) {

    }

    @Override
    public void onMapActionEvent(MapEvent mapEvent) {

    }
}
