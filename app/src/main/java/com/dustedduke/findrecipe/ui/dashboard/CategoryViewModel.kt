package com.dustedduke.findrecipe.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dustedduke.findrecipe.Category
import com.dustedduke.findrecipe.Recipe
import com.dustedduke.findrecipe.RecipeRepository

class CategoryViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is dashboard Fragment"
//    }
//    val text: LiveData<String> = _text


    private val recipeRepository: RecipeRepository = RecipeRepository()

    private val _categories: MutableLiveData<List<Category>> = recipeRepository.getCategories()
    val categories: LiveData<List<Category>> = _categories



}