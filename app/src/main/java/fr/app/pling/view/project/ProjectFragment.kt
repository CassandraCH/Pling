package fr.app.pling.view.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentNewProjectBinding
import fr.app.pling.model.Project
import fr.app.pling.model.User
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.util.convertToDate
import fr.app.pling.util.getCalendarDay
import fr.app.pling.view.MainActivity
import fr.app.pling.view.team.ListUserTeamAdapter
import fr.app.pling.viewmodel.profile.NotificationViewModel
import fr.app.pling.viewmodel.project.ProjectViewModel

/**
 * ProjectFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProjectFragment : Fragment() ,  IEventListener{

    private  var userTeam: MutableList<User> = mutableListOf()

    private  lateinit var b: FragmentNewProjectBinding

    private val args by navArgs<ProjectFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory)[ProjectViewModel::class.java]
    }

    private val vm by lazy {
        ViewModelProvider(
            this,
            MyApplication.notificationViewModelFactory)[NotificationViewModel::class.java]
    }

    private lateinit var listUserTeamAdapter: ListUserTeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding
        b = FragmentNewProjectBinding.inflate(layoutInflater)
        b.vm = viewModel

        viewModel.viewModelNotification = vm
        viewModel.eventListener = this

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar : Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar,getString(R.string.newProjectPage_title), true)
        setHasOptionsMenu(true)

        // check the project in the parameters
        if (args.project !== null){

            val project = args.project
            val newList = project?.teams

            // get all project's users
            viewModel.getUsersContainsIn(newList!!.toList())

            viewModel.title = project?.title.toString()
            viewModel.description = project?.description.toString()

            // initialize the due date
            if(project?.dueDate != null){
                // convert the timestamp into calendar day
                val currentDay = getCalendarDay(project.dueDate!!)
                b.calendarView.selectedDate = currentDay
                b.calendarView.currentDate = currentDay
            }
        }

        viewModel._userProjects.observe(viewLifecycleOwner){ list ->
            this.userTeam.addAll(list!!.toList())

            listUserTeamAdapter = ListUserTeamAdapter(this.userTeam.toHashSet())
            b.rvUserList.adapter = listUserTeamAdapter
        }

        b.addUser.setOnClickListener {
            val project = Project(
                title = b.edtProjectTitle.text.toString(),
                description = b.edtProjectDescription.text.toString()
            )

            // convert the date to the correct format
            val date = b.calendarView.currentDate?.let { convertToDate(it) }?.time
            // convert into timestamp for Firebase
            project.dueDate = date?.let { Timestamp(it) }

            // add all the user's id to the project
            args.project?.teams?.forEach { userId ->
                project.teams.add(userId)
            }

            val action = ProjectFragmentDirections.newProjectToShare(project)
            it.findNavController().navigate(action)
        }

        b.btnCreateProject.setOnClickListener {
            viewModel.dueDate = b.calendarView.selectedDate?.let { it1 -> convertToDate(it1).time }
            viewModel.createProject(requireContext(),this.userTeam)
        }
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // back button
            android.R.id.home -> {
                Navigation.findNavController(requireView()).navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSuccess() {
       findNavController().popBackStack(R.id.navigation_home, false)
    }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}