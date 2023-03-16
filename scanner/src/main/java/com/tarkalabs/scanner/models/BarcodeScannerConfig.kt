package com.tarkalabs.scanner.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class BarcodeScannerConfig internal constructor(
  internal val barcodeFormats: IntArray,
  internal val showFlashButton: Boolean,
) : Parcelable {

  data class Builder(
    private var barcodeFormats: IntArray = intArrayOf(),
    private var showFlashButton: Boolean = true,
  ) {
    fun barcodeFormats(barcodeFormats: IntArray) = apply { this.barcodeFormats = barcodeFormats }
    fun showFlashButton(show: Boolean) = apply { this.showFlashButton = show }

    fun build() =
      BarcodeScannerConfig(barcodeFormats, showFlashButton)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Builder

      if (!barcodeFormats.contentEquals(other.barcodeFormats)) return false
      if (showFlashButton != other.showFlashButton) return false

      return true
    }

    override fun hashCode(): Int {
      var result = barcodeFormats.contentHashCode()
      result = 31 * result + showFlashButton.hashCode()
      return result
    }
  }

  override fun toString(): String {
    return "BarcodeScannerConfig(barcodeFormats=${barcodeFormats.contentToString()}, showFlashButton=$showFlashButton)"
  }
}