package com.dustedduke.findrecipe

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dustedduke.findrecipe.ui.RecipeDescription
import com.dustedduke.findrecipe.ui.dashboard.CategoryFragment
import com.dustedduke.findrecipe.ui.search.SearchFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.storage.FirebaseStorage

class CategoryAdapter() : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var listener: (() -> Unit)? = null

    fun setListener(listener: (() -> Unit)?) {
        this.listener = listener
    }

    private val categories = emptyList<Category>().toMutableList()
    private val storageRef = FirebaseStorage.getInstance().reference

    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var itemId = ""
        var itemTitleText = ""
        var itemImage: ImageView
        var itemTitle: TextView

        init {
            itemImage = itemView.findViewById(R.id.categoryImageView)
            itemTitle = itemView.findViewById(R.id.categoryTitleView)

            itemView.setOnClickListener {

                Log.d("CATEGORY ID: ", itemId)
                Log.d("CATEGORY title: ", itemTitle.text.toString())

                listener?.invoke()

//                var categoryFragment: CategoryFragment  = CategoryFragment()
//
//                var bundle: Bundle = Bundle()
//                bundle.putStringArrayList("categoryTitle", itemTitle.text.toString())
//                categoryFragment.arguments = bundle
//
//                categoryFragment.arguments


            }

        }

    }

    fun setItems(newCategories: List<Category>) {

        Log.d("CATEGORYADAPTERTEST", "SETTING ITEMS: ${newCategories}")

        val diffResult  = DiffUtil.calculateDiff(CategoryDiffUtilCallback(categories, newCategories))

        categories.clear()
        categories.addAll(newCategories)

        Log.d("CATEGORYADAPTERTEST", "RECIPES UPDATED: ${categories}")

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_card, parent, false)
        return CategoryViewHolder(v)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        var category: Category = categories.get(position)

        //holder.itemImage.setImageURI(recipe.image)
        Log.d("STORAGE URL: ", category.image)

        Glide.with(holder.itemView)
            .load(category.image)
            .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
            .error(R.drawable.ic_camera)
            .fallback(R.drawable.ic_baseline_remove)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.itemImage)

        holder.itemId = category.id
        holder.itemTitle.setText(category.title)

        Log.d("CATEGORYADAPTERTEST", "HOLDER TEST: ${holder.itemTitle.text}")

    }

    override fun getItemCount(): Int {
        return categories.size
    }




}