package fr.app.pling.view.project

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentProjetDetailsBinding
import fr.app.pling.model.CalendarDate
import fr.app.pling.model.User
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.util.MyItemDecoration
import fr.app.pling.view.MainActivity
import fr.app.pling.view.team.ListUserTeamAdapter
import fr.app.pling.viewmodel.project.ProjectViewModel
import java.util.*

/**
 * ProjectDetailsFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProjectDetailsFragment : Fragment(),  IEventListener {

    private lateinit var b: FragmentProjetDetailsBinding

    private val args by navArgs<ProjectDetailsFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory)[ProjectViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentProjetDetailsBinding.inflate(layoutInflater)

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar: Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar, getString(R.string.projectDetailsPage_title), true)
        setHasOptionsMenu(true)

        viewModel.getByFilter(requireContext(),args.project.id!!, "ALL")

        viewModel._projectTask.observe(viewLifecycleOwner){
            b.rvTaskList.adapter = TaskDetailsAdapter(viewModel, it!!)
        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.eventListener = this

        val filterList = mutableListOf(getString(R.string.all_state),
            getString(R.string.todo_state),
            getString(R.string.state_progress),
            getString(R.string.completed_state))

        b.rvFilterList.adapter = TaskFilterAdapter(filterList) {
            viewModel.getByFilter(requireContext(), args.project.id!!, it)
        }

        val date = args.project.dueDate?.toDate()

        b.item = args.project

        /* Logic to display the team members */
        val userList = args.project.teams.map {
            User(username = it)
        }.toMutableList()

        b.rvUserList.addItemDecoration(MyItemDecoration())
        b.rvUserList.adapter = ListUserTeamAdapter(userList.toHashSet())

        // mask date if there are a lot of data
        b.rvTaskList.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
            if (oldScrollY < 0) b.btnNewProject.hide() else b.btnNewProject.show()
        }

        b.btnNewProject.setOnClickListener {
            val action = ProjectDetailsFragmentDirections.projectDetailsToNewTask(args.project)
            it.findNavController().navigate(action)
        }

        // display only 2 users
        if (userList.size > 2) {
            b.userCount.text = resources.getString(R.string.user_count, userList.size - 2)
        } else {
            b.userCount.visibility = View.GONE
        }

        /**
         * Logic to handle all the transformation of the date
         * Timestamp => Date
         */
        val cal = CalendarDate(date!!)
        b.dueDate.text = cal.calendar
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // back button
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            R.id.edit_task -> {
                val action = ProjectDetailsFragmentDirections.projetToEdit(args.project)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_task, menu)
    }

    override fun onSuccess() { }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}