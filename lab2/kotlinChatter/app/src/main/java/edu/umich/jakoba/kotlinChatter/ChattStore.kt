package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object ChattStore {
    private val _chatts = MutableLiveData<ArrayList<Chatt?>>(arrayListOf())
    val chatts: LiveData<ArrayList<Chatt?>> = _chatts
    private val nFields = Chatt::class.declaredMemberProperties.size

    private lateinit var queue: RequestQueue
    private const val serverUrl = "https://35.185.48.28/"


    fun postChatt(context: Context, chatt: Chatt) {
        val geoObj = chatt.geodata?.run{ JSONArray(listOf(lat, lon, loc, facing, speed)) }

        val jsonObj = mapOf(
            "username" to chatt.username,
            "message" to chatt.message,
            "geodata" to geoObj?.toString()
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl +"postmaps/", JSONObject(jsonObj),
            { Log.d("postChatt", "chatt posted!") },
            { getChatts(context) },
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
    }

    fun getChatts(context: Context) {
        val getRequest = JsonObjectRequest(
            serverUrl +"getmaps/",
            { response ->
                _chatts.value?.clear()
                val chattsReceived = try { response.getJSONArray("maps") } catch (e: JSONException) { JSONArray() }
                Log.d("number of chatts received", chattsReceived.length().toString())
                for (i in 0 until chattsReceived.length()) {
                    val chattEntry = chattsReceived[i] as JSONArray
                    if (chattEntry.length() == nFields) {
                        val geoArr = if (chattEntry[3] == JSONObject.NULL) null else JSONArray(chattEntry[3] as String)
                        _chatts.value?.add(Chatt(username = chattEntry[0].toString(),
                            message = chattEntry[1].toString(),
                            timestamp = chattEntry[2].toString(),
                            geodata = geoArr?.let { GeoData(
                                lat = it[0].toString().toDouble(),
                                lon = it[1].toString().toDouble(),
                                loc = it[2].toString(),
                                facing = it[3].toString(),
                                speed = it[4].toString()
                            )}
                        ))
                    } else {
                        Log.e("getChatts", "Received unexpected number of fields: " + chattEntry.length().toString() + " instead of " + nFields.toString())
                    }
                }
                _chatts.value = _chatts.value
            }, {}
        )


        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(getRequest)
    }
}