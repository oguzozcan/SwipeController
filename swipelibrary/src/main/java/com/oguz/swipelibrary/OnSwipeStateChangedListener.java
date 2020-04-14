package com.oguz.swipelibrary;

import androidx.annotation.NonNull;

public interface OnSwipeStateChangedListener<E extends BaseSwipeViewHolder> {

    void onSwipeStarted(final @NonNull E nextHolder);

    void onSwipeEnded(final @NonNull E prevHolder);

    // TODO optional
    //void onSwipeCancelled(final @NonNull E prevHolder);
}
