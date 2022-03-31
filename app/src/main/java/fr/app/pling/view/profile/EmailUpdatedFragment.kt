package fr.app.pling.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import fr.app.pling.R
import fr.app.pling.databinding.FragmentEmailUpdatedBinding
import fr.app.pling.view.MainActivity

/**
 * EmailUpdatedFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class EmailUpdatedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentEmailUpdatedBinding.inflate(layoutInflater)

        // redirection to the home
        b.btnBackHome.setOnClickListener { findNavController().navigate(R.id.navigation_home) }

        return b.root
    }
}