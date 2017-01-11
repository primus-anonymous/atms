package com.neocaptainnemo.testing.service;

import com.google.gson.annotations.SerializedName;
import com.neocaptainnemo.testing.model.AtmNode;

import java.util.List;

class OsmResponse {

    @SerializedName("elements")
    private List<AtmNode> atms;

    public List<AtmNode> getAtms() {
        return atms;
    }
}
