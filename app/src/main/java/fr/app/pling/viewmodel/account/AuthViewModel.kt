package fr.app.pling.viewmodel.account

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseUser
import fr.app.pling.R
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.model.repository.UserRepository
import fr.app.pling.view.account.LoginFragmentDirections
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * AuthViewModel
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    var username: String? = null
    var email: String? = "cass@gmail.com"
    var password: String? = "casscass"

    var newEmail: String? = null
    var passwordCheck: String? = null
    var newPassword: String? = null
    var newPasswordValid: String? = null

    /**
     * Authentication listener
     */
    var eventListener: IEventListener? = null

    val userData: MutableLiveData<FirebaseUser> by lazy { repository.userLiveData() }

    /**
     * Get the current user
     */
    val user by lazy { repository.currentUser() }

    val currentUser by lazy {  repository.user() }

    private val disposables = CompositeDisposable()

    /**
     * Perform the login
     */
    fun login() {

        //validating email and password
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            eventListener?.onFailure(R.string.login_error.toString())
            return
        }

        //calling login from repository to perform the actual authentication
        val disposable = repository.login(email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                eventListener?.onSuccess()
            }, {
                //sending a failure callback
                eventListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    /** Perform the registration **/
    fun signup() {

        if (username.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()) {
            eventListener?.onFailure(R.string.signup_error.toString())

            return
        }

        //calling login from repository to perform the actual authentication
        val disposable = repository.register(username!!, email!!, password!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                eventListener?.onSuccess()
            }, {
                //sending a failure callback
                eventListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun goToSignup(view: View) {
        view.findNavController().navigate(LoginFragmentDirections.loginToSignup())
    }

    fun updateEmail() {
        if (newEmail.isNullOrEmpty() || passwordCheck.isNullOrEmpty()) {
            eventListener?.onFailure("All fields are required")
            return
        }

        //calling updateEmail from repository
        val disposable = repository.updateEmail(newEmail!!, passwordCheck!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                eventListener?.onSuccess()
            }, {
                //sending a failure callback
                eventListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    /**
     * Update password
     */
    fun updatePassword() {
        if (passwordCheck.isNullOrEmpty() || newPassword.isNullOrEmpty() || newPasswordValid.isNullOrEmpty()) {
            eventListener?.onFailure("All fields are required")
            return
        }

        if (passwordCheck == newPassword) {
            eventListener?.onFailure("New password should be different from the old one")
            return
        }

        if (newPassword != newPasswordValid) {
            eventListener?.onFailure("Passwords don't match")
            return
        }

        //calling updatePassword from repository
        val disposable = repository.updatePassword(newPassword!!, passwordCheck!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                eventListener?.onSuccess()
            }, {
                //sending a failure callback
                eventListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    /**
     * Delete an accocunt
     */
    fun deleteAccount() {
        if (passwordCheck.isNullOrEmpty()) {
            eventListener?.onFailure("Password is required")
            return
        }


        //calling deleteAccount from repository
        val disposable = repository.deleteAccount(passwordCheck!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //sending a success callback
                eventListener?.onSuccess()
            }, {
                //sending a failure callback
                eventListener?.onFailure(it.message!!)
            })
        disposables.add(disposable)
    }

    fun logout() {
        repository.logout()
    }

    /**
     * Disposes all the allocated resources
     */
    override fun onCleared() {
        super.onCleared()
        // Clear all the data
        disposables.dispose()
    }
}