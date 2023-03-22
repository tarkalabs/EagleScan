package com.tarkalabs.scanner.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.tarkalabs.scanner.data.BarcodeData
import com.tarkalabs.scanner.extentions.toData
import com.tarkalabs.scanner.models.BarcodeResult
import com.tarkalabs.scanner.models.BarcodeResult.Error
import com.tarkalabs.scanner.models.BarcodeResult.NoResult
import com.tarkalabs.scanner.models.BarcodeResult.Success
import kotlinx.coroutines.tasks.await

class MLKitCodeScanner(private val barcodeFormats: IntArray) {

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
  suspend fun readBarcodeOn(image: ImageProxy): BarcodeResult {
    try {
      val results: MutableList<Barcode> = barcodeScanner.process(image.toInputImage()).await()
      if (results.isEmpty()) return NoResult
      val result: Barcode = results.first()
      val barcode: BarcodeData? = result.toData()
      return if (barcode == null) {
        NoResult
      } else {
        Success(barcode)
      }
    } catch (exception: Exception) {
      return Error(exception)
    }
  }

  @ExperimentalGetImage @Suppress("UnsafeCallOnNullableType")
  private fun ImageProxy.toInputImage() =
    InputImage.fromMediaImage(image!!, imageInfo.rotationDegrees)
}