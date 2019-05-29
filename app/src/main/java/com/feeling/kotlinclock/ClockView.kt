package com.feeling.kotlinclock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class ClockView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    //画外层圈
    private var mPaintOutCircle : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画外层圈上的文字
    private var mPaintOutText : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画刻度
    private var mPaintScale : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画刻度(渐变)
    private var mPaintScaleGradient : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画三角形
    private var mPaintTriangle : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画时针
    private var mPaintHour : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画分针
    private var mPaintMinute : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //画中间圆
    private var mPaintCenterCircle : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //View的高度和宽度
    private var mHeight : Int = 0
    private var mWidth : Int = 0
    //View的中心点
    private var mCenterX : Int = 0
    private var mCenterY : Int = 0
    //中心圆半径
    private var mInnerRadius : Float = dp2px(8f)
    //与外边框的间距
    private var mPaddingOut : Float = dp2px(20f)

    //秒的角度
    private var mSecondDegress : Int = 0
    //秒的角度
    private var mSecondMillDegress : Float = 0f
    //分的角度
    private var mMinuteDegress : Int = 0
    //时的角度
    private var mHourDegress : Int = 0

    //半透明白色
    private val color_halfwhite : Int = Color.parseColor("#80FFFFFF")
    //白色
    private val color_white : Int = Color.parseColor("#FFFFFF")

    init {
        mPaintOutCircle.color = color_halfwhite
        mPaintOutCircle.strokeWidth = dp2px(1f)
        mPaintOutCircle.style = Paint.Style.STROKE

        mPaintOutText.color = color_halfwhite
        mPaintOutText.strokeWidth = dp2px(1f)
        mPaintOutText.style = Paint.Style.STROKE
        mPaintOutText.textSize = dp2px(12f)
        mPaintOutText.textAlign = Paint.Align.CENTER

        mPaintScale.color = color_halfwhite
        mPaintScale.strokeWidth = dp2px(2f)
        mPaintScale.style = Paint.Style.STROKE

        mPaintScaleGradient.color = color_halfwhite
        mPaintScaleGradient.strokeWidth = dp2px(2f)
        mPaintScaleGradient.style = Paint.Style.STROKE

        mPaintTriangle.color = color_white
        mPaintTriangle.strokeWidth = dp2px(1f)
        mPaintTriangle.style = Paint.Style.FILL

        mPaintHour.color = color_halfwhite
        mPaintHour.strokeWidth = dp2px(1f)
        mPaintHour.style = Paint.Style.FILL
        mPaintMinute.strokeCap = Paint.Cap.ROUND

        mPaintMinute.color = color_white
        mPaintMinute.strokeWidth = dp2px(3f)
        mPaintMinute.style = Paint.Style.STROKE
        mPaintMinute.strokeCap = Paint.Cap.ROUND

        mPaintCenterCircle.color = color_white
        mPaintCenterCircle.strokeWidth = dp2px(3f)
        mPaintCenterCircle.style = Paint.Style.STROKE
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(mRunnable)
    }

    private fun start() {
        postDelayed(mRunnable, 1000)
    }

    private val mRunnable = Runnable {
        setTime()
        invalidate()
        start()
    }

    private fun setTime(){
        val mCalendar = Calendar.getInstance()
        val minute = mCalendar.get(Calendar.MINUTE)
        val second = mCalendar.get(Calendar.SECOND)
        val hour = mCalendar.get(Calendar.HOUR)
        val secondMills = mCalendar.get(Calendar.MILLISECOND)
        mHourDegress = hour * 30
        mMinuteDegress = minute * 6 + 180
        mSecondDegress = second * 6 + 180
        mSecondMillDegress = second.toFloat() * 6 + secondMills * 0.006f + 180
        val mills = secondMills * 0.006f
        //因为是没2°旋转一个刻度，所以这里要根据毫秒值来进行计算
        when (mills) {
            in 2 until 4 -> {
                mSecondDegress += 2
            }
            in 4 until 6 -> {
                mSecondDegress += 4
            }
        }
        Log.v("ClockView", "时:$hour, 分:$minute, 秒:$second, 毫秒:$secondMills, 角度:$mills")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        val imageSize = Math.min(width, height)
        setMeasuredDimension(imageSize, imageSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mCenterX = mWidth / 2
        mCenterY = mHeight / 2
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        setBackgroundColor(Color.parseColor("#000000"))
        //将视图移动到中心
        canvas.translate(mCenterX.toFloat(), mCenterY.toFloat())
        drawOutCircle(canvas)
        drawOutText(canvas)
        drawScale(canvas)
        drawTriangle(canvas)
        drawMinute(canvas)
        drawHour(canvas)
        drawCenterBall(canvas)
    }

    private fun drawCenterBall(canvas: Canvas) {
        canvas.drawCircle(0f, 0f, mInnerRadius / 2, mPaintCenterCircle)
    }

    //画时针
    private fun drawHour(canvas: Canvas) {
        val path = Path()
        canvas.save()
        canvas.rotate(mHourDegress.toFloat())
        path.moveTo(mInnerRadius / 2, 0f)
        path.lineTo(-mInnerRadius / 2, 0f)
        path.lineTo(-mInnerRadius / 6, -measuredWidth.toFloat() / 4)
        path.lineTo(mInnerRadius / 6, -measuredWidth.toFloat() / 4)
        path.close()
        canvas.drawPath(path, mPaintHour)
        canvas.restore()
    }

    //画分钟
    private fun drawMinute(canvas: Canvas) {
        canvas.save()
        canvas.rotate(mMinuteDegress.toFloat())
        canvas.drawLine(0f, 0f, 0f, measuredWidth.toFloat() / 3, mPaintMinute)
        canvas.restore()
    }

    //画三角形
    private fun drawTriangle(canvas: Canvas) {
        val min = measuredWidth / 2
        val path = Path()
        canvas.save()
        canvas.rotate(mSecondMillDegress.toFloat())
        path.moveTo(0f, min.toFloat() * 3/4 - dp2px(5f))
        path.lineTo(-dp2px(8f), min.toFloat() * 3/4 - dp2px(20f))
        path.lineTo(dp2px(8f), min.toFloat() * 3/4 - dp2px(20f))
        path.close()
        canvas.drawPath(path, mPaintTriangle)
        canvas.restore()
        //绘制渐变刻度
        for(i in 0..90 step 2){
            canvas.save()
            mPaintScaleGradient.setARGB((255 - 2.7 * i).toInt(), 255, 255, 255)
            canvas.rotate(-i.toFloat() + mSecondDegress)
//            canvas.rotate(((mSecondDegress - 90 - i).toFloat()))
            canvas.drawLine(0f, (min * 3/4).toFloat(), 0f, (min * 3/4).toFloat() + dp2px(10f), mPaintScaleGradient)
            canvas.restore()
        }
    }

    //画刻度(利用旋转画布画线实现)
    private fun drawScale(canvas: Canvas) {
        val min = measuredWidth / 2
        for (i in 0 until 360 step 2){
            canvas.save()
            canvas.rotate(i.toFloat())
            canvas.drawLine(0f, (min * 3/4).toFloat(), 0f, (min * 3/4).toFloat() + dp2px(10f), mPaintScale)
            canvas.restore()
        }
    }

    //画最外层的文字
    private fun drawOutText(canvas: Canvas) {
        val padding = (measuredWidth - mPaddingOut) / 2
        //Math.ceil:返回大于或等于传入数值的最小整数
        val textHeight = Math.ceil((mPaintOutText.fontMetrics.leading - mPaintOutText.fontMetrics.ascent).toDouble()).toInt()
        canvas.drawText("9", -padding, (textHeight / 2).toFloat(), mPaintOutText)
        canvas.drawText("3", padding, (textHeight / 2).toFloat(), mPaintOutText)
        canvas.drawText("12", 0f, -padding + textHeight / 2, mPaintOutText)
        canvas.drawText("6", 0f, padding + textHeight / 2, mPaintOutText)
    }

    //画最外层的圆
    private fun drawOutCircle(canvas: Canvas) {
        val size = measuredWidth;
        val rect = RectF(-(size - mPaddingOut) / 2, -(size - mPaddingOut) / 2, (size - mPaddingOut) / 2, (size - mPaddingOut) / 2)
        canvas.drawArc(rect, 5f, 80f, false, mPaintOutCircle)
        canvas.drawArc(rect, 95f, 80f, false, mPaintOutCircle)
        canvas.drawArc(rect, 185f, 80f, false, mPaintOutCircle)
        canvas.drawArc(rect, 275f, 80f, false, mPaintOutCircle)
    }

    /**
     * dp转px
     */
    fun View.dp2px(dipValue: Float): Float {
        return (dipValue * this.resources.displayMetrics.density + 0.5f)
    }

}