package fr.app.pling.view.home

import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fr.app.pling.MyApplication
import fr.app.pling.databinding.FragmentHomeBinding
import fr.app.pling.util.NotificationType
import fr.app.pling.util.getNotificationString
import fr.app.pling.view.project.ProjectAdapter
import fr.app.pling.view.task.TaskAdapter
import fr.app.pling.viewmodel.account.AuthViewModel
import fr.app.pling.viewmodel.profile.NotificationViewModel
import fr.app.pling.viewmodel.project.ProjectViewModel
import java.util.*

/**
 * HomeFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class HomeFragment : Fragment() {

    private lateinit var b: FragmentHomeBinding

    // view model for the projects
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory
        )[ProjectViewModel::class.java]
    }

    // view model for the notifications
    private val vm by lazy {
        ViewModelProvider(
            this,
            MyApplication.notificationViewModelFactory
        )[NotificationViewModel::class.java]
    }

    // view model for the authentification
    private val viewModelAuth by lazy {
        ViewModelProvider(
            this,
            MyApplication.authViewModelFactory
        )[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = FragmentHomeBinding.inflate(layoutInflater)

        // retrieve the user's last notification for display
        viewModelAuth.currentUser.subscribe { user ->
            vm.getLastNotification(user.userId)
        }
        // recovery of the day's tasks
        viewModel.getTaskForDay(Calendar.getInstance().time)
        // recovery of user's projects
        viewModel.getUserProject()

        // observation of changes for notifications
        vm.lastNotificationMLD.observe(viewLifecycleOwner) { notif ->
            if (notif !== null) {
                vm.lastNotification = notif

                val notificationMsg = context?.getNotificationString(NotificationType.values()[notif.notificationType])
                val underlineTitle = Html.fromHtml("<b>${notif.senderName}</b> $notificationMsg <u>${notif.itemName}</u>", FROM_HTML_MODE_COMPACT)

                b.text.text = underlineTitle

                b.notificationCardHome.visibility = View.VISIBLE

            } else {
                b.notificationCardHome.visibility = View.GONE
            }
        }

        // observation of changes for projects
        viewModel._projects.observe(viewLifecycleOwner){
            b.rvProjectsList.adapter = ProjectAdapter(it!!)
        }

        // observation of changes for tasks
        viewModel._dayTasks.observe(viewLifecycleOwner){
            b.rvTodayTaskList.adapter = it?.let { it1 -> TaskAdapter(it1) }
        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.vm = vm

        // delete the notification if the user clicks on the cross
        b.btnNotificationRead.setOnClickListener {
            vm.updateNotification()
            b.notificationCardHome.visibility = View.GONE
        }
    }
}