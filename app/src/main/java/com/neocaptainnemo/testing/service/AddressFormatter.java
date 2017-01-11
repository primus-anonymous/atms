package com.neocaptainnemo.testing.service;

import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;

import javax.inject.Inject;

public class AddressFormatter {

    @Inject
    public AddressFormatter() {

    }

    public String format(@NonNull AtmNode atmNode) {
        StringBuilder address = new StringBuilder();

        if (atmNode.getTags().getStreet() != null) {
            address.append(atmNode.getTags().getStreet());
        }

        if (atmNode.getTags().getHouseNumber() != null) {
            address.append(' ');
            address.append(atmNode.getTags().getHouseNumber());
        }

        if (atmNode.getTags().getPostCode() != null) {
            address.append(',');
            address.append(' ');
            address.append(atmNode.getTags().getPostCode());
        }

        if (atmNode.getTags().getCity() != null) {
            address.append(',');
            address.append(' ');
            address.append(atmNode.getTags().getCity());
        }

        String addressStr = address.toString().trim();

        if (addressStr.startsWith(",")) {
            addressStr = addressStr.substring(1);
        }

        if (addressStr.endsWith(",")) {
            addressStr = addressStr.substring(0, addressStr.length() - 1);
        }

        return addressStr.trim();

    }
}
