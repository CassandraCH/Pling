package fr.app.pling.model.repository

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fr.app.pling.model.Notification
import fr.app.pling.util.COLLECTION_NOTIFICATION
import io.reactivex.rxjava3.core.Completable

/**
 * NotificationRepository
 *
 *  Provides communication with Firebase for operations related to the notification.
 *
 */
class NotificationRepository() {

    private val notifRef: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_NOTIFICATION)
    }


    fun readNotifications(userId: String): FirestoreRecyclerOptions.Builder<Notification> {
        return notificationOptions(notifRef.whereEqualTo("userId", userId));
    }

    /**
     * Add a notification
     */
     fun addNotifications(notification: Notification): Completable {
        return Completable.create { emitter ->
            if (!emitter.isDisposed) {
                notifRef.document(notification.id)
                    .set(notification)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }

    /**
     * Return the last notification
     */
    fun getLastNotification(userId: String): Query {
        return  notifRef.whereEqualTo("userId", userId)
                        .whereEqualTo("read", false)
    }

    /**
     * Handle the creation of the FirestoreRecyclerOptions builder for the notification
     */
    private fun notificationOptions(query: Query): FirestoreRecyclerOptions.Builder<Notification> {
        return FirestoreRecyclerOptions.Builder<Notification>()
            .setQuery(
                query
            ) { snapshot ->
                snapshot.toObject(Notification::class.java)!!.also {
                    it.userId = snapshot.id
                }
            }
    }

    /**
     * Update the Notification state
     */
    fun updateNotificationRead(id: String, read: Boolean): Completable {
        return Completable.create { emitter ->
                notifRef.document(id)
                .update("read", read)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }


}