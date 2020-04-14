package com.oguz.swipecontroller

import com.oguz.swipelibrary.BaseSwipeViewHolder

data class Player(
    var name: String? = null,
    var nationality: String = "",
    var club: String = "",
    var rating: Int = 0,
    var age: Int = 0,
    var viewHolderType: BaseSwipeViewHolder.ViewHolderType? = null
)