package com.neocaptainnemo.atms.ui.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.neocaptainnemo.atms.R
import kotlinx.android.synthetic.main.item_settings_header.view.*
import kotlinx.android.synthetic.main.item_settings_value.view.*
import javax.inject.Inject

typealias SettingsItemClicked = ((item: ValueItem) -> Unit)

class SettingsAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val header = 0

    private val item = 1

    private val data = arrayListOf<SettingsItem>()

    var itemClicked: SettingsItemClicked? = null

    fun add(toAdd: Collection<SettingsItem>) = data.addAll(toAdd)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                header -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_header, parent, false))
                else -> ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_settings_value, parent, false), this)
            }

    override fun getItemCount(): Int = data.count()


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is HeaderViewHolder -> {

                with(data[position] as HeaderItem) {
                    holder.view.sectionHeader.text = header
                }

                holder.view.sectionHeader.text = (data[position] as? HeaderItem)?.header
            }

            is ItemViewHolder -> {

                with(data[position] as ValueItem) {
                    holder.view.itemHeader.text = header
                    holder.view.itemValue.text = value
                }

            }
        }
    }


    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is HeaderItem -> header
        is ValueItem -> item
    }

    fun clear() = data.clear()

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class ItemViewHolder(val view: View, adapter: SettingsAdapter) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                adapter.itemClicked?.invoke(adapter.data[adapterPosition] as ValueItem)
            }
        }

    }


}