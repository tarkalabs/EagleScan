package com.tarkalabs.eaglescan.extentions

import com.google.mlkit.vision.barcode.common.Barcode
import com.tarkalabs.eaglescan.data.BarcodeData

internal fun Barcode.toData(): BarcodeData? {
  val rawValue = rawValue
  if (rawValue.isNullOrEmpty()) {
    return null
  }
  return when (valueType) {
    Barcode.TYPE_UNKNOWN -> BarcodeData.Plain(rawValue)
    Barcode.TYPE_CONTACT_INFO -> contactInfo?.toDomain(rawValue)
    Barcode.TYPE_EMAIL -> email?.toDomain(rawValue)
    Barcode.TYPE_ISBN -> BarcodeData.Isbn(rawValue)
    Barcode.TYPE_PHONE -> phone.toDomain(rawValue)
    Barcode.TYPE_SMS -> sms.toDomain(rawValue)
    Barcode.TYPE_TEXT -> BarcodeData.Plain(rawValue)
    Barcode.TYPE_URL -> url.toDomain(rawValue)
    Barcode.TYPE_WIFI -> wifi.toDomain(rawValue)
    Barcode.TYPE_GEO -> geoPoint.toDomain(rawValue)
    Barcode.TYPE_CALENDAR_EVENT -> calendarEvent.toDomain(rawValue)
    Barcode.TYPE_DRIVER_LICENSE -> driverLicense.toDomain(rawValue)
    //TBD: Encapsulate it in new type? What all field it should have? format?
    Barcode.TYPE_PRODUCT -> BarcodeData.Plain(rawValue)
    else -> BarcodeData.Plain(rawValue)
  }
}

private fun Barcode.DriverLicense?.toDomain(rawValue: String): BarcodeData.DriverLicense =
  BarcodeData.DriverLicense(
    rawValue = rawValue, documentType = this?.documentType.orEmpty(),
    firstName = this?.firstName.orEmpty(), middleName = this?.middleName.orEmpty(),
    lastName = this?.lastName.orEmpty(), gender = this?.gender.orEmpty(),
    addressStreet = this?.addressStreet.orEmpty(), addressCity = this?.addressCity.orEmpty(),
    addressState = this?.addressState.orEmpty(), addressZip = this?.addressZip.orEmpty(),
    licenseNumber = this?.licenseNumber.orEmpty(), issueDate = this?.issueDate.orEmpty(),
    expiryDate = this?.expiryDate.orEmpty(), birthDate = this?.birthDate.orEmpty(),
    issuingCountry = this?.issuingCountry.orEmpty(),
  )

private fun Barcode.CalendarEvent?.toDomain(rawValue: String): BarcodeData.CalendarEvent =
  BarcodeData.CalendarEvent(
    rawValue = rawValue, summary = this?.summary.orEmpty(),
    description = this?.description.orEmpty(), location = this?.location.orEmpty(),
    organizer = this?.organizer.orEmpty(), status = this?.status.orEmpty(),
    start = this?.start.toDomain(), end = this?.end.toDomain()
  )

private fun Barcode.CalendarDateTime?.toDomain(): BarcodeData.CalendarEvent.CalendarDateTime =
  BarcodeData.CalendarEvent.CalendarDateTime(
    rawValue = this?.rawValue.orEmpty(), year = this?.year ?: -1, month = this?.month ?: -1,
    day = this?.day ?: -1, hours = this?.hours ?: -1, minutes = this?.minutes ?: -1,
    seconds = this?.seconds ?: -1, isUtc = this?.isUtc ?: false,
  )

private fun Barcode.UrlBookmark?.toDomain(rawValue: String): BarcodeData.Url =
  BarcodeData.Url(rawValue = rawValue, url = this?.url.orEmpty(), title = this?.title.orEmpty())

private fun Barcode.Sms?.toDomain(rawValue: String): BarcodeData.Sms = BarcodeData.Sms(
  rawValue = rawValue, message = this?.message.orEmpty(), phoneNumber = this?.phoneNumber.orEmpty()
)

private fun Barcode.GeoPoint?.toDomain(rawValue: String): BarcodeData.GeoPoint =
  BarcodeData.GeoPoint(rawValue = rawValue, lat = this?.lat ?: 0.0, lng = this?.lng ?: 0.0)

private fun Barcode.WiFi?.toDomain(rawValue: String): BarcodeData.WiFi = BarcodeData.WiFi(
  rawValue = rawValue, ssid = this?.ssid.orEmpty(), password = this?.password.orEmpty(),
  encryptionType = when (this?.encryptionType) {
    Barcode.WiFi.TYPE_OPEN -> BarcodeData.WiFi.EncryptionType.OPEN
    Barcode.WiFi.TYPE_WEP -> BarcodeData.WiFi.EncryptionType.WEP
    Barcode.WiFi.TYPE_WPA -> BarcodeData.WiFi.EncryptionType.WPA
    else -> BarcodeData.WiFi.EncryptionType.UNKNOWN
  }
)

private fun Barcode.ContactInfo?.toDomain(rawValue: String): BarcodeData.ContactInfo =
  BarcodeData.ContactInfo(
    rawValue = rawValue, name = this?.name.toDomain(), organization = this?.organization.orEmpty(),
    title = this?.title.orEmpty(), phones = this?.phones?.map { it.toDomain(rawValue) }.orEmpty(),
    emails = this?.emails?.map { it.toDomain(rawValue) }.orEmpty(), urls = this?.urls.orEmpty(),
    addresses = this?.addresses?.map { it.toDomain() }.orEmpty()
  )

private fun Barcode.Phone?.toDomain(rawValue: String): BarcodeData.Phone = BarcodeData.Phone(
  rawValue = rawValue, number = this?.number.orEmpty(), type = when (this?.type) {
  Barcode.Phone.TYPE_UNKNOWN -> BarcodeData.Phone.Type.UNKNOWN
  Barcode.Phone.TYPE_WORK -> BarcodeData.Phone.Type.WORK
  Barcode.Phone.TYPE_HOME -> BarcodeData.Phone.Type.HOME
  Barcode.Phone.TYPE_FAX -> BarcodeData.Phone.Type.FAX
  Barcode.Phone.TYPE_MOBILE -> BarcodeData.Phone.Type.MOBILE
  else -> BarcodeData.Phone.Type.UNKNOWN
}
)

private fun Barcode.Email?.toDomain(rawValue: String): BarcodeData.Email = BarcodeData.Email(
  rawValue = rawValue, type = when (this?.type) {
  Barcode.Email.TYPE_UNKNOWN -> BarcodeData.Email.Type.UNKNOWN
  Barcode.Email.TYPE_WORK -> BarcodeData.Email.Type.WORK
  Barcode.Email.TYPE_HOME -> BarcodeData.Email.Type.HOME
  else -> BarcodeData.Email.Type.UNKNOWN
}, address = this?.address.orEmpty(), subject = this?.subject.orEmpty(), body = this?.body.orEmpty()
)

private fun Barcode.PersonName?.toDomain(): BarcodeData.ContactInfo.PersonName =
  BarcodeData.ContactInfo.PersonName(
    formattedName = this?.formattedName.orEmpty(), pronunciation = this?.pronunciation.orEmpty(),
    prefix = this?.prefix.orEmpty(), first = this?.first.orEmpty(), middle = this?.middle.orEmpty(),
    last = this?.last.orEmpty(), suffix = this?.suffix.orEmpty()
  )

private fun Barcode.Address?.toDomain(): BarcodeData.ContactInfo.Address =
  BarcodeData.ContactInfo.Address(
    type = when (this?.type) {
      Barcode.Address.TYPE_UNKNOWN -> BarcodeData.ContactInfo.Address.Type.UNKNOWN
      Barcode.Address.TYPE_HOME -> BarcodeData.ContactInfo.Address.Type.HOME
      Barcode.Address.TYPE_WORK -> BarcodeData.ContactInfo.Address.Type.WORK
      else -> BarcodeData.ContactInfo.Address.Type.UNKNOWN
    }, addressLines = this?.addressLines?.toList().orEmpty()
  )