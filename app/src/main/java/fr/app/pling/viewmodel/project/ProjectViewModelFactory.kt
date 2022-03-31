package fr.app.pling.viewmodel.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.app.pling.model.repository.NotificationRepository
import fr.app.pling.model.repository.ProjectRepository
import fr.app.pling.model.repository.UserRepository

/**
 * ProjectViewModelFactory
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProjectViewModelFactory(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val notifRepository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProjectViewModel(projectRepository, userRepository, notifRepository) as T
    }
}
