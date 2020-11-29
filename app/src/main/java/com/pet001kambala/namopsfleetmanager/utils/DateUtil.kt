package com.pet001kambala.namopsfleetmanager.utils

import android.text.format.DateFormat
import com.google.firebase.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {
    companion object{
        const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        var ICON_PATH_PATTERN = ".*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*"
        val INCUBATION_TIME = 86400000 * 14

        fun today(): Timestamp {
            val cal = Calendar.getInstance()
            val yr = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DATE)
            val hr = cal.get(Calendar.HOUR)
            val min = cal.get(Calendar.MINUTE)
            cal.set(yr,month,day,hr,min)
            return Timestamp(cal.time)
        }

        //TODO need to mod this function to return a 24 hr format time
        fun Timestamp._24(): String{
            return SimpleDateFormat(DATE_FORMAT, Locale.UK).format(this.toDate())
        }

        fun parseDate(date: String?): Date? {
            try {
                return SimpleDateFormat(DATE_FORMAT, Locale.US).parse(date!!)
            } catch (ignore: ParseException) {
            }
            return null
        }

        fun getDate(dateStr: String): String? {
            return dateStr.replace(".*(\\d{4}-\\d{2}-\\d{2}).*".toRegex(), "$1")
        }

        fun getTime(dateStr: String): String? {
            return dateStr.replace(".*(\\d{2}:\\d{2}:\\d{2}).*".toRegex(), "$1")
        }

        fun fromLong(long: Long):String{
            return DateFormat.format("dd MMMM yyyy",Date(long)).toString()
        }

        fun expireDate():Timestamp{
            val backdate =  Date().time + INCUBATION_TIME
            return Timestamp(Date(backdate))
        }

        fun fromTimeStamp(stamp: Timestamp):String?{
            return SimpleDateFormat(DATE_FORMAT, Locale.US).format(stamp.toDate())
        }

        fun parseVisitDate(timestamp: Timestamp):String?{
            val dateLong = timestamp.toDate().time - INCUBATION_TIME
            val stamp =  Timestamp(Date(dateLong))
            return fromTimeStamp(stamp)
        }
    }
}