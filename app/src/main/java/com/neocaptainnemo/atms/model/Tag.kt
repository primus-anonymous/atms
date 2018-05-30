package com.neocaptainnemo.atms.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class Tag : Parcelable {

    @SerializedName("atm")
    var atm = ""
    @SerializedName("name")
    var name: String? = null
    @SerializedName("addr:city")
    var city: String? = null
    @SerializedName("addr:housenumber")
    var houseNumber: String? = null
    @SerializedName("addr:postcode")
    var postCode: String? = null
    @SerializedName("addr:street")
    var street: String? = null
    @SerializedName("atm:operator")
    private val atmOperator: String? = null
    @SerializedName("opening_hours")
    val openingHours: String? = null

}