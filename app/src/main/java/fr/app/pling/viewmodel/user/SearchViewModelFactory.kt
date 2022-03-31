package fr.app.pling.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.app.pling.model.repository.ProjectRepository
import fr.app.pling.model.repository.UserRepository

/**
 * ProjectViewModelFactory
 * Use to create the ViewModel with the corresponding parameters
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class SearchViewModelFactory(private val repository: ProjectRepository, private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository, userRepository) as T
    }
}
