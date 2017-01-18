package com.neocaptainnemo.atms.service;

import android.support.annotation.NonNull;

import com.neocaptainnemo.atms.model.AtmNode;
import com.neocaptainnemo.atms.model.ViewPort;

import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.functions.Func1;

class AtmsRepo implements Atms {

    private OpenStreetMap openStreetMapApi;

    AtmsRepo(OpenStreetMap openStreetMapApi) {
        this.openStreetMapApi = openStreetMapApi;
    }


    @Override
    public Observable<List<AtmNode>> request(@NonNull ViewPort viewPort) {

        StringBuilder sb = new StringBuilder();

        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("[out:json];node(%f,%f,%f,%f)[atm];out;", viewPort.getLatStart(),
                viewPort.getLngStart(), viewPort.getLatEnd(), viewPort.getLngEnd());

        return openStreetMapApi.request(sb.toString())
                .map(new Func1<OsmResponse, List<AtmNode>>() {
                    @Override
                    public List<AtmNode> call(OsmResponse osmResponse) {
                        if (osmResponse.getAtms() == null) {
                            return Collections.emptyList();
                        }

                        return osmResponse.getAtms();
                    }
                });
    }
}