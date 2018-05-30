package com.neocaptainnemo.atms.ui

import android.content.Context
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.service.Atms
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import javax.inject.Inject

class Presenter @Inject constructor(private val context: Context, private val atms: Atms) {

    private lateinit var view: IView
    private var viewPortBounds: ViewPort? = null
    private var observable: Observable<List<AtmNode>>? = null

    private var atmsSubscription: Disposable? = null
    private var filterStr: String? = null

    var openedTab: Tab? = null
        set(openedTab) {
            field = openedTab

            when (openedTab) {
                Presenter.Tab.MAP -> view.showMap()
                Presenter.Tab.LIST -> view.showList()
                Presenter.Tab.SETTINGS -> view.showSettings()
                else -> {
                }
            }
        }

    init {
        this.filterStr = ""
    }

    fun onStart() {

    }

    fun onStop() {
        if (atmsSubscription != null && !atmsSubscription!!.isDisposed) {
            atmsSubscription!!.dispose()
        }
    }

    fun fetchAtms(viewPort: ViewPort) {

        if (viewPortBounds == null || !viewPortBounds!!.isInside(viewPort)) {

            observable = atms
                    .request(viewPort)
                    .cache()
                    .doOnNext { _ -> viewPortBounds = viewPort }
                    .doOnError { _ -> viewPortBounds = null }

            doFetch()
        }
    }

    private fun doFetch() {
        if (observable == null) {
            return
        }
        if (atmsSubscription != null && !atmsSubscription!!.isDisposed) {
            atmsSubscription!!.dispose()
        }
        view.showProgress()
        atmsSubscription = observable!!
                .map<List<AtmNode>> { atmNodes ->
                    if (filterStr!!.isEmpty()) {
                        return@map atmNodes
                    }

                    val res = ArrayList<AtmNode>()

                    for (atmNode in atmNodes) {
                        if (atmNode.tags != null && atmNode.tags!!.name != null &&
                                atmNode.tags!!.name!!.toLowerCase().contains(filterStr!!.toLowerCase())) {
                            res.add(atmNode)
                        }
                    }

                    res
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ atmNodes ->
                    view.onGotAtms(atmNodes)
                    view.hideProgress()
                }) { throwable ->
                    view.hideProgress()
                    if (throwable is IOException) {
                        view.showError(context.getString(R.string.network_error))
                    } else {
                        view.showError(context.getString(R.string.general_error))
                    }
                }
    }

    fun setFilter(filter: String) {
        this.filterStr = filter.trim { it <= ' ' }
        doFetch()
    }

    fun setView(view: IView) {
        this.view = view
    }

    enum class Tab {
        MAP, LIST, SETTINGS
    }
}
