package com.neocaptainnemo.atms.service;

import android.support.annotation.NonNull;

import com.neocaptainnemo.atms.model.AtmNode;
import com.neocaptainnemo.atms.model.ViewPort;

import java.util.List;

import rx.Observable;

public interface Atms {

    /**
     * Fetches atms located in a given view port.
     *
     * @param viewPort - view port (bounds of lat, lng, where search is performed).
     * @return emits list of atms on success.
     */
    Observable<List<AtmNode>> request(@NonNull ViewPort viewPort);

}
