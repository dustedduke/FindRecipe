package com.dustedduke.findrecipe

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var itemImage: ImageView
    var itemTitle: TextView

    init {
        itemImage = itemView.findViewById(R.id.categoryImageView)
        itemTitle = itemView.findViewById(R.id.categoryTitleView)
    }

}