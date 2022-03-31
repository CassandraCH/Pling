package fr.app.pling.view.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentReportBinding
import fr.app.pling.model.Project
import fr.app.pling.model.Task
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.project.ProjectViewModel
import kotlin.math.roundToInt

class ReportFragment : Fragment() {

    private  lateinit var b: FragmentReportBinding

    private val viewModel by lazy {  ViewModelProvider(this, MyApplication.projectViewModelFactory)[ProjectViewModel::class.java] }

    private var projectsList = listOf<Project>()
    private var tasksList = listOf<Task>()

    private var itemSelected = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding
        b = FragmentReportBinding.inflate(layoutInflater)

        // get projects
        viewModel.getUserProject()
        // get tasks
        viewModel.getAllTasks()

        // Observe the data change
        viewModel._projects.observe(viewLifecycleOwner) { projects ->

            b.rvProjectsReport.adapter = ProjectReportAdapter(projects!!) {
                this.itemSelected = it

                viewModel.getTaskProject(projects[this.itemSelected].id!!)
            }

        }

        // recovery tasks
        viewModel._projectTask.observe(viewLifecycleOwner) { tasks ->
            // get the completed tasks number
            val isComplete = { t: Task -> t.state == "DONE" }
            val taskCompleted = tasks?.stream()
                ?.filter { t -> isComplete(t) }
                ?.count()

            // get the rest
            val taskOngoing = tasks?.stream()
                                    ?.filter { t -> !isComplete(t) }
                                    ?.count()

            b.tasksCompleted.text = taskCompleted.toString()
            b.tasksOngoing.text =  taskOngoing.toString()

            if(tasks?.isNotEmpty() == true){

                // calculate the the percentage of completed
                val cal = (taskCompleted?.toDouble()?.div(tasks.size))?.times(100)?.roundToInt()

                if (cal != null) {
                    b.tvPercentage.text = "$cal%"
                    b.circularProgressbar.progress = cal
                }
            }
            else {
                b.tvPercentage.text = "0%"
                b.circularProgressbar.progress = 0
            }

        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set the toolbar
        val mainActivity = activity as MainActivity
        val toolbar: Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar, getString(R.string.reportPage_title), true)
        setHasOptionsMenu(true)
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // back button
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}