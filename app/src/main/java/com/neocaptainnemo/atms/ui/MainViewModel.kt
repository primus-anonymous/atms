package com.neocaptainnemo.atms.ui

import android.arch.lifecycle.ViewModel
import android.location.Location
import com.neocaptainnemo.atms.Optional
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.carry
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.service.Atms
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import javax.inject.Inject


class MainViewModel @Inject constructor(private val atmsRepo: Atms) : ViewModel() {

    private val progress = BehaviorSubject.createDefault(false)

    private val empty = BehaviorSubject.createDefault(false)

    private val locationPermissionSubject = BehaviorSubject.createDefault(false)

    private val searchQuerySubject = BehaviorSubject.createDefault("")

    private val errorSubject = PublishSubject.create<Throwable>()

    private val viewPortSubject = BehaviorSubject.create<ViewPort>()

    private val selectedAtmNodeSubject = PublishSubject.create<Optional<AtmNode>>()

    private val locationSubject = BehaviorSubject.createDefault<Optional<Location>>(Optional.nullValue())

    private val tabSubject = BehaviorSubject.createDefault(Tab.MAP)

    private val zoomSubject = BehaviorSubject.createDefault(MainViewModel.targetZoom)

    private var atmRepoCall: Observable<List<AtmNode>> = Observable.just(listOf())

    private var cachedViewPort: ViewPort? = null

    val progressObservable: Observable<Boolean> = progress

    val emptyObservable: Observable<Boolean> = empty

    val locationObservable: Observable<Optional<Location>> = locationSubject

    val zoomInFurtherObservable: Observable<Boolean> = Observable.combineLatest(tabSubject, zoomSubject, BiFunction { t1, t2 ->
        t1 == Tab.MAP && t2 < minZoomLevel
    })

    val selectedAtmObservable: Observable<Optional<AtmNode>> = selectedAtmNodeSubject

    fun selectAtm(node: AtmNode) = selectedAtmNodeSubject.onNext(node.carry())

    fun clearAtm() = selectedAtmNodeSubject.onNext(Optional.nullValue())

    val errorObservable: Observable<Int> = errorSubject.map {
        if (it is IOException) return@map R.string.network_error

        return@map R.string.general_error
    }

    var viewPort: ViewPort
        get() = viewPortSubject.value
        set(value) {
            viewPortSubject.onNext(value)
        }

    var searchQuery: String
        get() = searchQuerySubject.value
        set(value) = searchQuerySubject.onNext(value)

    var tab: Tab
        get() = tabSubject.value
        set(value) = tabSubject.onNext(value)

    val tabObservable: Observable<Tab> = tabSubject

    var zoom: Float
        get() = zoomSubject.value
        set(value) = zoomSubject.onNext(value)

    var locationPermission: Boolean
        get() = locationPermissionSubject.value
        set(value) = locationPermissionSubject.onNext(value)

    fun setLocation(location: Location) = locationSubject.onNext(location.carry())

    fun clearLocation() = locationSubject.onNext(Optional.nullValue())

    val locationPermissionObservable: Observable<Boolean> = locationPermissionSubject

    val searchVisibilityObservable: Observable<Boolean> = tabSubject.map { it != Tab.SETTINGS }

    fun atms(): Observable<List<AtmNode>> = Observable.combineLatest(viewPortSubject, searchQuerySubject, zoomSubject,
            Function3<ViewPort, String, Float, Pair<String, Observable<List<AtmNode>>>> { viewPort, query, zoom ->

                if (zoom < minZoomLevel) {
                    cachedViewPort = null
                    atmRepoCall = Observable.just(listOf())
                } else {
                    if (cachedViewPort == null || !cachedViewPort!!.isInside(viewPort)) {

                        cachedViewPort = viewPort.extended()

                        atmRepoCall = atmsRepo.request(cachedViewPort!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError {
                                    cachedViewPort = null
                                    errorSubject.onNext(it)
                                }
                                .onErrorReturn { listOf() }
                                .cache()
                    }

                }

                Pair(query, atmRepoCall)

            })
            .doOnNext { progress.onNext(true) }
            .switchMap { arg ->

                arg.second.map {
                    val trimmedQuery = arg.first.trim()

                    if (trimmedQuery.isEmpty()) {
                        return@map it
                    }

                    return@map it.filter { it.tags?.name?.contains(trimmedQuery, true) ?: true }
                }
            }
            .doOnNext {
                progress.onNext(false)
                empty.onNext(it.isEmpty())
            }


    companion object {

        const val minZoomLevel = 12
        const val targetZoom = 15.0f
    }

}
