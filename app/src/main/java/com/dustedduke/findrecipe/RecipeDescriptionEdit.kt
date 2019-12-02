package com.dustedduke.findrecipe

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_recipe_description_edit.*
import java.util.Observer

class RecipeDescriptionEdit : AppCompatActivity() {

    private val recipeRepository: RecipeRepository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_description_edit)
        setSupportActionBar(toolbar)

        val itemId = intent.getStringExtra("recipeId")
        Log.d("ItemID inside EDITRECIPEDESCRIPTION: ", itemId)

        val recipe: LiveData<Recipe> = recipeRepository.getRecipeById(itemId)

        val recipeDescriptionAuthor = findViewById<TextView>(R.id.recipeDescriptionAuthorEdit)
        val recipeDescriptionDate = findViewById<TextView>(R.id.recipeDescriptionDateEdit)
        val recipeDescriptionCategories = findViewById<TextView>(R.id.recipeDescriptionCategoriesEdit)
        val recipeDescriptionText = findViewById<TextView>(R.id.recipeDescriptionTextEdit)
        val recipeDescriptionSteps = findViewById<TextView>(R.id.recipeDescriptionStepsEdit)
        val recipeDescriptionImage = findViewById<ImageView>(R.id.recipeDescriptionImageEdit)

        recipe.observe(this, Observer {
            Log.d("SETTING RECIPE", "RECIPE")
            recipeDescriptionAuthor.text = it.author
            recipeDescriptionDate.text = it.date.toString()
            recipeDescriptionCategories.text = it.categories.joinToString(", ")
            recipeDescriptionText.text = it.description
            recipeDescriptionSteps.text = it.steps

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                .error(R.drawable.ic_camera)
                .fallback(R.drawable.ic_baseline_remove)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(recipeDescriptionImage)

        })


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}
