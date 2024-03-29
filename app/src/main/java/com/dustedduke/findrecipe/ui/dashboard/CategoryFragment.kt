package com.dustedduke.findrecipe.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dustedduke.findrecipe.CategoryAdapter
import com.dustedduke.findrecipe.R
import com.dustedduke.findrecipe.RecipeAdapter

class CategoryFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel

    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryViewModel =
            ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val categoryAdapter = CategoryAdapter(fragmentManager!!)


        recyclerView = root.findViewById<RecyclerView>(R.id.categoriesRecyclerView).apply {

            // TODO grid layout manager
            layoutManager = GridLayoutManager(this.context, 2)
//            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        categoryViewModel.categories.observe(this, Observer {
            categoryAdapter.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("HOMEFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${categoryAdapter.itemCount}")

        })





        return root
    }
}