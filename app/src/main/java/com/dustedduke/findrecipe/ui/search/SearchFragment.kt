package com.dustedduke.findrecipe.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
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
import android.widget.*
import androidx.lifecycle.MutableLiveData
import com.dustedduke.findrecipe.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text


class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel

    private val recipeAdapter = RecipeAdapterSearch()



    // TODO попробовать отключить recyclerview, когда нет поиска
    // View.GONE

//    private val searchRecyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    private val recipeAdapter1 = RecipeAdapter()
    private val recipeAdapter2 = RecipeAdapter()
    private val recipeAdapter3 = RecipeAdapter()

//    private val recyclerView1: RecyclerView? = null
//    private val recyclerView2: RecyclerView? = null
//    private val recyclerView3: RecyclerView? = null


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


        val fabButton = root.findViewById<FloatingActionButton>(R.id.fabSearch)
        fabButton.hide()
        fabButton.setOnClickListener {
            Log.d("Pushed home fab button", "Pushed button")
            val intent = Intent(activity!!.applicationContext, RecipeCreateEdit::class.java)
            startActivity(intent)
        }

        val recyclerTextView1 = root.findViewById<TextView>(R.id.SearchHeader1)
        val recyclerTextView2 = root.findViewById<TextView>(R.id.SearchHeader2)
        val recyclerTextView3 = root.findViewById<TextView>(R.id.SearchHeader3)


        recyclerTextView3.visibility = View.GONE


        val recyclerView1 = root.findViewById<RecyclerView>(R.id.recipesRecyclerView1Search).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeAdapter1
        }

        val recyclerView2 = root.findViewById<RecyclerView>(R.id.recipesRecyclerView2Search).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeAdapter2
        }

//        val recyclerView3 = root.findViewById<RecyclerView>(R.id.recipesRecyclerView3).apply {
//            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = recipeAdapter3
//        }


        val searchRecyclerView = root.findViewById<RecyclerView>(R.id.searchRecyclerView).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

//            setRVLayoutManager()
//            setRVScrollListener()

            // TODO зачем это
            recipeAdapter.notifyDataSetChanged()
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



                recyclerView1.visibility = View.GONE
                recyclerView2.visibility = View.GONE

                recyclerTextView1.visibility = View.GONE
                recyclerTextView2.visibility = View.GONE

                searchRecyclerView.visibility = View.VISIBLE


                //foundRecipes = recipeRepository.getRecipesWithIngredients(ingredients)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //To change body of created functions use File | Settings | File Templates.
//                search.setQuery(newText, false)
                return false
            }



        })



        search.setOnCloseListener(object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {

                Log.d("SEARCH", "CLOSE BUTTON HIT")

                recipeAdapter1.setItems(emptyList)

                recyclerView1.visibility = View.VISIBLE
                recyclerView2.visibility = View.VISIBLE

                recyclerTextView1.visibility = View.VISIBLE
                recyclerTextView2.visibility = View.VISIBLE

                searchRecyclerView.visibility = View.GONE
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

        searchViewModel.popularRecipes.observe(this, Observer {
            recipeAdapter1.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("SEARCHFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter1.itemCount}")

        })

        searchViewModel.newRecipes.observe(this, Observer {
            recipeAdapter2.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("SEARCHFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter2.itemCount}")

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


        searchViewModel.user.observe(this, Observer {
            if(it.type == "chef") {
                fabButton.show()
            } else {
                fabButton.hide()
            }
        })




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