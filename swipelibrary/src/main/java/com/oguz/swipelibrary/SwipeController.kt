package com.oguz.swipelibrary

import android.animation.Animator
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

private const val TAG = "SwipeController"

/**
 * Main Class of this library
 *
 */
internal class SwipeController<E : BaseSwipeViewHolder> internal constructor(private val callback: SwipeCallback<E>, private val swipeActionLaunchedListener: OnSwipeActionLaunchedListener<E>,
                                                                             private val onSwipeStateChangedListener: OnSwipeStateChangedListener<E>) : SwipeAnimationListener<E>, ItemTouchHelper.Callback() {
    private var swipeBack = false
    private var rightVisibleItem: E? = null
    private var movDif = 0f
    private var swipeBackTouchListener: SwipeBackTouchListener? = null

    /**
     * Items can be swiped to left for now but this can be easily changed.
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    /**
     * Library currently doesn't support item move.
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
        return false
    }

    /**
     * We don't use this method but android system can merely call this method.
     * We remove layout changes by notifying the adapter in this case.
     */
    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        callback.resetSwipedView(viewHolder.layoutPosition)
    }

    /**
     * Full swipe usually triggers main swipe action
     */
    private fun fullSwipeActions(viewHolder: ViewHolder) {
        if (swipeBackTouchListener != null) {
            val swipedItemViewHolder = viewHolder as E
            swipedItemViewHolder.setButtonShowedState(BaseSwipeViewHolder.ButtonsState.FULLY_SWIPED)
            rightVisibleItem = null
            swipeBackTouchListener?.onSwipedFinished()
            swipeBackTouchListener = null
            //TODO This shall not be null ever but coming null sometimes, check reasoning
            swipedItemViewHolder.getMainSwipeAction()?.let {
                swipeActionLaunchedListener.onSwipeActionLaunched(swipedItemViewHolder, it)
            }
        }
    }

    /**
     *  Method is crucial in order to snap viewHolders
     */
    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    /**
     * Childs in this case is the action buttons which are bind to viewHolder, they only drawn in case of swipe
     */
    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        var dX = dX
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val baseSwipeHolder = viewHolder as E
            dX = baseSwipeHolder.decideStoppingPosAccordingToState(dX)
            if (baseSwipeHolder.getButtonShowedState() === BaseSwipeViewHolder.ButtonsState.FULLY_SWIPED) {
                fullSwipeActions(baseSwipeHolder)
            } else {
                setTouchListener(recyclerView, baseSwipeHolder, dX)
                onDrawDefault(canvas, recyclerView, baseSwipeHolder, dX, dY, isCurrentlyActive)
            }
        }
    }

    /**
     * THIS IS THE IMPORTANT PART, getDefaultUIUtil handles translations for us and draws the UI.
     */
    private fun onDrawDefault(c: Canvas, recyclerView: RecyclerView, viewHolder: E, dX: Float, dY: Float, isCurrentlyActive: Boolean) {
        movDif = viewHolder.foreground.x
        getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.foreground, dX, dY, ItemTouchHelper.ACTION_STATE_SWIPE, isCurrentlyActive)
        viewHolder.drawActionItems(movDif)
        movDif -= viewHolder.foreground.x
    }

    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: E, dX: Float) {
        viewHolder.setReady()
        if (swipeBackTouchListener == null) {
            swipeBackTouchListener = SwipeBackTouchListener(recyclerView)
            swipeBackTouchListener?.update(viewHolder, dX)
            recyclerView.setOnTouchListener(swipeBackTouchListener)
            onSwipeStateChangedListener.onSwipeStarted(viewHolder)
        } else {
            swipeBackTouchListener?.update(viewHolder, dX)
        }
    }

    // RecyclerView.setOnTouchListener can be used for collapsing banners or dialogs,
    // I should provide a touch event callback to the customers of this
    internal inner class SwipeBackTouchListener(private val recyclerView: RecyclerView) : OnTouchListener {
        private lateinit var viewHolder: E
        private var dX = 0f

        init {
            setItemsClickable(recyclerView, false)
        }

        fun update(viewHolder: E, dX: Float) {
            this.viewHolder = viewHolder
            this.dX = dX
        }

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                viewHolder.setButtonStateAccordingToPos(dX)
                if (viewHolder.isRightVisible()) {
                    rightVisibleItem = viewHolder
                } else if (viewHolder.getButtonShowedState() === BaseSwipeViewHolder.ButtonsState.GONE && dX == 0f) {
                    swipeBack = false
                    return false
                }
            } else {
                resetSwipeController(recyclerView)
            }
            return false
        }

        fun onSwipedFinished() {
            resetRowToInitialPos(recyclerView, viewHolder, -viewHolder.rowWidth)
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun resetRowToInitialPos(recyclerView: RecyclerView, viewHolder: E, dX: Float) {
        setItemsClickable(recyclerView, false)
        recyclerView.setOnTouchListener(null)
        resetViewHolder(viewHolder)
        val ra = RecoverAnim(this, recyclerView, viewHolder, ItemTouchHelper.ACTION_STATE_IDLE, dX, 0F, 0F, 0F)
        ra.start()
    }

    private fun resetViewHolder(viewHolder: BaseSwipeViewHolder) {
        swipeBack = false
        // might need to check if viewHolder == rightVisibleItem
        swipeBackTouchListener = null
        viewHolder.reset()
        getDefaultUIUtil().clearView(viewHolder.foreground)
    }

    fun resetSwipeController(recyclerView: RecyclerView) {
        rightVisibleItem?.let {
            if (it.isRightVisible()) {
                resetRowToInitialPos(recyclerView, it, -it.getBackgroundViewWidth())
                rightVisibleItem = null
            }
        }
    }

    override fun onSwipeAnimationStarted(animation: Animator, animationType: RecoverAnim.SwipeAnimationType, recyclerView: RecyclerView, animatedView: E, dX: Float, dY: Float) {
        // SWIPE ANIMATION START MIGHT BE NEEDED FOR DIFFERENT REASON but the reason of the onSwipeStateChangedListener callback is not to let consumer know all animation starts
    }

    override fun onSwipeAnimationEnded(animation: Animator, animationType: RecoverAnim.SwipeAnimationType, recyclerView: RecyclerView, animatedView: E, dX: Float, dY: Float) {
        setItemsClickable(recyclerView, true)
        onSwipeStateChangedListener.onSwipeEnded(animatedView)
    }
}