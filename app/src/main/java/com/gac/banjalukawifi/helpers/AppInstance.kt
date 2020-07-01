package com.gac.banjalukawifi.helpers

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.text.TextUtils
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gac.banjalukawifi.helpers.network.CustomVolleyError
import org.json.JSONObject

class AppInstance : Application() {
    private var volleyRequestQueue: RequestQueue? = null
    private val VOLLEY_DEFAULT_TAG = "BLWIFIVolleyDefaultTag"
    private var isMonitoringConnectivity = false
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        appInstance = this
        globalConfig = GlobalConfig(applicationContext)

        if (!isMonitoringConnectivity) {
            checkConnectivity()
        }
    }

    fun callAPI(
        method: Int,
        operationPath: String,
        params: JSONObject?,
        successListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        val globalConfig = globalConfig
        val url = "${globalConfig.getStringPref("base_url")}$operationPath"

        val req: StringRequest

        req = object : StringRequest(method, url, successListener, errorListener) {

            override fun getBody(): ByteArray {
                return params?.toString()?.toByteArray() ?: ByteArray(0)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError?.networkResponse == null) {
                    return super.parseNetworkError(
                        CustomVolleyError(
                            "Empty network response",
                            null,
                            url,
                            method
                        )
                    )
                }

                return super.parseNetworkError(
                    CustomVolleyError(
                        volleyError.message ?: "-- ERROR --",
                        volleyError.networkResponse,
                        url,
                        method
                    )
                )
                //return super.parseNetworkError(volleyError)
            }
        }

        req.retryPolicy = DefaultRetryPolicy(
            15000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        cancelPendingRequests(url)
        addToRequestQueue(req, url)
    }

    private fun getmRequestQueue(): RequestQueue? {
        if (volleyRequestQueue == null) {
            volleyRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return volleyRequestQueue
    }


    private fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) VOLLEY_DEFAULT_TAG else tag
        getmRequestQueue()?.add(req)
    }

    private fun cancelPendingRequests(tag: String) {
        getmRequestQueue()?.cancelAll(tag)
    }

    companion object {
        private var mAppContext: Context? = null
        lateinit var globalConfig: GlobalConfig
        lateinit var appInstance: AppInstance

        var appContext: Context?
            get() = mAppContext
            set(mAppContext) {
                AppInstance.mAppContext = mAppContext
            }
    }

    private val connectivityCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: android.net.Network) {
            super.onAvailable(network)
            globalConfig.isConnected = true
        }

        override fun onLost(network: android.net.Network) {
            super.onLost(network)
            globalConfig.isConnected = false
        }
    }

    private fun checkConnectivity() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (!isMonitoringConnectivity) {
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                connectivityCallback
            )
            isMonitoringConnectivity = true
        }
    }
}