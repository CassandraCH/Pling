package fr.app.pling.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentTopBarBinding
import fr.app.pling.viewmodel.account.AuthViewModel

/**
 * TopBarFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class TopBarFragment : Fragment() {

    private lateinit var b : FragmentTopBarBinding
    private val viewModel by lazy {  ViewModelProvider(this, MyApplication.authViewModelFactory)[AuthViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding
        b = FragmentTopBarBinding.inflate(layoutInflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUser.subscribe {
            b.icon = it.username[0].toString()
        }

        // redirection to the profile
        b.icProfile.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }
    }

}