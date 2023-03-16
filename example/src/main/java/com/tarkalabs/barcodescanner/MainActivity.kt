package com.tarkalabs.barcodescanner

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
    findViewById<Button>(R.id.btn_scan).setOnClickListener {
      scanResultReceiver.launch(
        BarcodeScannerConfig.Builder()
          .barcodeFormats(intArrayOf(Barcode.FORMAT_ALL_FORMATS))
          .showFlashButton(false)
          .build()
      )
    }
  }
}