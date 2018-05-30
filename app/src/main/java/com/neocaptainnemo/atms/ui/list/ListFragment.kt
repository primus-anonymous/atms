package com.neocaptainnemo.atms.ui.list

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.app.App
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.ui.IAtmsView
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

class ListFragment : Fragment(), IAtmsView {

    @Inject
    lateinit var adapter: AtmAdapter

    private var atmSelectedListener: IAtmsView.OnAtmSelected? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is IAtmsView.OnAtmSelected) {
            atmSelectedListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        atmSelectedListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity!!.application as App).appComponent!!.inject(this)

        adapter.atmClicked = {
            if (atmSelectedListener != null) {
                atmSelectedListener!!.onAtmSelected(it, ListFragment.tag)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        atmList.layoutManager = LinearLayoutManager(context)
        atmList.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_list, container, false)

    override fun showAtms(atmNodes: List<AtmNode>) {
        adapter.add(atmNodes)
        adapter.notifyDataSetChanged()

        if (atmNodes.isEmpty()) {
            empty.visibility = View.VISIBLE
        } else {
            empty.visibility = View.GONE
        }
    }

    override fun setMyLocation(location: Location) {
        adapter.location = location
        adapter.notifyDataSetChanged()
    }

    override fun clear() {
        adapter.clear()
    }


    companion object {

        const val tag = "ListFragment"

        fun instance(): ListFragment {
            return ListFragment()
        }

        fun onStack(manager: FragmentManager): IAtmsView? {
            val fragment = manager.findFragmentByTag(tag)
            return if (fragment is IAtmsView) {
                fragment
            } else null
        }
    }


}
