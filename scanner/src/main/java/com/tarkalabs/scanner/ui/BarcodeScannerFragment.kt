package com.tarkalabs.scanner.ui

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.mlkit.vision.barcode.common.Barcode
import com.tarkalabs.scanner.R
import com.tarkalabs.scanner.databinding.FragmentBarcodeScannerBinding
import com.tarkalabs.scanner.models.BarcodeResult
import com.tarkalabs.scanner.models.BarcodeScannerConfig
import com.tarkalabs.scanner.scanner.BarcodeAnalyser
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerFragment : Fragment(R.layout.fragment_barcode_scanner) {

  private lateinit var binding: FragmentBarcodeScannerBinding
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var analyser: BarcodeAnalyser
  private var barcodeFormats = intArrayOf(Barcode.FORMAT_QR_CODE)

  private var listener: ScanResultListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is ScanResultListener)
      listener = context
  }

  override fun onDestroy() {
    super.onDestroy()
    cameraExecutor.shutdown()
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    binding = FragmentBarcodeScannerBinding.bind(view)
    applyScannerConfig()
    setupAnalyserObserver()
  }

  private val checkPermission = registerForActivityResult(RequestPermission()) {
    onPermissionGrantResult(it)
  }

  override fun onResume() {
    super.onResume()
    askAndHandlePermission()
  }

  private fun onPermissionGrantResult(granted: Boolean) {
    if (granted) {
      startCamera()
    } else {
      listener?.onScanResult(BarcodeResult.MissingPermission)
    }
  }

  private fun isCameraPermissionGranted() =
    ContextCompat.checkSelfPermission(
      requireContext(), permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

  private fun askAndHandlePermission() {
    if (isCameraPermissionGranted()) {
      onPermissionGrantResult(true)
    } else {
      checkPermission.launch(permission.CAMERA)
    }
  }

  private fun buildPreviewUseCase(): Preview {
    return Preview.Builder().build()
      .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
  }

  private fun buildImageAnalysisUseCase(): ImageAnalysis {
    return ImageAnalysis.Builder()
      .setTargetResolution(Size(binding.previewView.width, binding.previewView.height))
      .build()
      .also { it.setAnalyzer(cameraExecutor, analyser) }
  }

  private fun startCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
    cameraProviderFuture.addListener({
      try {
        val cameraProvider = cameraProviderFuture.get()
        val preview = buildPreviewUseCase()
        val imageAnalysis = buildImageAnalysisUseCase()
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
          this,
          CameraSelector.DEFAULT_BACK_CAMERA,
          preview, imageAnalysis
        )
        if (camera.cameraInfo.hasFlashUnit() && binding.imgBtnFlash.isVisible) {
          binding.imgBtnFlash.setOnClickListener {
            camera.cameraControl.enableTorch(camera.cameraInfo.torchState.value == TorchState.OFF)
          }
          camera.cameraInfo.torchState.observe(viewLifecycleOwner) { torchState ->
            if (torchState == TorchState.OFF) {
              binding.imgBtnFlash.setImageResource(R.drawable.bs_ic_flash_on)
            } else {
              binding.imgBtnFlash.setImageResource(R.drawable.bs_ic_flash_off)
            }
          }
        }
        binding.previewView.visibility = View.VISIBLE
      } catch (e: Exception) {
        binding.previewView.visibility = View.INVISIBLE
        listener?.onScanResult(BarcodeResult.Error(e))
      }
    }, ContextCompat.getMainExecutor(requireContext()))
  }

  private fun setupAnalyserObserver() {
    analyser = BarcodeAnalyser(barcodeFormats, viewLifecycleOwner.lifecycleScope)
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        analyser.barcodeResults
          .distinctUntilChanged()
          .collect { result ->
            listener?.onScanResult(result)
          }
      }
    }
  }

  private fun applyScannerConfig() {
    arguments?.getParcelable<BarcodeScannerConfig>(EXTRA_CONFIG)?.let {
      barcodeFormats = it.barcodeFormats
      binding.imgBtnFlash.isVisible = it.showFlashButton
    }
  }

  companion object {

    @JvmStatic
    fun newInstance(config: BarcodeScannerConfig): BarcodeScannerFragment {
      val bundle = bundleOf(EXTRA_CONFIG to config)
      val fragment = BarcodeScannerFragment()
      fragment.arguments = bundle
      return fragment
    }

    const val EXTRA_CONFIG = "com.tarkalabs.scanner.ui.KEY_CONFIG"
  }

  interface ScanResultListener {
    fun onScanResult(barcodeResult: BarcodeResult)
  }
}