package com.dustedduke.findrecipe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.activity_recipe_description.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class RecipeDescription : AppCompatActivity() {

    private val recipeRepository: RecipeRepository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_description)
        setSupportActionBar(toolbar)

        val currentUser: String = FirebaseAuth.getInstance().currentUser!!.uid

        // Load everything from database
        val itemId = intent.getStringExtra("recipeId")
        var imageLink = ""
        var date: Date = Date()
        Log.d("ItemID inside RECIPEDESCRIPTION: ", itemId)

        val editButton = findViewById<ImageButton>(R.id.recipeDescriptionEditButton)
        editButton.visibility = View.GONE
        editButton.setOnClickListener {

            // TODO edit intent

            val intent = Intent(it.context, RecipeDescriptionEdit::class.java)
            intent.putExtra("recipeId", itemId)
            it.context.startActivity(intent)

        }


        val recipe: LiveData<Recipe> = recipeRepository.getRecipeById(itemId)
        val isFavorite: LiveData<Boolean> = recipeRepository.checkIfFavorite(itemId)
        var favoriteActive = false

        var recipeId: String = ""
        val recipeToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.recipeDescription_toolbar_layout)
        val recipeDescriptionAuthor = findViewById<TextView>(R.id.recipeDescriptionAuthor)
        val recipeDescriptionDate = findViewById<TextView>(R.id.recipeDescriptionDate)
        val recipeDescriptionCategories = findViewById<TextView>(R.id.recipeDescriptionCategories)
        val recipeDescriptionText = findViewById<TextView>(R.id.recipeDescriptionText)
        val recipeDescriptionSteps = findViewById<TextView>(R.id.recipeDescriptionSteps)
        val recipeDescriptionImage = findViewById<ImageView>(R.id.recipeDescriptionImage)
        val recipeDescriptionIngredients = findViewById<TextView>(R.id.recipeDescriptiontIngredients)
        val recipeRatingBar = findViewById<RatingBar>(R.id.recipeRatingBar)
        val recipeRatingSubmitButton = findViewById<Button>(R.id.recipeRatingSubmitButton)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Added to Favorites", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            recipeRepository.updateFavoriteRecipes(itemId, recipeToolbarLayout.title.toString(), imageLink, date)

            // TODO кнопка не обновляется автоматически
            if(favoriteActive) {
                fab.setImageResource(R.drawable.ic_favorite_red_border_24dp)
                favoriteActive = false
            } else {
                fab.setImageResource(R.drawable.ic_favorite_red_fill_24dp)
                favoriteActive = true
            }

        }

        isFavorite.observe(this, Observer {
            if(it.equals(true)) {
                fab.setImageResource(R.drawable.ic_favorite_red_fill_24dp)
                favoriteActive = true
            } else {
                fab.setImageResource(R.drawable.ic_favorite_red_border_24dp)
                favoriteActive = false
            }
        })

        // Добавить данные асинхронно
        recipe.observe(this, Observer {

            val format = SimpleDateFormat("dd/MM/yyyy")

            Log.d("SETTING RECIPE", "RECIPE:" + it.id)
            recipeId = it.id
            recipeToolbarLayout.title = it.title
            recipeDescriptionAuthor.text = it.author
            recipeDescriptionDate.text = format.format(it.date)
            recipeDescriptionIngredients.text = it.ingredients.distinct().toString().subSequence(1, it.ingredients.distinct().toString().length - 1)
            recipeDescriptionCategories.text = it.categories.joinToString(", ")
            recipeDescriptionText.text = it.description
            recipeDescriptionSteps.text = it.steps
            imageLink = it.image
            date = it.date

            // TODO берется общий рейтинг
            recipeRatingBar.rating = it.rating

            Log.d("Checking if", it.authorId + " == " + currentUser)

            if(it.authorId.toString() == currentUser.toString()) {
                Log.d("User created this recipe", it.title)
                editButton.visibility = View.VISIBLE
            } else {
                Log.d("User didn't create this recipe", it.title)
            }

            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                .error(R.drawable.ic_camera)
                .fallback(R.drawable.ic_baseline_remove)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(recipeDescriptionImage)

        })

        // TODO использовать ratingbar и reviews
        // TODO можно не использовать review потому что рейтинг постоянно меняется

        recipeRatingSubmitButton.setOnClickListener {
            // TODO update rating here
            recipeRepository.createRating(currentUser, recipeId, recipeRatingBar.rating)
            recipeRatingSubmitButton.visibility = View.GONE

            // TODO need to count number of reviews

            // TODO update rating in recipe
            recipeRepository.updateRecipeRating(recipeId, recipeRatingBar.rating)

        }

        val currentUserRatedThisRecipe: LiveData<Boolean> = recipeRepository.checkIfUserRated(currentUser, recipeId)

        currentUserRatedThisRecipe.observe(this, Observer {
            if(it == true) {
                recipeRatingSubmitButton.visibility = View.GONE
            } else {
                recipeRatingSubmitButton.visibility = View.VISIBLE
            }
        })


    }
}
