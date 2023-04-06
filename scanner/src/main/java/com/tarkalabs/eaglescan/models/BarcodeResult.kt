package com.tarkalabs.eaglescan.models

import com.tarkalabs.eaglescan.data.BarcodeData
import com.tarkalabs.eaglescan.ui.BarcodeScannerActivity
import com.tarkalabs.eaglescan.ui.BarcodeScannerFragment

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
   * Indicates the successful barcode scan. [data] will give you barcode content.
   */
  data class Success internal constructor(val data: BarcodeData) : BarcodeResult()

  /**
   * Indicates error occurred in scanning Barcode.
   */
  data class Error internal constructor(val exception: Exception) : BarcodeResult()
}