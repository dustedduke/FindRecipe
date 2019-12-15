package com.dustedduke.findrecipe

import java.util.*

data class Category(
    var id: String = "",
    var title: String = "",
    var order: Int = 0,
    var description: String = "",
    var image: String = ""
)