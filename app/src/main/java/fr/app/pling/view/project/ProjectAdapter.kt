package fr.app.pling.view.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.databinding.CardProjectHomeBinding
import fr.app.pling.model.CalendarDate
import fr.app.pling.model.Project
import fr.app.pling.view.home.HomeFragmentDirections

/**
 * ProjectAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProjectAdapter(private val list: List<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>(){

    inner class ProjectViewHolder(private val binding: CardProjectHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.item = project

            val cal = CalendarDate(project.dueDate!!.toDate())
            binding.tvDateProjectCard.text = cal.calendar

            // from the home => go the corresponding project
            binding.root.setOnClickListener {
                val action = HomeFragmentDirections.homeToProject(project)
                it.findNavController().navigate(action)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardProjectHomeBinding.inflate(layoutInflater, parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
