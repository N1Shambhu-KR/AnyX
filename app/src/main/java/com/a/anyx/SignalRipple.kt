package com.a.anyx

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation


class SignalRipple : View {

    companion object{
        const val TRANSMIT = 0
        const val RECEIVE = 1
    }

    private var signalType = TRANSMIT
    private var signalColor:Int = 0

    private lateinit var radiusAnimator:ValueAnimator
    private lateinit var alphaAnimation:ValueAnimator

    private var radius:Float = 0.0f
    private var colorAlpha:Float = 1.0f

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

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.SignalRipple,0,0)

        try {

            signalType = typedArray.getInt(R.styleable.SignalRipple_signalType, TRANSMIT)

            signalColor = getRuntimeValue(com.google.android.material.R.attr.colorPrimary)

            radius = dp2float(32f)
        }finally {
            typedArray.recycle()
        }
    }

    private fun getRuntimeValue(attrId:Int):Int{

        val runtimeValue = TypedValue()

        context.theme.resolveAttribute(attrId,runtimeValue,true)

        return runtimeValue.data
    }

    private fun dp2float(dp:Float):Float{

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,resources.displayMetrics)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val desiredWidth = Math.round(dp2float(100f))
        val desiredHeight = Math.round(dp2float(100f))

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width:Int
        var height:Int

        if (widthMode == MeasureSpec.EXACTLY){

            width = widthSize
        }else if (widthMode == MeasureSpec.AT_MOST){

            width = Math.min(desiredWidth,widthSize)
        }else{
            width = desiredWidth
        }

        if (heightMode == MeasureSpec.EXACTLY){

            height = heightSize
        }else if(heightMode == MeasureSpec.AT_MOST){

            height = Math.min(desiredHeight,heightSize)
        }else{

            height = desiredHeight
        }

        setMeasuredDimension(width,height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        radiusAnimator = ValueAnimator.ofFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var cx = width/2f
        var cy = 0f

        if (signalType == TRANSMIT){
            cy = height/2f
        }else if (signalType == RECEIVE){
            cy = 0f
        }

        val paint = Paint().also {

            val r = (signalColor shr 16) and 0xFF
            val g = (signalColor shr 8) and 0xFF
            val b = (signalColor ) and 0xFF

            it.color = Color.argb(255,r,g,b)
        }

        cx+=paddingLeft
        cx-=paddingRight

        cy+=paddingTop
        cy-=paddingBottom

        canvas.drawCircle(cx,cy, 100f,paint)

    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)

        if (gainFocus){

        }else{

        }
    }
}