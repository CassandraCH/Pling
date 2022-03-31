package fr.app.pling.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize


/**
 * Task
 */
@Parcelize
data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    var state: String = "TODO",
    var projectId: String = "",
    var projectName: String = "",
    var affectedAt: String = "",
    var searchTitle: String = "",
    var availableDays: MutableList<Timestamp> = mutableListOf()
) : Parcelable
