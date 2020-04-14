package com.oguz.swipelibrary;

import androidx.annotation.NonNull;

public interface OnSwipeActionLaunchedListener<E extends BaseSwipeViewHolder> {

    /**
     * Callback is called after an action is launched by either action-button click or long swipe.
     *
     * @param viewHolder  The ViewHolder on which the action is exercises.
     * @param swipeAction The launched action.
     */
    void onSwipeActionLaunched(final @NonNull E viewHolder, final @NonNull SwipeActionProvider.SwipeAction swipeAction);

    /**
     * Callback is called with a list item click. Item click events usually ends the animation or whole interaction
     *
     * @param viewHolder  The ViewHolder on which the action is exercises.
     */
    void onItemClicked(final @NonNull E viewHolder);

    /**
     * Callback is called with a list item long click. Long click can change action mode (ex: item selection for deletion)
     *
     * @param viewHolder  The ViewHolder on which the action is exercises.
     */
    void onItemLongClicked(final @NonNull E viewHolder);
}
