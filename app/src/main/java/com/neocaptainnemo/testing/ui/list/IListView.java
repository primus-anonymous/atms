package com.neocaptainnemo.testing.ui.list;

import android.location.Location;
import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;

import java.util.List;

public interface IListView {

    void showAtms(@NonNull List<AtmNode> atmNodes);

    void setMyLocation(Location location);

    interface OnAtmSelected {
        void onAtmSelected(@NonNull AtmNode atmNode);
    }
}
