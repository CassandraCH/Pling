package fr.app.pling.view.task


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.databinding.RowTaskDaysBinding
import fr.app.pling.model.Task

/**
 * TaskAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class TaskAdapter(
    private val list: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowTaskDaysBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolder(binding)
    }

    inner class TaskViewHolder(private val binding: RowTaskDaysBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.item = task

            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}