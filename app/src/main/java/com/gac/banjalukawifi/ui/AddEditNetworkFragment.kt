package com.gac.banjalukawifi.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.helpers.AppInstance
import com.gac.banjalukawifi.helpers.GlobalConfig
import kotlinx.android.synthetic.main.fragment_add_edit_network.*

class AddEditNetworkFragment : Fragment() {

    private lateinit var globalConfig: GlobalConfig
    private var geo_lat: Double = 0.0
    private var geo_long: Double = 0.0
    private var btnSave: Button? = null
    private var mLocationManager: LocationManager? = null
    private var canSubmit: Boolean = true
    private var provider: String = LocationManager.NETWORK_PROVIDER
    private var userID = ""

    private var v: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        globalConfig = AppInstance.globalConfig
        userID = globalConfig.getUserID() //TODO("GET USER ID IN NEW WAY")

        mLocationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            if (mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                provider = LocationManager.GPS_PROVIDER
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_add_edit_network, container, false)

        getUserLocation()

        /*if (globalConfig!!.isNetworkAvailable && DatabaseHelper.checkIfNetworkExists(globalConfig!!.networkSSID)) {
            if (globalConfig != null && globalConfig!!.networkSSID != null) {
                val network: NetworkModel? = DatabaseHelper.getNetwork(0, globalConfig!!.networkSSID)
                if (network != null) {
                    txtAddress.setText(network.address)
                    txtPassword.setText(network.password)
                    networkID = network.id
                }
            }
        }*/

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!globalConfig.isNetworkAvailable())
            canSubmit = false

        edtNetworkName.setText(globalConfig.getNetworkSSID())

        edtNetworkName.isEnabled = false
        edtNetworkName.setText(globalConfig.getNetworkSSID())

        /*btnSave.setOnClickListener {

            if (edtNetworkName.text.isEmpty()) {
                edtNetworkName.error = getString(R.string.field_required)
                canSubmit = false
            }


            if (edtNetworkPassword.text.isEmpty()) {
                edtNetworkPassword.error = getString(R.string.field_required)
                canSubmit = false
            }

            if (geo_lat == 0.0 && geo_long == 0.0) {
                canSubmit = false
                btnSave!!.isEnabled = false
                globalConfig.notifyMSG(getString(R.string.waiting_for_location))
            }

            if (canSubmit) {
                requireContext().let {
                    *//*VolleyTasks.addEditNetwork(
                        it1,
                        edtNetworkName.text,
                        txtAddress.text,
                        edtNetworkPassword.text,
                        btnSave!!,
                        userID,
                        networkID,
                        geo_lat,
                        geo_long
                    )*//*
                    //TODO("CREATE SUBMIT ENDPOINT")
                }
            }
        }*/
    }

    private fun getUserLocation() {
        try {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 10001)
        } catch (e: Exception) {
            e.printStackTrace()
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
                    geo_lat = loc.latitude
                    geo_long = loc.longitude
                }
                mLocationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null -> {
                    provider = LocationManager.NETWORK_PROVIDER
                    loc = mLocationManager!!.getLastKnownLocation(provider)
                    geo_lat = loc.latitude
                    geo_long = loc.longitude
                }
                else -> mLocationManager!!.requestLocationUpdates(provider, 10.toLong(), 50.toFloat(), mLocationListener)
            }
        } else {
            globalConfig.notifyMSG(getString(R.string.permission_need_to_work))
        }
    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            geo_lat = location.latitude
            geo_long = location.longitude

            btnSave!!.isEnabled = true
            mLocationManager!!.removeUpdates(this)
            canSubmit = true
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