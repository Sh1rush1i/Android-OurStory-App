package com.submis.ourstory.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.submis.ourstory.R

class CustomInfoWindowAdapter(private val inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker): View {
        val view = inflater.inflate(R.layout.custom_info_window, null)

        val title: TextView = view.findViewById(R.id.info_window_title)
        val snippet: TextView = view.findViewById(R.id.info_window_snippet)
        val imageView: ImageView = view.findViewById(R.id.info_window_image)

        title.text = marker.title
        snippet.text = marker.snippet

        val bitmap = marker.tag as? Bitmap
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        }

        return view
    }
}
