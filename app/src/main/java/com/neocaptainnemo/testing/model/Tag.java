package com.neocaptainnemo.testing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Tag implements Parcelable {

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
    @SerializedName("atm")
    private String atm;
    @SerializedName("name")
    private String name;
    @SerializedName("addr:city")
    private String city;
    @SerializedName("addr:housenumber")
    private String houseNumber;
    @SerializedName("addr:postcode")
    private String postCode;
    @SerializedName("addr:street")
    private String street;
    @SerializedName("atm:operator")
    private String atmOperator;
    @SerializedName("opening_hours")
    private String openingHours;


    public Tag() {

    }

    protected Tag(Parcel in) {
        atm = in.readString();
        name = in.readString();
        city = in.readString();
        houseNumber = in.readString();
        postCode = in.readString();
        street = in.readString();
        atmOperator = in.readString();
        openingHours = in.readString();
    }

    public String getAtm() {
        return atm;
    }

    public void setAtm(String atm) {
        this.atm = atm;
    }

    public String getName() {
        if (atmOperator != null) {
            return atmOperator;
        }
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }


    public String getOpeningHours() {
        return openingHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(atm);
        parcel.writeString(name);
        parcel.writeString(city);
        parcel.writeString(houseNumber);
        parcel.writeString(postCode);
        parcel.writeString(street);
        parcel.writeString(atmOperator);
        parcel.writeString(openingHours);
    }
}
