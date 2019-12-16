package com.dustedduke.findrecipe

import java.util.*

data class Recipe(
    var id: String = "",
    var title: String = "",
    var author: String = "",
    var authorId: String = "",
    var description: String = "",
    var steps: String = "",
    var ingredients: List<String> = emptyList<String>(),
    var categories: List<String> = emptyList<String>(),
    var rating: Float = 0.0F,
    var date: Date = Date(),
    var image: String = ""
)