package fr.app.pling.model

import com.google.firebase.Timestamp
import fr.app.pling.util.NotificationType

/**
 * Notification
 */
data class Notification (
    var id: String = "",
    var userId: String = "",
    var senderName: String = "",
    var isRead : Boolean = false,
    var text: String = "",
    var sendAt:  Timestamp? = null,
    var notificationType: Int = 0,
    var itemName: String = ""
)