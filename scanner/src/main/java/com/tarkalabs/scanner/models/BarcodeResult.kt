package com.tarkalabs.scanner.models

import com.tarkalabs.scanner.data.BarcodeData
import com.tarkalabs.scanner.ui.BarcodeScannerActivity
import com.tarkalabs.scanner.ui.BarcodeScannerFragment

/**
 * Result class encapsulating the All the possible Results from scanning
 */
sealed class BarcodeResult {
  /**
   * Indicates User denied Camara Permission.
   */
  object MissingPermission : BarcodeResult()

  /**
   * User pressed back on [BarcodeScannerActivity], and cancelled scanning.
   * [BarcodeScannerFragment] does not return this result.
   */
  object UserCanceled : BarcodeResult()

  /**
   * Indicates No Barcode found in the image frame. You probably don't need to do anything on this result.
   */
  object NoResult : BarcodeResult()

  /**
   * Indicates the successful barcode scan. [data] will give you barcode content.
   */
  data class Success internal constructor(val data: BarcodeData) : BarcodeResult()

  /**
   * Indicates error occurred in scanning Barcode.
   */
  data class Error internal constructor(val exception: Exception) : BarcodeResult()
}