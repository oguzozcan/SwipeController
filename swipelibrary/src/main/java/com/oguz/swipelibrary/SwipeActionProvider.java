package com.oguz.swipelibrary;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This interface is used by SwipeCallback to determine what actions are available
 * for a particular RecyclerView.ViewHolder.
 *
 * @see SwipeCallback
 * @see SwipeAction
 */
public interface SwipeActionProvider<E extends RecyclerView.ViewHolder> {

    /**
     * The method called by SwipeCallback to provide available SwipeActions
     * for a particular RecyclerView.ViewHolder which is being swiped.
     *
     * @return An array of actions available for the viewHolder.
     */
    SwipeAction[] getActions(final @NonNull E viewHolder);


    /**
     * The interface represents a single action launchable by clicking a button or performing a long swipe.
     * Actions are supposed to be context sensitive in the terms of ViewHolder being manipulated with.
     * A button is visual representation of an action revealed by performing a short swipe.
     * While buttons are revealed by performing a short swipe
     */
    interface SwipeAction {

        //TODO
        // Padding and size can be defined seperately

        /**
         * Returns the resource ID of action's background color.
         */
        @ColorRes int getBackgroundColor();

        /**
         * Returns the resource ID of action's text color.
         */
        @ColorRes int getTextColor();

        /**
         * Returns the resource ID of action's test size.
         */
        @DimenRes int getTextSize();

        /**
         * Returns the resource ID of action's text.
         */
        @StringRes int getText();

        /**
         * Returns the resource ID of action's drawable.
         */
        @DrawableRes int getDrawable();
    }
}

