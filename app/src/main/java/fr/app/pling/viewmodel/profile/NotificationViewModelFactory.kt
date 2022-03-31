package fr.app.pling.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.app.pling.model.repository.NotificationRepository


/**
 * NotificationViewModelFactory
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class NotificationViewModelFactory(
    private val notificationRepository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationViewModel(notificationRepository) as T
    }
}
