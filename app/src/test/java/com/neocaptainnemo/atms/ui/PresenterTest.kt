package com.neocaptainnemo.atms.ui


import android.content.Context
import com.google.gson.Gson
import com.neocaptainnemo.atms.InstantRxTestRule
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.service.Atms
import com.neocaptainnemo.atms.service.OsmResponse
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

@RunWith(MockitoJUnitRunner::class)
class PresenterTest {

    @Mock
    lateinit var view: IView
    @Mock
    lateinit var context: Context
    @Mock
    lateinit var atms: Atms

    @JvmField
    @Rule
    val rxTestRule = InstantRxTestRule()

    lateinit var presenter: Presenter

    private val osmResponse: OsmResponse
        get() {
            val stream = ClassLoader.getSystemResourceAsStream("response.json")

            val reader = InputStreamReader(stream)
            return Gson().fromJson(reader, OsmResponse::class.java)
        }

    @Before
    fun before() {
        presenter = Presenter(context, atms)
        presenter.setView(view)
    }

    @Test
    fun fetchFirstTime() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(viewPort)).then { Observable.just(listOf<AtmNode>()) }

        presenter.fetchAtms(viewPort)

        verify<IView>(view).showProgress()
    }

    @Test
    fun fetchInsideCachedViewport() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(viewPort)).then { Observable.just(listOf<AtmNode>()) }

        presenter.fetchAtms(viewPort)
        presenter.fetchAtms(viewPort)

        verify<IView>(view, times(1)).showProgress()
    }

    @Test
    fun fetchOutsideCachedViewport() {

        val viewPort1 = ViewPort(1.0, 1.0, 1.0, 1.0)
        val viewPort2 = ViewPort(1.0, 1.0, 2.0, 1.0)

        whenever(atms.request(viewPort1)).then { Observable.just(listOf<AtmNode>()) }
        whenever(atms.request(viewPort2)).then { Observable.just(listOf<AtmNode>()) }

        presenter.fetchAtms(viewPort1)
        presenter.fetchAtms(viewPort2)

        verify<IView>(view, times(2)).showProgress()
    }

    @Test
    fun fetchErrorInBetween() {

        val viewPort1 = ViewPort(1.0, 1.0, 1.0, 1.0)
        val viewPort2 = ViewPort(1.0, 1.0, 2.0, 1.0)
        val viewPort3 = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(viewPort1)).then { Observable.just(listOf<AtmNode>()) }
        presenter.fetchAtms(viewPort1)

        whenever(atms.request(viewPort2)).then { Observable.error<List<AtmNode>>(RuntimeException()) }
        presenter.fetchAtms(viewPort2)

        whenever(atms.request(viewPort3)).then { Observable.just(listOf<AtmNode>()) }
        presenter.fetchAtms(viewPort3)

        verify<IView>(view, times(3)).showProgress()
    }

    @Test
    fun fetchSuccess() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(viewPort)).then { Observable.just(listOf<AtmNode>()) }

        presenter.fetchAtms(viewPort)

        verify<IView>(view).onGotAtms(listOf())
    }

    @Test
    fun fetchError() {
        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(viewPort)).then { Observable.error<List<AtmNode>>(RuntimeException()) }

        presenter.fetchAtms(viewPort)

        verify<IView>(view).hideProgress()
    }

    @Test
    fun fetchSuccessNoFilter() {

        val response = osmResponse

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(atms.request(viewPort)).then { Observable.just(response.atms) }

        presenter.fetchAtms(viewPort)
        verify<IView>(view).onGotAtms(response.atms!!)
    }

    @Test
    fun fetchSuccessFilterResults() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        val response = osmResponse

        whenever(atms.request(viewPort)).then { Observable.just(response.atms) }

        presenter.fetchAtms(viewPort)
        presenter.setFilter("B SEB")

        val atmNode = AtmNode()
        atmNode.id = 325105300

        verify<IView>(view).onGotAtms(arrayListOf(atmNode))
    }

    @Test
    fun fetchSuccessFilterNoResults() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        val response = osmResponse

        whenever(atms.request(viewPort)).then { Observable.just(response.atms) }

        presenter.fetchAtms(viewPort)
        presenter.setFilter("B SEB!2")

        verify<IView>(view).onGotAtms(listOf())
    }

    @Test
    fun fetchGeneralError() {

        val viewPort = ViewPort(1.0, 1.0, 1.0, 1.0)

        whenever(context.getString(R.string.general_error)).thenReturn("GeneralError")

        whenever(atms.request(viewPort)).then { Observable.error<List<AtmNode>>(RuntimeException()) }
        presenter.fetchAtms(viewPort)

        verify<IView>(view).showError("GeneralError")
    }

    @Test
    fun fetchIOError() {

        val viewPort = ViewPort(1.0, 1.0, 2.0, 1.0)

        whenever(context.getString(R.string.network_error)).thenReturn("NetworkError")

        whenever(atms.request(viewPort)).then { Observable.error<List<AtmNode>>(IOException()) }
        presenter.fetchAtms(viewPort)

        verify<IView>(view).showError("NetworkError")
    }


    @Test
    fun openMap() {
        presenter.openedTab = Presenter.Tab.MAP
        verify<IView>(view).showMap()
    }

    @Test
    fun openList() {
        presenter.openedTab = Presenter.Tab.LIST
        verify<IView>(view).showList()
    }

    @Test
    fun openSettings() {
        presenter.openedTab = Presenter.Tab.SETTINGS
        verify<IView>(view).showSettings()
    }


}
