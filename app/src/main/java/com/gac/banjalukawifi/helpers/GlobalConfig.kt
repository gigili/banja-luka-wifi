package com.gac.banjalukawifi.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.helpers.network.CustomVolleyError
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


@Suppress(
    "unused",
    "MemberVisibilityCanBePrivate",
    "PropertyName",
    "PrivatePropertyName",
    "UNUSED_PARAMETER",
    "CommitPrefEdits"
)
open class GlobalConfig constructor(protected var context: Context) {


    //Private & protected vars
    private val preferences: SharedPreferences
    private val preferencesEditor: SharedPreferences.Editor
    private lateinit var pDialog: AlertDialog
    private val LOG_TAG: String = "BLWIFI_TAG"
    private var PER_PAGE = 20
    var isConnected: Boolean = false

    //Public vars
    var base_url: String

    init {
        @Suppress("LocalVariableName")
        val PREFS_TAG = "MyPrivateGlobalPrefs${context.applicationContext.packageName}"

        this.base_url = "https://banjalukawifi.igorilic.net/index.php/api/"

        preferences = context.getSharedPreferences(PREFS_TAG, 0)
        preferencesEditor = preferences.edit()

        if (getStringPref("base_url").isEmpty()) {
            setStringPref("base_url", base_url)
        }

        if (getIntPref("PER_PAGE") == -1) {
            setIntPref("PER_PAGE", PER_PAGE)
        }

        if (this.base_url != getStringPref("base_url") && getStringPref("base_url").isNotEmpty()) {
            this.base_url = getStringPref("base_url")
        }
    }

    fun getPreferences(): SharedPreferences {
        return preferences
    }

    fun getStringPref(name: String): String {
        val x = preferences.getString(name, "")
        return when (x == null) {
            true -> ""
            false -> x
        }
    }

    fun getBooleanPref(name: String): Boolean {
        return preferences.getBoolean(name, true)
    }

    fun getIntPref(name: String): Int {
        return preferences.getInt(name, -1)
    }

    fun getLongPref(name: String): Long {
        return preferences.getLong(name, -1L)
    }

    fun setLongPref(name: String, value: Long?) {
        preferencesEditor.putLong(name, value!!)
        preferencesEditor.commit()
    }

    fun setBooleanPref(name: String, value: Boolean?) {
        preferencesEditor.putBoolean(name, value!!)
        preferencesEditor.commit()
    }

    fun setStringPref(name: String, value: String) {
        preferencesEditor.putString(name, value)
        preferencesEditor.commit()
    }

    fun setIntPref(name: String, value: Int) {
        preferencesEditor.putInt(name, value)
        preferencesEditor.commit()
    }

    fun isNetworkAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT < 23) {
            val connManager = context.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi: NetworkInfo? = connManager.activeNetworkInfo
            (mWifi != null && mWifi.isConnected)
        } else {
            isConnected
        }
    }

    fun isConnectedToWiFi(): Boolean? {
        val connManager = context.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT < 23) {
            val ni = connManager.activeNetworkInfo;
            if (ni != null) {
                (ni.isConnected && (ni.type == TYPE_WIFI))
            } else {
                false
            }
        } else {
            val nc = connManager.getNetworkCapabilities(connManager.activeNetwork)
            nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        }
    }

    fun isConnectedToNetwork(): Boolean {
        val connManager = context.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT < 23) {
            val ni = connManager.activeNetworkInfo;
            ni?.isConnected ?: false
        } else {
            val nc = connManager.getNetworkCapabilities(connManager.activeNetwork)
            (nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false || nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false)
        }
    }

    fun logMsg(str: String, tag: String? = null, logLevel: Level = Level.INFO) {
        val log = Logger.getAnonymousLogger()
        val logTag = tag ?: LOG_TAG
        val logString = "$logTag | $str"
        log.log(logLevel, logString)
    }

    fun handleExceptionErrors(
        ex: Exception?,
        errorMsg: String = "",
        errorTitle: String? = null,
        displayMessage: Boolean = true
    ) {
        try {
            val mErrorTitle = errorTitle ?: context.getString(R.string.there_was_an_error)
            if (ex != null) {
                val logLevel = Level.SEVERE
                val logTag = "BLWIFI_EXCEPTION_ERROR_TAG"

                if (ex is TimeoutError) {
                    logMsg("Timeout Exception: ${ex.message}")
                    if (displayMessage) {
                        showMessageDialog(context.getString(R.string.timeout_error))
                    } else {
                        logMsg("Network timeout error", logTag, logLevel)
                    }
                } else if (ex is CustomVolleyError || ex is VolleyError) {
                    val url = if (ex is CustomVolleyError) ex.getUrl() else ""

                    var code = -1
                    if (ex is CustomVolleyError) {
                        code = ex.statusCode
                    } else if (ex is VolleyError && ex.networkResponse != null) {
                        code = ex.networkResponse.statusCode
                    }

                    val time = if (ex is CustomVolleyError) ex.networkTimeMs else (ex as VolleyError).networkTimeMs

                    var message = ex.message

                    var requestMethod = ""
                    if (ex is CustomVolleyError)
                        requestMethod = getRequestMethod(ex.getMethod())

                    val errorType =
                        if (ex is CustomVolleyError) "CustomVolleyError" else "VolleyError"

                    logMsg("Error type: $errorType", logTag, logLevel)
                    logMsg("Network url: $url $requestMethod", logTag, logLevel)
                    logMsg("Network code: $code", logTag, logLevel)
                    logMsg("Network time: $time ms", logTag, logLevel)

                    if (ex is CustomVolleyError && ex.headers.isNotEmpty()) {
                        logMsg("Network headers: ${ex.headers}", logTag, logLevel)
                    } else if (
                        ex is VolleyError && ex.networkResponse != null &&
                        ex.networkResponse.allHeaders != null &&
                        ex.networkResponse.allHeaders.isNotEmpty()
                    ) {
                        val hd = ex.networkResponse.allHeaders
                        logMsg("Network headers: $hd", logTag, logLevel)
                    }

                    if (ex is CustomVolleyError && ex.body.isNotEmpty()) {
                        logMsg("Network body: ${ex.getData()}", logTag, logLevel)
                        val msg = JSONObject(ex.getData())
                        if (msg.has("message") && msg.getString("message").isNotEmpty()) {
                            message = msg.getString("message")
                        }
                    }

                    logMsg("Network message: $message", logTag, logLevel)

                    if (message != null && displayMessage) {
                        showMessageDialog(message.toString(), mErrorTitle)
                    }
                } else {
                    ex.printStackTrace()
                    if (ex.message != null && displayMessage) {
                        showMessageDialog(ex.message.toString(), mErrorTitle)
                    }
                }
            }

            if (errorMsg.isNotEmpty() && displayMessage)
                showMessageDialog(errorMsg, mErrorTitle)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getRequestMethod(method: Int?): String {
        var requestMethod = ""
        if (method != null) {
            requestMethod = " | " + when (method) {
                Request.Method.GET -> "GET"
                Request.Method.POST -> "POST"
                Request.Method.PUT -> "PUT"
                Request.Method.PATCH -> "PATCH"
                Request.Method.DELETE -> "DELETE"
                else -> "N/A"
            }
        }

        return requestMethod
    }

    fun showMessageDialog(msg: String, title: String = "") {
        try {
            val mTitle = if (title.isNotEmpty()) title else context.getString(R.string.warning)
            AlertDialog.Builder(context)
                .setTitle(mTitle)
                .setMessage(msg)
                .setPositiveButton(context.getString(R.string.ok)) { dl, _ -> dl.dismiss() }
                .show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun notifyMSG(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }

    fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(context as Activity, status, 2404).show()
            }
            return false
        }
        return true
    }

    fun formatDate(
        lastUpdate: String,
        dateFormat: String,
        inputFormat: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone? = TimeZone.getTimeZone("Europe/Belgrade")
    ): String {
        try {
            var format = SimpleDateFormat(inputFormat, DEFAULT_APP_LOCALE)
            format.timeZone = TimeZone.getTimeZone("UTC")
            val newDate = format.parse(lastUpdate)

            format = SimpleDateFormat(dateFormat, DEFAULT_APP_LOCALE)
            if (timeZone != null)
                format.timeZone = timeZone

            return format.format(newDate!!).toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    fun getDateFromTimeStamp(time: Long, format: String, timeZone: String = "ETC/GMT"): String {
        val dateFormat = SimpleDateFormat(format, DEFAULT_APP_LOCALE)
        dateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return dateFormat.format(time).toString()
        //return DateFormat.format(format, cal).toString()
    }

    fun quitApp(activity: AppCompatActivity) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.confirm_quit_app))
            .setMessage(context.getString(R.string.confirm_quit_app_message))
            .setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                dialog.cancel()

                (context as Activity).finish()
            }
            .setNegativeButton(context.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
            .show()
    }

    fun getNetworkSSID(): String? {
        var networkName = ""
        try {
            if (isNetworkAvailable() && isConnectedToWiFi() == true) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info: WifiInfo = wifiManager.connectionInfo
                networkName = info.ssid
                networkName = networkName.replace("\"", "")
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return networkName
        }
        return networkName
    }

    companion object {
        val DEFAULT_APP_LOCALE: Locale = Locale.UK
    }
}
