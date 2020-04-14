package com.oguz.swipecontroller

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.oguz.swipelibrary.SwipeActionProvider

//Could be defined as data class
enum class ActionType(@field:ColorRes private val backgroundColor: Int, @field:DrawableRes private val drawable: Int, @field:StringRes private val text: Int) : SwipeActionProvider.SwipeAction {

    ONE(
        R.color.swipe_button_background_one,
        R.drawable.ic_submit,
        R.string.swipe_button_one),
    TWO(
        R.color.swipe_button_background_two,
        R.drawable.ic_close,
        R.string.swipe_button_two),
    //Last Item is the main action button that gets triggered on FullSwipes
    THREE(
        R.color.swipe_button_background_three,
        R.drawable.ic_round_trip,
        R.string.swipe_button_three);

    @ColorRes
    override fun getBackgroundColor(): Int {
        return backgroundColor
    }

    @ColorRes
    override fun getTextColor(): Int {
        return R.color.white
    }

    @DimenRes
    override fun getTextSize(): Int {
        return R.dimen.swipe_button_text_size
    }

    @StringRes
    override fun getText(): Int {
        return text
    }

    @DrawableRes
    override fun getDrawable(): Int {
        return drawable
    }
}