package fr.app.pling.view.profile

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentProfileBinding
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.account.AuthViewModel
import fr.app.pling.viewmodel.profile.NotificationViewModel

/**
 * ProfileFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProfileFragment : Fragment(), IEventListener {

    private val viewModelAuth by lazy {
        ViewModelProvider(
            this,
            MyApplication.authViewModelFactory)[AuthViewModel::class.java]
    }

    private lateinit var b : FragmentProfileBinding
    private val viewModelNotification by lazy {
        ViewModelProvider(
            this,
            MyApplication.notificationViewModelFactory)[NotificationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentProfileBinding.inflate(layoutInflater)
        b.viewModelAuth = viewModelAuth
        b.viewModelNotification = viewModelNotification
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get the notification of the current user
        viewModelAuth.currentUser.subscribe {
            val notifBuilder = this.viewModelNotification.getNotifications(it.userId)

            if(notifBuilder !== null) {
                notifBuilder.setLifecycleOwner(viewLifecycleOwner).build().apply {
                    b.rvNotifications.adapter = NotificationAdapter(this)
                }
            }
        }
        val c = context as Activity

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar : Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar,getString(R.string.profilePage_title), true)
        setHasOptionsMenu(true)

        viewModelAuth.eventListener = this

        // delete account
        b.tvDeleteAccount.setOnClickListener{
            findNavController().navigate(ProfileFragmentDirections.profileToDelete())
        }

        // sign out
        b.btnSignOut.setOnClickListener {
            viewModelAuth.logout()
            val c = context as Activity
            findNavController().navigate(R.id.accountActivity)
            c.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_profile, menu)
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // back button
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            // redirect to the edit email fragment
            R.id.edit_email -> {
                findNavController().navigate(R.id.navigation_editEmail)
                true
            }
            // redirect to the edit password fragment
            R.id.edit_password -> {
                findNavController().navigate(R.id.navigation_editPassword)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSuccess() { }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}