package com.mzw.rippleview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import java.util.ArrayList

class RippleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr) {

    private val DEFAULT_RIPPLE_COUNT = 6
    private val DEFAULT_DURATION_TIME = 3000
    private val DEFAULT_SCALE = 6.0f
    private val DEFAULT_FILL_TYPE = 0

    private var rippleColor: Int = 0
    private var rippleStrokeWidth: Float = 0.toFloat()
    private var rippleRadius: Float = 0.toFloat()
    private var rippleDurationTime: Int = 0
    private var rippleAmount: Int = 0
    private var rippleDelay: Int = 0
    private var rippleScale: Float = 0.toFloat()
    private var rippleType: Int = 0
    private var paint: Paint? = null
    private var animationRunning = false
    private var animatorSet: AnimatorSet? = null
    private var animatorList: MutableList<Animator>? = null
    private var circleParams: LayoutParams? = null
    private val circleViewList = ArrayList<Circle>()

    init {

        requireNotNull(attrs) { "Attributes should be provided to this view" }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView)
        rippleColor = typedArray.getColor(
            R.styleable.RippleView_rb_color,
            getResources().getColor(R.color.red)
        )
        rippleStrokeWidth = typedArray.getDimension(
            R.styleable.RippleView_rb_strokeWidth,
            getResources().getDimension(R.dimen.rippleStrokeWidth)
        )
        rippleRadius = typedArray.getDimension(
            R.styleable.RippleView_rb_radius,
            getResources().getDimension(R.dimen.rippleRadius)
        )
        rippleDurationTime =
            typedArray.getInt(R.styleable.RippleView_rb_duration, DEFAULT_DURATION_TIME)
        rippleAmount =
            typedArray.getInt(R.styleable.RippleView_rb_rippleAmount, DEFAULT_RIPPLE_COUNT)
        rippleScale = typedArray.getFloat(R.styleable.RippleView_rb_scale, DEFAULT_SCALE)
        rippleType = typedArray.getInt(R.styleable.RippleView_rb_type, DEFAULT_FILL_TYPE)
        typedArray.recycle()

        rippleDelay = rippleDurationTime / rippleAmount

        paint = Paint()
        paint!!.isAntiAlias = true
        if (rippleType == DEFAULT_FILL_TYPE) {
            rippleStrokeWidth = 0f
            paint!!.style = Paint.Style.FILL
        } else
            paint!!.style = Paint.Style.STROKE
        paint!!.color = rippleColor

        circleParams = LayoutParams(
            (2 * (rippleRadius + rippleStrokeWidth)).toInt(),
            (2 * (rippleRadius + rippleStrokeWidth)).toInt()
        )
        circleParams!!.addRule(CENTER_IN_PARENT, TRUE)

        animatorSet = AnimatorSet()
        animatorSet!!.duration = rippleDurationTime.toLong()
        animatorSet!!.interpolator = AccelerateDecelerateInterpolator()
        animatorList = ArrayList()

        for (i in 0 until rippleAmount) {
            val circle = Circle(getContext())
            addView(circle, circleParams)
            circleViewList.add(circle)
            val scaleXAnimator = ObjectAnimator.ofFloat(circle, "ScaleX", 1.0f, rippleScale)
            scaleXAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleXAnimator.repeatMode = ObjectAnimator.RESTART
            scaleXAnimator.startDelay = (i * rippleDelay).toLong()
            animatorList!!.add(scaleXAnimator)
            val scaleYAnimator = ObjectAnimator.ofFloat(circle, "ScaleY", 1.0f, rippleScale)
            scaleYAnimator.repeatCount = ObjectAnimator.INFINITE
            scaleYAnimator.repeatMode = ObjectAnimator.RESTART
            scaleYAnimator.startDelay = (i * rippleDelay).toLong()
            animatorList!!.add(scaleYAnimator)
            val alphaAnimator = ObjectAnimator.ofFloat(circle, "Alpha", 1.0f, 0f)
            alphaAnimator.repeatCount = ObjectAnimator.INFINITE
            alphaAnimator.repeatMode = ObjectAnimator.RESTART
            alphaAnimator.startDelay = (i * rippleDelay).toLong()
            animatorList!!.add(alphaAnimator)
        }

        animatorSet!!.playTogether(animatorList)
    }

    fun startRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (circle in circleViewList) {
                circle.setVisibility(View.VISIBLE)
            }
            animatorSet!!.start()
            animationRunning = true
        }
    }

    fun stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
            animatorSet!!.end()
            animationRunning = false
        }
    }

    fun isRippleAnimationRunning(): Boolean {
        return animationRunning
    }

    private inner class Circle(context: Context) : View(context) {

        init {
            this.visibility = INVISIBLE
        }

        override fun onDraw(canvas: Canvas) {
            val radius = Math.min(width, height) / 2
            canvas.drawCircle(
                radius.toFloat(),
                radius.toFloat(),
                radius - rippleStrokeWidth,
                paint!!
            )
        }
    }
}