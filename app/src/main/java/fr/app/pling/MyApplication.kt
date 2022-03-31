package fr.app.pling

import android.app.Application
import fr.app.pling.model.repository.NotificationRepository
import fr.app.pling.model.repository.ProjectRepository
import fr.app.pling.model.repository.UserRepository
import fr.app.pling.viewmodel.account.AuthViewModelFactory
import fr.app.pling.viewmodel.profile.NotificationViewModelFactory
import fr.app.pling.viewmodel.project.ProjectViewModelFactory
import fr.app.pling.viewmodel.user.SearchViewModelFactory

/**
 * MyApplication used to inject dependencies
 *
 * Created to avoid using dependency injection librairies
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class MyApplication : Application() {

     companion object {

          private val userRepository = UserRepository()
          private val projectRepository = ProjectRepository()
          private val notificationRepository = NotificationRepository()

          val authViewModelFactory = AuthViewModelFactory(userRepository)

          val notificationViewModelFactory = NotificationViewModelFactory(notificationRepository)

          val projectViewModelFactory = ProjectViewModelFactory(projectRepository, userRepository, notificationRepository)

          val searchViewModelFactory = SearchViewModelFactory(projectRepository, userRepository)
     }
}