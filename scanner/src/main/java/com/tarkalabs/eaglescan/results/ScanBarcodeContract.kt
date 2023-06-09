package com.tarkalabs.eaglescan.results

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.result.contract.ActivityResultContract
import com.tarkalabs.eaglescan.data.BarcodeData
import com.tarkalabs.eaglescan.data.BarcodeData.Plain
import com.tarkalabs.eaglescan.models.BarcodeResult
import com.tarkalabs.eaglescan.models.BarcodeResult.Error
import com.tarkalabs.eaglescan.models.BarcodeResult.MissingPermission
import com.tarkalabs.eaglescan.models.BarcodeResult.Success
import com.tarkalabs.eaglescan.models.BarcodeResult.UserCanceled
import com.tarkalabs.eaglescan.models.BarcodeScannerConfig
import com.tarkalabs.eaglescan.ui.BarcodeScannerActivity
import com.tarkalabs.eaglescan.ui.BarcodeScannerActivity.Companion.EXTRA_RESULT_EXCEPTION
import com.tarkalabs.eaglescan.ui.BarcodeScannerActivity.Companion.EXTRA_RESULT_VALUE
import com.tarkalabs.eaglescan.ui.BarcodeScannerActivity.Companion.RESULT_ERROR
import com.tarkalabs.eaglescan.ui.BarcodeScannerActivity.Companion.RESULT_MISSING_PERMISSION
import com.tarkalabs.eaglescan.ui.BarcodeScannerFragment.Companion.EXTRA_CONFIG

class ScanBarcodeContract : ActivityResultContract<BarcodeScannerConfig?, BarcodeResult>() {

  override fun createIntent(
    context: Context,
    input: BarcodeScannerConfig?
  ): Intent = Intent(context, BarcodeScannerActivity::class.java).apply {
    if (input != null) {
      putExtra(EXTRA_CONFIG, input)
    }
  }

  override fun parseResult(
    resultCode: Int,
    intent: Intent?
  ): BarcodeResult {
    return when (resultCode) {
      RESULT_OK -> if (intent == null) {
        Success(Plain(""))
      } else if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        Success(intent.getParcelableExtra(EXTRA_RESULT_VALUE, BarcodeData::class.java)!!)
      } else {
        Success(intent.getParcelableExtra(EXTRA_RESULT_VALUE)!!)
      }
      RESULT_CANCELED -> UserCanceled
      RESULT_MISSING_PERMISSION -> MissingPermission
      RESULT_ERROR -> Error(intent.getRootException())
      else -> Error(IllegalStateException("Unknown activity result code $resultCode"))
    }
  }

  private fun Intent?.getRootException(): Exception {
    this?.getSerializableExtra(EXTRA_RESULT_EXCEPTION).let {
      return if (it is Exception) it else IllegalStateException("Could retrieve root exception")
    }
  }
}