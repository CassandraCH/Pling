package fr.app.pling.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import fr.app.pling.R
import fr.app.pling.databinding.FragmentPasswordResetBinding

/**
 * PasswordResetFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class PasswordResetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentPasswordResetBinding.inflate(layoutInflater)

        b.btnBackHome.setOnClickListener { findNavController().navigate(R.id.navigation_home) }

        return b.root
    }
}