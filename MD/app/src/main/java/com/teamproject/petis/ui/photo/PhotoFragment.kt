package com.teamproject.petis.ui.photo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamproject.petis.R
import com.teamproject.petis.api.PredictionResponse
import com.teamproject.petis.databinding.FragmentPhotoBinding
import com.teamproject.petis.helper.ImageClassifier
import com.teamproject.petis.ui.custom.CustomLoadingDialog
import com.teamproject.petis.ui.history.HistoryFragment
import com.teamproject.petis.ui.result.ResultActivity
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoFragment : Fragment() {
    private var shouldAutoStartCamera = true
    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!

    private val imageClassifier: ImageClassifier by lazy {
        ImageClassifier(requireContext())
    }

    private var imageUri: Uri? = null
    private var isFlashOn = false
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // Launchers
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            handleImageSelection(safeUri)
        } ?: showToast("Failed to load image.")
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            handleImageSelection(imageUri!!)
        } else {
            showToast("Failed to take picture.")
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handleCameraPermissionResult(isGranted)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupUI()
        setupScanningAnimation()
        checkCameraPermission()
    }

    private fun setupBackButton() {
        // Gunakan backButtonOverlay untuk navigasi
        binding.backButtonOverlay.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupUI() {
        // Pastikan tombol back terlihat

        binding.uploadButton.setOnClickListener { openGallery() }
        binding.fabCapture.setOnClickListener { capturePhoto() }
        binding.HistoryButton.setOnClickListener { toggleFlash() }
        binding.scannerFrame.setOnClickListener { toggleFlash() }
    }

    private fun setupScanningAnimation() {
        // Tunggu layout selesai di-render
        binding.scanningOverlayContainer.post {
            val scanningLine = binding.scanningLine
            val scannerFrame = binding.scannerFrame

            // Set lebar scanning line sesuai lebar scanner frame
            val layoutParams = scanningLine.layoutParams
            layoutParams.width = scannerFrame.width
            scanningLine.layoutParams = layoutParams

            // Gunakan ValueAnimator untuk animasi naik turun
            val animator = ValueAnimator.ofFloat(0f, scannerFrame.height.toFloat() - scanningLine.height).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()

                addUpdateListener { animation ->
                    scanningLine.translationY = animation.animatedValue as Float
                }
            }

            // Animator untuk fade effect
            val fadeAnimator = ObjectAnimator.ofFloat(scanningLine, "alpha", 0.3f, 1f, 0.3f).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
            }

            // Jalankan animasi
            AnimatorSet().apply {
                playTogether(animator, fadeAnimator)
                start()
            }
        }
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            binding.photoImageView.setImageURI(uri)
            binding.photoCard.visibility = View.VISIBLE
            binding.cameraPreviewView.visibility = View.GONE
            processImage(uri)
        } catch (e: Exception) {
            Log.e("ImageSelection", "Error processing image", e)
            showToast("Failed to process image")
        }
    }

    private fun handleCameraPermissionResult(isGranted: Boolean) {
        when {
            isGranted -> {
                initializeCamera()
                updateInstructionText("Camera permission granted", 2000)
            }
            !shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
            }
            else -> {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun updateInstructionText(message: String, delay: Long = 0) {
        binding.scanInstructionText.text = message
        if (delay > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.scanInstructionText.text = "Point the camera at the plant"
            }, delay)
        }
    }

    private fun checkCameraPermission() {
        if (allPermissionsGranted()) {
            initializeCamera()
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun initializeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.cameraPreviewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch(exc: Exception) {
                Log.e("CameraSetup", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            "photo_${System.currentTimeMillis()}.jpg"
        )

        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("PhotoCapture", "Photo capture failed", exc)
                    showToast("Failed to take photo: ${exc.message}")
                } override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageUri?.let { uri ->
                        binding.photoImageView.setImageURI(uri)
                        binding.photoCard.visibility = View.VISIBLE
                        binding.cameraPreviewView.visibility = View.GONE
                        processImage(uri)
                    }
                }
            }
        )
    }

    private fun processImage(uri: Uri) {
        shouldAutoStartCamera = false

        val loadingDialog = CustomLoadingDialog(requireContext())
        loadingDialog.show()
        loadingDialog.updateText("Detecting images...")

        try {
            imageClassifier.classifyImage(uri) { predictionResponse, description ->
                requireActivity().runOnUiThread {
                    loadingDialog.dismiss()
                    navigateToResult(predictionResponse, description, uri)
                }
            }
        } catch (e: Exception) {
            requireActivity().runOnUiThread {
                loadingDialog.dismiss()
                Log.e("ImageProcessing", "Error processing image", e)
                showToast("Error processing image: ${e.message}")
            }
        }
    }

    private fun navigateToResult(
        predictionResponse: PredictionResponse,
        description: String,
        imageUri: Uri
    ) {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("PREDICTION", predictionResponse.predictedClass)
            putExtra("CONFIDENCE", predictionResponse.confidence.toString())
            putExtra("DESCRIPTION", description)
            putExtra("IMAGE_URI", imageUri.toString())
        }
        startActivity(intent)
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Camera Permission Required")
            .setMessage("The app requires camera permission to detect plants. Do you want to grant the permission?")
            .setPositiveButton("Allow") { _, _ ->
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                showGalleryOnlyMode()
            }
            .show()
    }

    private fun showGalleryOnlyMode() {
        binding.cameraPreviewView.visibility = View.GONE
        binding.scanInstructionText.text = "Camera permission not available\nUse gallery option"
        binding.uploadButton.requestFocus()
        binding.uploadButton.startAnimation(
            AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_animation)
        )
    }

    // Tambahkan visual feedback untuk status flash
    private fun toggleFlash() {
        camera?.let { cam ->
            if (cam.cameraInfo.hasFlashUnit()) {
                isFlashOn = !isFlashOn
                cam.cameraControl.enableTorch(isFlashOn)

                // Ubah warna atau icon untuk memberi indikasi status flash
                binding.HistoryButton.setColorFilter(
                    if (isFlashOn)
                        ContextCompat.getColor(requireContext(), R.color.flash_on_color)
                    else
                        ContextCompat.getColor(requireContext(), R.color.flash_off_color)
                )

                showToast(if (isFlashOn) "Flash On" else "Flash off")
            } else {
                showToast("Device does not support flash")
            }
        } ?: showToast("Camera not ready")
    }

    override fun onResume() {
        super.onResume()

        if (shouldAutoStartCamera) {
            resetToCameraMode()
            checkCameraPermission()
        }

        // Reset flag kembali ke true
        shouldAutoStartCamera = true
        // Reset tampilan ke mode kamera
        resetToCameraMode()

        // Periksa izin kamera
        checkCameraPermission()
    }

    private fun resetToCameraMode() {
        // Kembalikan tampilan ke mode kamera
        binding.photoCard.visibility = View.GONE
        binding.cameraPreviewView.visibility = View.VISIBLE

        // Reset teks instruksi
        binding.scanInstructionText.text = "Point the camera at the plant"

        // Inisialisasi ulang kamera jika sudah pernah diinisialisasi sebelumnya
        if (allPermissionsGranted()) {
            initializeCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        imageClassifier.close()
        cameraExecutor.shutdown()
        _binding = null
    }
}