package com.github.dromanenko.animations.anim

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.github.dromanenko.animations.R
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min


class DrawAnim @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs) {
    // dp -> px
    fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    private val topMargin: Float
    private val leftMargin: Float
    private val distanceBetweenElements: Float


    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFBB86FC.toInt()
    }


    private val rectangle = RectF()
    private val cornerRadiusRectangle: Float
    private val lengthLongSide: Float
    private var lengthShortSide: Float


    private var curPointNum = 0
    private var dotScale: Float = 1f
    private var dotScaleFactor = 1.3f
    private var cornerRadiusDot: Float
    private val lengthBetweenDots: Float

    private var animDelay: Long
    private var animLength: Long

    init {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.DrawAnim, defStyleAttr, defStyleRes
        )
        try {
            leftMargin = dpToPx(a.getFloat(R.styleable.DrawAnim_valueMarginLR, 0f))
            topMargin = dpToPx(a.getFloat(R.styleable.DrawAnim_valueMarginTD, 0f))
            val valueScale = a.getFloat(R.styleable.DrawAnim_valueScale, 1f)
            cornerRadiusDot = valueScale * dpToPx(4.5f)
            cornerRadiusRectangle = valueScale * dpToPx(4.5f)
            lengthShortSide = valueScale * dpToPx(10f)
            lengthLongSide = valueScale * dpToPx(40f)
            lengthBetweenDots = lengthLongSide - 2 * lengthShortSide
            distanceBetweenElements = valueScale * dpToPx(15f)

            animDelay = a.getInt(
                R.styleable.DrawAnim_valueAnimationDelay, 500
            ).toLong()
            animLength = a.getInt(
                R.styleable.DrawAnim_valueAnimationLength, 1000
            ).toLong()

            paint.color = a.getColor(R.styleable.DrawAnim_valueColor, paint.color)
        } finally {
            a.recycle() // Не забывайте, это важно!
        }
    }

    private val areaRectangle = hypot(lengthLongSide, lengthShortSide)
    private val desiredWidth =
        areaRectangle +
                2 * leftMargin +
                distanceBetweenElements +
                lengthShortSide +
                lengthBetweenDots +
                lengthShortSide * dotScaleFactor
    private val desiredHeight =
        2 * topMargin +
                max(
                    lengthLongSide,
                    lengthShortSide + lengthShortSide * dotScaleFactor + lengthBetweenDots
                )

    private fun drawRectangle(
        canvas: Canvas,
        angle: Float,
        left: Float,
        top: Float,
        hShift: Float,
        vShift: Float
    ) {
        val save = canvas.save()
        rectangle.set(left, top, left + hShift, top + vShift)
        canvas.rotate(
            angle,
            left + hShift / 2,
            top + vShift / 2
        )
        canvas.drawRoundRect(
            rectangle,
            cornerRadiusRectangle,
            cornerRadiusRectangle,
            paint
        )
        canvas.restoreToCount(save)
    }

    private fun drawDot(
        canvas: Canvas,
        pointNum: Int,
        x: Float,
        y: Float
    ) {
        val save = canvas.save()
        canvas.translate(x, y)
        var scale = 1f
        when (curPointNum) {
            pointNum -> scale = dotScale
            pointNum + 1 -> scale = 1 + dotScaleFactor - dotScale
        }
        canvas.scale(scale, scale, cornerRadiusDot, cornerRadiusDot)
        canvas.drawRoundRect(
            RectF(0f, 0f, lengthShortSide, lengthShortSide),
            cornerRadiusDot,
            cornerRadiusDot,
            paint
        )
        canvas.restoreToCount(save)
    }

    data class Draw(
        val type: String,
        var timeAnima: Long,
        val impl: (canvas: Canvas, shift: Float, more: Boolean) -> Unit,
        val colRepeat: Float = 0f,
        val iterationShift: Float = 0f,
        val border: Float = 0f,
        var curValIter: Float = 0f,
        var curBorder: Float = 0f
    )

    private fun onDrawRectangleImpl(
        canvas: Canvas,
        shiftAngle: Float,
        more: Boolean
    ) {
        val length = (lengthLongSide - lengthShortSide) / 2
        drawRectangle(
            canvas,
            0f + shiftAngle,
            leftMargin,
            topMargin + length,
            lengthLongSide,
            lengthShortSide
        )
        drawRectangle(
            canvas,
            1f + shiftAngle,
            leftMargin + length,
            topMargin,
            lengthShortSide,
            lengthLongSide
        )
        if (more)
            onDrawDotsImpl(canvas, 0f, false)
    }

    private fun onDrawDotsImpl(
        canvas: Canvas,
        emp: Float,
        more: Boolean
    ) {
        val firstDotX = leftMargin + distanceBetweenElements + areaRectangle
        val shift = lengthBetweenDots + lengthShortSide
        drawDot(
            canvas,
            0,
            firstDotX + shift / 2,
            topMargin
        )
        // right dot
        drawDot(
            canvas,
            1,
            firstDotX + shift,
            topMargin + shift / 2
        )
        // down dot
        drawDot(
            canvas,
            2,
            firstDotX + shift / 2,
            topMargin + shift
        )
        // left dot
        drawDot(
            canvas,
            3,
            firstDotX,
            topMargin + shift / 2
        )
        if (more)
            onDrawRectangleImpl(canvas, 0f, false)
    }

    private fun onDrawEmpty(
        canvas: Canvas,
        emp: Float,
        more: Boolean
    ) {
        onDrawRectangleImpl(canvas, 0f, more)
    }

    private var drawRec =
        Draw(
            "rec", animLength, ::onDrawRectangleImpl, 80f,
            360 / 80f, 1000 / 80f, 0f, 1000 / 80f
        )
    private var drawDots =
        Draw(
            "dots", animLength, ::onDrawDotsImpl, 4f,
            1f, animLength / 6f, 0f, animLength / 6f
        )
    private var curTime = 0f
    private val timeShift = 16f
    private var numAnim = 0
    private var drawEmpty = Draw("emp", animDelay, ::onDrawEmpty)
    private var animations = listOf(drawRec, drawDots, drawEmpty)

    private fun defaultParam() {
        animations[0].curValIter = 0f
        animations[0].curBorder = animLength / 80f
        animations[1].curValIter = 0f
        animations[1].curBorder = animLength / 6f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        curTime += timeShift
        if (animations[numAnim].type == "emp")
            animations[numAnim].impl(canvas, 0f, true)

        if (curTime > animations[numAnim].timeAnima) {
            animations[numAnim].impl(canvas, 0f, true)
            if (animations[numAnim].type == "dots")
                curPointNum = 0
            numAnim++
            curTime = 0f
            if (numAnim == animations.size) {
                numAnim = 0
                defaultParam()
            }
        } else if (curTime > animations[numAnim].curBorder) {
            if (animations[numAnim].type == "dots")
                curPointNum = animations[numAnim].curValIter.toInt()
            animations[numAnim].impl(canvas, animations[numAnim].curValIter, true)
            animations[numAnim].curValIter += animations[numAnim].iterationShift
            animations[numAnim].curBorder += animations[numAnim].border
        }
        if (animations[numAnim].type == "dots")
            animations[numAnim].impl(canvas, animations[numAnim].curValIter, true)
        invalidate()
    }

    // https://medium.com/@quiro91/custom-view-mastering-onmeasure-a0a0bb11784d
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        setMeasuredDimension(
            measureDimension(desiredWidth.toInt(), widthMeasureSpec),
            measureDimension(desiredHeight.toInt(), heightMeasureSpec)
        )
    }

    private fun measureDimension(
        desired: Int,
        measureSpec: Int
    ): Int {
        val size = MeasureSpec.getSize(measureSpec)
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> min(size, desired)
            MeasureSpec.UNSPECIFIED -> desired
            else -> desired
        }
    }
}