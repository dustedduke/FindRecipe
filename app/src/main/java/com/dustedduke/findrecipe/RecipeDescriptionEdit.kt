package com.dustedduke.findrecipe

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_recipe_description_edit.*
import androidx.lifecycle.Observer
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.Timestamp
import com.google.firebase.storage.UploadTask
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RecipeDescriptionEdit : AppCompatActivity() {

    private val recipeRepository: RecipeRepository = RecipeRepository()
    private var itemId: String = ""
    var currentPhotoPath: String = ""
    var imageDocPath: String = "recipeImages/" + itemId + ".jpg"

//    var recipeDescriptionImageDelete: ImageView
//    var recipeDescriptionImageAdd

    var recipeDescriptionImageAdd: ImageView? = null
    var recipeDescriptionImageDelete: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_description_edit)
        setSupportActionBar(toolbar)

        itemId = intent.getStringExtra("recipeId")
        Log.d("ItemID inside EDITRECIPEDESCRIPTION: ", itemId)

        val recipeDescriptionTitleEdit = findViewById<EditText>(R.id.recipeDescriptionTitleEdit)
        val recipeDescriptionAuthorEdit = findViewById<EditText>(R.id.recipeDescriptionAuthorEdit)
        val recipeDescriptionDateEdit = findViewById<EditText>(R.id.recipeDescriptionDateEdit)
        val recipeDescriptionCategoriesEdit = findViewById<EditText>(R.id.recipeDescriptionCategoriesEdit)
        val recipeDescriptionIngredientsEdit = findViewById<EditText>(R.id.recipeDescriptionIngredientsEdit)
        val recipeDescriptionTextEdit = findViewById<EditText>(R.id.recipeDescriptionTextEdit)
        val recipeDescriptionStepsEdit = findViewById<EditText>(R.id.recipeDescriptionStepsEdit)
        val recipeDescriptionImageEdit = findViewById<ImageView>(R.id.recipeDescriptionImageEdit)

        val recipeDescriptionDeleteButton = findViewById<Button>(R.id.recipeDescriptionDeleteEdit)

        recipeDescriptionImageDelete = findViewById<ImageView>(R.id.recipeDescriptionImageDeleteEdit)
        recipeDescriptionImageAdd = findViewById<ImageView>(R.id.recipeDescriptionImageAddEdit)

//        val recipeDescriptionImageDelete = findViewById<ImageView>(R.id.recipeDescriptionImageDeleteEdit)
//        val recipeDescriptionImageAdd = findViewById<ImageView>(R.id.recipeDescriptionImageAddEdit)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            // TODO save to DB and return
            // TODO редактирование изображения

            val recipe = Recipe()
            recipe.id = itemId
            recipe.title = recipeDescriptionTitleEdit.text.toString()
            recipe.author = recipeDescriptionAuthorEdit.text.toString()
            recipe.date = Date()//recipeDescriptionDateEdit.toString())
            recipe.image =  imageDocPath
            recipe.description = recipeDescriptionTextEdit.text.toString()
            recipe.categories = recipeDescriptionCategoriesEdit.text.toString().split(", ")
            recipe.ingredients = recipeDescriptionIngredientsEdit.text.toString().split(", ")
            recipe.rating = Float.MIN_VALUE

            Log.d("RECIPEEDIT: ", recipeDescriptionStepsEdit.toString())

            recipe.steps = recipeDescriptionStepsEdit.text.toString()

            recipeRepository.createRecipe(recipe)

            // TODO replace with setResult()
            super.finish()

        }


        if(itemId.isNotEmpty()) {
            val recipe: LiveData<Recipe> = recipeRepository.getRecipeById(itemId)
            recipe.observe(this, Observer {


                Log.d("SETTING RECIPE", "RECIPE")
                recipeDescriptionTitleEdit.setText(it.title)
                recipeDescriptionAuthorEdit.setText(it.author)
                recipeDescriptionDateEdit.setText(it.date.toString())
                recipeDescriptionCategoriesEdit.setText(it.categories.joinToString(", "))
                recipeDescriptionTextEdit.setText(it.description)
                recipeDescriptionStepsEdit.setText(it.steps)


                recipeDescriptionDeleteButton.setOnClickListener{ view ->
                    Log.d("Deleting recipe", it.title)
                    recipeRepository.deleteRecipe(itemId)
                    val intent = Intent(this, LoggedInActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }

                recipeDescriptionImageAdd!!.setOnClickListener { view ->
                    Log.d("Add image button: ", "hit")
                    showPictureDialog()


                    // TODO не дожидается завершения showPictureDialog?

                    // Load image to database / return image path
                    // Можно здесь, id документа не меняется

//                    if(currentPhotoPath.isNotEmpty()) {
//                        Log.d("RECIPE DESCRIPTION EDIT: ", "Sending photo to Firestore")
//                        var uploadTask = recipeRepository.uploadRecipeImage(itemId, currentPhotoPath)
//                        uploadTask.addOnFailureListener {
//                            Log.d("RECIPE DESCRIPTION EDIT: ", "Image upload failed: " + it.message)
//                        }.addOnSuccessListener {
//                            it.storage.downloadUrl.addOnSuccessListener {
//                                Log.d("RECIPE DESCRIPTION EDIT: ", "Image link: " + it.toString())
//                                imageDocPath = it.toString()
//
//                                //Отключить add button
//                                recipeDescriptionImageAdd.visibility = View.INVISIBLE
//                                recipeDescriptionImageDelete.visibility = View.VISIBLE
//
//                                Glide.with(this)
//                                    .load(it)
//                                    .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
//                                    .error(R.drawable.ic_camera)
//                                    .fallback(R.drawable.ic_baseline_remove)
//                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
//                                    .into(recipeDescriptionImageEdit)
//                            }
//                            Log.d("RECIPE DESCRIPTION EDIT: ", "Image upload successful")
//                        }
//
//                    } else {
//                        Log.d("RECIPE DESCRIPTION EDIT: ", "Empty photo path")
//                    }


                }





                if (it.image.isNotEmpty()) {
                    recipeDescriptionImageDelete!!.visibility = View.VISIBLE
                    recipeDescriptionImageDelete!!.setOnClickListener { view ->
                        Log.d("Delete image button: ", "hit")

                        it.image = ""
                        recipeDescriptionImageEdit.setImageResource(R.drawable.ic_camera)
                        recipeDescriptionImageDelete!!.visibility = View.INVISIBLE
                        recipeDescriptionImageAdd!!.visibility = View.VISIBLE
                    }
                } else {
                    recipeDescriptionImageAdd!!.visibility = View.VISIBLE

                }

                Glide.with(this)
                    .load(it.image)
                    .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                    .error(R.drawable.ic_camera)
                    .fallback(R.drawable.ic_baseline_remove)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(recipeDescriptionImageEdit)

            })
        } else {
            recipeDescriptionImageAdd!!.visibility = View.VISIBLE
        }



    }


    private fun showPictureDialog() {
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

    private fun takePhotoFromCamera() {
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

            val compressedImageFile = Compressor(this).compressToFile(File(currentPhotoPath))
            Log.d("RECIPE DESCRIPTION EDIT: COMPRESSED FILE PATH:", compressedImageFile.path)
            // TODO удаление фотографии из базы? не нужно, потому что обновление?

            if(currentPhotoPath.isNotEmpty()) {
                Log.d("RECIPE DESCRIPTION EDIT: ", "Sending photo to Firestore: " + currentPhotoPath)
//                var uploadTask = recipeRepository.uploadRecipeImage(itemId, currentPhotoPath)
                var uploadTask = recipeRepository.uploadRecipeImage(itemId, compressedImageFile.path)

                uploadTask.addOnFailureListener {
                    Log.d("RECIPE DESCRIPTION EDIT: ", "Image upload failed: " + it.message)
                }.addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener {
                        Log.d("RECIPE DESCRIPTION EDIT: ", "Image link: " + it.toString())
                        imageDocPath = it.toString()

                        //Отключить add button
                        recipeDescriptionImageAdd!!.visibility = View.INVISIBLE
                        recipeDescriptionImageDelete!!.visibility = View.VISIBLE

                        // Удалить изображение с устройства
                        deleteImageFile(currentPhotoPath)

                        // TODO ошибка при загрузке it?

                        Glide.with(this)
                            .load(it)
                            .placeholder(R.drawable.ic_baseline_remove) // TODO  Можно loading spinner
                            .error(R.drawable.ic_camera)
                            .fallback(R.drawable.ic_baseline_remove)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(recipeDescriptionImageEdit)
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





    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if(itemId.isEmpty()) {
            Log.d("RECIPE DESCRIPTION EDIT: ", "Empty itemId")
        }

        Log.d("RecipeDescription: ", "CREATEIMAGEFILE Itemid: " + itemId)

        return File.createTempFile(
            itemId, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath


        }
    }

    private fun deleteImageFile(filepath: String) {
        // Create an image file name
        var file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filepath)
        file.delete()

    }

}
