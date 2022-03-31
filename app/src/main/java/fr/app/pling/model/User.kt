package fr.app.pling.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User
 */
@Parcelize
data class User(
    var userId: String = "",
    var username: String = "",
    var email: String = "",
    var searchname: String = "",
    var projects: MutableList<String> = mutableListOf(),
) : Parcelable