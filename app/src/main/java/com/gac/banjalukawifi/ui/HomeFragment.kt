package com.gac.banjalukawifi.ui

import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.adapters.NetworkAdapter
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.daos.NetworkDao
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.AppInstance
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger


class HomeFragment : Fragment() {

    private lateinit var appDB: AppDatabase
    private lateinit var networks: ArrayList<Network>
    private lateinit var networkAdapter: NetworkAdapter
    private lateinit var networkDao: NetworkDao
    private lateinit var lstNetworks: RecyclerView

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
        networkAdapter = NetworkAdapter(networks) { network ->
            copyNetworkPasswordToClipBoard(network)
        }
        appDB = AppDatabase.getDatabase(requireContext().applicationContext)
        networkDao = appDB.networkDao()

        requireContext().registerReceiver(
            broadcastReceiver,
            IntentFilter("BLWIFI_NETWORKS_UPDATED")
        )

        lstNetworks = view.findViewById(R.id.lstNetwork)
        lstNetworks.adapter = networkAdapter
        lstNetworks.layoutManager = LinearLayoutManager(requireContext())


        loadNetworks()
        try {
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    networkAdapter.clear()
                    Executors.newSingleThreadExecutor().execute {
                        val term = if (p0.toString().isNotEmpty()) "%${p0.toString()}%" else "%%"
                        networkAdapter.addAll(networkDao.findByName(term) as ArrayList<Network>)

                        lstNetworks.post {
                            networkAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        } catch (e: Exception) {
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "BLWIFI_NETWORKS_UPDATED" -> {
                    loadNetworks()
                }
            }
        }
    }

    private fun loadNetworks() {
        networkAdapter.clear()
        Executors.newSingleThreadExecutor().execute {
            try {
                networkAdapter.addAll(networkDao.getAll() as ArrayList<Network>)
                lstNetworks.post {
                    edtSearch.setText("")
                    networkAdapter.notifyDataSetChanged()
                }
            } catch (e: java.lang.Exception) {
                val log = Logger.getAnonymousLogger()
                val logString = "BLWIFI_DBG | ${e.message}"
                log.log(Level.INFO, logString)
            }
        }
    }

    override fun onDestroy() {
        try {
            requireContext().unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    private fun copyNetworkPasswordToClipBoard(network: Network) {
        val clipboard: ClipboardManager? =
            requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard != null) {
            val clip = ClipData.newPlainText(network.name, network.password)
            clipboard.setPrimaryClip(clip)
            AppInstance.globalConfig.notifyMSG(getString(R.string.password_copied_success))
        } else {
            AppInstance.globalConfig.notifyMSG(getString(R.string.password_copied_error))
        }
    }
}