package fr.app.pling.viewmodel.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import fr.app.pling.model.Notification
import fr.app.pling.model.Project
import fr.app.pling.model.User
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.model.repository.NotificationRepository
import fr.app.pling.util.NotificationType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*


/**
 * NotificationViewModel
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class NotificationViewModel(
    private val notifRepository: NotificationRepository
) : ViewModel() {

    var eventListener: IEventListener? = null

    var lastNotification: Notification? = null

    val lastNotificationMLD: MutableLiveData<Notification?> by lazy {
        MutableLiveData<Notification?>()
    }

    private val disposables = CompositeDisposable()

    /**
     * Return all the notifications
     */
    fun getNotifications(userId: String): FirestoreRecyclerOptions.Builder<Notification> {
        return this.notifRepository.readNotifications(userId)
    }

    /**
        Get The last notification
     */
    fun getLastNotification(userId: String): ListenerRegistration {
        return notifRepository.getLastNotification(userId)
            .addSnapshotListener { value, _ ->

                if (value != null) {
                    val resultList = value.toObjects(Notification::class.java)
                    val notification = resultList.maxByOrNull { it.sendAt!! } // Return the last notification by searching the max

                    if(notification != null){
                      lastNotificationMLD.postValue(notification)
                    }

                } else {
                    lastNotificationMLD.postValue(null)
                }
            }
    }

    /**
     * Create a new notification
     */
    fun addNotification(user: User, receiverId: String, message: String) {

        val notif = Notification(
            id = UUID.randomUUID().toString(),
            senderName = user.username!!,
            text = message,
            userId = receiverId,
            isRead = false
        )

        notifRepository.addNotifications(notif)
            .doOnComplete {
                eventListener?.onSuccess()
            }
            .doOnError { eventListener?.onFailure(it.message!!) }
    }

    // Update the notification
    fun updateNotification() {

        if (lastNotification == null) {
            return
        }

        val disposable = notifRepository.updateNotificationRead(
            lastNotification!!.id!!,
            !lastNotification!!.isRead
        )
            .subscribe({
                eventListener?.onSuccess()
                this.lastNotificationMLD.postValue(null)

            }, {
                eventListener?.onFailure(it.message!!)
            })

        disposables.add(disposable)

    }
}