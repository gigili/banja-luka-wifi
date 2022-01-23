package com.gac.banjalukawifi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.AppInstance
import java.util.*


class NetworkAdapter(
    private val networks: ArrayList<Network>,
    private val listener: (Network) -> Unit
) : RecyclerView.Adapter<NetworkAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNetworkName: TextView = view.findViewById(R.id.txtNetworkName)
        val txtNetworkPassword: TextView = view.findViewById(R.id.txtNetworkPassword)
        val txtLastUpdate: TextView = view.findViewById(R.id.txtLastUpdate)
    }

    override fun getItemCount(): Int {
        return networks.size
    }

    private fun getItem(i: Int): Network {
        return networks[i]
    }

    override fun getItemId(i: Int): Long {
        return networks[i].id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.network_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.txtNetworkName.text = getItem(i).name
        holder.txtNetworkPassword.text = getItem(i).password
        holder.txtLastUpdate.text = String.format(
            AppInstance.appContext!!.getString(R.string.lbl_last_update),
            AppInstance.globalConfig.formatDate(
                getItem(i).lastUpdate ?: Calendar.getInstance().time.toString(),
                "dd.MM.yyyy"
                //TODO("Consider adding this to the settings as well, so users can chose their format")
            )
        )

        holder.itemView.setOnClickListener {
            listener(getItem(i))
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    fun clear() {
        networks.clear()
    }

    fun addAll(ns: ArrayList<Network>): Boolean {
        networks.addAll(ns)
        return true
    }
}