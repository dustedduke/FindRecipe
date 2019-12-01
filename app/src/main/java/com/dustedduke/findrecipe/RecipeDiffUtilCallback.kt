package com.dustedduke.findrecipe

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Anatoliy Lukyanov on 09/03/2019.
 *
 */
class RecipeDiffUtilCallback(
    private val oldList: List<Recipe>,
    private val newList: List<Recipe>
): DiffUtil.Callback() {



    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }
}