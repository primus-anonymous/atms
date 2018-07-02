package com.neocaptainnemo.atms.ui.settings


import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.service.ISettings
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment : Fragment() {

    @Inject
    lateinit var adapter: SettingsAdapter

    @Inject
    lateinit var settings: ISettings

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SettingsViewModel

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {

        daggerInject()

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SettingsViewModel::class.java)

        adapter.itemClicked = {

            when (it.type) {

                ItemType.MAP_TYPE -> MapTypeSelectionDialog.instance().show(childFragmentManager, MapTypeSelectionDialog.tag)

                ItemType.RATE -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.neocaptainnemo.atms"))
                    try {
                        startActivity(intent)
                    } catch (exc: Exception) {
                        //do nothing
                    }
                }

                ItemType.ABOUT_SM -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.openstreetmap.org/about"))
                    try {
                        startActivity(intent)
                    } catch (exc: Exception) {
                        //do nothing
                    }

                }

                else -> {
                    //do nothing
                }

            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsList.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        compositeDisposable.add(viewModel.items(context!!).subscribe {

            adapter.clear()
            adapter.add(it)
            adapter.notifyDataSetChanged()

        })

    }

    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()
    }


    companion object {

        const val tag = "SettingsFragment"

        fun instance(): SettingsFragment = SettingsFragment()
    }
}


