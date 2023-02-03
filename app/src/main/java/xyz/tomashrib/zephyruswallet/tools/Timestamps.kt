package xyz.tomashrib.zephyruswallet.tools

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

// converts ULong from Transaction.Confirmed type to human readable date format
fun ULong.timestampToString(): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = (this * 1000u).toLong()
    return DateFormat.format("MMMM d yyyy HH:mm", calendar).toString()
}
