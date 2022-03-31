package fr.app.pling.util

import android.content.Context
import com.google.firebase.Timestamp
import com.prolificinteractive.materialcalendarview.CalendarDay
import fr.app.pling.R
import fr.app.pling.model.CalendarDate
import org.threeten.bp.LocalDate
import java.util.*

/**
 * Get the state corresponding to the string
 * Use for the task state
 */
fun Context.getStateString(string: String): String =
    when (string) {
        getString(R.string.todo_state) -> "TODO"
        getString(R.string.state_progress) -> "IN PROGRESS"
        else -> "DONE"
    }

/**
 * Return the notification text depending on the NotificationType
 */
fun Context.getNotificationString(notif: NotificationType): String =
    when (notif) {
        NotificationType.ADDED_TO_PROJECT -> getString(R.string.notification_added_to_project)
        NotificationType.ADDED_TO_TASK -> getString(R.string.notification_added_to_task)
        NotificationType.REMOVE_FROM_PROJECT -> getString(R.string.notification_remove_from_project)
        NotificationType.REMOVE_FROM_TASK -> getString(R.string.notification_remove_from_task)
        else -> ""
    }

/**
 * Convert a list of objects of type CalendarDay in Date
 */
fun convertToDates(dates: List<CalendarDay> ): List<Calendar> {

    val list = mutableListOf<Calendar>()
    dates.forEach {
        list.add(convertToDate(it))
    }
    return  list.toList()
}

/**
 * Convert an object CalendarDay into Dato
 */
fun convertToDate(cdate : CalendarDay): Calendar {
    val date = Calendar.getInstance()
    date.set(cdate.year, cdate.month - 1, cdate.day)
    return date
}

/**
 * Get the object CalendayDay corresponding at the Timestamp
 */
fun getCalendarDay(time: Timestamp): CalendarDay? {
    val cdate = CalendarDate(time.toDate(), false)
    return CalendarDay.from(
        LocalDate.of(
            cdate.year,
            cdate.month + 1,
            cdate.day
        )
    )
}

/**
 * Remove the time for the date
 */
fun removeTime(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.time
}

