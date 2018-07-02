package com.neocaptainnemo.atms.ui.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class RxGoogleMap(mapFragment: SupportMapFragment) {

    private val mapSubject: BehaviorSubject<GoogleMap> = BehaviorSubject.create()


    init {

        Observable.create<GoogleMap> {

            mapFragment.getMapAsync { map ->
                it.onNext(map)
            }
        }.subscribe(mapSubject)


    }

    fun map(): Observable<GoogleMap> = mapSubject

}