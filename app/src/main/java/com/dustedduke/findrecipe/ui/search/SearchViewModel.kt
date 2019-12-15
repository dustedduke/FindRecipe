package com.dustedduke.findrecipe.ui.search

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
import com.google.firebase.firestore.Query
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class SearchViewModel : ViewModel() {


    private val recipeRepository: RecipeRepository = RecipeRepository()

    private val _foundRecipes: MutableLiveData<List<Recipe>> = recipeRepository.getRecipesInOrder("rating", 10)
    val foundRecipes: LiveData<List<Recipe>> = _foundRecipes





}