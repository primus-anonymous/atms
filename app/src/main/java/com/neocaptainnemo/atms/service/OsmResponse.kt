package com.neocaptainnemo.atms.service

import com.google.gson.annotations.SerializedName
import com.neocaptainnemo.atms.model.AtmNode

class OsmResponse {

    @SerializedName("elements")
    val atms: List<AtmNode>? = null
}
