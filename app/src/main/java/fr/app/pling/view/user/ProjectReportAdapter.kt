package fr.app.pling.view.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.R
import fr.app.pling.databinding.CardProjectReportBinding
import fr.app.pling.model.Project

/**
 * ProjectReportAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ProjectReportAdapter(private val list: List<Project>, var listener: (itemSelect : Int) -> Unit ) :
    RecyclerView.Adapter<ProjectReportAdapter.ProjectViewHolder>()
     {

    private var selectedItemPos = 0

    inner class ProjectViewHolder(private val b: CardProjectReportBinding) :
        RecyclerView.ViewHolder(b.root) {
            fun bind(project: Project) {
                b.item = project

                b.root.setOnClickListener {
                    selectTaskListItem(bindingAdapterPosition)
                    listener.invoke(bindingAdapterPosition)
                }

                b.executePendingBindings()
        }

        private fun selectTaskListItem(pos: Int) {
            val previousItem: Int = selectedItemPos
            selectedItemPos = pos
            notifyItemChanged(previousItem)
            notifyItemChanged(pos)
        }

        fun defaultBg() {
            itemView.setBackgroundResource(R.drawable.card_white_rounded)
            b.tvTitleProjectCard.setTextColor( ContextCompat.getColor(
                itemView.context,
                R.color.brown_800
            ))
        }

        fun selectedBg() {
            itemView.setBackgroundResource(R.drawable.card_purple_rounded)
            b.tvTitleProjectCard.setTextColor( ContextCompat.getColor(
                itemView.context,
                R.color.white
            ))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardProjectReportBinding.inflate(layoutInflater, parent, false)
        return ProjectViewHolder(binding)
    }

     override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {

         if(position == selectedItemPos)
             holder.selectedBg()
         else
             holder.defaultBg()

         holder.bind(list[position])
     }

     override fun getItemCount(): Int {
        return list.size
     }
 }
