package com.gac.banjalukawifi.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gac.banjalukawifi.R
import com.gac.banjalukawifi.db.AppDatabase
import com.gac.banjalukawifi.db.entities.Network
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true
        val mapMarker = getBitmap(requireContext(), R.drawable.ic_map_marker)

        val networks = ArrayList<Network>()

        AsyncTask.execute {
            networks.addAll(AppDatabase.getDatabase(requireContext().applicationContext).networkDao().getAll() as ArrayList<Network>)
            requireActivity().runOnUiThread {
                networks.forEach { network ->
                    val loc = LatLng(network.geoLat!!.toDouble(), network.geoLong!!.toDouble())
                    googleMap.addMarker(
                        MarkerOptions().position(loc).title(network.name).snippet(network.address).icon(BitmapDescriptorFactory.fromBitmap(mapMarker))
                    )
                }
            }
        }

        val target = LatLng(44.769545, 17.189526)
        val cameraPosition = CameraPosition.Builder().target(target).zoom(12f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    private fun getBitmap(context: Context, drawableId: Int): Bitmap {
        val drawable: Drawable = ContextCompat.getDrawable(context, drawableId)!!
        val bitmap: Bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                153
            )
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions:
        Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            153 -> {
                if (grantResults.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.app_needs_permissions_to_run), Toast.LENGTH_LONG).show()
                    return
                }

                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(requireContext(), getString(R.string.app_needs_permissions_to_run), Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
        }
    }
}