package fr.app.pling.view.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.R
import fr.app.pling.databinding.ItemRowFilterBinding

/**
 * TaskFilterAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class TaskFilterAdapter(
    private val list: MutableList<String>,
    private val listener: (filterName : String) -> Unit
) : RecyclerView.Adapter<TaskFilterAdapter.FilterViewHolder>(){

    private var selectedItemPos = 0

    inner class FilterViewHolder(private val b: ItemRowFilterBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(data: String) {
            b.name = data

            b.root.setOnClickListener {

                selectTaskListItem(bindingAdapterPosition)

                listener.invoke(data)
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
            b.cardFilter.setBackgroundResource(R.drawable.card_white_rounded)
            b.tvFilterName.setTextColor( ContextCompat.getColor(
                itemView.context,
                R.color.brown_800
            ))
        }

        fun selectedBg() {
            b.cardFilter.setBackgroundResource(R.drawable.card_purple_rounded)
            b.tvFilterName.setTextColor( ContextCompat.getColor(
                itemView.context,
                R.color.color_primary
            ))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRowFilterBinding.inflate(layoutInflater, parent, false)
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(parent: FilterViewHolder, position: Int) {
        if(position == selectedItemPos)
            parent.selectedBg()
        else
            parent.defaultBg()

        parent.bind(list[position])
    }

    override fun getItemCount() = list.size
}