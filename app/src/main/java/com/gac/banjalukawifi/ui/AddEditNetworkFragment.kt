package com.gac.banjalukawifi.ui

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.AppInstance
import com.gac.banjalukawifi.helpers.GlobalConfig
import com.gac.banjalukawifi.helpers.ProgressDialogHelper
import com.gac.banjalukawifi.helpers.network.VolleyTasks
import kotlinx.android.synthetic.main.fragment_add_edit_network.*
import java.util.concurrent.Executors

class AddEditNetworkFragment : Fragment() {

    private lateinit var globalConfig: GlobalConfig
    private var geoLat: Double = 0.0
    private var geoLong: Double = 0.0
    private var mLocationManager: LocationManager? = null
    private var provider: String = LocationManager.NETWORK_PROVIDER
    private var network: Network? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLocationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            if (mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER
            } else if (mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalConfig = AppInstance.globalConfig

        if (!globalConfig.isConnectedToWiFi()) {
            globalConfig.showMessageDialog(getString(R.string.wifi_internet_required))
            btnSave.isEnabled = false
            return
        }

        getUserLocation()
        val networkDao = AppDatabase.getDatabase(requireContext().applicationContext).networkDao()
        val networkSSID = globalConfig.getNetworkSSID()
        edtNetworkName.setText(networkSSID)

        globalConfig.logMsg("SSID: $networkSSID")

        edtNetworkName.isEnabled = false

        Executors.newSingleThreadExecutor().execute {
            if (networkSSID.isNotBlank()) {
                network = networkDao.findByName("%${edtNetworkName.text}%").firstOrNull()

                if (network != null) {
                    if (network!!.name.isNotBlank()) {
                        requireActivity().runOnUiThread {
                            edtNetworkAddress.setText(network!!.address)
                            edtNetworkPassword.setText(network!!.password)
                        }
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            var canSubmit = true

            if (edtNetworkName.text.isEmpty()) {
                edtNetworkName.error = getString(R.string.field_required)
                canSubmit = false
            }

            if (edtNetworkPassword.text.isEmpty()) {
                edtNetworkPassword.error = getString(R.string.field_required)
                canSubmit = false
            }

            if (geoLat == 0.0 && geoLong == 0.0) {
                canSubmit = false
                btnSave!!.isEnabled = false
                globalConfig.notifyMSG(getString(R.string.waiting_for_location))
            }

            if (canSubmit) {

                if (network != null) {
                    network!!.address = edtNetworkAddress.text.toString()
                    network!!.password = edtNetworkPassword.text.toString()
                    network!!.geoLat = geoLat.toString()
                    network!!.geoLong = geoLong.toString()
                } else {
                    network = Network()
                    network!!.name = edtNetworkName.text.toString()
                    network!!.address = edtNetworkAddress.text.toString()
                    network!!.password = edtNetworkPassword.text.toString()
                    network!!.geoLat = geoLat.toString()
                    network!!.geoLong = geoLong.toString()
                }

                ProgressDialogHelper.showProgressDialog(requireActivity())

                VolleyTasks.submitNetwork(network!!, { response ->
                    try {
                        globalConfig.logMsg("NETWORK RESPONSE: $response")
                        if (!response.contains("error")) {
                            globalConfig.showMessageDialog(
                                getString(R.string.network_saved_success),
                                getString(R.string.notice)
                            )
                            btnSave.isEnabled = false
                        } else {
                            globalConfig.showMessageDialog(getString(R.string.network_saved_error))
                            btnSave.isEnabled = true
                        }
                    } catch (e: Exception) {
                        globalConfig.showMessageDialog(getString(R.string.network_saved_error))
                    } finally {
                        ProgressDialogHelper.hideProgressDialog()
                    }
                }, { error ->
                    globalConfig.handleExceptionErrors(error)
                    ProgressDialogHelper.hideProgressDialog()
                })
            }
        }
    }

    private fun getUserLocation() {
        try {
            val loc: Location?
            if (
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                when {
                    mLocationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null -> {
                        provider = LocationManager.GPS_PROVIDER
                        loc = mLocationManager!!.getLastKnownLocation(provider)
                        if (loc != null) {
                            geoLat = loc.latitude
                        }
                        if (loc != null) {
                            geoLong = loc.longitude
                        }
                    }
                    mLocationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null -> {
                        provider = LocationManager.NETWORK_PROVIDER
                        loc = mLocationManager!!.getLastKnownLocation(provider)
                        if (loc != null) {
                            geoLat = loc.latitude
                        }
                        if (loc != null) {
                            geoLong = loc.longitude
                        }
                    }
                    else -> mLocationManager!!.requestLocationUpdates(
                        provider,
                        10.toLong(),
                        50.toFloat(),
                        mLocationListener
                    )
                }
            } else {
                globalConfig.notifyMSG(getString(R.string.location_required_to_run))
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(mLocationListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(mLocationListener)
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (mLocationManager != null) {
                mLocationManager!!.requestLocationUpdates(
                    provider,
                    10.toLong(),
                    50.toFloat(),
                    mLocationListener
                )
            }
        }
    }
}