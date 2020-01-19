package com.dustedduke.findrecipe.ui.search

import android.util.AndroidException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.dustedduke.findrecipe.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class SearchViewModel : ViewModel() {


    private val recipeRepository: RecipeRepository = RecipeRepository()

    private var _foundRecipes: MutableLiveData<List<Recipe>> = MutableLiveData<List<Recipe>>() //recipeRepository.getRecipesInOrder("rating", 10)
    var foundRecipes: LiveData<List<Recipe>> = _foundRecipes

    private val _mediator = MediatorLiveData<List<Recipe>>()
    val mediator: LiveData<List<Recipe>> = _mediator






    fun updateIngredients(ingredients: List<String>) {

        Log.d("SearchViewModel", "Original live data:  " + _foundRecipes.toString() + " " + foundRecipes.toString())

        Log.d("SearchViewModel", "Updated ingredients with " + ingredients.toString())


//        var updatedRecipes: MutableLiveData<List<Recipe>> = recipeRepository.getRecipesWithIngredients(ingredients)

//        _mediator.addSource(recipeRepository.getRecipesWithIngredients(ingredients)) {
//
//        }

        val data = recipeRepository.getRecipesWithIngredients(ingredients)


        // TODO проверить что можно использовать
        data.observeForever(androidx.lifecycle.Observer {
            Log.d("SearchViewModel", "Observe forever, returned: " + it.toString())
            _foundRecipes.postValue(it)
        })


//        _mediator.addSource(data) {
//            Log.d("SearchViewModel", "Using mediator, returned: " + it.toString())
//            //_foundRecipes.value = it
//            _foundRecipes.postValue(it)
//            _mediator.removeSource(data)
//        }
//        _mediator.addSource(recipeRepository.getRecipesWithIngredients(ingredients), androidx.lifecycle.Observer {
//            Log.d("SearchViewModel", "Using mediator, returned: " + it.toString())
//            //_foundRecipes.value = it
//            _foundRecipes.postValue(it)
//        })





//        _foundRecipes = recipeRepository.getRecipesWithIngredients(ingredients)
//        foundRecipes = _foundRecipes

        Log.d("SearchViewModel", "New live data:  " + _foundRecipes.toString() + " " + foundRecipes.toString())
        Log.d("SearchViewModel", "_Found recipes " + _foundRecipes.value.toString())
        Log.d("SearchViewModel", "Found recipes " + foundRecipes.value.toString())

    }





}