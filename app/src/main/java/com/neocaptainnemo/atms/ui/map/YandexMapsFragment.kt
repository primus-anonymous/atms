package com.neocaptainnemo.atms.ui.map

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neocaptainnemo.atms.Optional
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.ui.MainViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateSource
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_yandex_maps.*
import javax.inject.Inject


class YandexMapsFragment : Fragment(), CameraListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    private var locationSet = false

    private val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {

        daggerInject()

        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("YANDEX_MAP_KEY")
        MapKitFactory.initialize(context)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_yandex_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.map.addCameraListener(this)
    }


    override fun onStart() {
        super.onStart()

        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        compositeDisposable.add(viewModel.locationObservable.subscribe {
            if (it.isNotNull()) {

                val location = it.safeValue()

                if (!locationSet) {

                    val cameraPosition = CameraPosition(Point(location.latitude, location.longitude), MainViewModel.targetZoom, 0.0f, 0.0f)

                    mapView.map.move(
                            cameraPosition,
                            Animation(Animation.Type.SMOOTH, 0.0f)) { updateViewPort(mapView.map) }
                    locationSet = true
                }
            }
        })


        compositeDisposable.add(Observable.combineLatest(viewModel.atms(), viewModel.selectedAtmObservable,
                BiFunction<List<AtmNode>, Optional<AtmNode>, Pair<List<AtmNode>, Optional<AtmNode>>> { atms, selected ->

                    Pair(atms, selected)
                }).subscribe { result ->


            mapView.map.mapObjects.clear()

            result.first.forEach {

                val mark = mapView.map.mapObjects.addPlacemark(Point(it.lat, it.lon))

                if (result.second.isNotNull() && result.second.safeValue() == it) {
                    mark.setIcon(ImageProvider.fromResource(context, R.drawable.ic_map_pin_atm_selected))
                } else {
                    mark.setIcon(ImageProvider.fromResource(context, R.drawable.ic_map_pin_atm))
                }

                mark.addTapListener { _, _ ->

                    viewModel.selectAtm(it)


                    true
                }
            }
        })

        compositeDisposable.add(viewModel.selectedAtmObservable.subscribe {

            if (it.isNotNull()) {

                val atmNode = it.safeValue()

                mapView.map.move(
                        CameraPosition(Point(atmNode.lat, atmNode.lon), MainViewModel.targetZoom, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1.0f),
                        null)

            }

        })

        compositeDisposable.add(viewModel.locationPermissionObservable.subscribe {
            mapView.map.userLocationLayer.isEnabled = it
        })


    }


    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()

        MapKitFactory.getInstance().onStop()

        mapView.onStop()

    }


    override fun onCameraPositionChanged(map: Map?, position: CameraPosition?, updateSource: CameraUpdateSource?, finished: Boolean) {

        if (finished && updateSource == CameraUpdateSource.GESTURES && map != null) {
            updateViewPort(map)
        }
    }

    private fun updateViewPort(map: Map) {
        val visibleRegion = map.visibleRegion

        val zoomLevel = map.cameraPosition.zoom

        viewModel.zoom = zoomLevel

        val startLng = visibleRegion.bottomLeft.longitude
        val startLat = visibleRegion.bottomLeft.latitude

        val endLng = visibleRegion.topRight.longitude
        val endLat = visibleRegion.topRight.latitude

        val viewPort = ViewPort(startLng, startLat, endLng, endLat)

        viewModel.viewPort = viewPort

    }


    companion object {
        fun instance() = YandexMapsFragment()

        const val tag = "YandexMapsFragment"
    }

}

