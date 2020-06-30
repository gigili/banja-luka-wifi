package com.gac.banjalukawifi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.entities.Network


class NetworkAdapter(private val networks: ArrayList<Network>) : RecyclerView.Adapter<NetworkAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNetworkName: TextView = view.findViewById(R.id.txtNetworkName)
        val txtNetworkPassword: TextView = view.findViewById(R.id.txtNetworkPassword)
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
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    fun clear() {
        networks.clear()
        notifyDataSetChanged()
    }

    fun add(network: Network): Boolean {
        networks.add(network)
        notifyDataSetChanged()
        return true
    }

    fun addAll(ns: ArrayList<Network>): Boolean {
        networks.addAll(ns)
        notifyDataSetChanged()
        return true
    }
}