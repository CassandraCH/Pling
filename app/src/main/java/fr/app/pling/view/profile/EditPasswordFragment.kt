package fr.app.pling.view.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentEditPasswordBinding
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.account.AuthViewModel

/**
 * EditPasswordFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class EditPasswordFragment : Fragment(), IEventListener {

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.authViewModelFactory)[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val b = FragmentEditPasswordBinding.inflate(layoutInflater)
        b.viewModel = viewModel

        b.btnUpdatePassword.setOnClickListener { viewModel.updatePassword() }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar : Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar, getString(R.string.editPassword), true)
        setHasOptionsMenu(true)

        viewModel.eventListener = this
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // back button
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSuccess() {
        findNavController().navigate(EditPasswordFragmentDirections.editPasswordToSuccess())
    }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}