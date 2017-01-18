package com.neocaptainnemo.atms.service;

import com.google.gson.annotations.SerializedName;
import com.neocaptainnemo.atms.model.AtmNode;

import java.util.List;

public class OsmResponse {

    @SerializedName("elements")
    private List<AtmNode> atms;

    public List<AtmNode> getAtms() {
        return atms;
    }
}
