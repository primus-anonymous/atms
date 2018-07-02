package com.neocaptainnemo.atms.ui.settings

import android.app.Dialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.daggerInject
import com.neocaptainnemo.atms.model.MapType
import javax.inject.Inject

class MapTypeSelectionDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SettingsViewModel


    override fun onAttach(context: Context?) {
        daggerInject()

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SettingsViewModel::class.java)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.choose_map_type)
                .setSingleChoiceItems(R.array.map_type_array, if (viewModel.mapType == MapType.GOOGLE) 0 else 1) { _, position ->

                    when (position) {
                        0 -> viewModel.mapType = MapType.GOOGLE
                        else -> viewModel.mapType = MapType.YANDEX
                    }

                    dismiss()

                }
        return builder.create()
    }

    companion object {

        const val tag = "MapTypeSelectionDialog"

        fun instance() = MapTypeSelectionDialog()
    }
}
