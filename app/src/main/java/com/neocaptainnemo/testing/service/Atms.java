package com.neocaptainnemo.testing.service;

import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;

import java.util.List;

import rx.Observable;

public interface Atms {

    Observable<List<AtmNode>> request(@NonNull ViewPort viewPort);

}
