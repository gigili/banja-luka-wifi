package com.gac.banjalukawifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                153
            )
        }

        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_add,
                R.id.navigation_map,
                R.id.navigation_bug,
                R.id.navigation_about
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            var status = false

            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.action_global_navigation_home)
                    status = true
                }

                R.id.navigation_add -> {
                    navController.navigate(R.id.action_global_navigation_add_edit)
                    status = true
                }

                R.id.navigation_map -> {
                    navController.navigate(R.id.action_global_navigation_map)
                    status = true
                }
            }

            status
        }

        initializeAds()
    }

    private fun initializeAds() {
        var testDeviceID: String? = null
        if (BuildConfig.testAdDeviceID.isNotEmpty())
            testDeviceID = BuildConfig.testAdDeviceID

        val rcb = RequestConfiguration.Builder().setTestDeviceIds(listOf(testDeviceID)).build()
        MobileAds.setRequestConfiguration(rcb)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest
            .Builder()
            .build()
        adView.loadAd(adRequest)

    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        if (navView.selectedItemId != R.id.navigation_home) {
            navController.navigate(R.id.action_global_navigation_home)
            navView.selectedItemId = R.id.navigation_home
            navController.backStack.clear()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finish()
                return
            }

            Toast.makeText(this, getString(R.string.double_back_to_exit), Toast.LENGTH_SHORT).show()

            this.doubleBackToExitPressedOnce = true

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions:
        Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            153 -> {
                if (grantResults.isEmpty()) {
                    Toast.makeText(this, getString(R.string.app_needs_permissions_to_run), Toast.LENGTH_LONG).show()
                    return
                }

                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, getString(R.string.app_needs_permissions_to_run), Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
        }
    }
}