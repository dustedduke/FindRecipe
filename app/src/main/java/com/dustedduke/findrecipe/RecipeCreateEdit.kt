package com.dustedduke.findrecipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_recipe_create_edit.*
import kotlinx.android.synthetic.main.activity_recipe_create_edit.fab
import kotlinx.android.synthetic.main.activity_recipe_create_edit.toolbar
import java.io.File
import java.io.IOException
import java.util.*

class RecipeCreateEdit : AppCompatActivity() {

    private val recipeRepository: RecipeRepository = RecipeRepository()
    var fbAuth = FirebaseAuth.getInstance()
    var currentPhotoPath: String = ""
    var currentPhotoURI: Uri? = null
    var randomId: String = recipeRepository.getRandomString(20)
    var imageDocPath: String = "recipeImages/" + randomId + ".jpg"

//    var recipeDescriptionImageDelete: ImageView
//    var recipeDescriptionImageAdd

    var recipeDescriptionImageAdd: ImageView? = null
    var recipeDescriptionImageDelete: ImageView? = null

    var hasImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_create_edit)
        setSupportActionBar(toolbar)


        val recipeDescriptionTitleEdit = findViewById<EditText>(R.id.recipeCreateTitleEdit)
//        val recipeDescriptionAuthorEdit = findViewById<EditText>(R.id.recipeCreateAuthorEdit)
//        val recipeDescriptionDateEdit = findViewById<EditText>(R.id.recipeCreateDateEdit)
        val recipeDescriptionCategoriesEdit =
            findViewById<EditText>(R.id.recipeCreateCategoriesEdit)
        val recipeDescriptionIngredientsEdit =
            findViewById<EditText>(R.id.recipeCreateIngredientsEdit)
        val recipeDescriptionTextEdit = findViewById<EditText>(R.id.recipeCreateTextEdit)
        val recipeDescriptionStepsEdit = findViewById<EditText>(R.id.recipeCreateStepsEdit)
        val recipeDescriptionImageEdit = findViewById<ImageView>(R.id.recipeCreateImageEdit)

        recipeDescriptionImageDelete = findViewById<ImageView>(R.id.recipeCreateImageDeleteEdit)
        recipeDescriptionImageAdd = findViewById<ImageView>(R.id.recipeCreateImageAddEdit)

//        val recipeDescriptionImageDelete = findViewById<ImageView>(R.id.recipeDescriptionImageDeleteEdit)
//        val recipeDescriptionImageAdd = findViewById<ImageView>(R.id.recipeDescriptionImageAddEdit)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            // TODO save to DB and return
            // TODO редактирование изображения

            val recipe = Recipe()
            recipe.id = randomId
            recipe.title = recipeDescriptionTitleEdit.text.toString()
            recipe.authorId = fbAuth.currentUser!!.uid
            recipe.author = fbAuth.currentUser!!.displayName!!
            recipe.date = Date()//recipeDescriptionDateEdit.toString())
            recipe.image = imageDocPath
            recipe.description = recipeDescriptionTextEdit.text.toString()
            recipe.categories = recipeDescriptionCategoriesEdit.text.toString().split(" ")
            recipe.ingredients = recipeDescriptionIngredientsEdit.text.toString().split(" ")
            recipe.rating = 0.0f

            Log.d("RECIPEEDIT: ", recipeDescriptionStepsEdit.toString())

            recipe.steps = recipeDescriptionStepsEdit.text.toString()

            recipeRepository.createRecipe(recipe)

            // TODO replace with setResult()
            super.finish()

        }

        recipeDescriptionImageDelete!!.setOnClickListener { view ->
            Log.d("Delete image button: ", "hit")

            recipeDescriptionImageEdit.setImageResource(0)
            hasImage = false
            recipeDescriptionImageDelete!!.visibility = View.INVISIBLE
            recipeDescriptionImageAdd!!.visibility = View.VISIBLE
        }

        Log.d("RANDOM ID IS NOT EMPTY", randomId)

        recipeDescriptionImageAdd!!.setOnClickListener { view ->
            Log.d("Add image button: ", "hit")
            showPictureDialog()
        }

        if(randomId.isNotEmpty()) {

            Log.d("HAS IMAGE", hasImage.toString())

            if (hasImage == true) {
                Log.d("RECIPE CREATE IMAGE", "Not null")
                recipeDescriptionImageDelete!!.visibility = View.VISIBLE

            } else {
                recipeDescriptionImageAdd!!.visibility = View.VISIBLE

            }

//                Glide.with(this)
//                    .load(it.image)
//                    .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
//                    .error(R.drawable.ic_camera)
//                    .fallback(R.drawable.ic_baseline_remove)
//                    .diskCacheStrategy(DiskCacheStrategy.DATA)
//                    .into(recipeDescriptionImageEdit)
        } else {
            recipeDescriptionImageAdd!!.visibility = View.VISIBLE
        }


    }

    fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if(randomId.isEmpty()) {
            Log.d("RECIPE DESCRIPTION EDIT: ", "Empty itemId")
        }

        Log.d("RecipeDescription: ", "CREATEIMAGEFILE Itemid: " + randomId)

        return File.createTempFile(
            randomId, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("CURRENT PHOTO PATH ", currentPhotoPath)


        }
    }

    fun deleteImageFile(filepath: String) {
        // Create an image file name
        var file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filepath)
        file.delete()

    }

    fun choosePhotoFromGallery() {
        var pickPhoto: Intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto , 4);
    }

    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                //0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    val REQUEST_TAKE_PHOTO = 1

    fun takePhotoFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.d("RECIPE DESCRIPTION EDIT: ", "Error during image capture")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.dustedduke.fileprovider",
                        it
                    )

                    Log.d("RECIPE DESCRIPTION: ", "GENERATED URI " + photoURI.toString())

                    currentPhotoURI = photoURI

                    // TODO здесь файл еще пустой
                    Log.d("RECIPE DESCRIPTION: ", "PHOTO FILE SIZE: " + photoFile.length())


                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                } // TODO photofile
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            Log.d("RECIPE DESCRIPTION: ", "CALLED ON ACTIVITY RESULT: " + REQUEST_TAKE_PHOTO)
            //processCapturedPhoto()

            Log.d("CURRENT PHOTO PATH BEFORE COMPRESS", currentPhotoPath)

            val internalFile = File(currentPhotoPath)

            Log.d("EXTERNAL FILE PATH", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath)

            Log.d("INTERNAL FILE PATH", internalFile.path)
            Log.d("INTERNAL FILE NAME", internalFile.name)

            val compressedImageFile = Compressor(this).compressToFile(internalFile)



            Log.d("RECIPE DESCRIPTION EDIT: COMPRESSED FILE PATH:", compressedImageFile.path)
            // TODO удаление фотографии из базы? не нужно, потому что обновление?

            if(currentPhotoPath.isNotEmpty()) {
                Log.d("RECIPE DESCRIPTION EDIT: ", "Sending photo to Firestore: " + currentPhotoPath)
//                var uploadTask = recipeRepository.uploadRecipeImage(itemId, currentPhotoPath)
                var uploadTask = recipeRepository.uploadRecipeImage(randomId, compressedImageFile.path)

                uploadTask.addOnFailureListener {
                    Log.d("RECIPE DESCRIPTION EDIT: ", "Image upload failed: " + it.message)
                }.addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {
                        Log.d("RECIPE DESCRIPTION EDIT: ", "Image link: " + it.toString())
                        imageDocPath = it.toString()

                        //Отключить add button
                        recipeDescriptionImageAdd!!.visibility = View.INVISIBLE
                        recipeDescriptionImageDelete!!.visibility = View.VISIBLE
                        hasImage = true

                        // Удалить изображение с устройства
                        deleteImageFile(currentPhotoPath)

                        // TODO ошибка при загрузке it?

                        Glide.with(this)
                            .load(it)
                            .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                            .fallback(R.drawable.ic_baseline_remove)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(recipeCreateImageEdit)
                    }
                    Log.d("RECIPE DESCRIPTION EDIT: ", "Image upload successful")
                }

            } else {
                Log.d("RECIPE DESCRIPTION EDIT: ", "Empty photo path")
            }


        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
