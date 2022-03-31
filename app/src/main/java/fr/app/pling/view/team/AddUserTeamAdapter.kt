package fr.app.pling.view.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.R
import fr.app.pling.databinding.FragmentTeamBinding
import fr.app.pling.model.User

/**
 * AddUserTeamAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class AddUserTeamAdapter(
    private val alreadyAddUser: MutableList<String>,
    private val list : List<User>,
private val listener: ((user: User, removed: Boolean) -> Unit))
:
    RecyclerView.Adapter<AddUserTeamAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentTeamBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: FragmentTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var removed = false

        fun bind(user: User) {
           binding.item = user

            /** * Check if a user has already been added */
            if (alreadyAddUser.isNotEmpty()) {

                /** If the user exist you can remove it */
                if (alreadyAddUser.contains(user.userId)) {
                    binding.addUser.setBackgroundResource(R.drawable.ic_delete)
                    this.removed = true
                }
            } else {

                /** Otherwise, you can add it */
                binding.addUser.setBackgroundResource(R.drawable.ic_save)
                this.removed = false
            }

            binding.cardProjectItem.setOnClickListener {
                val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_save)

                if(drawable == binding.addUser.background){
                    binding.addUser.setBackgroundResource(R.drawable.ic_delete)
                }
                else {
                    binding.addUser.setBackgroundResource(R.drawable.ic_save)
                }

               listener.invoke(user, this.removed)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.list[position])
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

}