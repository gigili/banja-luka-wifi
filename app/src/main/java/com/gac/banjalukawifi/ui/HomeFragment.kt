package com.gac.banjalukawifi.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.adapters.NetworkAdapter
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.entities.Network


class HomeFragment : Fragment() {

    private lateinit var appDB: AppDatabase
    private lateinit var networks: List<Network>
    private lateinit var networkAdapter : NetworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networks = ArrayList()
        networkAdapter = NetworkAdapter(networks as ArrayList<Network>)
        appDB = AppDatabase.getDatabase(requireContext().applicationContext)

        val lstNetworks = view.findViewById<RecyclerView>(R.id.lstNetwork)
        lstNetworks.adapter = networkAdapter
        lstNetworks.layoutManager = LinearLayoutManager(requireContext())

        AsyncTask.execute {
            networkAdapter.clear()
            networkAdapter.addAll(appDB.networkDao().getAll() as ArrayList<Network>)
        }
    }
}