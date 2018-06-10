package com.neocaptainnemo.atms.ui

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.AtmNode
import java.util.*

class NavigationDecisionDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.choose_the_nav_way)
                .setItems(R.array.nav_way_array) { _, which ->

                    val atm = arguments!!.getParcelable<AtmNode>("atm")

                    val geoBuilder = StringBuilder()
                    val formatter = Formatter(geoBuilder, Locale.US)

                    when (which) {
                        0 -> formatter.format("google.navigation:q=%f,%f&mode=%s", atm!!.lat, atm.lon, "w")

                        1 -> formatter.format("google.navigation:q=%f,%f&mode=%s", atm!!.lat, atm.lon, "d")

                        else -> {
                        }
                    }//do nothing


                    val gmmIntentUri = Uri.parse(geoBuilder.toString())
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    startActivity(mapIntent)

                }
        return builder.create()
    }

    companion object {

        const val tag = "NavigationDecisionDialogFragment"

        internal fun instance(atmNode: AtmNode): NavigationDecisionDialogFragment {
            val fragment = NavigationDecisionDialogFragment()
            val arg = Bundle()
            arg.putParcelable("atm", atmNode)
            fragment.arguments = arg
            return fragment
        }
    }
}
