package com.dustedduke.findrecipe

import androidx.recyclerview.widget.DiffUtil

class CategoryDiffUtilCallback(
    private val oldList: List<Category>,
    private val newList: List<Category>
): DiffUtil.Callback() {


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

}