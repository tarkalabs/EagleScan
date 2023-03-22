package com.tarkalabs.scanner.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.tarkalabs.scanner.models.BarcodeResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class BarcodeAnalyser(
  codeFormat: IntArray,
  private val coroutineScope: CoroutineScope
) : ImageAnalysis.Analyzer {

  private val barcodeCodeScanner = MLKitCodeScanner(codeFormat)
  private var resultsFlow: MutableSharedFlow<BarcodeResult> =
    MutableStateFlow(BarcodeResult.NoResult)
  val barcodeResults: SharedFlow<BarcodeResult> = resultsFlow

  @SuppressLint("UnsafeOptInUsageError")
  override fun analyze(image: ImageProxy) {
    coroutineScope.launch {
      val result = barcodeCodeScanner.readBarcodeOn(image)
      if (result !is BarcodeResult.NoResult)
        resultsFlow.tryEmit(result)
      image.close()
    }
  }
}