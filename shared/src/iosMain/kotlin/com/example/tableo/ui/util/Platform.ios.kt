package com.example.tableo.ui.util

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun formatDateTime(millis: Long, pattern: String): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = pattern
    formatter.locale = NSLocale(localeIdentifier = "de_DE")
    return formatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(millis / 1000.0))
}

actual fun currentMinuteOfDay(): Int {
    val components = NSCalendar.currentCalendar.components(NSCalendarUnitHour or NSCalendarUnitMinute, fromDate = NSDate())
    return (components.hour * 60 + components.minute).toInt()
}
