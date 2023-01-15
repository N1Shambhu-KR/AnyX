package com.a.anyx.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.a.anyx.R
import java.util.concurrent.Executors


class SignalRipple : View {

    companion object{
        const val TRANSMIT = 0
        const val RECEIVE = 1
    }

    private var signalType = TRANSMIT
    private var signalColor:Int = 0

    private lateinit var radiusAnimator:ValueAnimator

    private lateinit var alphaAnimation:ValueAnimator

    private var animatedAlpha:Int = 0
    private var animatedRadius:Float = 0f

    private var strokeWidth:Float = 0f

    private var idleRadius:Float = 0f

    private val rippleAnimationDuration:Long = 2000L

    private var canvas:Canvas?=null

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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SignalRipple,0,0)

        try {

            signalType = typedArray.getInt(R.styleable.SignalRipple_signalType, TRANSMIT)

            signalColor = typedArray.getColor(R.styleable.SignalRipple_signalColor,getRuntimeValue(
                androidx.appcompat.R.attr.colorPrimary))

            strokeWidth = typedArray.getDimension(R.styleable.SignalRipple_signalStrokeWidth,dp2float(10f))

            idleRadius = typedArray.getDimension(R.styleable.SignalRipple_idleRadius,dp2float(32f))

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

        radiusAnimator = ValueAnimator.ofFloat(0f,Math.max(width,height).toFloat()).also {

            it.repeatCount = ValueAnimator.INFINITE
            it.duration = rippleAnimationDuration

            it.addUpdateListener {

                animatedRadius = it.animatedValue as Float
                invalidate()
            }
        }

        alphaAnimation = ValueAnimator.ofInt(255,0).also {

            it.repeatCount = ValueAnimator.INFINITE
            it.duration = rippleAnimationDuration

            it.addUpdateListener {

                animatedAlpha = it.animatedValue as Int
            }
        }

        radiusAnimator.start()
        alphaAnimation.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        this.canvas = canvas

        var cx = width/2f
        var cy = 0f

        if (signalType == TRANSMIT){
            cy = height/2f
        }else if (signalType == RECEIVE){
            cy = 0f
        }

        val paint = Paint().also {

            it.color = signalColor
        }

        cx+=paddingLeft
        cx-=paddingRight

        cy+=paddingTop
        cy-=paddingBottom

        while (animatedRadius > 0){

            canvas.drawCircle(cx,cy,animatedRadius,paint.apply { alpha = animatedAlpha })

            animatedRadius-=Math.max(width,height)/4
        }

    }

    fun stopAnimation(){
        radiusAnimator.end()
        alphaAnimation.end()
        radiusAnimator.cancel()
        alphaAnimation.cancel()
    }

    fun startAnimation(){


        radiusAnimator.start()
        alphaAnimation.start()
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)

        if (gainFocus){

        }else{

        }
    }
}