package fr.app.pling.view.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.databinding.ItemUserTeamBinding


import fr.app.pling.model.User

/**
 * ListUserTeamAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ListUserTeamAdapter(private val dataSet: HashSet<User>) :
    RecyclerView.Adapter<ListUserTeamAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemUserTeamBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user : User){

            if(user.username.isNotEmpty()){
                binding.idUser.text = user.username[0].toString().uppercase()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserTeamBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(parent: ViewHolder, position: Int) {
        parent.bind(dataSet.elementAt(position))
    }

    override fun getItemCount() = dataSet.size
}