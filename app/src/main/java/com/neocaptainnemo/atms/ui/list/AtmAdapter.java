package com.neocaptainnemo.atms.ui.list;

import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.neocaptainnemo.atms.R;
import com.neocaptainnemo.atms.databinding.ItemAtmBinding;
import com.neocaptainnemo.atms.model.AtmNode;
import com.neocaptainnemo.atms.service.AddressFormatter;
import com.neocaptainnemo.atms.service.DistanceFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

class AtmAdapter extends RecyclerView.Adapter<AtmAdapter.AtmHolder> {

    private AddressFormatter addressFormatter;
    private DistanceFormatter distanceFormatter;
    private List<AtmNode> data = new ArrayList<>();
    private Location location;
    private OnAtmClicked atmListener;

    @Inject
    AtmAdapter(AddressFormatter addressFormatter, DistanceFormatter distanceFormatter) {
        this.addressFormatter = addressFormatter;
        this.distanceFormatter = distanceFormatter;
    }

    void clear() {
        data.clear();
    }

    void add(@NonNull Collection<AtmNode> atms) {
        data.addAll(atms);

        if (location != null) {
            Collections.sort(data, (node1, node2) -> {
                double distance1 = SphericalUtil.computeDistanceBetween(new LatLng(node1.getLat(), node1.getLon()),
                        new LatLng(location.getLatitude(), location.getLongitude()));

                double distance2 = SphericalUtil.computeDistanceBetween(new LatLng(node2.getLat(), node2.getLon()),
                        new LatLng(location.getLatitude(), location.getLongitude()));

                return (int) ((distance1 - distance2) * 100);
            });
        }
    }

    @Override
    public AtmHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemAtmBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_atm, parent, false);
        return new AtmHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(AtmHolder holder, int position) {
        AtmNode node = data.get(position);
        holder.binding.name.setText(node.getTags().getName());

        String address = addressFormatter.format(node);

        if (address.isEmpty()) {
            holder.binding.address.setText(R.string.no_address);
        } else {
            holder.binding.address.setText(address);
        }

        if (location == null) {
            holder.binding.distance.setVisibility(View.GONE);
        } else {
            holder.binding.distance.setVisibility(View.VISIBLE);
            double distance = SphericalUtil.computeDistanceBetween(new LatLng(node.getLat(), node.getLon()),
                    new LatLng(location.getLatitude(), location.getLongitude()));

            holder.binding.distance.setText(distanceFormatter.format(distance));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    void setLocation(Location location) {
        this.location = location;
    }

    void setAtmListener(OnAtmClicked atmListener) {
        this.atmListener = atmListener;
    }

    interface OnAtmClicked {
        void onAtmClicked(@NonNull AtmNode atmNode);
    }

    static class AtmHolder extends RecyclerView.ViewHolder {

        ItemAtmBinding binding;

        AtmHolder(ItemAtmBinding binding, AtmAdapter atmAdapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(view -> {
                if (atmAdapter.atmListener != null) {
                    atmAdapter.atmListener.onAtmClicked(atmAdapter.data.get(getAdapterPosition()));
                }

            });
        }
    }
}
