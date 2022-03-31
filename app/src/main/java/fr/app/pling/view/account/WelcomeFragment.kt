package fr.app.pling.view.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.app.pling.databinding.FragmentWelcomeBinding

/**
 * WelcomeFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class WelcomeFragment : Fragment() {

    private lateinit var b: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        b = FragmentWelcomeBinding.inflate(layoutInflater)

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btnWelcomeStart.setOnClickListener {
            findNavController().navigate( WelcomeFragmentDirections.welcomeToLogin() )
        }
    }
}