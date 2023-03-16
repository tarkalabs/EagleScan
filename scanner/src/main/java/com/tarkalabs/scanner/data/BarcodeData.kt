package com.tarkalabs.scanner.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class BarcodeData(open val rawValue: String) : Parcelable {
  override fun toString(): String {
    return rawValue
  }

  @Parcelize
  data class Plain(override val rawValue: String) : BarcodeData(rawValue), Parcelable

  @Parcelize
  data class Isbn(override val rawValue: String) : BarcodeData(rawValue), Parcelable

  @Parcelize
  data class WiFi(
    override val rawValue: String,
    val ssid: String,
    val password: String,
    val encryptionType: EncryptionType
  ) : BarcodeData(rawValue), Parcelable {
    enum class EncryptionType {
      UNKNOWN,
      OPEN,
      WPA,
      WEP
    }
  }

  @Parcelize
  data class Url(
    override val rawValue: String,
    val url: String,
    val title: String,
  ) : BarcodeData(rawValue), Parcelable

  @Parcelize
  data class Sms(
    override val rawValue: String,
    val message: String,
    val phoneNumber: String,
  ) : BarcodeData(rawValue), Parcelable

  @Parcelize
  data class GeoPoint(
    override val rawValue: String,
    val lat: Double,
    val lng: Double,
  ) : BarcodeData(rawValue), Parcelable

  @Parcelize
  data class ContactInfo(
    override val rawValue: String,
    val name: PersonName,
    val organization: String,
    val title: String,
    val phones: List<Phone>,
    val emails: List<Email>,
    val urls: List<String>,
    val addresses: List<Address>
  ) : BarcodeData(rawValue), Parcelable {
    @Parcelize
    data class PersonName(
      val formattedName: String,
      val pronunciation: String,
      val prefix: String,
      val first: String,
      val middle: String,
      val last: String,
      val suffix: String,
    ) : Parcelable

    @Parcelize
    data class Address(
      val type: Type,
      val addressLines: List<String>
    ) : Parcelable {
      enum class Type {
        UNKNOWN,
        WORK,
        HOME,
      }
    }
  }

  @Parcelize
  data class Email(
    override val rawValue: String,
    val type: Type,
    val address: String,
    val subject: String,
    val body: String,
  ) : BarcodeData(rawValue), Parcelable {
    enum class Type {
      UNKNOWN,
      WORK,
      HOME,
    }
  }

  @Parcelize
  data class Phone(
    override val rawValue: String,
    val number: String,
    val type: Type
  ) : BarcodeData(rawValue), Parcelable {
    enum class Type {
      UNKNOWN,
      WORK,
      HOME,
      FAX,
      MOBILE,
    }
  }

  @Parcelize
  data class DriverLicense(
    override val rawValue: String,
    val documentType: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val gender: String,
    val addressStreet: String,
    val addressCity: String,
    val addressState: String,
    val addressZip: String,
    val licenseNumber: String,
    val issueDate: String,
    val expiryDate: String,
    val birthDate: String,
    val issuingCountry: String,
  ) : BarcodeData(rawValue), Parcelable

  @Parcelize
  data class CalendarEvent(
    override val rawValue: String,
    val summary: String,
    val description: String,
    val location: String,
    val organizer: String,
    val status: String,
    // Could have kept nullable. But MLKit returns non null object with -1 as default value.
    // So keeping it as non nullable and ask users to rely on -1 value as identification for missing info.
    val start: CalendarDateTime,
    val end: CalendarDateTime,
  ) : BarcodeData(rawValue), Parcelable {
    @Parcelize
    data class CalendarDateTime(
      val rawValue: String,
      val year: Int,
      val month: Int,
      val day: Int,
      val hours: Int,
      val minutes: Int,
      val seconds: Int,
      val isUtc: Boolean,
    ) : Parcelable
  }
}