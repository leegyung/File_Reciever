package leegyung.file_reciever

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerDecorator(private val height : Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = height
        outRect.bottom = height
        outRect.right = 10
        outRect.left = 10
        view.setBackgroundResource(R.color.list_color)
    }

}