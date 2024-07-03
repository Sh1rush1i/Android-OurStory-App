package com.submis.ourstory.ui.main

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.submis.ourstory.R
import com.submis.ourstory.databinding.FragmentUploadBinding
import com.submis.ourstory.dom.Result
import com.submis.ourstory.dom.story.model.StoryUpload
import com.submis.ourstory.ui.main.viewmodel.UploadVIewModel
import com.submis.ourstory.utils.IdlingResourceHelper
import com.submis.ourstory.utils.*
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding
    private val viewModel by viewModels<UploadVIewModel>()
    private val navArgs by navArgs<UploadFragmentArgs>()
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private lateinit var locationRequest: LocationRequest
    private var location: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            val imageResult = navArgs.imageResult

            // Display the image from camera or gallery
            if (imageResult.isFromCamera) {
                storyImage.setImageBitmap(imageResult.imageBitmap)
            } else {
                storyImage.setImageURI(imageResult.imageUri)
            }

            // Set up close navigate back
            btnClose.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            // Set up upload, compress after that upload
            btnUploadStory.setOnClickListener {
                val imageStory = imageResult.imageFile
                val descriptionStory = edAddDescription.text.toString().trim()

                if (imageStory == null || descriptionStory.isEmpty()) {
                    showMessage(getString(R.string.main_data_empty))
                    return@setOnClickListener
                }

                showLoading(true)
                compressUpload(imageStory, descriptionStory, location)
            }

            // Set up the switch for user location
            getLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    fetchUserLocation()
                    invokeReqLocation()
                } else location = null
            }

            viewModel.uploadStoryResult.observe(viewLifecycleOwner, uploadObserver)
        }
    }

    // Compress and upload an image func
    private fun compressUpload(imageFile: File, description: String, location: Location?) {
        IdlingResourceHelper.increment()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val compressedFile = Compressor.compress(requireActivity(), imageFile) {
                    quality(50)
                    size(1_000_000)
                }
                viewModel.uploadStory(compressedFile, description, location)
            } finally {
                IdlingResourceHelper.decrement()
            }
        }
    }

    // Observer handle result upload story action
    private val uploadObserver = Observer<Result<StoryUpload>> { result ->
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                showMessage(result.data.message)

                activity?.run {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            is Result.Error -> {
                showLoading(false)
                showMessage(result.message)
            }
        }
    }

    // Request location permissions launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            fetchUserLocation()
        } else {
            showMessage(getString(R.string.permission_denied_error))
        }
    }


    // Request location updates and check location settings func
    private fun invokeReqLocation() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(1)).apply {
            setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(1))
        }.build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        val settingsClient = LocationServices.getSettingsClient(requireActivity())

        // Check location settings
        settingsClient.checkLocationSettings(settingsRequest)
            .addOnSuccessListener { fetchUserLocation() }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    handleResolvableException(exception)
                }
            }
    }

    // Handle resolvable exceptions for location settings
    private fun handleResolvableException(exception: ResolvableApiException) {
        try {
            val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
            locationSettingsLauncher.launch(intentSenderRequest)
        } catch (sendIntentException: IntentSender.SendIntentException) {
            showMessage(sendIntentException.message)
        }
    }

    // Launcher to handle the result location settings resolution
    private val locationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                showMessage(getString(R.string.txt_wait))
            }
            Activity.RESULT_CANCELED -> {
                showMessage(getString(R.string.gps_error))
            }
        }
    }

    // Fetch user loc
    private fun fetchUserLocation() {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    this.location = location
                    if (location == null) {
                        showMessage(getString(R.string.location_not_found_error))
                    }
                }
            } catch (e: SecurityException) {
                showMessage(getString(R.string.permission_denied_error))
            }
        } else {
            locationPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    // Check if the app has location permissions
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Show loading lottie
    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            loading.apply {
                isVisible = isLoading
                if (isLoading) {
                    playAnimation()
                } else {
                    cancelAnimation()
                }
            }
        }
    }

    // Display a message in custom dialog
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
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}