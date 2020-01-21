package com.dustedduke.findrecipe.ui.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
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
import kotlinx.android.synthetic.main.activity_recipe_description_edit.*
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import kotlin.math.log
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    private val recipeAdapterFavorites = RecipeAdapterUser()
    private val recipeAdapterCreated = RecipeAdapterUser()
    private var recyclerViewFavorites: RecyclerView? = null
    private var recyclerViewCreated: RecyclerView? = null

    private val TAG1 = "Favorites"
    private val TAG2 = "Created"

    var fbAuth = FirebaseAuth.getInstance()
    val repository = RecipeRepository()

    val REQUEST_TAKE_PHOTO = 1
    var currentPhotoPath: String = ""
//    var imageDocPath: String = "recipeImages/" + itemId + ".jpg"

    fun deleteImageFile(filepath: String) {
        // Create an image file name
        var file = File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filepath)
        file.delete()

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel =
            ViewModelProviders.of(this).get(UserViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user, container, false)







        fun choosePhotoFromGallery() {
            var pickPhoto: Intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto , 4);
        }


        var userPic = root.findViewById<ImageView>(R.id.userImage)
        var userName = root.findViewById<TextView>(R.id.userName)
        var logoutButton = root.findViewById<ImageButton>(R.id.userLogoutButton)

//        var favoritesCount = root.findViewById<TextView>(R.id.userFavoritesNumber)
//        var createdCount = root.findViewById<TextView>(R.id.userCreatedNumber)


        fun createImageFile(): File {
            // Create an image file name
            val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            if(userName.text.isEmpty()) {
                Log.d("USER DESCRIPTION EDIT: ", "Empty itemId")
            }

            Log.d("User: ", "CREATEIMAGEFILE Itemid: " + fbAuth.currentUser!!.uid)

            return File.createTempFile(
                fbAuth.currentUser!!.uid, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath


            }
        }

        fun takePhotoFromCamera() {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("USER EDIT: ", "Error during image capture")
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            activity!!.applicationContext!!,
                            "com.dustedduke.fileprovider",
                            it
                        )

                        Log.d("USER DESCRIPTION: ", "GENERATED URI " + photoURI.toString())


                        // TODO здесь файл еще пустой
                        Log.d("USER DESCRIPTION: ", "PHOTO FILE SIZE: " + photoFile.length())


                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                    } // TODO photofile
                }
            }
        }

        fun showPictureDialog() {
            val pictureDialog = AlertDialog.Builder(context!!)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }

        userPic.setOnClickListener{
            showPictureDialog()
        }



        logoutButton.setOnClickListener{
            Log.d("UserFragment", "Logging out")
            fbAuth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }


        userViewModel.user.observe(this, Observer {
            Log.d("SETTING USER", it.name.toString())
            userName.text = it.name

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
            //favoritesCount.setText(it.size.toString())
            //recipeAdapter.notifyDataSetChanged()


        })

        userViewModel.createdRecipes.observe(this, Observer {
            recipeAdapterCreated.setItems(it)
            //createdCount.setText(it.size.toString())
            //recipeAdapter.notifyDataSetChanged()

            Log.d("USER FRAGMENT", "CREATED ADAPTER ITEMS COUNT: ${recipeAdapterCreated.itemCount}")

        })


        // End of tabs





        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO || requestCode == 4) {
            Log.d("USER DESCRIPTION: ", "CALLED ON ACTIVITY RESULT: " + REQUEST_TAKE_PHOTO)
            //processCapturedPhoto()

            val internalFile = File(currentPhotoPath)
            Log.d("INTERNAL FILE PATH", internalFile.path)
            Log.d("INTERNAL FILE ABS PATH", internalFile.absolutePath)

            val compressedImageFile =
                Compressor(activity!!.applicationContext).compressToFile(internalFile)
            Log.d("USER DESCRIPTION EDIT: COMPRESSED FILE PATH:", compressedImageFile.path)
            // TODO удаление фотографии из базы? не нужно, потому что обновление?

            if (currentPhotoPath.isNotEmpty()) {
                Log.d("USER DESCRIPTION EDIT: ", "Sending photo to Firestore: " + currentPhotoPath)
//                var uploadTask = recipeRepository.uploadRecipeImage(itemId, currentPhotoPath)
                var uploadTask =
                    repository.uploadUserImage(fbAuth.currentUser!!.uid, compressedImageFile.path)

                uploadTask.addOnFailureListener {
                    Log.d("USER DESCRIPTION EDIT: ", "Image upload failed: " + it.message)
                }.addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {
                        Log.d("RECIPE DESCRIPTION EDIT: ", "Image link: " + it.toString())
//                        imageDocPath = it.toString()

                        // Удалить изображение с устройства
                        deleteImageFile(currentPhotoPath)

                        // TODO ошибка при загрузке it?

                        Glide.with(activity!!.applicationContext!!)
                            .load(it)
                            .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                            .error(R.drawable.ic_camera)
                            .fallback(R.drawable.ic_baseline_remove)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(userImage)
                    }
                    Log.d("USER DESCRIPTION EDIT: ", "Image upload successful")
                }

            //} else if(requestCode == 4) {
            //    var selectedImage: Uri? = data!!.data
            //    Log.d("USER", "Got image from gallery " + selectedImage)

            } else {
                Log.d("USER DESCRIPTION EDIT: ", "Empty photo path")
            }


        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}