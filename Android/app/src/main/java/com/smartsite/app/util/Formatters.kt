package com.smartsite.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val frenchDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Formats a date as `dd/MM/yyyy`, as everywhere in the mockup. */
fun formatDate(date: LocalDate): String = date.format(frenchDateFormatter)

/** Material 3 DatePicker uses UTC epoch millis — convert without timezone shifts. */
fun LocalDate.toPickerMillis(): Long =
    atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

fun Long.pickerMillisToLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
