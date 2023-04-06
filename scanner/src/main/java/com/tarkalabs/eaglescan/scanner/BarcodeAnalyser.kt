package com.tarkalabs.eaglescan.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.tarkalabs.eaglescan.data.BarcodeData

class BarcodeAnalyser(
  codeFormat: IntArray,
  onSuccess: (BarcodeData) -> Unit,
  onError: (Exception) -> Unit,
) : ImageAnalysis.Analyzer {

  private val barcodeCodeScanner = MLKitCodeScanner(codeFormat, onSuccess, onError)

  @SuppressLint("UnsafeOptInUsageError")
  override fun analyze(image: ImageProxy) {
    barcodeCodeScanner.readBarcodeOn(image)
  }
}