package com.dustedduke.findrecipe.ui.home

import android.util.AndroidException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.dustedduke.findrecipe.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class HomeViewModel : ViewModel() {

    val fbAuth = FirebaseAuth.getInstance()

    private val recipeRepository: RecipeRepository = RecipeRepository()

    private val _popularRecipes: MutableLiveData<List<Recipe>> = recipeRepository.getRecipesInOrder("rating", 10)
    val popularRecipes: LiveData<List<Recipe>> = _popularRecipes

    private val _user: MutableLiveData<User> = recipeRepository.getUserById(fbAuth.currentUser!!.uid)
    var user: LiveData<User> = _user




}