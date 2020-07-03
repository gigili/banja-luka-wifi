package com.gac.banjalukawifi.helpers.network

import com.android.volley.Request
import com.android.volley.Response
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.AppInstance

class VolleyTasks {
    companion object {
        fun getNetworks(responseListener: Response.Listener<String>, errorListener: Response.ErrorListener) {
            AppInstance.appInstance.callAPI(
                Request.Method.GET,
                "",
                null,
                responseListener,
                errorListener
            )
        }

        fun submitNetwork(network: Network, responseListener: Response.Listener<String>, errorListener: Response.ErrorListener){
            val params = HashMap<String, String>()

            params["networkID"] = network.id.toString()
            params["name"] = network.name
            params["password"] = network.password
            params["address"] = network.address.toString()
            params["geo_lat"] = network.geoLat.toString()
            params["geo_long"] = network.geoLong.toString()
            params["userID"] = network.userID.toString()

            AppInstance.appInstance.callAPI(
                Request.Method.POST,
                "addUpdateNetwork",
                params,
                responseListener,
                errorListener
            )
        }
    }
}