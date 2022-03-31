package fr.app.pling.view.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentSearchBinding
import fr.app.pling.view.project.ProjectAdapter
import fr.app.pling.view.task.TaskAdapter
import fr.app.pling.viewmodel.project.ProjectViewModel
import fr.app.pling.viewmodel.user.SearchViewModel

/**
 * SearchFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class SearchFragment : Fragment() {

    private lateinit var b: FragmentSearchBinding

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.searchViewModelFactory
        )[SearchViewModel::class.java]
    }

    private val vm by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory
        )[ProjectViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding
        b = FragmentSearchBinding.inflate(layoutInflater)
        b.vm = viewModel

        viewModel._projectList.observe(viewLifecycleOwner){
             b.rvList.adapter = ProjectAdapter(it!!)
        }

        viewModel._tasksList.observe(viewLifecycleOwner){
             b.rvTaskList.adapter = TaskAdapter(it!!)
        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.searchView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(string: Editable?) {

                if(b.radioProject.isChecked){

                    b.rvTaskList.visibility = View.GONE
                    b.rvList.visibility = View.VISIBLE

                    viewModel.searchProjects()
                }else {
                    b.rvList.visibility = View.GONE
                    b.rvTaskList.visibility = View.VISIBLE

                    viewModel.searchTasks()
                }
            }
        })

        b.radioProject.setOnCheckedChangeListener { compoundButton, _ ->
            updateDisplay(compoundButton)
        }

        b.radioTask.setOnCheckedChangeListener { compoundButton, _ ->
            updateDisplay(compoundButton)
        }

        b.btnBack.setOnClickListener{ findNavController().popBackStack() }
    }

    private fun updateDisplay(compoundButton : CompoundButton){
        if(compoundButton.isChecked){
            b.searchView.setText("")
            compoundButton.setBackgroundResource(R.drawable.filter_search_selected)
            compoundButton.setTextColor(requireContext().getColor(R.color.white))
        } else {

            compoundButton.setBackgroundResource(R.drawable.filter_search)
            compoundButton.setTextColor(requireContext().getColor(R.color.brown_800))
        }
    }
}

