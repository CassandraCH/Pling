package fr.app.pling.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

/**
 * Project
 */
@Parcelize
data class Project(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var dueDate: Timestamp? = null,
    var state: String = "TODO",
    var searchTitle: String = "",
    var teams: MutableList<String> = mutableListOf(),
) : Parcelable