package com.gac.banjalukawifi.helpers

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gac.banjalukawifi.BuildConfig
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.entities.Network
import com.gac.banjalukawifi.helpers.network.VolleyTasks
import org.json.JSONArray

open class CustomBaseActivity : AppCompatActivity() {

    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = AlertDialog
            .Builder(this)
            .setMessage(getString(R.string.networks_updating))
            .setView(R.layout.loading)
            .setCancelable(false)
            .create()

        registerReceiver(
            broadcastReceiver,
            IntentFilter("BLWIFI_NETWORK_ONLINE")
        )
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "BLWIFI_NETWORK_ONLINE" -> {
                    loadNetworks()
                }
            }
        }
    }

    private fun loadNetworks() {
        val networks = ArrayList<Network>()

        val lastNetworkUpdate = AppInstance.globalConfig.getLongPref("lastNetworkUpdate")
        //TODO("Move this interval into a settings screen and allow the user to choose how often the networks should updated")
        if (lastNetworkUpdate != -1L && (System.currentTimeMillis() - lastNetworkUpdate) < BuildConfig.networkUpdateInterval.toLong()) {
            return //No need to run frequent updates of networks
        }

        if (!progressDialog.isShowing) {
            progressDialog.show()
        }

        VolleyTasks.getNetworks({ response ->
            try {
                val res = JSONArray(response)
                (0 until res.length())
                    .map { res.getJSONObject(it) }
                    .mapTo(networks) {
                        val network = Network(
                            it.optString("name", ""),
                            it.optString("password", ""),
                            it.optString("address", ""),
                            it.optString("geo_lat", ""),
                            it.optString("geo_long", ""),
                            it.optString("userID", ""),
                            it.optString("last_update", "")
                        )
                        network.setID(it.optInt("id", 0))

                        Thread {
                            try {
                                AppDatabase.getDatabase(this).networkDao().insert(network)
                            } catch (constrainError: SQLiteConstraintException) {
                                AppDatabase.getDatabase(this).networkDao().update(network)
                            } catch (e: Exception) {
                            }
                        }
                        network
                    }

                sendBroadcast(Intent("BLWIFI_NETWORKS_UPDATED"))
                AppInstance.globalConfig.setLongPref("lastNetworkUpdate", System.currentTimeMillis())
            } catch (e: Exception) {
                AppInstance.globalConfig.showMessageDialog(getString(R.string.error_loading_networks))
            } finally {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
            }
        }, { error ->
            AppInstance.globalConfig.handleExceptionErrors(error)
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        })
    }

    override fun onDestroy() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()

        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

}