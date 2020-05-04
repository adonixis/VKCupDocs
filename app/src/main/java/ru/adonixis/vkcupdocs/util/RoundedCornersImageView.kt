package ru.adonixis.vkcupdocs.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import ru.adonixis.vkcupdocs.util.Utils.convertDpToPx

class RoundedCornersImageView : AppCompatImageView {
    private var radius = 0f
    private var clipPath: Path? = null
    private var rect: RectF? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        radius = convertDpToPx(6.0f)
        clipPath = Path()
        rect = RectF(0.0f, 0.0f, width.toFloat(), height.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measuredHeight
        val width = measuredWidth
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        rect!![0f, 0f, width.toFloat()] = height.toFloat()
        clipPath!!.addRoundRect(rect!!, radius, radius, Path.Direction.CW)
        canvas.clipPath(clipPath!!)
        super.onDraw(canvas)
    }
}