<img width="120" alt="Logo" src="https://user-images.githubusercontent.com/5791518/230404098-bb393e7c-8753-4865-ae96-b8fa936f275b.png">

## EagleScan

EagleScan is Barcode scanning library that was born from our internal needs. It uses [Firebase ML Kit](https://developers.google.com/ml-kit/vision/barcode-scanning) internally to read barcode/QR codes.
For list of supported formats look [here](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode.BarcodeFormat).

**Note:** It bundles Firebase's Bundled ML Kit library that statically links ML model to the app at build time. Because of this, your app's size may grow by 2.4 MB. Supporting unbundled capability is not the priority right now. You may want to use Google's [code-scanner](https://developers.google.com/ml-kit/code-scanner) If you want to go unbundled.

## Screenshot


https://user-images.githubusercontent.com/5791518/227022045-d3e56c72-1332-4a8e-b42e-4092251165d6.mp4


## Instructions

**Installation**:

    implementation('com.tarkalabs:barcode-scanner:1.1.0')

**Code Sample**:

Library provides two way of integrations.
1. `BarcodeScannerActivity`: This is the simplest way to integrate, Just register for activity result and launch the scanner Activity
2. `BarcodeScannerFragment`: Use this fragment when using `BarcodeScannerActivity` is not possible due to any UI design constraints.

Using `BarcodeScannerActivity`.
```
val scanResultReceiver = registerForActivityResult(ScanBarcodeContract()) { result->
  handleBarcodeResult(result)
}

override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContentView(R.layout.activity_main)
  findViewById<Button>(R.id.btn_scan_activity).setOnClickListener {
    scanResultReceiver.launch(null)
  }
}
```

Using `BarcodeScannerFragment`

```
class MainActivity : AppCompatActivity(), ScanResultListener {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  
    supportFragmentManager.commit {
      add(R.id.scanner_fragment_container, BarcodeScannerFragment.newInstance())
    }
  }
  
  override fun onScanResult(result: BarcodeResult) {
    handleBarcodeResult(result)
  }
```

`BarcodeResult` is a sealed class that represents Barcode scanning result.
example:
```
fun handleBarcodeResult(result: BarcodeResult) {  
  when (result) {  
    is Error -> {
      // Error occured while scanning.
      result.exception.printStackTrace()
    }
    is Success -> {
      //Bacode scanned successfully.
      val barcode: BarcodeData = result.data
    }
    MissingPermission -> {
      // Camera scanning permission not availble.
      // Library asks for Camera permission, but if user denys it, It sends this Result.
    }
    UserCanceled -> {
      // Only BarcodeScannerActivity sends this result.
      // User cancelled barcode scanning by pressing back button in Barcode Scanner screen.
    }
  }  
}
```

`BarcodeResult.Success` exposes `BarcodeData` that contains actual barcode data.
List of exposed Barcode types:

- Plain
- Isbn
- WiFi
- Url
- Sms
- GeoPoint
- ContactInfo
- Email
- DriverLicense
- CalendarEvent

**Configuration**:
`BarcodeScannerConfig`  lets to specify barcode format to be enabled and also lets you control visibility of flash button.

```
val scannerConfig = BarcodeScannerConfig.Builder()
  .barcodeFormats(Barcode.FORMAT_ALL_FORMATS)
  .showFlashButton(true)
  .build()
```
---
Check out the [example](https://github.com/tarkalabs/BarcodeScanner/tree/main/example) inside this repo for complete integration steps.
