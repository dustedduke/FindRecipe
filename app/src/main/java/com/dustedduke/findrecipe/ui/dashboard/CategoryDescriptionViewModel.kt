package com.dustedduke.findrecipe.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dustedduke.findrecipe.Category
import com.dustedduke.findrecipe.Recipe
import com.dustedduke.findrecipe.RecipeRepository

class CategoryDescriptionViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text


    private val recipeRepository: RecipeRepository = RecipeRepository()

    private var _recipes: MutableLiveData<List<Recipe>> = MutableLiveData<List<Recipe>>()
    var recipes: LiveData<List<Recipe>> = _recipes

    fun updateCategory(categoryName: String) {
        _recipes = recipeRepository.getRecipesWithCategory(categoryName)
        recipes = _recipes
    }



}