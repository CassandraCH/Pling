package fr.app.pling.view.task

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.snackbar.Snackbar
import fr.app.pling.MyApplication
import fr.app.pling.R
import fr.app.pling.databinding.FragmentTaskCalendarBinding
import fr.app.pling.model.CalendarDate
import fr.app.pling.model.interf.IEventListener
import fr.app.pling.util.SwipeGesture
import fr.app.pling.view.MainActivity
import fr.app.pling.viewmodel.project.ProjectViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * TaskCalendarFragment
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class TaskCalendarFragment : Fragment() , IEventListener{

    private lateinit var b: FragmentTaskCalendarBinding
    private val sdf = SimpleDateFormat.getDateInstance()
    private val cal = Calendar.getInstance()
    private val currentDate: Calendar = Calendar.getInstance()
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDate>()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MyApplication.projectViewModelFactory)[ProjectViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentTaskCalendarBinding.inflate(layoutInflater)

        viewModel.getTaskForDay(currentDate.time)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        viewModel._dayTasks.observe(viewLifecycleOwner){
            b.rvTasksCalendar.adapter = it?.let { it1 -> TaskAdapter(it1) }
        }

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add the toolbar
        val mainActivity = activity as MainActivity
        val toolbar: Toolbar = mainActivity.findViewById(R.id.toolbar)
        mainActivity.setToolbar(toolbar, getString(R.string.calendarPage_title), true)
        setHasOptionsMenu(true)

        viewModel.eventListener = this
    }

    /**
     * Set up click listener
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpClickListener() {
        // set up the swipe for months
        b.parentDateContainer.setOnTouchListener(object : SwipeGesture(requireContext()) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()

                cal.add(Calendar.MONTH, 1)
                setUpCalendar()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()

                cal.add(Calendar.MONTH, -1)
                if (cal == currentDate)
                    setUpCalendar()
                else
                    setUpCalendar()
            }
        })
    }

    /**
     * Setting up adapter for recyclerview
     */
    private fun setUpAdapter() {
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(b.recyclerView)

        adapter = CalendarAdapter { c: CalendarDate, position: Int ->
            viewModel.getTaskForDay(c.data)

            calendarList2.forEachIndexed { index, calendarModel ->
              calendarModel.isSelected = index == position

                if (calendarModel.isSelected){
                    b.recyclerView.scrollToPosition(index+2)
                }
            }
            adapter.setData(calendarList2)
        }

        b.recyclerView.scrollToPosition(findCurrentDatePosition())

        b.recyclerView.adapter = adapter
    }

    /**
     * Function to setup calendar for every month
     */
    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarDate>()
        b.tvDateMonth.text = sdf.format(cal.time)

        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        dates.clear()

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {

            dates.add(monthCalendar.time)
            calendarList.add(CalendarDate(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendarList2.clear()
        calendarList2.addAll(calendarList)

        b.recyclerView.scrollToPosition(findCurrentDatePosition())

        adapter.setData(calendarList)
    }

    override fun onSuccess() { }

    override fun onFailure(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun findCurrentDatePosition(): Int {
        val ele : CalendarDate? = calendarList2.find {  it.data == currentDate.time   }
        return calendarList2.indexOf(ele)
    }

    // manage the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // back button
            android.R.id.home -> {
                Navigation.findNavController(requireView()).navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}