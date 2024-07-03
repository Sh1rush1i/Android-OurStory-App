package com.submis.ourstory.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.submis.ourstory.R
import com.submis.ourstory.databinding.FragmentMapsBinding
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.Story
import com.submis.ourstory.ui.adapter.CustomInfoWindowAdapter
import com.submis.ourstory.ui.main.viewmodel.MapsViewModel
import com.submis.ourstory.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<MapsViewModel>()

    private lateinit var gMaps: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMaps = googleMap

        // Set custom info window adapter
        gMaps.setInfoWindowAdapter(CustomInfoWindowAdapter(layoutInflater))

        // Set initiate position at Indo
        val indonesiaPosition = LatLng(-2.5, 108.0)
        gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesiaPosition, 5f))

        // Apply custom map style and handle errors
        try {
            val success = gMaps.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style_assasin_creed)
            )
            if (!success) {
                Log.e(TAG, "applyMapStyle: Style parsing failed.")
                showMessage(getString(R.string.map_style_parsing_failed))
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "applyMapStyle: Style resource not found", e)
            showMessage(getString(R.string.map_style_resource_not_found))
        }

        // Fetch story locations
        viewModel.getStoryLocation()

        // Observe story data changes and update UI
        viewModel.stories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    setLoadingVisibility(true)
                }
                is Result.Success -> {
                    setLoadingVisibility(false)
                    placeMarkers(result.data)
                }
                is Result.Error -> {
                    setLoadingVisibility(false)
                    showMessage(result.message)
                }
            }
        }
    }

    // Set loading animation lottie
    private fun setLoadingVisibility(visible: Boolean) {
        binding?.loading?.let {
            if (visible) {
                it.visibility = View.VISIBLE
                it.playAnimation()
            } else {
                it.visibility = View.GONE
                it.pauseAnimation()
            }
        }
    }

    // Add markers to the map for each story
    private fun placeMarkers(stories: List<Story>) {
        stories.forEach { story ->
            val storyPosition = LatLng(story.lat ?: return, story.lon ?: return)

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        Glide.with(requireContext())
                            .asBitmap()
                            .load(story.photoUrl)
                            .override(150, 150)
                            .circleCrop()
                            .submit()
                            .get()
                    }

                    val marker = gMaps.addMarker(
                        MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .position(storyPosition)
                            .title(story.name)
                            .snippet(story.description)
                    )
                    marker?.tag = bitmap // Set the bitmap as the tag
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading image for marker: ${story.photoUrl}", e)
                }
            }
        }
    }

    // Show a custom dialog with the provided message and alignment
    private fun showMessage(message: String?) {
        showDialogInfo(
            context = requireActivity(),
            message = message.toString(),
            alignment = Gravity.CENTER
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MapsFragment"
    }
}