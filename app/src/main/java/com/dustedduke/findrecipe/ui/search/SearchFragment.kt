package com.dustedduke.findrecipe.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dustedduke.findrecipe.R
import com.dustedduke.findrecipe.RecipeAdapter
import com.dustedduke.findrecipe.Recipe
import com.dustedduke.findrecipe.RecipeRepository
import java.util.ArrayList
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Toast
import com.dustedduke.findrecipe.MainActivity
import android.graphics.ColorSpace.Model
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.SnapshotParser




class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    private val recipeAdapter = RecipeAdapter()
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val root =
            inflater.inflate(com.dustedduke.findrecipe.R.layout.fragment_search, container, false)


        var searchTitle = root.findViewById<TextView>(R.id.text_search)

//        val searchRequestString = arguments!!.getStringArrayList("request")
//        searchTitle.setText(searchRequestString.toString())

        var predictions = ArrayList<String>()

        arguments?.getStringArrayList("predictions")?.let {
            predictions = it
            searchTitle.setText(predictions.toString())
        }





        searchViewModel.popularRecipes.observe(this, Observer {
            recipeAdapter.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("SEARCHFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter.itemCount}")

        })

        Log.d("SEARCHFRAGMENTTEST", "RECIPEADAPTER ITEM COUNT ${recipeAdapter.itemCount}")

        return root
    }

}