package com.submis.ourstory.ui.main

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import android.content.pm.PackageManager
import android.util.Log
import android.view.Gravity
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraUnavailableException
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.submis.ourstory.R
import com.submis.ourstory.databinding.FragmentCameraBinding
import com.submis.ourstory.utils.*

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding
    private var selectCam = CameraSelector.DEFAULT_BACK_CAMERA
    private var getImage: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionActivityResultLauncher.launch(PERMISSIONS.first())

        binding?.apply {
            btnTakePhoto.setOnClickListener {
                // Increment EspressoIdlingResource to indicate ongoing image capture process
                IdlingResourceHelper.increment()

                // Retrieve the ImageCapture instance
                val imgCapture = getImage

                // Generate a photo file to store the captured image
                val photoFile = generateFile(requireActivity().application)

                // Configure output options for the captured image
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                // Initiate image capture process
                imgCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(requireActivity()),
                    object : OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val imageBitmap = photoFile.convertToBitmap()
                            navigateToUpload(ImageResult(photoFile, imageBitmap = imageBitmap))
                            IdlingResourceHelper.decrement()
                        }

                        // On error message handling
                        override fun onError(exception: ImageCaptureException) {
                            // Determine error message based on the type of exception
                            val errorMessage = when(exception.imageCaptureError) {
                                ImageCapture.ERROR_UNKNOWN -> getString(R.string.taking_photo_failed_unknown_error)
                                ImageCapture.ERROR_FILE_IO -> getString(R.string.taking_photo_failed_file_io_error)
                                ImageCapture.ERROR_CAPTURE_FAILED -> getString(R.string.taking_photo_failed_capture_failed_error)
                                ImageCapture.ERROR_CAMERA_CLOSED -> getString(R.string.taking_photo_failed_camera_closed_error)
                                else -> getString(R.string.taking_photo_failed_generic_error)
                            }

                            // Display error message
                            showDialogInfo(
                                context = requireActivity(),
                                message = errorMessage,
                                alignment = Gravity.CENTER
                            )

                            // Decrement indicate completion of image capture process
                            IdlingResourceHelper.decrement()
                        }
                    }
                )
            }

            btnSwitchCamera.setOnClickListener {
                selectCam = if (selectCam == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
                openCam()
            }

            btnOpenGallery.setOnClickListener {
                val mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                openGalleryActivityResult.launch(PickVisualMediaRequest(mediaType))
            }

            btnClose.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        openCam()
    }

    private fun openCam() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding?.viewFinder?.surfaceProvider)
                    }

                getImage = ImageCapture.Builder().build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    selectCam,
                    preview,
                    getImage
                )

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is CameraUnavailableException -> getString(R.string.open_camera_failed_camera_unavailable)
                    is CameraInfoUnavailableException -> getString(R.string.open_camera_failed_camera_info_unavailable)
                    else -> getString(R.string.open_camera_failed_generic)
                }

                showDialogInfo(
                    context = requireActivity(),
                    message = errorMessage,
                    alignment = Gravity.CENTER
                )
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun navigateToUpload(imageResult: ImageResult) {
        // Log the image details before navigating
        Log.d("NavToUpload", "Navigating to UploadFragment with imageResult: $imageResult")

        // Navigate to UploadFragment
        val intent = CameraFragmentDirections.cameraToUpload(imageResult)
        findNavController().navigate(intent)
    }

    private val openGalleryActivityResult = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedUri ->
            val imageFile = selectedUri.convertToFile(requireActivity())
            navigateToUpload(ImageResult(imageFile, selectedUri, isFromCamera = false))
        }
    }

    private val permissionActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handlePermissionResult(isGranted)
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        if (!isGranted && !areAllPermissionsGranted()) {
            Toast.makeText(requireActivity(), getString(R.string.access_denied),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}