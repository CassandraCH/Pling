package fr.app.pling.view.task

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentEditTaskBinding
import fr.app.pling.model.User
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.util.convertToDates
import fr.app.pling.util.getCalendarDay
import fr.app.pling.util.getStateString
import fr.app.pling.util.removeTime
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.project.ProjectViewModel
import java.util.*

/**
 * EditTaskFragement
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class EditTaskFragment : Fragment(), IEventListener{

    private lateinit var b: FragmentEditTaskBinding
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory
        )[ProjectViewModel::class.java]
    }
    private val args by navArgs<EditTaskFragmentArgs>()

    private var userList: List<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentEditTaskBinding.inflate(layoutInflater)

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar: Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar, getString(R.string.editTask), true)
        setHasOptionsMenu(true)

        b.vm = viewModel
        b.item = args.task
        viewModel.eventListener = this

        viewModel.getAllUsersProject(args.task.projectId)

        viewModel._userProjects.observe(viewLifecycleOwner) { it ->
            this.userList = it
            // create the content of the spinner
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                it!!
            )

            val userId: String = args.task.affectedAt

            // search the user in the list
            val element = it.find { user ->
                user.userId == userId
            }

            // search the position of the element in the adapter
            val pos = adapter.getPosition(element)
            b.managerSpinner.adapter = adapter

            // select the element
            if (pos != null) {
                b.managerSpinner.setSelection(pos)
            }
        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.state_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // display the state with the correct string
            val searchText = when (args.task.state) {
                "DONE" -> getString(R.string.done_state)
                "IN PROGRESS" -> getString(R.string.state_progress)
                else -> getString(R.string.done_state)
            }
            val pos = adapter.getPosition(searchText)

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            b.taskSpinner.adapter = adapter
            b.taskSpinner.setSelection(pos)
        }

        val startDay = getCalendarDay(args.task.startDate!!)
        val endDay = getCalendarDay(args.task.endDate!!)
        b.calendarView.selectRange(startDay, endDay)
        b.calendarView.currentDate = startDay

        b.edtTaskTitle.setText(args.task.title)
        b.edtTaskDescription.setText(args.task.description)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_toolbar_task, menu)
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_task -> {

                b.item?.state = requireContext().getStateString(b.taskSpinner.selectedItem.toString())
                val user = this.userList?.get(b.managerSpinner.selectedItemPosition)

                // Affect the task at the user selected
                b.item?.affectedAt = user?.userId!!

                // Convert all the selected date
                // By default the org.threeten.bp library use LocalDate as a format
                // We must convert it to being able to work properly with it
                val datesSelected: List<Calendar> = convertToDates(b.calendarView.selectedDates)

                b.item?.startDate = Timestamp(removeTime(datesSelected.first().time))
                b.item?.endDate = Timestamp(removeTime(datesSelected.last().time))

                // Remove all the previous available dates
                b.item?.availableDays?.clear()

                b.item?.title = b.edtTaskTitle.text.toString()

                b.item?.description = b.edtTaskDescription.text.toString()

                b.item?.searchTitle = b.edtTaskTitle.text.toString().lowercase()

                // Transform all the date in timestamp
                b.item?.availableDays?.addAll(datesSelected.map { Timestamp(removeTime(it.time)) })

                // Update the task
                viewModel.updateTask(b.item!!)

                true
            }
            R.id.delete_task -> {

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_task))
                    .setMessage(getString(R.string.delete_task_confirmation))
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.yes)
                    ) { _, _ ->
                        viewModel.deleteTask(b.item!!)

                    }
                    .setNegativeButton(
                        getString(R.string.no)
                    ){
                            _,_ ->
                    }
                    .show()
                true
            }
            // back button
            android.R.id.home -> {
                findNavController().navigateUp()
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