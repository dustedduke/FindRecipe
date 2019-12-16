package com.dustedduke.findrecipe.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dustedduke.findrecipe.*
import com.dustedduke.findrecipe.ui.RecipeDescription
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import kotlin.math.log

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    private val recipeAdapterFavorites = RecipeAdapterUser()
    private val recipeAdapterCreated = RecipeAdapterUser()
    private var recyclerViewFavorites: RecyclerView? = null
    private var recyclerViewCreated: RecyclerView? = null

    private val TAG1 = "Favorites"
    private val TAG2 = "Created"

    var fbAuth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel =
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)

        var userPic = root.findViewById<ImageView>(R.id.userImage)
        var userName = root.findViewById<TextView>(R.id.userName)
        var logoutButton = root.findViewById<ImageButton>(R.id.userLogoutButton)

        var favoritesCount = root.findViewById<TextView>(R.id.userFavoritesNumber)
        var createdCount = root.findViewById<TextView>(R.id.userCreatedNumber)


        logoutButton.setOnClickListener{
            Log.d("UserFragment", "Logging out")
            fbAuth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }


        userViewModel.user.observe(this, Observer {
            Log.d("SETTING USER", it.firstName.toString())
            userName.text = it.firstName + " " + it.lastName

            Glide.with(this)
                .load(it.image)
                .circleCrop()
                .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                .error(R.drawable.ic_camera)
                .fallback(R.drawable.ic_baseline_remove)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(userPic)

        })



        // Tabs


        var tabHost: TabHost = root.findViewById(android.R.id.tabhost)
        tabHost.setup()

        var tabSpec = tabHost.newTabSpec(TAG1)
        tabSpec.setIndicator(TAG1)
        tabSpec.setContent(R.id.userFavoritesGrid)
        tabHost.addTab(tabSpec)

        tabSpec = tabHost.newTabSpec(TAG2)
        tabSpec.setIndicator(TAG2)
        tabSpec.setContent(R.id.userCreatedGrid)
        tabHost.addTab(tabSpec)

        tabHost.setCurrentTabByTag(TAG1)

        tabHost.setOnTabChangedListener(TabHost.OnTabChangeListener {
            if(it.equals(TAG1)) {

            } else if(it.equals(TAG2)) {

            }
        })

        recyclerViewFavorites = root.findViewById<RecyclerView>(R.id.userFavoritesGrid).apply {
            layoutManager = GridLayoutManager(this.context, 3)
            adapter = recipeAdapterFavorites
        }

        recyclerViewCreated = root.findViewById<RecyclerView>(R.id.userCreatedGrid).apply {
            layoutManager = GridLayoutManager(this.context, 3)
            adapter = recipeAdapterCreated
        }

        userViewModel.favoriteRecipes.observe(this, Observer {

            Log.d("USER FRAGMENT", "FAVORITES ADAPTER ITEMS COUNT: ${recipeAdapterFavorites.itemCount}")

            recipeAdapterFavorites.setItems(it)
            favoritesCount.setText(it.size.toString())
            //recipeAdapter.notifyDataSetChanged()


        })

        userViewModel.createdRecipes.observe(this, Observer {
            recipeAdapterCreated.setItems(it)
            createdCount.setText(it.size.toString())
            //recipeAdapter.notifyDataSetChanged()

            Log.d("USER FRAGMENT", "CREATED ADAPTER ITEMS COUNT: ${recipeAdapterCreated.itemCount}")

        })


        // End of tabs





        return root
    }
}