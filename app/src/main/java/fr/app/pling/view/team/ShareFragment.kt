package fr.app.pling.view.team

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import fr.app.pling.MyApplication
import fr.app.pling.databinding.FragmentShareBinding
import fr.app.pling.viewmodel.user.SearchViewModel

/**
 * ShareFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class ShareFragment : Fragment() {

    private lateinit var b: FragmentShareBinding

    private var listAddUser = mutableListOf<String>()

    private val args by navArgs<ShareFragmentArgs>()

    private val vmSearch by lazy { ViewModelProvider(this, MyApplication.searchViewModelFactory)[SearchViewModel::class.java] }

    private var addUserTeamAdapter: AddUserTeamAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        b = FragmentShareBinding.inflate(layoutInflater)
        b.vm = vmSearch

        b.textInputLayout.setEndIconOnClickListener { it ->
            args.project?.teams = listAddUser

            // handle logic depending if we are in edit or new project
            if(args.isEdit){
                val action = ShareFragmentDirections.shareToEdit(args.project!!)
                it.findNavController().navigate(action)
            }
            else {
                val action = ShareFragmentDirections.shareToProject().setProject(args.project)
                it.findNavController().navigate(action)
            }
        }
        if(args.project?.teams?.isNotEmpty() == true){
            val userlist = args.project?.teams
            val newList = mutableListOf<String>()

            userlist?.forEach { newList.add(it) }

            this.listAddUser.addAll(newList)
        }

        vmSearch._userList.observe(viewLifecycleOwner) {

            if(it?.isNotEmpty() == true){

                addUserTeamAdapter = AddUserTeamAdapter(this.listAddUser, it) { user, removed ->
                    if (!removed) {
                        listAddUser.add(user.userId)
                    } else {
                        listAddUser.remove(user.userId)
                    }
                }

                b.list.adapter = addUserTeamAdapter
            }
        }

        b.searchView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(string: Editable?) {

                if (!string.isNullOrEmpty()) {
                    vmSearch.searchUsers()
                }
            }
        })

        b.btnBack.setOnClickListener {
            //vmSearch
            it.findNavController().navigateUp()
        }
        return b.root
    }
}