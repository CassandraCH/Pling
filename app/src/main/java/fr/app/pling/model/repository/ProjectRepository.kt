package fr.app.pling.model.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fr.app.pling.model.Project
import fr.app.pling.model.Task
import fr.app.pling.util.COLLECTION_PROJECT
import fr.app.pling.util.COLLECTION_TASK
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable

/**
 * ProjectRepository
 *
 *  Provides communication with Firebase for operations related to the user.
 *
 */
class ProjectRepository {


    private val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val projectRef by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_PROJECT)
    }

    private val taskRef: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_TASK)
    }

    val _allProjects by lazy {
        projectRef.orderBy("title")
    }

    val _allTasks by lazy {
        FirebaseFirestore.getInstance().collectionGroup("tasksAffected")
    }

    /**
     * Create a new project
     */
    fun newProject(project: Project): Completable {
        return Completable.create { emitter ->
            if (!emitter.isDisposed) {
                projectRef
                    .document(project.id!!)
                    .set(project)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }

    /**
     * Update an existing project
     */
    fun updateProject(project: Project): Completable {
        return Completable.create { emitter ->
            if (!emitter.isDisposed) {
                projectRef
                    .document(project.id!!)
                    .set(project)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(Throwable("Error")) }
            }
        }
    }

    /**
     * Delete project
     */
    fun deleteProject(id: String): Completable =
        Completable.create { emitter ->
            if (!emitter.isDisposed) {
                projectRef.document(id)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onComplete()
                        } else
                            emitter.onError(Throwable("Error"))
                    }
            }
        }

    /**
     * Used to search some projects
     */
    fun searchProjects(searchText: String): Query {
        return projectRef.orderBy("searchTitle")
                            .startAt(searchText.trim().lowercase())
                            .endAt(searchText.trim().lowercase() + "\uf8ff")
    }

    /**
     * Search some tasks
     */
    fun searchTasks(searchText: String): Query {
        return FirebaseFirestore.getInstance()
            .collectionGroup("tasksAffected")
            .startAt(searchText.trim().lowercase())
            .endAt(searchText.trim().lowercase() + "\uf8ff")
    }

    /**
     * Save a new task
     */
    fun saveTask(task: Task): Completable {
        return Completable.create { emitter ->
            if (!emitter.isDisposed) {

                projectRef
                    .document(task.projectId)
                    .collection("tasksAffected")
                    .document(task.id!!)
                    .set(task)
                    .addOnSuccessListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }

    /**
    *  Return the task affected at the specified project
    **/
    fun getTaskProject(projectId: String): Query {
        return projectRef.document(projectId)
                        .collection("tasksAffected")
    }

    /**
     * Delete a task
     */
    fun deleteTask(task: Task): Completable {
        return Completable.create { emitter ->
            projectRef.document(task.projectId)
                .collection("tasksAffected")
                .document(task.id)
                .delete()
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
    }

    fun getUserProjects(userId: String) : Query {
        return projectRef.whereArrayContains("teams", userId)
    }
    /**
     * Update a task
     */
    fun updateTask(task: Task): @NonNull Completable {
        return Completable.create { emitter ->
            if (!emitter.isDisposed) {
                projectRef.document(task.projectId)
                    .collection("tasksAffected")
                    .document(task.id)
                    .set(task)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
            }
        }
    }

    /**
     * Return the task filtered by their state or all the tasks affected
     */
    fun getTaskFiltered(projectId: String, filterName: String): Query {
        val query = projectRef.document(projectId).collection("tasksAffected")

        if(filterName.isNotEmpty()){
            return  query.whereEqualTo("state", filterName)
        }

        return query
    }

}