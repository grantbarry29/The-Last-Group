package edu.umich.jakoba.kotlinChatter

import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
    private var infoWindow: MapInfoWindow

    init {
        infoWindow = MapInfoWindow(context)
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        infoWindow.titleTextView.text = marker.title
        infoWindow.snippetTextView.text = marker.snippet

        return infoWindow
    }
}