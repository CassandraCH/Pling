package fr.app.pling.viewmodel.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.app.pling.model.repository.UserRepository

/**
 * AuthViewModelFactory
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}
