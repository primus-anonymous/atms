package com.neocaptainnemo.testing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;


public class AtmNode implements Parcelable, ClusterItem {

    public static final Creator<AtmNode> CREATOR = new Creator<AtmNode>() {
        @Override
        public AtmNode createFromParcel(Parcel in) {
            return new AtmNode(in);
        }

        @Override
        public AtmNode[] newArray(int size) {
            return new AtmNode[size];
        }
    };
    @SerializedName("id")
    private long id;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lon")
    private double lon;
    @SerializedName("type")
    private String type;
    @SerializedName("tags")
    private Tag tags;

    public AtmNode() {

    }

    protected AtmNode(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
        type = in.readString();
        tags = in.readParcelable(Tag.class.getClassLoader());
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getType() {
        return type;
    }

    public Tag getTags() {
        return tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(type);
        parcel.writeParcelable(tags, i);
    }

    public long getId() {
        return id;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lon);
    }

    public void setTag(Tag tag) {
        this.tags = tag;
    }
}
