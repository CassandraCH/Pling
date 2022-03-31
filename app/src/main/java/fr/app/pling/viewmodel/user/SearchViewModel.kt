package fr.app.pling.viewmodel.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.app.pling.model.Project
import fr.app.pling.model.Task
import fr.app.pling.model.User
import fr.app.pling.model.repository.ProjectRepository
import fr.app.pling.model.repository.UserRepository

/**
 * Viewmodel related to the research
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class SearchViewModel(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var search: String = ""

    val _userList = MutableLiveData<List<User>?>()

    val _tasksList = MutableLiveData<List<Task>?>()

    val _projectList = MutableLiveData<List<Project>?>()

    /**
     * Search the tasks
     */
    fun searchTasks() {
        projectRepository.searchTasks(search).addSnapshotListener { value, _ ->

            if (value != null) {
                val result = value.toObjects(Task::class.java)

                this._tasksList.postValue(result)
            } else {
                this._tasksList.postValue(null)
            }

        }
    }

    /**
     * Search projects
     */
    fun searchProjects() {
        projectRepository.searchProjects(search).addSnapshotListener { value, _ ->

            if (value != null) {
                val result = value.toObjects(Project::class.java)

                this._projectList.postValue(result)
            } else {
                this._projectList.postValue(null)
            }

        }
    }

    /**
     * Search users
     */
    fun searchUsers() {
        userRepository.searchUsers(search).addSnapshotListener { value, _ ->

            if (value != null) {
                val result = value.toObjects(User::class.java)

                this._userList.postValue(result)
            } else {
                this._userList.postValue(null)
            }

        }
    }
}