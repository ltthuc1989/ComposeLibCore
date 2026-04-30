package com.ltthuc.utils.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.format(pattern: String = "d MMMM yyyy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this)
}

fun String.formatAsDate(
    parsePattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    formatPattern: String = "dd.MM.yy"
): String? {
    val dateFormat = SimpleDateFormat(parsePattern, Locale.getDefault())
    val date = dateFormat.parse(this) ?: return null
    return date.format(formatPattern)
}