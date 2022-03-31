package fr.app.pling.view.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentTaskBinding
import fr.app.pling.model.Task
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.project.ProjectViewModel

/**
 * NewTaskFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class NewTaskFragment : Fragment(), IEventListener{

    private val args by navArgs<NewTaskFragmentArgs>()

    private  lateinit var b: FragmentTaskBinding

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory)[ProjectViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // data binding
        b = FragmentTaskBinding.inflate(layoutInflater)
        b.vm = viewModel

        val project = args.project
        val task = Task()

        b.btnCreateTask.setOnClickListener {
            viewModel.createTask(requireContext(), project, task, b.calendarView.selectedDates)
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.state_array,
            android.R.layout.simple_spinner_item
        )

        viewModel.getAllUsersProject(project.id!!)

        // observe the changes of the user's projects
        viewModel._userProjects.observe(viewLifecycleOwner) { it ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it!!)

            b.managerSpinner.adapter = adapter
        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.eventListener = this

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar : Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar,getString(R.string.newTask_title), true)
        setHasOptionsMenu(true)
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
        Navigation.findNavController(requireView()).navigateUp()
    }

    override fun onFailure(message: String) { }
}