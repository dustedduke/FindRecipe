package com.dustedduke.findrecipe

import android.content.Intent
import android.net.Uri
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.storage.FirebaseStorage


//class RecipeAdapter(): FirestoreRecyclerAdapter<Recipe, RecipeAdapter.RecipeViewHolder>() {
//
//    private val recipes = emptyList<Recipe>().toMutableList()
//
//    inner class RecipeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        var itemImage: ImageView
//        var itemTitle: TextView
//        var itemDescription: TextView
//
//        init {
//            itemImage = itemView.findViewById(R.id.recipeImageView)
//            itemTitle = itemView.findViewById(R.id.recipeTitleView)
//            itemDescription = itemView.findViewById(R.id.recipeDescriptionView)
//        }
//
//    }
//
//    fun setItems(newRecipes: List<Recipe>) {
//        val diffResult  = DiffUtil.calculateDiff(RecipeDiffUtilCallback(recipes, newRecipes))
//
//        recipes.clear()
//        recipes.addAll(newRecipes)
//
//        diffResult.dispatchUpdatesTo(this)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
//        val v = LayoutInflater.from(parent.context)
//            .inflate(R.layout.recipe_card, parent, false)
//        return RecipeViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int, recipe: Recipe) {
////        var recipe: Recipe = recipes.get(position)
//        //holder.itemImage.setImageURI(recipe.image)
//        holder.itemTitle.setText(recipe.title)
//        holder.itemDescription.setText(recipe.description)
//    }
//
//    override fun getItemCount(): Int {
//        return recipes.size
//    }
//}


class RecipeAdapter() : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val recipes = emptyList<Recipe>().toMutableList()
    private val storageRef = FirebaseStorage.getInstance().reference

    inner class RecipeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var itemId = ""
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDescription: TextView

        init {
            itemImage = itemView.findViewById(R.id.recipeImageView)
            itemTitle = itemView.findViewById(R.id.recipeTitleView)
            itemDescription = itemView.findViewById(R.id.recipeDescriptionView)

            itemView.setOnClickListener {

                Log.d("RECIPE ID: ", itemId)

                val intent = Intent(itemView.context, RecipeDescription::class.java)
                intent.putExtra("recipeId", itemId)
                itemView.context.startActivity(intent)
            }

        }

    }

    fun setItems(newRecipes: List<Recipe>) {

        Log.d("RECIPEADAPTERTEST", "SETTING ITEMS: ${newRecipes}")

        val diffResult  = DiffUtil.calculateDiff(RecipeDiffUtilCallback(recipes, newRecipes))

        recipes.clear()
        recipes.addAll(newRecipes)

        Log.d("RECIPEADAPTERTEST", "RECIPES UPDATED: ${recipes}")

        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_card, parent, false)
        return RecipeViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        var recipe: Recipe = recipes.get(position)

        //holder.itemImage.setImageURI(recipe.image)
        Log.d("STORAGE URL: ", recipe.image)

        Glide.with(holder.itemView)
            .load(recipe.image)
            .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
            .error(R.drawable.ic_camera)
            .fallback(R.drawable.ic_baseline_remove)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.itemImage)

        holder.itemId = recipe.id
        holder.itemTitle.setText(recipe.title)
        holder.itemDescription.setText(recipe.description)

        Log.d("RECIPEADAPTERTEST", "HOLDER TEST: ${holder.itemTitle.text}")

    }

    override fun getItemCount(): Int {
        return recipes.size
    }




}