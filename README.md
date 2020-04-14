# SwipeController

Limited version of the SwipeLibrary written by Oguz Emre Ozcan.

Used following library as data source and for inspration: https://github.com/FanFataL/swipe-controller-demo

Main advantage of this library is that it uses real views for automation purposes. 
Plus views are higly customizable unlike shapes.

Customer of this library only needs to extend from BaseSwipeViewHolder and call SwipeCallback class 
which is actually using SwipeController class in the background to swipe the views.
No change in adapter or recycler view is needed. This minimizes the code change required to implement swipes.

Animations are only used for rolling back swipes and fullswipes. 
Since animations usually slows down the recycler view new animations can be defined by changing the RecoverAnim class

Most importantly: Animation like behaviour while swiping is achieved by using ConstraintLayout/LinearLayout properties on swipeable rows.
This is customizable as well by changing layout_weight plus constraint properties.
