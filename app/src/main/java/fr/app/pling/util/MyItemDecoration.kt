package fr.app.pling.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemDecoration used to create an overlap for the team project
 */
class MyItemDecoration : RecyclerView.ItemDecoration() {
    private val overlapValue = -30

    override fun getItemOffsets(outRect : Rect, view : View, parent : RecyclerView, state : RecyclerView.State) {
        outRect.set(0, 0, overlapValue,0 )  // args is : left,top,right,bottom
    }
}
