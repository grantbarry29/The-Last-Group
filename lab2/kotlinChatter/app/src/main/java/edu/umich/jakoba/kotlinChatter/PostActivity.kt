package edu.umich.jakoba.kotlinChatter

import android.Manifest
import edu.umich.jakoba.kotlinChatter.ChattStore.postChatt
import android.annotation.SuppressLint
import android.app.usage.UsageEvents.Event.NONE
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.Collator.ReorderCodes.FIRST
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.*


@SuppressLint("MissingPermission")
class PostActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var view: PostView
    private var enableSend = true

    private var lat = 0.0
    private var lon = 0.0
    private var speed = -1.0f

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = PostView(this)
        setContentView(view)

        LocationServices.getFusedLocationProviderClient(applicationContext)
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    lat = it.result.latitude
                    lon = it.result.longitude
                    speed = it.result.speed
                    if (!enableSend) {
                        submitChatt()
                    }
                } else {
                    Log.e("PostActivity getFusedLocation", it.exception.toString())
                }
            }

        // read sensors to determine bearing
        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            add(NONE, FIRST, NONE, getString(R.string.send))
            getItem(0).setIcon(android.R.drawable.ic_menu_send).setEnabled(enableSend)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == FIRST) {
            enableSend = false
            invalidateOptionsMenu()
            if (speed < 0f) {
                toast("Getting location fix . . .")
            } else {
                submitChatt()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun submitChatt() {
        val chatt = Chatt(username = view.usernameTextView.text.toString(),
            message = view.messageTextView.text.toString(),
            geodata = GeoData(lat, lon, convertLoc(),
                convertBearing(), convertSpeed())
        )

        postChatt(applicationContext, chatt)
        finish()
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    var gravity: FloatArray = emptyArray<Float>().toFloatArray()
    var geomagnetic: FloatArray = emptyArray<Float>().toFloatArray()

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values
    }
    override fun onDestroy() {
        super.onDestroy()
        accelerometer?.let {
            sensorManager.unregisterListener(this, it)
        }
        magnetometer?.let {
            sensorManager.unregisterListener(this, it)
        }
    }
    fun convertBearing(): String {
        if (gravity.isNotEmpty() && geomagnetic.isNotEmpty()) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                // the 3 elements of orientation: azimuth, pitch, and roll,
                // bearing is azimuth = orientation[0], in rad
                val bearingdeg = (Math.toDegrees(orientation[0].toDouble()) + 360).rem(360)
                val compass = arrayOf("North", "NE", "East", "SE", "South", "SW", "West", "NW", "North")
                val index = (bearingdeg / 45).toInt()
                return compass[index]
            }
        }
        return "unknown"
    }
    fun convertLoc(): String {
        val locations = Geocoder(applicationContext, Locale.getDefault()).getFromLocation(lat, lon, 1)
        if (locations.size > 0) {
            val geoloc = locations[0]
            return geoloc.locality ?: geoloc.subAdminArea ?: geoloc.adminArea ?: geoloc.countryName
            ?: "unknown"
        }
        return "unknown"
    }
    fun convertSpeed(): String {
        return when (speed) {
            in 1.2..4.9 -> "walking"
            in 5.0..6.9 -> "running"
            in 7.0..12.9 -> "cycling"
            in 13.0..89.9 -> "driving"
            in 90.0..138.9 -> "in train"
            in 139.0..224.9 -> "flying"
            else -> "resting"
        }
    }
}