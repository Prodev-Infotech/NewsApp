package org.kotlin.multiplatform.newsapp.utils

import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun generateId(): String = uuid4().toString()
fun getCurrentFormattedDate(): String {
    val current = Clock.System.now()
    val localDateTime = current.toLocalDateTime(TimeZone.currentSystemDefault())

    // Handle 12-hour format and AM/PM manually
    val (hour12, amPm) = when (localDateTime.hour) {
        0 -> Pair(12, "AM")
        in 1..11 -> Pair(localDateTime.hour, "AM")
        12 -> Pair(12, "PM")
        else -> Pair(localDateTime.hour - 12, "PM")
    }

    return buildString {
        append(localDateTime.dayOfMonth)  // Day (22)
        append(" ")
        append(localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() })  // Month (April)
        append(" ")
        append(localDateTime.year)  // Year (2025)
        append(" ")
        append(hour12)  // Hour (3)
        append(":")
        append(localDateTime.minute.toString().padStart(2, '0'))  // Minute (28)
        append(" ")
        append(amPm)  // AM/PM
    }
}

const val baseUrl = "http://192.168.34.2:8080/"
fun parseToInstantFromCustomFormat(dateStr: String): Instant? {
    return try {
        val parts = dateStr.split(" ")
        val day = parts[0].toInt()
        val month = Month.valueOf(parts[1].uppercase()).number
        val year = parts[2].toInt()

        val timeParts = parts[3].split(":")
        var hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        val isPm = parts[4].equals("PM", ignoreCase = true)

        if (isPm && hour != 12) hour += 12
        if (!isPm && hour == 12) hour = 0

        LocalDateTime(year, month, day, hour, minute)
            .toInstant(TimeZone.currentSystemDefault())
    } catch (e: Exception) {
        null
    }
}
fun getTimeAgo(dateStr: String): String {
    val postTime = parseToInstantFromCustomFormat(dateStr) ?: return "unknown"
    val now = Clock.System.now()
    val duration = now - postTime

    return when {
        duration.inWholeMinutes < 1 -> "just now"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes} min ago"
        duration.inWholeHours < 24 -> "${duration.inWholeHours} hr ago"
        else -> "${duration.inWholeDays} days ago"
    }
}

fun formatDateOnly(dateStr: String): String {
    val instant = parseToInstantFromCustomFormat(dateStr) ?: return "Unknown Date"
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val dow = dt.date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val day = dt.date.dayOfMonth.toString().padStart(2, '0')
    val mon = dt.date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$dow, $day $mon ${dt.date.year}"
}


// Regex email validation function for Kotlin Multiplatform
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return email.matches(emailRegex)
}
