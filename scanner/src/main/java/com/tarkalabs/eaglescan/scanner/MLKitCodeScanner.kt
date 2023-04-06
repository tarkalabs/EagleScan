package com.tarkalabs.eaglescan.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tarkalabs.eaglescan.data.BarcodeData
import com.tarkalabs.eaglescan.extentions.toData

class MLKitCodeScanner(
  private val barcodeFormats: IntArray,
  private val onSuccess: (BarcodeData) -> Unit,
  private val onError: (Exception) -> Unit,
) {

  private val barcodeScanner by lazy {
    val optionsBuilder = if (barcodeFormats.size > 1) {
      BarcodeScannerOptions.Builder()
        .setBarcodeFormats(barcodeFormats.first(), *barcodeFormats.drop(1).toIntArray())
    } else {
      BarcodeScannerOptions.Builder()
        .setBarcodeFormats(barcodeFormats.firstOrNull() ?: Barcode.FORMAT_UNKNOWN)
    }
    BarcodeScanning.getClient(optionsBuilder.build())
  }

  @SuppressLint("UnsafeOptInUsageError")
  fun readBarcodeOn(image: ImageProxy) {
    barcodeScanner.process(image.toInputImage()).addOnSuccessListener { results ->
      if (results.isNotEmpty()) {
        onSuccess(results.first().toData() ?: return@addOnSuccessListener)
      }
    }.addOnFailureListener { ex ->
      onError(ex)
    }.addOnCompleteListener {
      image.close()
    }
  }

  @ExperimentalGetImage @Suppress("UnsafeCallOnNullableType")
  private fun ImageProxy.toInputImage() =
    InputImage.fromMediaImage(image!!, imageInfo.rotationDegrees)
}