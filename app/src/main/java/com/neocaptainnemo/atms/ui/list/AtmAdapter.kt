package com.neocaptainnemo.atms.ui.list

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.service.AddressFormatter
import com.neocaptainnemo.atms.service.DistanceFormatter
import kotlinx.android.synthetic.main.item_atm.view.*
import java.util.*
import javax.inject.Inject

class AtmAdapter @Inject constructor(private val addressFormatter: AddressFormatter, private val distanceFormatter: DistanceFormatter)
    : RecyclerView.Adapter<AtmAdapter.AtmHolder>() {

    private val data = ArrayList<AtmNode>()

    var location: Location? = null

    var atmClicked: ((atmNode: AtmNode) -> Unit)? = null

    fun clear() = data.clear()

    fun add(atms: Collection<AtmNode>) {
        data.addAll(atms)

        if (location != null) {
            data.sortWith(Comparator { node1, node2 ->
                val distance1 = SphericalUtil.computeDistanceBetween(LatLng(node1.lat, node1.lon),
                        LatLng(location!!.latitude, location!!.longitude))

                val distance2 = SphericalUtil.computeDistanceBetween(LatLng(node2.lat, node2.lon),
                        LatLng(location!!.latitude, location!!.longitude))

                ((distance1 - distance2) * 100).toInt()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtmHolder =
            AtmHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_atm, parent, false), this)


    override fun onBindViewHolder(holder: AtmHolder, position: Int) {

        val node = data[position]

        holder.root.name.text = node.tags!!.name

        val address = addressFormatter.format(node)

        if (address.isEmpty()) {
            holder.root.address.setText(R.string.no_address)
        } else {
            holder.root.address.text = address
        }

        if (location == null) {
            holder.root.distance.visibility = View.GONE
        } else {
            holder.root.distance.visibility = View.VISIBLE
            val distance = SphericalUtil.computeDistanceBetween(LatLng(node.lat, node.lon),
                    LatLng(location!!.latitude, location!!.longitude))

            holder.root.distance.text = distanceFormatter.format(distance)
        }
    }

    override fun getItemCount(): Int = data.size


    class AtmHolder(val root: View, atmAdapter: AtmAdapter) : RecyclerView.ViewHolder(root) {

        init {
            root.setOnClickListener { _ ->
                atmAdapter.atmClicked?.invoke(atmAdapter.data[adapterPosition])

            }
        }
    }
}
