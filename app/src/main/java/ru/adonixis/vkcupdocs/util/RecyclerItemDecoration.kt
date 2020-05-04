package ru.adonixis.vkcupdocs.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ru.adonixis.vkcupdocs.util.Utils.convertDpToPx

class RecyclerItemDecoration(context: Context?) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = convertDpToPx(16.0f).toInt()
        } else if (position == state.itemCount - 1) {
            outRect.bottom = convertDpToPx(12.0f).toInt()
        }
    }
}