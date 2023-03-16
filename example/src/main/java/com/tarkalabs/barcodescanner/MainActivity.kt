package com.tarkalabs.barcodescanner

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tarkalabs.scanner.results.ScanBarcodeContract

class MainActivity : AppCompatActivity() {

  private val scanResultReceiver = registerForActivityResult(ScanBarcodeContract()) {
    Toast.makeText(this, "result: $it", Toast.LENGTH_SHORT).show()
  }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<Button>(R.id.btn_scan).setOnClickListener {
      scanResultReceiver.launch(null)
    }
  }
}