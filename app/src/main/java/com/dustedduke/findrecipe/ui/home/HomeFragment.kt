package com.dustedduke.findrecipe.ui.home

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
import java.util.ArrayList
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.Toast
import android.graphics.ColorSpace.Model
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dustedduke.findrecipe.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.SnapshotParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_recipe_create_edit.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val recipeAdapter1 = RecipeAdapter()
    private val recipeAdapter2 = RecipeAdapter()
    private val recipeAdapter3 = RecipeAdapter()

    private var recyclerView1: RecyclerView? = null
    private var recyclerView2: RecyclerView? = null
    private var recyclerView3: RecyclerView? = null

    private var adapter1: RecyclerView.Adapter<*>? = null
    private var adapter2: RecyclerView.Adapter<*>? = null
    private var adapter3: RecyclerView.Adapter<*>? = null

    private var layoutManager1: RecyclerView.LayoutManager? = null
    private var layoutManager2: RecyclerView.LayoutManager? = null
    private var layoutManager3: RecyclerView.LayoutManager? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root =
            inflater.inflate(com.dustedduke.findrecipe.R.layout.fragment_home, container, false)


        val fabButton = root.findViewById<FloatingActionButton>(R.id.fabHome)
        fabButton.hide()
        fabButton.setOnClickListener {
            Log.d("Pushed home fab button", "Pushed button")
            val intent = Intent(activity!!.applicationContext, RecipeCreateEdit::class.java)
            startActivity(intent)
        }

        recyclerView1 = root.findViewById<RecyclerView>(R.id.recipesRecyclerView1).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeAdapter1
        }

        recyclerView2 = root.findViewById<RecyclerView>(R.id.recipesRecyclerView2).apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeAdapter2
        }

//        recyclerView3 = root.findViewById<RecyclerView>(R.id.recipesRecyclerView3Search).apply {
//            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = recipeAdapter3
//        }

        homeViewModel.popularRecipes.observe(this, Observer {
            recipeAdapter1.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("HOMEFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter1.itemCount}")

        })

        homeViewModel.popularRecipes.observe(this, Observer {
            recipeAdapter2.setItems(it)
            //recipeAdapter.notifyDataSetChanged()

            Log.d("HOMEFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter2.itemCount}")

        })

//        homeViewModel.popularRecipes.observe(this, Observer {
//            recipeAdapter3.setItems(it)
//            //recipeAdapter.notifyDataSetChanged()
//
//            Log.d("HOMEFRAGMENTTEST", "RECIPE ADAPTER ITEMS COUNT: ${recipeAdapter3.itemCount}")
//
//        })

        homeViewModel.user.observe(this, Observer {
            if(it.type == "chef") {
                fabButton.show()
            } else {
                fabButton.hide()
            }
        })






        return root
    }

}