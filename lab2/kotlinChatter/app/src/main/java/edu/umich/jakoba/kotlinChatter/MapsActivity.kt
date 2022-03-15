package edu.umich.jakoba.kotlinChatter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    /*private lateinit var binding: ActivityMapsBinding*/

    private fun renderChatt(chatt: Chatt) {
        val geodata = chatt.geodata ?: return
        val pos = LatLng(geodata.lat, geodata.lon)

        val snippet = "${chatt.username}\n${chatt.message}\n\n"+
                "Posted from ${geodata.loc}, while facing ${geodata.facing}"+
                " moving at ${geodata.speed} speed."

        mMap.addMarker(MarkerOptions().position(pos)
            .title(chatt.timestamp)                // timestamp
            .snippet(snippet))
        mMap.setInfoWindowAdapter(MapInfoAdapter(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        val index = intent.getIntExtra("INDEX", -1)
        if (index < 0) {
            ChattStore.chatts.value
                ?.filterNotNull()
                ?.forEach {
                    renderChatt(it)
                }

            // center and zoom in on user's current location
            LocationServices.getFusedLocationProviderClient(applicationContext)
                .getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val pos = LatLng(it.result.latitude, it.result.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16f))
                    } else {
                        Log.e("MapsActivity getFusedLocation", it.exception.toString())
                    }
                }

            return
        }
        ChattStore.chatts.value?.let {
            val chatt = it[index] ?: return
            renderChatt(chatt)
            val geodata = chatt.geodata ?: return
            val pos = LatLng(geodata.lat, geodata.lon)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16f))
        }

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}