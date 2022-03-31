package fr.app.pling.view.profile

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import fr.app.pling.databinding.CardNotificationBinding
import fr.app.pling.model.Notification
import fr.app.pling.util.NotificationType
import fr.app.pling.util.getNotificationString

/**
 * NotificationAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class NotificationAdapter(options: FirestoreRecyclerOptions<Notification>) :
     FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationViewHolder>(options )  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        // transformation of the xml layout into a Java object
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardNotificationBinding.inflate(layoutInflater, parent, false)
        return NotificationViewHolder(binding)
    }

    // ViewHolder permet d’accéder à chaque vue d’élément de liste sans avoir besoin de rechercher
    class NotificationViewHolder(private val b: CardNotificationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(model: Notification) {
            b.tvSenderNotification.text = model.senderName
            val notificationMsg = itemView.context?.getNotificationString(NotificationType.values()[model.notificationType])
            val underlineTitle = Html.fromHtml("$notificationMsg <u>${model.itemName}</u>",
                Html.FROM_HTML_MODE_COMPACT
            )
            b.tvActionNotification.text = underlineTitle
        }
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int,
        model: Notification
    ) {
        holder.bind(model)
    }
}