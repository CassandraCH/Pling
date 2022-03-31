package fr.app.pling.model

import fr.app.pling.R
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

/**
 * CalendarDate - Custom class use to handle easily dates
 */
data class CalendarDate(var data: Date, var isSelected: Boolean = false) {

    private val cal = Calendar.getInstance()

    val calendarDay: String
        get() = SimpleDateFormat("EE", Locale.getDefault()).format(data)

    val calendar: String
        get() = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(data)

    val calendarDate: String
        get() {
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH].toString()
        }

    val day: Int
        get() {
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH]
        }

    val month : Int
        get() {
            cal.time = data
            return cal[Calendar.MONTH]
        }

    val year : Int
        get() {
            cal.time = data
            return cal[Calendar.YEAR]
        }
}
