package com.gac.banjalukawifi

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.gac.banjalukawifi.helpers.AppInstance
import com.gac.banjalukawifi.helpers.CustomBaseActivity
import com.gac.banjalukawifi.helpers.GlobalConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CustomBaseActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppInstance.globalConfig = GlobalConfig(this)
        val missingPermissions = (
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_NETWORK_STATE
                        ) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_WIFI_STATE
                        ) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                )

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionGranted = permissionGranted && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }*/

        if (missingPermissions) {
            val permissionsList = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionsList =
                    permissionsList.plusElement(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }*/

            ActivityCompat.requestPermissions(this, permissionsList, 153)
        }

        if (AppInstance.globalConfig.getBooleanPref("first_run")) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(resources.getString(R.string.terms_of_use))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setMessage(
                    Html.fromHtml(
                        applicationContext.getString(R.string.terms_and_conditions),
                        Html.FROM_HTML_MODE_LEGACY
                    )
                )
            } else {
                builder.setMessage(Html.fromHtml(applicationContext.getString(R.string.terms_and_conditions)))
            }

            builder.setPositiveButton(resources.getString(R.string.accept_terms)) { _: DialogInterface, _: Int ->
                run {
                    AppInstance.globalConfig.setBooleanPref("first_run", false)
                }
            }
            builder.setCancelable(false)
            builder.show()
        }

        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            var status = false

            var appBarTitle = getString(R.string.app_name)

            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.action_global_navigation_home)
                    status = true
                }

                R.id.navigation_add -> {
                    navController.navigate(R.id.action_global_navigation_add_edit)
                    status = true
                    appBarTitle += " - ${getString(R.string.title_add_edit_network)}"
                }

                R.id.navigation_map -> {
                    navController.navigate(R.id.action_global_navigation_map)
                    appBarTitle += " - ${getString(R.string.title_map)}"
                    status = true
                }

                R.id.navigation_bug -> {
                    navController.navigate(R.id.action_global_navigation_report_bug)
                    appBarTitle += " - ${getString(R.string.title_report_bug)}"
                    status = true
                }

                R.id.navigation_about -> {
                    navController.navigate(R.id.action_global_navigation_about)
                    appBarTitle += " - ${getString(R.string.about_app)}"
                    status = true
                }
            }

            supportActionBar?.title = appBarTitle
            status
        }

        if (AppInstance.globalConfig.isConnectedToNetwork()) {
            sendBroadcast(Intent("BLWIFI_NETWORK_ONLINE"))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        initializeAds()
    }

    private fun initializeAds() {
        var testDeviceID: String? = null
        if (BuildConfig.testAdDeviceID.isNotEmpty())
            testDeviceID = BuildConfig.testAdDeviceID

        val rcb = RequestConfiguration.Builder().setTestDeviceIds(listOf(testDeviceID)).build()
        MobileAds.setRequestConfiguration(rcb)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if (navView.selectedItemId != R.id.navigation_home) {
            navController.navigate(R.id.action_global_navigation_home)
            navView.selectedItemId = R.id.navigation_home
            navController.backStack.clear()
        } else {
            AppInstance.globalConfig.quitApp()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions:
        Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            153 -> {
                if (grantResults.isEmpty()) {
                    Toast.makeText(
                        this,
                        getString(R.string.app_needs_permissions_to_run),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            this,
                            getString(R.string.app_needs_permissions_to_run),
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                }
            }
        }
    }
}