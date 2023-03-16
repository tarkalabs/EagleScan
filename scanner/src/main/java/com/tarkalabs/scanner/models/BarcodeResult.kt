package com.tarkalabs.scanner.models

import com.tarkalabs.scanner.data.BarcodeData

sealed class BarcodeResult {
  object MissingPermission : BarcodeResult()
  object UserCanceled : BarcodeResult()
  object NoResult : BarcodeResult()
  data class Success internal constructor(val data: BarcodeData) : BarcodeResult()
  data class Error internal constructor(val exception: Exception) : BarcodeResult()
}