package com.gac.banjalukawifi.helpers.network

import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import java.nio.charset.Charset

class CustomVolleyError(
    message: String,
    response: NetworkResponse?,
    url: String? = null,
    method: Int? = null
) : VolleyError(message) {

    var errorMessage = message
    var errorResponse = response
    val statusCode = response?.statusCode ?: -1
    val body = String(errorResponse?.data ?: ByteArray(0))
    val headers: MutableMap<String, String> = response?.headers ?: HashMap()
    private val mMethod = method

    private val mURL = url

    override fun toString(): String {
        return errorMessage
    }

    fun getData(): String {
        return String(errorResponse?.data ?: ByteArray(0), Charset.forName("UTF-8"))
    }

    fun getUrl(): String? {
        return mURL
    }

    fun getMethod() : Int?{
        return mMethod
    }
}