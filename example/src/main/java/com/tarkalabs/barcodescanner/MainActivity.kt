package com.tarkalabs.barcodescanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.common.Barcode
import com.tarkalabs.scanner.models.BarcodeScannerConfig
import com.tarkalabs.scanner.results.ScanBarcodeContract

class MainActivity : AppCompatActivity() {

  private val scanResultReceiver = registerForActivityResult(ScanBarcodeContract()) {
    Toast.makeText(this, "result: $it", Toast.LENGTH_SHORT).show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<Button>(R.id.btn_scan_activity).setOnClickListener {
      scanResultReceiver.launch(
        BarcodeScannerConfig.Builder()
          .barcodeFormats(Barcode.FORMAT_ALL_FORMATS)
          .showFlashButton(true)
          .build()
      )
    }

    findViewById<Button>(R.id.btn_scan_fragment).setOnClickListener {
      startActivity(Intent(this, ScanFragmentHolderActivity::class.java))
    }
  }
}