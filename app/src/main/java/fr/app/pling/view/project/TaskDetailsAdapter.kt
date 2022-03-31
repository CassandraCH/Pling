package fr.app.pling.view.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.R
import fr.app.pling.databinding.CardTaskProjectDetailsBinding
import fr.app.pling.model.Task
import fr.app.pling.viewmodel.project.ProjectViewModel

/**
 * TaskDetailsAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class TaskDetailsAdapter(
    private val vm: ProjectViewModel,
    private val list : List<Task>
) :  RecyclerView.Adapter<TaskDetailsAdapter.TaskViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardTaskProjectDetailsBinding.inflate(layoutInflater, parent, false)

        return TaskViewHolder(vm,binding)
    }

    inner class TaskViewHolder(private val vm : ProjectViewModel, private val b: CardTaskProjectDetailsBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(task: Task) {
            b.item = task

            if(task.description.isEmpty()){
                b.tvDescriptionProjectCard.visibility = View.GONE
            }

            when (task.state) {
                "DONE" -> {
                    b.tvState.text = itemView.context.getString(R.string.done_state)
                }
                "IN PROGRESS" -> {
                    b.tvState.text = itemView.context.getString(R.string.state_progress)
                }
                else -> {
                    b.tvState.text = itemView.context.getString(R.string.todo_state)
                }
            }

            b.root.setOnClickListener {
                // Navigate to the Task Details
                val action = ProjectDetailsFragmentDirections.projectToTaskDetails(task)
                it.findNavController().navigate(action)
            }

            b.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
       return list.size
    }
}