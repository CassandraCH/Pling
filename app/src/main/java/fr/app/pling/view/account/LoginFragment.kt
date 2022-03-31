package fr.app.pling.view.account

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentLoginBinding
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.viewmodel.account.AuthViewModel

/**
 * LoginFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class LoginFragment : Fragment() , IEventListener {

    private val viewModel by lazy {  ViewModelProvider(this, MyApplication.authViewModelFactory)[AuthViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val b = FragmentLoginBinding.inflate(layoutInflater)
        b.viewmodel = viewModel

        b.btnLoginSignin.setOnClickListener {  viewModel.login()  }

        viewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                val c = context as Activity
                findNavController().navigate(R.id.mainActivity)
                c.finish()
            }
        }

        return b.root
    }

    private fun redirectToSignUp() {
        findNavController().navigate(WelcomeFragmentDirections.welcomeToLogin())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.eventListener = this
    }

    override fun onSuccess() { }

    override fun onFailure(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}