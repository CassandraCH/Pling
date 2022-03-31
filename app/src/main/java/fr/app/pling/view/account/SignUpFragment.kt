package fr.app.pling.view.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.app.pling.MyApplication
import fr.app.pling.databinding.FragmentSignUpBinding
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.viewmodel.account.AuthViewModel

/**
 * SignUpFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class SignUpFragment : Fragment(), IEventListener {

    private val viewModel by lazy {  ViewModelProvider(this, MyApplication.authViewModelFactory)[AuthViewModel::class.java] }
    private lateinit var b : FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b = FragmentSignUpBinding.inflate(layoutInflater)
        b.viewmodel = viewModel
        b.btnRegister.setOnClickListener { viewModel.signup() }
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btnRegister.setOnClickListener {  viewModel.signup();  }
        viewModel.eventListener = this
    }

    override fun onSuccess() {
        findNavController().navigate(SignUpFragmentDirections.signupToLogin())
    }

    override fun onFailure(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}