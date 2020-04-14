package com.oguz.swipelibrary

import android.animation.Animator
import android.animation.ValueAnimator
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.getDefaultUIUtil
import androidx.recyclerview.widget.RecyclerView

internal class RecoverAnim<E : BaseSwipeViewHolder>(private val animationListener: SwipeAnimationListener<E>?, private val recyclerView: RecyclerView, private val viewHolder: E,
                                           private val actionState: Int, private val startX: Float, private val startY: Float, private val targetX: Float, private val targetY: Float) : Animator.AnimatorListener {

    private val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var x = 0f
    private var y = 0f
    private var fraction = 0f
    private var animatorRunning = false
    private val type: SwipeAnimationType = if (startX == viewHolder.getBackgroundViewWidth()) {
        SwipeAnimationType.SWIPE_BACK
    } else {
        SwipeAnimationType.RESET
    }

    init {
        valueAnimator.addUpdateListener { animation ->
            setFraction(animation.animatedFraction)
            update()
        }
        valueAnimator.setTarget(viewHolder.foreground)
        valueAnimator.addListener(this)
        setFraction(0f)
    }

    fun setDuration(duration: Long) {
        valueAnimator.duration = duration
    }

    fun start() {
        animatorRunning = true
        viewHolder.setIsRecyclable(false)
        valueAnimator.start()
    }

    fun cancel() {
        valueAnimator.cancel()
    }

    private fun setFraction(fraction: Float) {
        this.fraction = fraction
    }

    /**
     * We run updates on onDraw method but use the fraction from animator callback.
     * This way, we can sync translate x/y values w/ the animators to avoid one-off frames.
     */
    @VisibleForTesting
    fun update() {
        x = if (startX == targetX) {
            ViewCompat.getTranslationX(viewHolder.itemView)
        } else {
            startX + fraction * (targetX - startX)
        }
        y = if (startY == targetY) {
            ViewCompat.getTranslationY(viewHolder.itemView)
        } else {
            startY + fraction * (targetY - startY)
        }
        getDefaultUIUtil().onDraw(null, recyclerView, viewHolder.foreground, x, y, actionState, false)
    }

    override fun onAnimationStart(animation: Animator) {
        viewHolder.isAnimating = true
        animationListener?.onSwipeAnimationStarted(animation, type, recyclerView, viewHolder, startX, startY)
    }

    override fun onAnimationEnd(animation: Animator) {
        animatorRunning = false
        getDefaultUIUtil().onDraw(null, recyclerView, viewHolder.foreground, 0f, 0f, ItemTouchHelper.ACTION_STATE_IDLE, false)
        getDefaultUIUtil().clearView(viewHolder.foreground)
        viewHolder.isAnimating = false
        animationListener?.onSwipeAnimationEnded(animation, type, recyclerView, viewHolder, targetX, targetY)
    }

    override fun onAnimationCancel(animation: Animator) {
        setFraction(1f) //make sure we recover the view's state.
    }

    override fun onAnimationRepeat(animation: Animator) {}

    internal enum class SwipeAnimationType {
        //Swipe back to initial pos
        SWIPE_BACK,
        //Reset after full swipe
        RESET
    }
}