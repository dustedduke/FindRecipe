package com.dustedduke.findrecipe

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dustedduke.findrecipe.ui.RecipeDescription

class RecipeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var itemImage: ImageView
    var itemTitle: TextView
    var itemDescription: TextView

    init {
        itemImage = itemView.findViewById(R.id.recipeImageView)
        itemTitle = itemView.findViewById(R.id.recipeTitleView)
        itemDescription = itemView.findViewById(R.id.recipeDescriptionView)
    }

}
