package com.oguz.swipelibrary

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.oguz.swipelibrary.BaseSwipeViewHolder.ViewHolderType.*;
import kotlin.math.min

/**
 * Purpose of this class is to be a wrapper for any viewHolder class
 * Wrapping basically adds swipeable and clickable views to adapter items.
 * Same functionality could be implemented in adapter but this would require bigger changes
 * Especially for projects which already have different types of adapters.
 *
 * It also defines rules for viewHolder, some of these rules can be changed by overriding
 */
abstract class BaseSwipeViewHolder(parent: ViewGroup, foregroundLayoutId: Int, private val viewHolderTypeId: Int, private val resetSwipeControllerListener: SwipeCallback.OnResetSwipeController) : ViewHolder(wrapSwipeableRowLayout(parent, foregroundLayoutId, viewHolderTypeId)) {

    private var buttonShowedState = ButtonsState.GONE
    private val background: ViewGroup = itemView.findViewById<View>(R.id.background) as ViewGroup
    private var isResetted = false
    private lateinit var swipeActions: Array<SwipeActionProvider.SwipeAction>
    private var backgroundWidth: Int = parent.resources.getDimensionPixelSize(R.dimen.sel_swipe_button_width)
    //Minumum backgroundWidth
    private var minbackgroundWidth: Int = parent.resources.getDimensionPixelSize(R.dimen.sel_swipe_button_width)
    var isAnimating = false
    val foreground: ViewGroup = itemView.findViewById<View>(R.id.foreground) as ViewGroup
    val rowWidth: Float = parent.context.resources.displayMetrics.widthPixels.toFloat()

    companion object {

        private lateinit var onSwipeActionLaunchedListener: OnSwipeActionLaunchedListener<BaseSwipeViewHolder>
        // Full-swipe threshold - swipes after x% of the screen counted as full swipe
        private var swipeThreshold: Float = 0.75f

        private fun wrapSwipeableRowLayout(parent: ViewGroup, foregroundLayoutId: Int, viewHolderTypeId: Int): View {
            val inflater = LayoutInflater.from(parent.context)
            val parentView = inflater.inflate(R.layout.base_swipeable_row, parent, false)
            val parentForegroundView = parentView.findViewById<ViewGroup>(R.id.foreground)
            inflater.inflate(foregroundLayoutId, parentForegroundView, true)
            val viewHolderType = getViewHolderType(viewHolderTypeId = viewHolderTypeId)
            // This means NO_ACTION_BUTTON
            if (viewHolderType.getBackgroundLayoutId() != 0) {
                val parentBackgroundView = parentView.findViewById<ViewGroup>(R.id.background)
                inflater.inflate(viewHolderType.getBackgroundLayoutId(), parentBackgroundView, true)
            }
            return parentView
        }

        private fun getViewHolderType(viewHolderTypeId: Int): ViewHolderType {
            return when (viewHolderTypeId) {
                ONE_ACTION_BUTTON.getTypeId() -> ONE_ACTION_BUTTON
                TWO_ACTION_BUTTON.getTypeId() -> TWO_ACTION_BUTTON
                THREE_ACTION_BUTTON.getTypeId() -> THREE_ACTION_BUTTON
                else -> NO_ACTION_BUTTON
            }
        }

        @JvmStatic
        fun getItemViewType(swipeActionSize : Int) : Int {
            return when (swipeActionSize) {
                1 -> ONE_ACTION_BUTTON.getTypeId()
                2 -> TWO_ACTION_BUTTON.getTypeId()
                3 -> THREE_ACTION_BUTTON.getTypeId()
                else -> NO_ACTION_BUTTON.getTypeId()
            }
        }

        @JvmStatic
        fun setSwipeActionLaunchListener(onSwipeActionLaunchedListener: OnSwipeActionLaunchedListener<BaseSwipeViewHolder>) {
            BaseSwipeViewHolder.onSwipeActionLaunchedListener = onSwipeActionLaunchedListener
        }

        /**
         * @param swipeThreshold : Min screen percentage for swipe actions to be counted as full-swipes
         */
        @JvmStatic
        fun setSwipeThreshold(swipeThreshold: Float) {
            BaseSwipeViewHolder.swipeThreshold = swipeThreshold
        }
    }

    internal open fun setButtonShowedState(buttonShowedState: ButtonsState) {
        this.buttonShowedState = buttonShowedState
        setActionButtonState()
    }

    fun getButtonShowedState(): ButtonsState {
        return buttonShowedState
    }

    fun isRightVisible(): Boolean {
        return buttonShowedState == ButtonsState.RIGHT_VISIBLE
    }

    //Returns last swipeAction as the main one
    open fun getMainSwipeAction(): SwipeActionProvider.SwipeAction? {
        return if (swipeActions.isNotEmpty()) {
            swipeActions[swipeActions.size - 1]
        } else null
    }

    open fun bind(context: Context, swipeActions: Array<SwipeActionProvider.SwipeAction>) {
        this.swipeActions = swipeActions

        itemView.setOnClickListener {
            if (buttonShowedState == ButtonsState.GONE) {
                onSwipeActionLaunchedListener.onItemClicked(this)
                resetSwipeControllerListener.resetSwipeController()
            }
        }
        itemView.setOnLongClickListener(View.OnLongClickListener {
            if (buttonShowedState == ButtonsState.GONE) {
                onSwipeActionLaunchedListener.onItemLongClicked(this)
                resetSwipeControllerListener.resetSwipeController()
                return@OnLongClickListener true
            } else {
                return@OnLongClickListener false
            }
        })
        swipeActions.forEachIndexed { index, swipeAction -> swipeAction.setActionProperties(context, index, onSwipeActionLaunchedListener) }
        setBackgroundViewProperties(swipeActions = swipeActions)
    }

    private fun setBackgroundViewProperties(swipeActions: Array<SwipeActionProvider.SwipeAction>) {
        if (swipeActions.isNotEmpty()) {
            backgroundWidth = swipeActions.size * getActionView(swipeActions.size - 1, itemView).width
        }
    }

    private fun getActionView(index: Int, parentView: View): TextView {
        return when (index) {
            0 -> parentView.findViewById(R.id.actionButton1)
            1 -> parentView.findViewById(R.id.actionButton2)
            2 -> parentView.findViewById(R.id.actionButton3)
            else -> {
                throw IllegalArgumentException("SwipeController only supports 3 swipe actions now")
            }
        }
    }

    private fun SwipeActionProvider.SwipeAction.setActionProperties(context: Context, index: Int, onSwipeActionLaunchedListener: OnSwipeActionLaunchedListener<BaseSwipeViewHolder>) {
        val actionView = getActionView(index, itemView)
        val backgroundColor = ContextCompat.getColor(context, this.backgroundColor)
        actionView.setBackgroundColor(backgroundColor)
        actionView.setTextColor(ContextCompat.getColor(context, this.textColor))
        actionView.setCompoundDrawablesWithIntrinsicBounds(0, this.drawable, 0, 0)
        // Modify this textsize
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(this.textSize))
        actionView.setText(this.text)
        actionView.setOnClickListener {
            if (isRightVisible()) {
                onSwipeActionLaunchedListener.onSwipeActionLaunched(this@BaseSwipeViewHolder, this)
            } else {
                onSwipeActionLaunchedListener.onItemClicked(this@BaseSwipeViewHolder)
            }
            resetSwipeControllerListener.resetSwipeController()
        }
    }

    internal fun drawActionItems(movDif: Float) {
        val backgroundRight = background.x + background.width
        val foregroundRight = foreground.x + foreground.width
        val dif = foregroundRight - backgroundRight
        if (dif != 0f) {
            background.x = foregroundRight
            val curMovement: Float = rowWidth - foregroundRight
            if (movDif != 0f && curMovement >= getBackgroundViewWidth()) {
                background.layoutParams = ConstraintLayout.LayoutParams(curMovement.toInt(), background.height)
            }
        }
    }

    open fun reset() {
        if (buttonShowedState != ButtonsState.GONE) {
            setButtonShowedState(ButtonsState.GONE)
        }
        isResetted = true
    }

    open fun setReady() {
        isResetted = false
    }

    private fun setActionButtonState() {
        setActionButtonsEnabled(isRightVisible())
    }

    private fun setActionButtonsEnabled(isEnabled: Boolean) {
        //for (action in getActions()) {
        //action.actionView.isEnabled = isEnabled
        //}
        itemView.isEnabled = !isEnabled
    }

    private fun getFullSwipeLimit(): Float {
        if (getViewHolderType(viewHolderTypeId) == NO_ACTION_BUTTON) {
            return 0F
        }
        return -rowWidth * swipeThreshold
    }

    internal fun getBackgroundViewWidth(): Float {
        return when (getViewHolderType(viewHolderTypeId)) {
            ONE_ACTION_BUTTON -> minbackgroundWidth.toFloat()
            TWO_ACTION_BUTTON -> minbackgroundWidth * TWO_ACTION_BUTTON.getActionCount().toFloat()
            THREE_ACTION_BUTTON -> minbackgroundWidth * THREE_ACTION_BUTTON.getActionCount().toFloat()
            else -> 0F
        }
    }

    private fun getRollbackLimit(): Float {
        if (THREE_ACTION_BUTTON == getViewHolderType(viewHolderTypeId)) {
            return -(getBackgroundViewWidth() / THREE_ACTION_BUTTON.getActionCount())
        }
        return -(getBackgroundViewWidth() / TWO_ACTION_BUTTON.getActionCount())
    }

    internal fun setButtonStateAccordingToPos(dX: Float) {
        buttonShowedState = when {
            dX < getFullSwipeLimit() -> {
                ButtonsState.FULLY_SWIPED
            }
            dX < getRollbackLimit() -> {
                ButtonsState.RIGHT_VISIBLE
            }
            else -> ButtonsState.GONE
        }
        setActionButtonState()
    }

    internal open fun decideStoppingPosAccordingToState(dX: Float): Float {
        return when (buttonShowedState) {
            ButtonsState.RIGHT_VISIBLE -> min(dX, -getBackgroundViewWidth())
            ButtonsState.FULLY_SWIPED -> min(dX, -rowWidth)
            else -> dX
        }
    }

    enum class ButtonsState {
        GONE,
        RIGHT_VISIBLE,
        FULLY_SWIPED
    }

    enum class ViewHolderType constructor(private val id: Int, private val layoutId: Int, private val actionCount : Int) {
        NO_ACTION_BUTTON(0, 0, 0),
        ONE_ACTION_BUTTON(1, R.layout.one_button_layout, 1),
        TWO_ACTION_BUTTON(2, R.layout.two_button_layout, 2),
        THREE_ACTION_BUTTON(3, R.layout.three_button_layout, 3);

        fun getTypeId(): Int {
            return id
        }

        fun getBackgroundLayoutId(): Int {
            return layoutId
        }

        fun getActionCount() : Int {
            return actionCount
        }
    }
}
