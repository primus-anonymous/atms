package com.neocaptainnemo.atms.ui

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

import com.neocaptainnemo.atms.R

class AllowNavigationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage(R.string.allow_navigation)
                .setPositiveButton(R.string.btn_allow) { _, _ ->
                    ActivityCompat.requestPermissions(activity!!,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MainActivity.locationPermission)
                }
                .setNegativeButton(R.string.btn_disallow) { _, _ ->
                    //do nothing
                }
        return builder.create()
    }

    companion object {

        const val tag = "AllowNavigationDialog"

        fun instance(): AllowNavigationDialog {
            return AllowNavigationDialog()
        }
    }

}
