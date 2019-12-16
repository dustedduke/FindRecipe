package com.dustedduke.findrecipe.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dustedduke.findrecipe.Recipe
import com.dustedduke.findrecipe.RecipeRepository
import com.dustedduke.findrecipe.User
import com.google.firebase.auth.FirebaseAuth

class UserViewModel : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is notifications Fragment"
//    }
//    val text: LiveData<String> = _text

    private val recipeRepository: RecipeRepository = RecipeRepository()

    private val _user: LiveData<User> = recipeRepository.getUserById(FirebaseAuth.getInstance().currentUser!!.uid)
    val user: LiveData<User> = _user

    private val _favoriteRecipes: MutableLiveData<List<Recipe>> = recipeRepository.getUserFavoritesById(FirebaseAuth.getInstance().currentUser!!.uid)
    val favoriteRecipes: LiveData<List<Recipe>> = _favoriteRecipes

    private val _createdRecipes: MutableLiveData<List<Recipe>> = recipeRepository.getUserCreatedById(FirebaseAuth.getInstance().currentUser!!.uid)
    val createdRecipes: LiveData<List<Recipe>> = _createdRecipes

}