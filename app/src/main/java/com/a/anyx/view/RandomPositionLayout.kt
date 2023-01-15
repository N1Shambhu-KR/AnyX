package com.a.anyx.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import java.util.*

class RandomPositionLayout: ViewGroup{

    private lateinit var currentTouchedView: View

    constructor(context: Context) : super(context) {
        init(null, 0)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int){}

    private val mRandom: Random
    private val mBackgroundPaint: Paint

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)

        // Measure children
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec,heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)

            placeChildNonOverlapping(child)
        }
    }

    private fun placeChildNonOverlapping(placeChild:View){

        var x:Int
        var y :Int

        do {

            x = (0..width).random()
            y = (0..height).random()

        }while (isOutOfView(x+placeChild.measuredWidth,y+measuredHeight) && isOverlapping(placeChild))

        placeChild.layout(x,y,placeChild.measuredWidth,placeChild.measuredHeight)
    }

    private fun isOverlapping(child1:View):Boolean{

        for (i in 0 until childCount){

            val child2 = getChildAt(i)

            if (child1 != child2){

                return child1.x == child2.x && child1.y == child2.y
            }
        }

        return false
    }

    private fun isOutOfView(viewRight:Int,viewBottom:Int):Boolean{
        return viewRight > width && viewBottom > height
    }

    override fun onDraw(canvas: Canvas) {
        // Draw background
        val rect = Rect(0, 0, width, height)
        canvas.drawRect(rect, mBackgroundPaint)

        // Draw children
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            child.draw(canvas)
        }
    }

    init {
        mRandom = Random()
        mBackgroundPaint = Paint()
        mBackgroundPaint.color = 0x00000000
    }
}