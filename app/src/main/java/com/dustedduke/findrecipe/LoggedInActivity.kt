package com.dustedduke.findrecipe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dustedduke.findrecipe.ui.search.SearchFragment
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_recipe_description_edit.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class LoggedInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val actionBar = supportActionBar
        actionBar!!.elevation = 0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.drawable.logo)
        actionBar.setDisplayUseLogoEnabled(true)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_search, R.id.navigation_dashboard, R.id.navigation_user
            )
        )

//        setOf(
//            R.id.navigation_home, R.id.navigation_search, R.id.navigation_dashboard, R.id.navigation_user
//        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_photo -> {
            // Activate camera dialog
            showPictureDialog()

            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    val REQUEST_DETECT = 3

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
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

    private fun choosePhotoFromGallery() {
        var pickPhoto: Intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto , 4);
    }

    private fun takePhotoFromCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, 2)
        val intent = Intent(this, DetectorActivity::class.java) // this?

        // TODO for result?
        startActivityForResult(intent, REQUEST_DETECT)

    }

    private var imageview: ImageView? = null
    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == REQUEST_DETECT) {
            if(resultCode == Activity.RESULT_OK) {
                var predictions = data!!.getStringArrayListExtra("predictions")
                Log.d("LOGGEDINACTIVITY: ", "TF RETURNED: " + predictions)

                // TODO START SEARCH FRAGMENT
                var searchFragment: SearchFragment = SearchFragment()

                var bundle: Bundle = Bundle()
                bundle.putStringArrayList("predictions", predictions)
                searchFragment.arguments = bundle

                // TODO START SEARCH FRAGMENT
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_search, bundle)

//                searchFragment.arguments
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.nav_host_fragment, searchFragment)
//                    .addToBackStack(null)
//                    .commit()

            }
        }




        if (requestCode == 1)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@LoggedInActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageview!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@LoggedInActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == 2)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageview!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this@LoggedInActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }


        if(requestCode == 4) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                var selectedImage: Uri? = data.data

                // TODO putExtra selectedImage

//                val intent = Intent(this, DetectorActivity::class.java) // this?
//                intent.putExtra("imageUri", selectedImage.toString())
//                startActivityForResult(intent, REQUEST_DETECT)

                val intent = Intent(this, StaticDetectorActivity::class.java) // this?
                intent.putExtra("imageUri", selectedImage.toString())
                startActivityForResult(intent, 5)

            }
        }


        if(requestCode == 5) {
            // Тоже возвращается лист предсказаний
            var predictions = data!!.getStringArrayListExtra("predictions")
            Log.d("LOGGEDINACTIVITY: ", "TF STATIC IMAGE RETURNED: " + predictions)

            var bundle: Bundle = Bundle()
            bundle.putStringArrayList("predictions", predictions)

            // TODO START SEARCH FRAGMENT
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_search, bundle)



//            var searchFragment: SearchFragment = SearchFragment()
//
//
//
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.root_container, searchFragment)
//                .addToBackStack(null)
//                .commit()

        }


    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        makeRequest()

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")

        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            RECORD_REQUEST_CODE)
    }

    companion object {
        private val IMAGE_DIRECTORY = "/findrecipe"
    }


}
