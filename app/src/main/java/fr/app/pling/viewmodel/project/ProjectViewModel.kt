package fr.app.pling.viewmodel.project


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.prolificinteractive.materialcalendarview.CalendarDay
import fr.app.pling.R
import fr.app.pling.model.Notification
import fr.app.pling.model.Project
import fr.app.pling.model.Task
import fr.app.pling.model.User
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.model.repository.NotificationRepository
import fr.app.pling.model.repository.ProjectRepository
import fr.app.pling.model.repository.UserRepository
import fr.app.pling.util.NotificationType
import fr.app.pling.util.convertToDates
import fr.app.pling.util.getNotificationString
import fr.app.pling.util.removeTime
import fr.app.pling.viewmodel.profile.NotificationViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*
import kotlin.streams.toList

/**
 * ProjectViewModel
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProjectViewModel(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    lateinit var viewModelNotification: NotificationViewModel

    var title: String? = null
    var description: String? = null
    var dueDate: Date? = null

    val _userProjects = MutableLiveData<List<User>?>()

    val _projects = MutableLiveData<List<Project>?>()

    val _tasks = MutableLiveData<List<Task>?>()

    val _projectTask = MutableLiveData<List<Task>?>()

    val _dayTasks = MutableLiveData<List<Task>?>()

    var eventListener: IEventListener? = null

    private val disposables = CompositeDisposable()

    /**
     * Dispatch the creation of a project
     */
    fun createProject(
        context: Context,
        teams: MutableList<User>?
    ) {

        // Check if the fields aren't empty
        if (title.isNullOrEmpty() || description.isNullOrEmpty()) {
            eventListener?.onFailure(context.getString(R.string.input_empty))
            return
        }

        val disposable = userRepository.user()
            .flatMapCompletable { user ->

                val t = Timestamp(dueDate!!); // Generate a Timestamp
                val id = UUID.randomUUID().toString() // Generate an unique ID

                // Create the data model corresponding to the new project
                val project = Project(
                    id = id,
                    title = title.toString(),
                    description = description.toString(),
                    dueDate = t,
                    searchTitle = title.toString().lowercase()
                )
                // Add the project to the current user
                user.projects.add(project.id!!)

                // Check if some people has been added to the project
                if (teams != null) {

                    // Send a notification to all the users
                    teams.map {
                        viewModelNotification.addNotification(
                            user,
                            it.userId,
                            context.getNotificationString(NotificationType.ADDED_TO_PROJECT)
                        )
                    }

                    // Add the teams to the project
                    project.teams.addAll(teams.map {
                        it.userId!!
                    })
                }

                // Create the project
                projectRepository.newProject(project)
                    .doOnComplete { eventListener?.onSuccess() }
                    .doOnError { eventListener?.onFailure(it.message!!) }

            }
            .subscribe({
                eventListener?.onSuccess()
            },{
                eventListener?.onFailure(it.message!!)
            })

        disposables.add(disposable)
    }

    /**
     * Update a project
     */
    fun updateProject(project: Project, userList: MutableList<User>?) {

        if (project.teams != null) {
            project.teams.clear()
            project.teams.addAll(userList!!.map {
                it.userId
            })
        }

        project.description = this.description.toString()
        project.dueDate = Timestamp(removeTime(dueDate!!))

      val dis =  projectRepository.updateProject(project)
           .subscribe({
               eventListener?.onSuccess()
           }, {
               eventListener?.onFailure(it.message.toString())
           })

        disposables.add(dis)

    }

    /**
     * Remove a task
     */
    fun deleteTask(task: Task) {

        projectRepository.deleteTask(task).subscribe({
            eventListener?.onSuccess()
        }, {
            eventListener?.onFailure(it.message!!)
        })

    }

    /**
     * Get all the projects user's
     */
    fun getUserProject() {
        val userId = userRepository.currentUser()?.uid
        projectRepository.getUserProjects(userId!!).addSnapshotListener { value, _ ->

            val result = value?.toObjects(Project::class.java)

            if (value != null) {
                this._projects.postValue(result)
            } else {
                this._projects.postValue(null)
            }
        }
    }

    /**
     * Get all the tasks filtered by their state
     * Todo,In progress, Done
     */
    fun getByFilter(
        context: Context,
        projectId: String,
        name: String
    ) {

        val filterName = when (name) {
            context.getString(R.string.todo_state) -> "TODO"
            context.getString(R.string.state_progress) -> "IN PROGRESS"
            context.getString(R.string.completed_state) -> "DONE"
            else -> ""
        }

        projectRepository.getTaskFiltered(projectId, filterName).addSnapshotListener { value, _ ->
            val result = value?.toObjects(Task::class.java)
            if (value != null) {
                this._projectTask.postValue(result)
            } else {
                this._projectTask.postValue(null)
            }
        }
    }

    fun getTaskProject(projectId: String) {
        projectRepository.getTaskProject(projectId).addSnapshotListener { value, _ ->
            if (value !== null) {
                val result = value.toObjects(Task::class.java)

                _projectTask.postValue(result)
            } else {

                _projectTask.postValue(null)
            }

        }
    }

    // Create a task
    fun createTask(
        context: Context,
        project: Project,
        task: Task,
        selectedDates: List<CalendarDay>
    ) {

        val datesSelected: List<Calendar> = convertToDates(selectedDates)

        if (title.isNullOrEmpty() || description.isNullOrEmpty()) {
            eventListener?.onFailure(context.getString(R.string.input_empty))
            return
        }

        task.id = UUID.randomUUID().toString()
        task.projectId = project.id!!
        task.projectName = project.title
        task.title = title!!
        task.searchTitle = title!!.lowercase()
        task.description = description!!
        task.startDate = Timestamp(removeTime(datesSelected.first().time))
        task.endDate = Timestamp(removeTime(datesSelected.last().time))

        // Remove all the previous available dates
        task.availableDays.clear()

        // Transform all the date in timestamp
        task.availableDays.addAll(datesSelected.map { Timestamp(removeTime(it.time)) })

        val disp = projectRepository.saveTask(task)
            .subscribe({
                eventListener?.onSuccess()
            }, {
                eventListener?.onFailure(it.message!!)
            })

        disposables.add(disp)
    }

    // Update a task
    fun updateTask(task: Task) {

        userRepository.user().flatMapCompletable { user ->

            // Send an notification
            val notif = Notification(
                id = UUID.randomUUID().toString(),
                senderName = user.username,
                userId = task.affectedAt,
                isRead = false,
                sendAt = Timestamp.now(),
                notificationType = NotificationType.ADDED_TO_TASK.ordinal,
                itemName = task.title
            )

            projectRepository.updateTask(task)
                .andThen(
                    notificationRepository.addNotifications(notif)
                )
                .doOnComplete {
                    eventListener?.onSuccess()
                }

        }.subscribe({
            eventListener?.onSuccess()
        }, {

        })


    }

    /**
     * Get all the tasks
     */
    fun getAllTasks() {
        projectRepository._allTasks.addSnapshotListener { value, _ ->

            if (value !== null) {

                val result = value.toObjects(Task::class.java)
                _tasks.value = result

            } else {

                _tasks.value = null
            }

        }
    }

    fun getAllUsersProject(projectId: String) {

        userRepository.getUsersInProject(projectId).addSnapshotListener { value, _ ->
            if (value !== null) {
                val resultList = value.toObjects(User::class.java)

                _userProjects.postValue(resultList)
            } else {
                _userProjects.postValue(null)
            }

        }
    }

    /**
       Return all the task planning for today
     */
    fun getTaskForDay(date: Date) {

        val userId = userRepository.currentUser()?.uid
        val currentDate = Timestamp(removeTime(date))

        projectRepository._allTasks.addSnapshotListener { value, _ ->

            if (value !== null) {

                val resultList: List<Task> = value.toObjects(Task::class.java)
                val filterResult = resultList.stream() // Filter the result to keep only the task assigned to the user
                    .filter { it.availableDays.contains(currentDate) } // Keep only the task planner for today
                    .filter { it.affectedAt == userId }
                    .toList()

                _dayTasks.postValue(filterResult)

            } else {
                _dayTasks.postValue(null)
            }
        }
    }

    /**
     * @param list
     */
    fun getUsersContainsIn(list: List<String>) {

        this.userRepository.test(list).addSnapshotListener { value, _ ->

            if (value !== null) {

                val resultList: List<User> = value.toObjects(User::class.java)

                _userProjects.postValue(resultList)

            } else {
                _userProjects.postValue(null)
            }
        }

    }

    /**
     * Clear all the dat
     */
    override fun onCleared() {
        super.onCleared()
        // Clear all the disposables resources ( observers, and so on.. )
        disposables.dispose()
    }
}