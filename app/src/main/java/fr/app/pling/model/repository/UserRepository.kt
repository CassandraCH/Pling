package fr.app.pling.model.repository

import androidx.lifecycle.MutableLiveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fr.app.pling.model.User
import fr.app.pling.util.COLLECTION_USER
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable


/**
 * UserRepository
 *
 *  Provides communication with Firebase for operations related to the user.
 *
 */
class UserRepository() {

    val TAG = "USER_REPOSITORY"

    private val firebaseUserLD: MutableLiveData<FirebaseUser> by lazy { MutableLiveData() }

    fun userLiveData() = firebaseUserLD

    /**
     * Get the FirebaseAuth Instance
     */
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    /**
     * Get the FirebaseFirestore Instance
     */
    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val userRef : CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection(COLLECTION_USER)
    }

    /**
     * Login a user
     */
    fun login(email: String, password: String): Completable = Completable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    firebaseUserLD.postValue(firebaseAuth.currentUser)
                    emitter.onComplete()
                } else {
                    emitter.onError(it.exception!!)
                }
            }
        }

    }

    /**
     * Register a user
     */
    fun register(userName: String, email: String, password: String): Completable =
        Completable.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        firebaseUserLD.postValue(firebaseAuth.currentUser)
                        /** If the user has been successfully registered, we add a new User to the User collection of FirebaseStore. **/
                        val fbUser: FirebaseUser? = it.result?.user
                        val user = User(fbUser!!.uid, userName, email, userName.lowercase() )
                        firebaseFirestore.collection(COLLECTION_USER)
                            .document(fbUser.uid)
                            .set(user)
                            .addOnSuccessListener { emitter.onComplete() }
                            .addOnFailureListener { emitter.onError(it) }
                    } else {
                        emitter.onError(it.exception!!)
                    }
                }
        }

    /**
     * Get the current User
     */
    fun currentUser() = firebaseAuth.currentUser

    /**
     * Update email
     */
    fun updateEmail(newEmail: String, password: String): Completable {
        val credential =
            EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email.toString(), password)

        return Completable.create { emitter ->
            // requires that the user has recently logged in => re-authentication
            firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    // update email
                    firebaseAuth.currentUser!!.updateEmail(newEmail)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                emitter.onComplete()
                            } else {
                                emitter.onError(it.exception!!)
                            }
                        }
                } else {
                    emitter.onError(it.exception!!)
                }
            }
        }
    }

    /**
     * Reset password
     */
    fun updatePassword(newPassword: String, password: String): Completable {

        // In order to being able to update the password, the user need to be re-authenticated
        val credential = EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email.toString(), password)

        return Completable.create { emitter ->
            // requires that the user has recently logged in => re-authentication
            firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    // update password
                    firebaseAuth.currentUser!!.updatePassword(newPassword)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                emitter.onComplete()
                            } else {
                                emitter.onError(it.exception!!)
                            }
                        }
                } else {
                    emitter.onError(it.exception!!)
                }
            }
        }
    }

    /**
     * Disconnect the current user
     */
    fun logout() {
        firebaseAuth.signOut()
        firebaseUserLD.postValue(null)
    }

    /**
     *  Delete a user
     */
    fun deleteAccount(password: String): Completable {
        val credential =
            EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email.toString(), password)

        return Completable.create { emitter ->
            firebaseFirestore.collection(COLLECTION_USER)
                .document(firebaseAuth.currentUser!!.uid).delete().addOnCompleteListener {
                    if (it.isSuccessful) {
                        // requires that the user has recently logged in => re-authentication
                        firebaseAuth.currentUser!!.reauthenticate(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    firebaseAuth.currentUser!!.delete()
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                emitter.onComplete()
                                            } else {
                                                emitter.onError(it.exception!!)
                                            }
                                        }
                                } else {
                                    emitter.onError(it.exception!!)
                                }
                            }
                    } else {
                        emitter.onError(it.exception!!)
                    }
                }
        }
    }

    /**
     * Get the user profile in firestore
     */
    fun user(): Observable<User> {
        return Observable.create { emitter ->
            val user = currentUser()
            if (user === null) {
                emitter.onError(Throwable("You're not signed in"))
            } else {
                FirebaseFirestore.getInstance().collection(COLLECTION_USER)
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val result = snapshot.toObject(User::class.java)
                        if (result != null) {
                            emitter.onNext(result)
                        } else {
                            emitter.onError(Throwable("Failed to get user"))
                        }
                    }
            }
        }
    }

    /**
     * Used to search the user
     */
    fun searchUsers(search: String): Query {
        val query = userRef.orderBy("searchname")

        if(search.isNotEmpty()){
           return query.startAt(search.trim().lowercase())
                .endAt(search.trim().lowercase() + "\uf8ff")
        }

        return  query
    }

    /**
     * Returns all the users that are part of a project
     */
    fun getUsersInProject(projectId: String): Query {
        return  userRef
              .whereArrayContains("projects", projectId)

    }

    fun test(project: List<String>): Query {
        return userRef
               .whereIn("userId", project)
    }

    /**
     * Return all the users
     */
    fun getAllUsers(): Query {
        return userRef.orderBy("username")
    }

}