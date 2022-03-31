package fr.app.pling.view.profile

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentDeleteAccountBinding
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.account.AuthViewModel

/**
 * DeleteAccountFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class DeleteAccountFragment : Fragment(), IEventListener {

    private lateinit var b : FragmentDeleteAccountBinding
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.authViewModelFactory)[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentDeleteAccountBinding.inflate(layoutInflater)
        b.viewModel = viewModel

        viewModel.eventListener = this

        // delete account
        b.btnDelete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)

            dialogBuilder.setMessage(getString(R.string.valid_delete_account))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.deleteAccount)) { _, _ ->
                    viewModel.deleteAccount()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle(getString(R.string.attention))
            alert.show() }
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar : Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar, getString(R.string.deleteAccount), true)
        setHasOptionsMenu(true)
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
        val c = context as Activity
        findNavController().navigate(R.id.accountActivity)
        viewModel.logout()
        c.finish()
    }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}