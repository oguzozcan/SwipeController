package com.oguz.swipelibrary

import android.animation.Animator

/**
 * The interface needed by caller context so that adapter events (clicks etc) will not trigger further action while swipe animation is in progress
 * Some actions could be postponed after animation end, like swipe to refresh etc.
 */
internal interface SwipeAnimationListener<E : BaseSwipeViewHolder> {

    fun onSwipeAnimationStarted(animation: Animator, animationType: RecoverAnim.SwipeAnimationType, recyclerView: androidx.recyclerview.widget.RecyclerView, animatedView: E, dX: Float, dY: Float)
    fun onSwipeAnimationEnded(animation: Animator, animationType: RecoverAnim.SwipeAnimationType, recyclerView: androidx.recyclerview.widget.RecyclerView, animatedView: E, dX: Float, dY: Float)
}
