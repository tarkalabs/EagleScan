package com.tarkalabs.barcodescanner

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import com.tarkalabs.barcodescanner.R.id
import com.tarkalabs.barcodescanner.databinding.ActivityScanFragmentHolderBinding
import com.tarkalabs.scanner.models.BarcodeResult
import com.tarkalabs.scanner.models.BarcodeResult.Error
import com.tarkalabs.scanner.models.BarcodeResult.MissingPermission
import com.tarkalabs.scanner.models.BarcodeResult.NoResult
import com.tarkalabs.scanner.models.BarcodeResult.Success
import com.tarkalabs.scanner.models.BarcodeResult.UserCanceled
import com.tarkalabs.scanner.models.BarcodeScannerConfig
import com.tarkalabs.scanner.ui.BarcodeScannerFragment
import com.tarkalabs.scanner.ui.BarcodeScannerFragment.ScanResultListener

class ScanFragmentHolderActivity : AppCompatActivity(), ScanResultListener {
  private lateinit var binding: ActivityScanFragmentHolderBinding
  private var requestedPermissionInSettings = false
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityScanFragmentHolderBinding.inflate(layoutInflater)
    setContentView(binding.root)

    supportFragmentManager.commit {
      setReorderingAllowed(true)
      add(
        id.fragment_container, BarcodeScannerFragment.newInstance(
        BarcodeScannerConfig.Builder()
          .barcodeFormats(Barcode.FORMAT_ALL_FORMATS)
          .showFlashButton(true)
          .build(), false
      )
      )
    }
  }

  override fun onResume() {
    super.onResume()
    if (requestedPermissionInSettings) {
      requestedPermissionInSettings = false
      if (isCameraPermissionGranted()) {
        startScannerCamera()
      } else {
        removeScannerFragment()
      }
    }
  }

  private val requestPermissionLauncher =
    registerForActivityResult(
      RequestPermission()
    ) { isGranted: Boolean ->
      if (isGranted) {
        startScannerCamera()
      } else {
        onPermissionRejected()
      }
    }

  private fun startScannerCamera() {
    binding.fragmentContainer.getFragment<BarcodeScannerFragment?>()?.startCamera()
  }

  private fun onPermissionRejected() {
    AlertDialog.Builder(this).setMessage("App requires Camera Permission to scan barcode.")
      .setPositiveButton(
        "Allow"
      ) { _, _ ->
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
          requestPermissionLauncher.launch(
            android.Manifest.permission.CAMERA
          )
        } else {
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .apply {
              data = Uri.fromParts("package", packageName, null)
            }
          startActivity(intent)
        }
      }
      .setNegativeButton("Cancel") { _, _ ->
        supportFragmentManager.commit {
          remove(
            binding.fragmentContainer.getFragment() ?: return@setNegativeButton
          )
        }
      }.show()
  }

  override fun onScanResult(barcodeResult: BarcodeResult) {
    when (barcodeResult) {
      is Error -> {
        Snackbar.make(
          binding.root, barcodeResult.exception.message.orEmpty(), Snackbar.LENGTH_SHORT
        ).show()
      }
      MissingPermission -> {
        AlertDialog.Builder(this).setMessage("App requires Camera Permission to scan barcode.")
          .setPositiveButton(
            "Allow"
          ) { _, _ ->
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
              requestPermissionLauncher.launch(
                android.Manifest.permission.CAMERA
              )
            } else {
              val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .apply {
                  data = Uri.fromParts("package", packageName, null)
                }
              startActivity(intent)
              requestedPermissionInSettings = true
            }
          }
          .setNegativeButton("Cancel") { _, _ ->
            removeScannerFragment()
          }.show()
      }
      is Success -> {
        binding.tvBarcode.text = "Barcode: ${barcodeResult.data.rawValue}"
      }
      NoResult, UserCanceled -> {
      }
    }
  }

  private fun removeScannerFragment() {
    supportFragmentManager.commit {
      remove(
        binding.fragmentContainer.getFragment() ?: return
      )
    }
  }

  private fun isCameraPermissionGranted() = ContextCompat.checkSelfPermission(
    this@ScanFragmentHolderActivity, permission.CAMERA
  ) == PackageManager.PERMISSION_GRANTED
}