package com.tarkalabs.eaglescan.ui

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import com.tarkalabs.eaglescan.R
import com.tarkalabs.eaglescan.databinding.ActivityBarcodeScannerBinding
import com.tarkalabs.eaglescan.models.BarcodeResult
import com.tarkalabs.eaglescan.models.BarcodeResult.MissingPermission
import com.tarkalabs.eaglescan.models.BarcodeResult.Success
import com.tarkalabs.eaglescan.models.BarcodeResult.UserCanceled
import com.tarkalabs.eaglescan.models.BarcodeScannerConfig
import com.tarkalabs.eaglescan.ui.BarcodeScannerFragment.Companion.EXTRA_CONFIG
import com.tarkalabs.eaglescan.ui.BarcodeScannerFragment.ScanResultListener

class BarcodeScannerActivity : AppCompatActivity(), ScanResultListener {

  companion object {
    const val EXTRA_RESULT_VALUE: String = "com.tarkalabs.scanner.result_value"
    const val EXTRA_RESULT_EXCEPTION: String = "com.tarkalabs.scanner.result_error"
    const val RESULT_MISSING_PERMISSION: Int = 10
    const val RESULT_ERROR: Int = 11
  }

  private lateinit var binding: ActivityBarcodeScannerBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val appThemeLayoutInflater = applicationInfo.theme.let { appThemeRes ->
      if (appThemeRes != 0) layoutInflater.cloneInContext(
        ContextThemeWrapper(this, appThemeRes)
      ) else layoutInflater
    }
    binding = ActivityBarcodeScannerBinding.inflate(appThemeLayoutInflater)
    setContentView(binding.root)
    setupFullScreenUI()
    val config = getScannerConfigOrDefault()
    addFragment(config, true)
  }

  private fun setupFullScreenUI() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
  }

  private fun getScannerConfigOrDefault(): BarcodeScannerConfig? {
    val config = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
      intent.getParcelableExtra(EXTRA_CONFIG, BarcodeScannerConfig::class.java)
    } else {
      intent.getParcelableExtra(EXTRA_CONFIG)
    }
    return config
  }

  private fun addFragment(
    config: BarcodeScannerConfig?,
    adjustInsets: Boolean
  ) {
    supportFragmentManager.commit {
      setReorderingAllowed(true)
      add(R.id.container, BarcodeScannerFragment.newInstance(config, adjustInsets))
    }
  }

  override fun onScanResult(barcodeResult: BarcodeResult) {
    when (barcodeResult) {
      is Success -> setResult(
        RESULT_OK,
        Intent().apply { putExtra(EXTRA_RESULT_VALUE, barcodeResult.data) }
      )
      is MissingPermission -> setResult(RESULT_MISSING_PERMISSION, null)
      is UserCanceled -> {
        setResult(RESULT_CANCELED, null)
      }
      is BarcodeResult.Error -> setResult(
        RESULT_ERROR,
        Intent().putExtra(EXTRA_RESULT_EXCEPTION, barcodeResult.exception)
      )
    }
    finish()
  }
}