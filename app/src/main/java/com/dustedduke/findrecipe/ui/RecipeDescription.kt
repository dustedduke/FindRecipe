package com.dustedduke.findrecipe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dustedduke.findrecipe.R
import com.dustedduke.findrecipe.Recipe
import com.dustedduke.findrecipe.RecipeDescriptionEdit
import com.dustedduke.findrecipe.RecipeRepository
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.activity_recipe_description.*
import org.w3c.dom.Text

class RecipeDescription : AppCompatActivity() {

    private val recipeRepository: RecipeRepository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_description)
        setSupportActionBar(toolbar)

        // Load everything from database
        val itemId = intent.getStringExtra("recipeId")
        Log.d("ItemID inside RECIPEDESCRIPTION: ", itemId)

        val editButton = findViewById<ImageButton>(R.id.recipeDescriptionEditButton)
        editButton.setOnClickListener {

            // TODO edit intent

            val intent = Intent(it.context, RecipeDescriptionEdit::class.java)
            intent.putExtra("recipeId", itemId)
            it.context.startActivity(intent)

        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            recipeRepository.updateFavoriteRecipes(itemId)

        }

        val recipe: LiveData<Recipe> = recipeRepository.getRecipeById(itemId)


        val recipeToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.recipeDescription_toolbar_layout)
        val recipeDescriptionAuthor = findViewById<TextView>(R.id.recipeDescriptionAuthor)
        val recipeDescriptionDate = findViewById<TextView>(R.id.recipeDescriptionDate)
        val recipeDescriptionCategories = findViewById<TextView>(R.id.recipeDescriptionCategories)
        val recipeDescriptionText = findViewById<TextView>(R.id.recipeDescriptionText)
        val recipeDescriptionSteps = findViewById<TextView>(R.id.recipeDescriptionSteps)
        val recipeDescriptionImage = findViewById<ImageView>(R.id.recipeDescriptionImage)

        // Добавить данные асинхронно
        recipe.observe(this, Observer {
            Log.d("SETTING RECIPE", "RECIPE")
            recipeToolbarLayout.title = it.title
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


    }
}
