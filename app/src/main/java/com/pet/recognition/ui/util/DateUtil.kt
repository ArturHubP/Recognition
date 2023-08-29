package com.pet.recognition.ui.util

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone


fun timeFilter(input: AnnotatedString): TransformedText {
    val text = input.text.replace(":", "")
    val trimmed = if (text.length >= 4) text.substring(0..3) else text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i == 1) out += ":"
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset + 1
            return offset // Handle the offset value of 5 (unchanged)
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 2) return offset
            if (offset <= 4) return offset - 1
            return offset // Handle the offset value of 5 (unchanged)
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}

fun dateFilter(text: AnnotatedString): TransformedText {
    val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 4) out += "."
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset +1
            if (offset <= 8) return offset +2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <=2) return offset
            if (offset <=5) return offset -1
            if (offset <=10) return offset -2
            return 8
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}
fun isValidDate(date: String): Boolean {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    dateFormat.isLenient = false


    return try {
        dateFormat.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}

fun formatDate(inputDate:String): String{
    if(inputDate.isNotEmpty()) {
        val inputFormat = "dd.MM.yyyy"
        val outputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        // Parse input date
        val parser = SimpleDateFormat(inputFormat, Locale.getDefault())
        val date = parser.parse(inputDate)

        // Format output date
        val formatter = SimpleDateFormat(outputFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val outputDate = formatter.format(date!!)
        Log.d("dateForServ", outputDate)
        return outputDate
    }else{
        return ""
    }

}

fun formatTimeToUTC(time: String): String{
    val givenTime = LocalTime.parse(time) // Replace with the given time in HH:mm format
    val sourceTimeZone = ZoneId.of(TimeZone.getDefault().id)
    val targetTimeZone = ZoneId.of("UTC")

    val sourceDateTime = ZonedDateTime.of(
        ZonedDateTime.now().toLocalDate(), // Use the current date or specify a specific date
        givenTime,
        sourceTimeZone
    )
    val utcDateTime = sourceDateTime.withZoneSameInstant(targetTimeZone)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    println("Given Time in Asia/Yekaterinburg: $givenTime")
    println("UTC Time: $utcDateTime")
    return utcDateTime.format(formatter)
}


fun beautifulDate(inputDate:String?,removeDots: Boolean = false): String{
    val outputFormat = "dd.MM.yyyy"
    val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val inputFormat2 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    val formatter = SimpleDateFormat(outputFormat, Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    var result: String
    // Parse input date
    var parser = SimpleDateFormat(inputFormat, Locale.getDefault())
    if (inputDate.isNullOrBlank()) return ""
    try {
        result = formatter.format(parser.parse(inputDate))
    }catch (_:Throwable){
        parser = SimpleDateFormat(inputFormat2, Locale.getDefault())
        result = formatter.format(parser.parse(inputDate))
    }

    if(removeDots){
        result = result.replace(".","")
    }

    return result
}

fun beautifulDateWithoutLocale(inputDate: String?, removeDots: Boolean = false): String {
    val outputFormat = "dd.MM.yyyy"
    val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val inputFormat2 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    val formatter = SimpleDateFormat(outputFormat, Locale.getDefault())
    var result: String
    // Parse input date
    var parser = SimpleDateFormat(inputFormat, Locale.getDefault())
    if (inputDate.isNullOrBlank()) return ""
    try {
        val date = parser.parse(inputDate)
        result = formatter.format(date)
    } catch (_: Throwable) {
        parser = SimpleDateFormat(inputFormat2, Locale.getDefault())
        val date = parser.parse(inputDate)
        result = formatter.format(date)
    }
    if (removeDots) {
        result = result.replace(".", "")
    }
    return result
}
fun calculateAge(dateOfBirth: String): String {
    val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val inputFormat2 = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    var parser = DateTimeFormatter.ofPattern(inputFormat, Locale.getDefault())

    val date =
        try {
            LocalDate.parse(dateOfBirth, parser)
        } catch (_: Throwable) {
            parser = DateTimeFormatter.ofPattern(inputFormat2, Locale.getDefault())
            LocalDate.parse(dateOfBirth, parser)
        }

    val period = Period.between(date, LocalDate.now())

    return buildString {
        if (period.years > 0) {
            append("${period.years} ")
            append(when {
                period.years % 10 == 1 && period.years % 100 != 11 -> "год"
                period.years % 10 in 2..4 && period.years % 100 !in 12..14 -> "года"
                else -> "лет"
            })
        }

        if (period.months > 0) {
            if (isNotEmpty()) append(" ")
            append("${period.months} ")
            append(when {
                period.months % 10 == 1 && period.months % 100 != 11 -> "месяц"
                period.months % 10 in 2..4 && period.months % 100 !in 12..14 -> "месяца"
                else -> "месяцев"
            })
        }

        if (period.days > 0 && period.months == 0 && period.years == 0) {
            if (isNotEmpty()) append(" ")
            append("${period.days} ")
            append(when {
                period.days % 10 == 1 && period.days % 100 != 11 -> "день"
                period.days % 10 in 2..4 && period.days % 100 !in 12..14 -> "дня"
                else -> "дней"
            })
        }

        if (isEmpty()) append("менее дня")
    }
}

fun formatUTCToLocalTime(hhmm: String): String {
    if(hhmm.isNotEmpty()) {
        val time = hhmm.split(":")
        val givenTime = LocalTime.of(time[0].toInt(), time[1].toInt())
        val sourceTimeZone = ZoneId.of("UTC")
        val targetTimeZone = ZoneId.of(TimeZone.getDefault().id)

        val sourceDateTime = ZonedDateTime.of(
            ZonedDateTime.now().toLocalDate(),
            givenTime,
            sourceTimeZone
        )
        val localDateTime = sourceDateTime.withZoneSameInstant(targetTimeZone)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        println("Given Time in UTC: $givenTime")
        println("Local Time: $localDateTime")
        return localDateTime.format(formatter)
    }else{
        return ""
    }
}

fun formatWithRussianMonth(dateTime: LocalDateTime, formatter: DateTimeFormatter): String {
    val month = dateTime.month
    val monthName = when (month) {
        // Например, библиотеку Time4J.
        java.time.Month.JANUARY -> "января"
        java.time.Month.FEBRUARY -> "февраля"
        java.time.Month.MARCH -> "марта"
        java.time.Month.APRIL -> "апреля"
        java.time.Month.MAY -> "мая"
        java.time.Month.JUNE -> "июня"
        java.time.Month.JULY -> "июля"
        java.time.Month.AUGUST -> "августа"
        java.time.Month.SEPTEMBER -> "сентября"
        java.time.Month.OCTOBER -> "октября"
        java.time.Month.NOVEMBER -> "ноября"
        java.time.Month.DECEMBER -> "декабря"
    }
    return dateTime.format(formatter).replace("{month}", monthName)
}






