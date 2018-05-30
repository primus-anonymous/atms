package com.neocaptainnemo.atms.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import kotlinx.android.parcel.Parcelize


@Parcelize
class AtmNode : Parcelable, ClusterItem {
    override fun getSnippet(): String = ""

    override fun getTitle(): String = ""

    override fun getPosition(): LatLng = LatLng(lat, lon)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AtmNode

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    @SerializedName("id")
    var id: Long = 0
    @SerializedName("lat")
    val lat = 0.0
    @SerializedName("lon")
    val lon = 0.0
    @SerializedName("type")
    val type = ""
    @SerializedName("tags")
    var tags: Tag? = null
}
