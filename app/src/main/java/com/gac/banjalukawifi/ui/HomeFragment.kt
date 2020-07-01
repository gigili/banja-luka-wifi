package com.gac.banjalukawifi.ui

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.adapters.NetworkAdapter
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.daos.NetworkDao
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.AppInstance
import com.gac.banjalukawifi.helpers.ProgressDialogHelper
import com.gac.banjalukawifi.helpers.network.VolleyTasks
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray


class HomeFragment : Fragment() {

    private lateinit var appDB: AppDatabase
    private lateinit var networks: ArrayList<Network>
    private lateinit var networkAdapter: NetworkAdapter
    private lateinit var networkDao: NetworkDao

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
        networkAdapter = NetworkAdapter(networks)
        appDB = AppDatabase.getDatabase(requireContext().applicationContext)
        networkDao = appDB.networkDao()

        val lstNetworks = view.findViewById<RecyclerView>(R.id.lstNetwork)
        lstNetworks.adapter = networkAdapter
        lstNetworks.layoutManager = LinearLayoutManager(requireContext())

        if (AppInstance.globalConfig.isNetworkAvailable()) {
            loadNetworks()
        } else {
            AsyncTask.execute {
                networkAdapter.clear()
                networkAdapter.addAll(networkDao.getAll() as ArrayList<Network>)
            }
        }

        try {
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    networkAdapter.clear()
                    AsyncTask.execute {
                        val term = if (p0.toString().isNotEmpty()) "%${p0.toString()}%" else "%%"
                        networkAdapter.addAll(networkDao.findByName(term) as ArrayList<Network>)

                        lstNetworks.post {
                            networkAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }catch (e : Exception){}
    }

    private fun loadNetworks() {
        val globalConfig = AppInstance.globalConfig
        ProgressDialogHelper.showProgressDialog(requireActivity())
        VolleyTasks.getNetworks(Response.Listener { response ->
            try {
                networks.clear()
                networkAdapter.clear()

                val res = JSONArray(response)
                (0 until res.length())
                    .map { res.getJSONObject(it) }
                    .mapTo(networks) {
                        val network = Network(
                            it.optString("name", ""),
                            it.optString("password", ""),
                            it.optString("address", ""),
                            it.optString("geo_lat", ""),
                            it.optString("geo_long", ""),
                            it.optString("userID", "")
                        )
                        network.setID(it.optInt("id", 0))

                        AsyncTask.execute {
                            if (networkDao.get(it.optInt("id", 0)).name.isBlank()) {
                                networkDao.insert(network)
                            } else {
                                networkDao.update(network)
                            }
                        }
                        network
                    }

                networkAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                globalConfig.showMessageDialog(getString(R.string.error_loading_networks))
            } finally {
                ProgressDialogHelper.hideProgressDialog()
            }
        }, Response.ErrorListener { error ->
            globalConfig.handleExceptionErrors(error)
        })
    }
}