package com.gac.banjalukawifi.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.daos.NetworkDao
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.AppInstance
import kotlinx.android.synthetic.main.fragment_add_edit_network.*

class AddEditNetworkFragment : Fragment() {

    private lateinit var networkDao : NetworkDao
    private var network : Network? = Network()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appDB = AppDatabase.getDatabase(requireContext().applicationContext)
        networkDao = appDB.networkDao()

        val gl = AppInstance.globalConfig
        gl.logMsg("WiFI: ${gl.isConnectedToWiFi()} | Name: ${gl.getNetworkSSID()}")

        if (AppInstance.globalConfig.isConnectedToWiFi()!!) {
            val networkName = AppInstance.globalConfig.getNetworkSSID()
            if(networkName == null || networkName.isBlank() || networkName == "<unknown ssid>"){
                disableSubmitButton()
            }else{
                getNetwork()
                edtNetworkName.setText(networkName)
            }
        } else {
            disableSubmitButton()
        }

        val mBntSave = view.findViewById<Button>(R.id.btnSave)
        mBntSave.setOnClickListener {
            submitNetwork()
        }
    }

    private fun disableSubmitButton(){
        btnSave.isEnabled = false
        AppInstance.globalConfig.showMessageDialog(getString(R.string.needs_wifi_connection_to_submit))
    }

    private fun getNetwork(){
        AsyncTask.execute {
            network = networkDao.findByName("%${edtNetworkName.text}%").find {
                it.name == edtNetworkName.text.toString()
            }

            if(network != null){
                edtNetworkName.setText(network!!.name)
                edtNetworkPassword.setText(network!!.password)
                edtNetworkAddress.setText(network!!.address)
            }
        }
    }

    private fun submitNetwork(){
        AsyncTask.execute {
            if (network == null) {
                network = Network()
            }

            network!!.name = edtNetworkName.text.toString()
            network!!.password = edtNetworkPassword.text.toString()
            network!!.address = edtNetworkAddress.text.toString()

            if (network!!.id.toString().isNotBlank()) {
                networkDao.update(network!!)
            } else {
                networkDao.insert(network!!)
            }

            this@AddEditNetworkFragment.run{
                AppInstance.globalConfig.showMessageDialog(getString(R.string.network_saved_success))
            }
        }
    }
}