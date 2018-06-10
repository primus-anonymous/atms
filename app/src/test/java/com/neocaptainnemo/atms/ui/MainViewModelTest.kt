package com.neocaptainnemo.atms.ui


import com.google.gson.Gson
import com.neocaptainnemo.atms.InstantRxTestRule
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.service.Atms
import com.neocaptainnemo.atms.service.OsmResponse
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.io.InputStreamReader

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Mock
    lateinit var atms: Atms

    @JvmField
    @Rule
    val rxTestRule = InstantRxTestRule()

    lateinit var viewModel: MainViewModel

    private val osmResponse: OsmResponse
        get() {
            val stream = ClassLoader.getSystemResourceAsStream("response.json")

            val reader = InputStreamReader(stream)
            return Gson().fromJson(reader, OsmResponse::class.java)
        }

    @Before
    fun before() {
        viewModel = MainViewModel(atms)
    }

    @Test
    fun fetchSuccessNoFilter() {

        val response = osmResponse

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.just(response.atms) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.atms().test()

        testable.assertValue(response.atms)
    }

    @Test
    fun fetchSuccessFilterResults() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        val response = osmResponse

        whenever(atms.request(any())).then { Observable.just(response.atms) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort
        viewModel.searchQuery = "B SEB"

        val testable = viewModel.atms().test()

        val atmNode = AtmNode()
        atmNode.id = 325105300

        testable.assertValue(arrayListOf(atmNode))
    }

    @Test
    fun fetchSuccessFilterNoResults() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        val response = osmResponse

        whenever(atms.request(any())).then { Observable.just(response.atms) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort
        viewModel.searchQuery = "B SEB!2"

        val testable = viewModel.atms().test()

        testable.assertValue(listOf())
    }

    @Test
    fun progressSuccess() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.just(listOf<AtmNode>()) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.progressObservable.test()

        viewModel.atms().test().assertNoErrors()

        testable.assertValues(false, true, false)
    }

    @Test
    fun progressFailure() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.error<AtmNode>(RuntimeException()) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.progressObservable.test()

        viewModel.atms().test().assertNoErrors()

        testable.assertValues(false, true, false)
    }

    @Test
    fun zoomInFurtherShown() {
        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        viewModel.viewPort = viewPort
        viewModel.tab = Tab.MAP

        val testable = viewModel.zoomInFurtherObservable.test()

        viewModel.zoom = 10.0f

        testable.assertValues(false, true)

    }

    @Test
    fun zoomInFurtherNotShownZoomLevel() {
        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        viewModel.viewPort = viewPort
        viewModel.tab = Tab.MAP

        val testable = viewModel.zoomInFurtherObservable.test()

        viewModel.zoom = 20.0f

        testable.assertValues(false, false)

    }

    @Test
    fun zoomInFurtherNotShownListTab() {

        viewModel.tab = Tab.LIST

        val testable = viewModel.zoomInFurtherObservable.test()

        viewModel.zoom = 10.0f

        testable.assertValues(false, false)

    }

    @Test
    fun zoomInFurtherNotShownSettingsTab() {

        viewModel.tab = Tab.SETTINGS

        val testable = viewModel.zoomInFurtherObservable.test()

        viewModel.zoom = 10.0f

        testable.assertValues(false, false)

    }

    @Test
    fun belowMinimumZoomLevel() {
        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        viewModel.zoom = 20.0f
        viewModel.tab = Tab.MAP

        viewModel.viewPort = viewPort

        verify(atms, never()).request(any())
    }


    @Test
    fun fetchBelowMinimumZoomLevel() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        viewModel.zoom = 10.0f
        viewModel.viewPort = viewPort

        val testable = viewModel.atms().test()

        testable.assertValue(listOf())

        verify(atms, never()).request(any())
    }

    @Test
    fun searchVisibleMap() {
        viewModel.tab = Tab.MAP

        val testable = viewModel.searchVisibilityObservable.test()

        testable.assertValue(true)
    }

    @Test
    fun searchVisibleList() {
        viewModel.tab = Tab.LIST

        val testable = viewModel.searchVisibilityObservable.test()

        testable.assertValue(true)
    }

    @Test
    fun searchNotVisibleSettings() {
        viewModel.tab = Tab.SETTINGS

        val testable = viewModel.searchVisibilityObservable.test()

        testable.assertValue(false)
    }

    @Test
    fun fetchInsideCachedViewport() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.just(listOf<AtmNode>()) }

        viewModel.viewPort = viewPort
        viewModel.atms().test().assertNoErrors()

        viewModel.viewPort = viewPort
        viewModel.atms().test().assertNoErrors()

        verify(atms).request(viewPort.extended())
    }

    @Test
    fun fetchOutSideCachedViewport() {

        val viewPort1 = ViewPort(1.0, 1.0, 1.0, 1.0)
        val viewPort2 = ViewPort(2.0, 2.0, 2.0, 2.0)

        whenever(atms.request(any())).then { Observable.just(listOf<AtmNode>()) }

        viewModel.viewPort = viewPort1
        viewModel.atms().test().assertNoErrors()

        viewModel.viewPort = viewPort2
        viewModel.atms().test().assertNoErrors()

        verify(atms).request(viewPort1.extended())
        verify(atms).request(viewPort2.extended())
    }

    @Test
    fun generalError() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.error<AtmNode>(RuntimeException()) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.errorObservable.test()

        viewModel.atms().test().assertNoErrors()

        testable.assertValue(R.string.general_error)
    }


    @Test
    fun ioError() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.error<AtmNode>(IOException()) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.errorObservable.test()

        viewModel.atms().test().assertNoErrors()

        testable.assertValue(R.string.network_error)
    }

    @Test
    fun notEmpty() {

        val response = osmResponse

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.just(response.atms) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.emptyObservable.test()

        viewModel.atms().test().assertNoErrors()

        testable.assertValues(false, false)
    }

    @Test
    fun empty() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.just(listOf<AtmNode>()) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.emptyObservable.test()

        viewModel.atms().test().assertNoErrors()

        testable.assertValues(false, true)
    }

    @Test
    fun errorEmptyResult() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(any())).then { Observable.error<AtmNode>(RuntimeException()) }

        viewModel.zoom = MainViewModel.targetZoom
        viewModel.viewPort = viewPort

        val testable = viewModel.atms().test()

        testable.assertValue(listOf())
    }
}
