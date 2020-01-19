package com.dustedduke.findrecipe.ui.search

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
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
import java.util.ArrayList
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import android.graphics.ColorSpace.Model
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dustedduke.findrecipe.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.SnapshotParser
import kotlinx.android.synthetic.main.activity_camera.view.*


class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    private val recipeAdapter = RecipeAdapterSearch()
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val recipeRepository: RecipeRepository = RecipeRepository()
    var foundRecipes: MutableLiveData<List<Recipe>>? = recipeRepository
        .getRecipesInOrder("rating", 10)

    val emptyList: List<Recipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val root =
            inflater.inflate(com.dustedduke.findrecipe.R.layout.fragment_search, container, false)



        recyclerView = root.findViewById<RecyclerView>(R.id.searchRecyclerView).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = recipeAdapter
            //recipeAdapter.setItems(emptyList)
        }


        // Get the SearchView and set the searchable configuration
        val search: SearchView = root.findViewById(R.id.searchField)
        search.isSubmitButtonEnabled = true
        search.isIconifiedByDefault = false
        search.focusable = View.NOT_FOCUSABLE
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // TODO db вызывается напрямую, поэтому found recipes не обновляется


                Log.d("SEARCH FRAGMENT: ", "QUERY SUBMITTED: " + query)
                var ingredients = query!!.split(" ")
                searchViewModel.updateIngredients(ingredients)
                //foundRecipes = recipeRepository.getRecipesWithIngredients(ingredients)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //To change body of created functions use File | Settings | File Templates.
//                search.setQuery(newText, false)
                return false
            }

        })

        searchViewModel.foundRecipes.observe(this, Observer {
            Log.d("SearchFragment", "Updating search recycler with " + it.toString())
            recipeAdapter.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("SEARCHFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter.itemCount}")
            Log.d("SEARCHFRAGMENTTEST", "RECIPES: " + it.toString())

        })

//        search.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(, SearchableActivity::class.java)))
//        val searchRequestString = arguments!!.getStringArrayList("request")
//        searchTitle.setText(searchRequestString.toString())

        var predictions = listOf<String>()

        arguments?.getStringArrayList("predictions")?.let {
            predictions = it.distinct()
            Log.d("SEARCH FRAGMENT: ", "RECEIVED: " + predictions.toString())
        }

        // TODO ошибка в regex с двойным пробелом

        if(predictions.isNotEmpty()) {
            // Submit: true
            var predictionsString = predictions.toString()
            var processedPredictionsString = predictionsString
                .subSequence(1, predictionsString.length - 1)
                .replace("[,]".toRegex(), "")

            Log.d("SEARCH FRAGMENT:  ", "PREDICT STRING: " + processedPredictionsString)
            search.setQuery(processedPredictionsString, true)
        }






//        searchViewModel.foundRecipes.observe(this, Observer {
//            recipeAdapter.setItems(it)
//            //recipeAdapter.notifyDataSetChanged()
//
//            Log.d("SEARCHFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter.itemCount}")
//
//        })

        return root
    }

}