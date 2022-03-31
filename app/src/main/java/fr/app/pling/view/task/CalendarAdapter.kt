package fr.app.pling.view.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.app.pling.R
import fr.app.pling.databinding.RowDateCalendarBinding
import fr.app.pling.model.CalendarDate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * CalendarAdapter
 *
 * @author Calvados Cindy
 * @author Chaumulon Cassandra
 */
class CalendarAdapter(private val listener: (calendarDateModel: CalendarDate, position: Int) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {
    private val list = ArrayList<CalendarDate>()

    private var selectedDate : CalendarDate? = null
    private val currentDate = Calendar.getInstance()
    private val sdf = SimpleDateFormat.getDateInstance()

    inner class MyViewHolder(private val b: RowDateCalendarBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(calendarDateModel: CalendarDate) {
            b.item = calendarDateModel

            val sdf1 = sdf.format(currentDate.time)
            val sdf2 = sdf.format(calendarDateModel.data)

            /** Select the current date */
            if(sdf1.equals(sdf2)){
                if(selectedDate === null){
                    selectedDate = calendarDateModel
                    calendarDateModel.isSelected = true
                }
            }

            /** Logic to handle all the color change of text etc at the moment ( Need to change, depending on the integration )**/
            if (calendarDateModel.isSelected) {
                b.tvCalendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                b.tvCalendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                b.cardCalendar.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.purple_400
                    )
                )
            } else {
                b.tvCalendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.color_secondary
                    )
                )
                b.tvCalendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.color_secondary
                    )
                )
                b.cardCalendar.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.row_calendar_bg
                    )
                )
            }
            b.cardCalendar.setOnClickListener {
                selectedDate?.isSelected = false
                listener.invoke(calendarDateModel, bindingAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowDateCalendarBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarDate>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}
