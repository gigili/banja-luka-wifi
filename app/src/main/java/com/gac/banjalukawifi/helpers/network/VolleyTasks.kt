package com.gac.banjalukawifi.helpers.network

import com.android.volley.Request
import com.android.volley.Response
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
    }
}