package com.oguz.swipelibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class adds long/short swipe functionality to Google Support Library's ItemTouchHelper.Callback.
 * The reason why to add such functionality are the means of showing context-aware actions
 * per each item in RecyclerView and letting users to launch them easily.
 * <p>
 * The short swipe from right to left reveals actions as a lineup of buttons
 * Clicking one launches the corresponding action.
 * The long swipe beyond a threshold (typically 75% of screen width) triggers the primary action,
 * which is the last one in the lineup.
 *
 * SwipeController action is the main class for the swipe library.
 * Purpose of this class is to hide implementation details of SwipeController
 * And provide a limited customization option to the customers.
 *
 * @see OnSwipeActionLaunchedListener
 */
public class SwipeCallback<E extends BaseSwipeViewHolder> {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<E> adapter;
    private SwipeController swipeController;
    private OnSwipeActionLaunchedListener<E> actionLaunchedListener;
    private OnSwipeStateChangedListener<E> stateChangedListener;
    private float mSwipeThreshold;

    protected void init(final Builder<E> builder) {
        recyclerView = builder.recyclerView;
        adapter = builder.adapter;
        actionLaunchedListener = builder.actionLaunchedListener;
        stateChangedListener = builder.stateChangedListener;
        mSwipeThreshold = builder.swipeThreshold;
        recyclerView.setAdapter(adapter);
        BaseSwipeViewHolder.setSwipeActionLaunchListener((OnSwipeActionLaunchedListener<BaseSwipeViewHolder>) actionLaunchedListener);
        BaseSwipeViewHolder.setSwipeThreshold(mSwipeThreshold);
        swipeController = new SwipeController(this, actionLaunchedListener, stateChangedListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    void resetSwipedView(int layoutPosition) {
        adapter.notifyItemChanged(layoutPosition);
    }

    public void resetSwipeController() {
        swipeController.resetSwipeController(recyclerView);
    }

    public interface OnResetSwipeController {
        void resetSwipeController();
    }

    /**
     * The builder to help with the initialization of SwipeCallback.
     */
    public static class Builder<E extends BaseSwipeViewHolder> {

        private static final float SWIPE_THRESHOLD = 0.75f;
        private RecyclerView recyclerView;
        private RecyclerView.Adapter<E> adapter;
        private OnSwipeActionLaunchedListener<E> actionLaunchedListener;
        private OnSwipeStateChangedListener<E> stateChangedListener;
        private float swipeThreshold;

        /**
         * Creates the empty builder which desperately needs its setter methods to be called.
         */
        public Builder() {
            swipeThreshold = SWIPE_THRESHOLD;
        }

        /**
         * Sets the RecyclerView to which this SwipeCallback will be attached to.
         */
        @NonNull
        public Builder<E> setRecyclerView(final @NonNull RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            return this;
        }

        /**
         * Sets the RecyclerView to which this SwipeCallback will be attached to.
         */
        @NonNull
        public Builder<E> setAdapter(final @NonNull RecyclerView.Adapter<E> adapter) {
            this.adapter = adapter;
            return this;
        }

        /**
         * Sets the OnSwipeActionLaunchedListener will be called each time an action is launched
         * either by clicking a button or doing a long swipe.
         */
        @NonNull
        public Builder<E> setOnSwipeActionLaunchedListener(final @Nullable OnSwipeActionLaunchedListener<E> actionListener) {
            actionLaunchedListener = actionListener;
            return this;
        }

        @NonNull
        public Builder<E> setOnSwipeStateChangedListener(final @Nullable OnSwipeStateChangedListener<E> stateListener) {
            stateChangedListener = stateListener;
            return this;
        }

        /**
         * Optionally sets swipe threshold after which a long swipe is triggered.
         * The threshold must be at least 0 and at most 1.
         */
        @NonNull
        public Builder<E> setSwipeThreshold(final float swipeThreshold) throws IndexOutOfBoundsException {
            if ((swipeThreshold < 0) || (swipeThreshold > 1)) {
                throw new IndexOutOfBoundsException("Swipe threshold must be at least 0 and at most 1.");
            }
            this.swipeThreshold = swipeThreshold;
            return this;
        }

        @NonNull
        public SwipeCallback<E> build() {
            final SwipeCallback<E> callback = new SwipeCallback<>();
            callback.init(this);
            return callback;
        }

        @NonNull
        @Override
        public String toString() {
            return "Builder{recyclerView=" + recyclerView +
                    ", actionLaunchedListener=" + actionLaunchedListener +
                    ", stateChangedListener=" + stateChangedListener +
                    ", swipeThreshold=" + swipeThreshold + '}';
        }
    }
}


