package fr.app.pling.view.task

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentTaskDetailsBinding
import fr.app.pling.model.CalendarDate
import fr.app.pling.model.User
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.project.ProjectViewModel
import java.util.*

/**
 * TaskDetailsFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class TaskDetailsFragment : Fragment() , IEventListener{

    private  lateinit var b: FragmentTaskDetailsBinding
    private val args by navArgs<TaskDetailsFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory)[ProjectViewModel::class.java]
    }

    private  var userList: List<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding
        b = FragmentTaskDetailsBinding.inflate(layoutInflater)
        b.item = args.task
        viewModel.eventListener = this

        b.edtTaskDescription.setText(args.task.description)
        b.edtTaskTitle.setText(args.task.title)

        val mainActivity = activity as MainActivity
        val toolbar : Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar,getString(R.string.taskDetailsPage_title), true)
        setHasOptionsMenu(true)

        viewModel.getAllUsersProject(args.task.projectId)

        return  b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startDate = getTextFormat(args.task.startDate?.toDate()!!)
        val endDate = getTextFormat(args.task.endDate?.toDate()!!)

        b.textTask.text = getString(R.string.task_details_date, startDate, endDate)

        viewModel.eventListener = this

        // observe the changes of the team
        viewModel._userProjects.observe(viewLifecycleOwner) { it ->
            this.userList = it?.sortedBy { it.searchname }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, this.userList!!)

            val el: String = args.task.affectedAt
            val element = it!!.find {  it.userId == el  }

            b.managerSpinner.isEnabled = false
            b.managerSpinner.adapter = adapter

            val pos = adapter.getPosition(element)

            if(pos != -1){
                b.managerSpinner.setSelection(pos)
            }
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.state_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            val searchText = when(args.task.state) {
                "DONE" -> getString(R.string.done_state)
                "IN PROGRESS" -> getString(R.string.state_progress)
                else -> getString(R.string.done_state)
            }
            val pos = adapter.getPosition(searchText)

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            b.taskSpinner.isEnabled = false
            b.taskSpinner.adapter = adapter
            b.taskSpinner.setSelection(pos)
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
            R.id.edit_task -> {
                val action = TaskDetailsFragmentDirections.taskDetailToEditTask(args.task)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getTextFormat(date: Date): String {
        val startCal = CalendarDate(date!!)
         return startCal.calendar
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_task, menu)
    }


    override fun onSuccess() {
    }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}