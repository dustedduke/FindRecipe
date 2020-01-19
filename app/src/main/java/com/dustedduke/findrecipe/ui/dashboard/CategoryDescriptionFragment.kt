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
import com.dustedduke.findrecipe.*

class CategoryDescriptionFragment : Fragment() {

    private lateinit var categoryDescriptionViewModel: CategoryDescriptionViewModel




    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var recipeAdapter = RecipeAdapterCategory()
    private var title: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoryDescriptionViewModel =
            ViewModelProviders.of(this).get(CategoryDescriptionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_category_description, container, false)



        var categoryName = arguments!!.getString("categoryName")!!
        categoryDescriptionViewModel.updateCategory(categoryName)



        title = root.findViewById<TextView>(R.id.categoryDescriptionTitle)
        title!!.text = categoryName
        recyclerView = root.findViewById<RecyclerView>(R.id.categoryDescriptionGrid).apply {

            layoutManager = GridLayoutManager(this.context, 3)
            adapter = recipeAdapter
        }

        categoryDescriptionViewModel.recipes.observe(this, Observer {
            recipeAdapter.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            //Log.d("HOMEFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${categoryAdapter.itemCount}")

        })





        return root
    }
}