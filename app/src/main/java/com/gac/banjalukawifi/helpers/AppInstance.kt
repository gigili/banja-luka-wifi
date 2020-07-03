package com.gac.banjalukawifi.helpers

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.helpers.network.CustomVolleyError

class AppInstance : Application() {
    private var volleyRequestQueue: RequestQueue? = null
    private val volleyDefaultTag = "BLWIFIVolleyDefaultTag"

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        appInstance = this
        globalConfig = GlobalConfig(applicationContext)
    }

    fun callAPI(
        method: Int,
        operationPath: String,
        params: HashMap<String, String>?,
        successListener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        val globalConfig = globalConfig
        val url = "${globalConfig.getStringPref("base_url")}$operationPath"

        val req: StringRequest

        req = object : StringRequest(method, url, successListener, errorListener) {
            override fun getParams(): Map<String, String> {
                return params ?: HashMap()
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError?.networkResponse == null) {
                    return super.parseNetworkError(
                        CustomVolleyError(
                            getString(R.string.no_network_response),
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
            5000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        cancelPendingRequests(url)
        addToRequestQueue(req, url)
    }

    private fun getRequestQueue(): RequestQueue? {
        if (volleyRequestQueue == null) {
            volleyRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return volleyRequestQueue
    }

    private fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) volleyDefaultTag else tag
        getRequestQueue()?.add(req)
    }

    private fun cancelPendingRequests(tag: String) {
        getRequestQueue()?.cancelAll(tag)
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
}