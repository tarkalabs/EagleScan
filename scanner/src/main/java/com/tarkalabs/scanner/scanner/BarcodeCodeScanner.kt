package com.tarkalabs.scanner.scanner

import androidx.camera.core.ImageProxy
import com.tarkalabs.scanner.data.BarcodeData
import com.tarkalabs.scanner.models.BarcodeResult

interface BarcodeCodeScanner {
  suspend fun readBarcodeOn(image: ImageProxy) : BarcodeResult
}