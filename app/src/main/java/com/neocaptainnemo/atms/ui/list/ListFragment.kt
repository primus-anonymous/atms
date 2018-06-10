package com.neocaptainnemo.atms.ui.list

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.ui.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class ListFragment : Fragment() {

    @Inject
    lateinit var adapter: AtmAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: MainViewModel

    private val compositeDisposable = CompositeDisposable()


    override fun onAttach(context: Context) {

        daggerInject()

        super.onAttach(context)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)

        adapter.atmClicked = {
            viewModel.selectAtm(it)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        atmList.layoutManager = LinearLayoutManager(context)
        atmList.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_list, container, false)


    override fun onStart() {
        super.onStart()

        compositeDisposable.add(viewModel.atms().subscribe({
            adapter.clear()
            adapter.add(it)
            adapter.notifyDataSetChanged()
        }, {

        }))

        compositeDisposable.add(viewModel.emptyObservable.subscribe {
            empty.visibility = if (it) View.VISIBLE else View.GONE
        })

        compositeDisposable.add(viewModel.locationObservable.subscribe {
            adapter.location = it.value
            adapter.notifyDataSetChanged()

        })
    }

    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()
    }


    companion object {

        const val tag = "ListFragment"

        fun instance(): ListFragment = ListFragment()
    }


}
