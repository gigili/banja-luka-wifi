package com.gac.banjalukawifi.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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

    private lateinit var networkDao: NetworkDao
    private var network: Network? = Network()
    private var geoLat: Double = 0.0
    private var geoLong: Double = 0.0
    private var provider: String = LocationManager.NETWORK_PROVIDER
    private var mLocationManager: LocationManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_add_edit_network, container, false)

        mLocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            if (mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return v
    }

    private fun getUserLocation() {
        try {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10001)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appDB = AppDatabase.getDatabase(requireContext().applicationContext)
        networkDao = appDB.networkDao()

        val gl = AppInstance.globalConfig

        if (gl.isConnectedToWiFi()!!) {
            val networkName = gl.getNetworkSSID()
            if (networkName == null || networkName.isBlank() || networkName == "<unknown ssid>") {
                disableSubmitButton()
            } else {
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

    private fun disableSubmitButton() {
        btnSave.isEnabled = false
        AppInstance.globalConfig.showMessageDialog(getString(R.string.needs_wifi_connection_to_submit))
    }

    private fun getNetwork() {
        AsyncTask.execute {
            network = networkDao.findByName("%${edtNetworkName.text}%").find {
                it.name == edtNetworkName.text.toString()
            }

            if (network != null) {
                edtNetworkName.setText(network!!.name)
                edtNetworkPassword.setText(network!!.password)
                edtNetworkAddress.setText(network!!.address)
            }
        }
    }

    private fun submitNetwork() {
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

            this@AddEditNetworkFragment.run {
                AppInstance.globalConfig.showMessageDialog(getString(R.string.network_saved_success))
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10001 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val loc: Location?
            when {
                mLocationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null -> {
                    provider = LocationManager.GPS_PROVIDER
                    loc = mLocationManager!!.getLastKnownLocation(provider)
                    geoLat = loc.latitude
                    geoLong = loc.longitude
                }
                mLocationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null -> {
                    provider = LocationManager.NETWORK_PROVIDER
                    loc = mLocationManager!!.getLastKnownLocation(provider)
                    geoLat = loc.latitude
                    geoLong = loc.longitude
                }
                else -> mLocationManager!!.requestLocationUpdates(provider, 10.toLong(), 50.toFloat(), mLocationListener)
            }
        }
    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            geoLat = location.latitude
            geoLong = location.longitude

            btnSave!!.isEnabled = true
            mLocationManager!!.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    override fun onPause() {
        super.onPause()
        mLocationManager!!.removeUpdates(mLocationListener)
    }
}