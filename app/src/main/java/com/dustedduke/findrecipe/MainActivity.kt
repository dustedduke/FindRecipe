package com.dustedduke.findrecipe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dustedduke.findrecipe.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity: AppCompatActivity() {
    var fbAuth = FirebaseAuth.getInstance()
    val repository = RecipeRepository()

    val DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/findrecipe-a4f88.appspot.com/o/recipeImages%2FDXdt5iYu7go0ClcQx1de.jpg?alt=media&token=88ee9e5b-6376-469c-8be0-b3c110abd5b2"
    var MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }





        var emailField = findViewById<EditText>(R.id.email)
        var passwordField = findViewById<EditText>(R.id.password)

        var loginButton = findViewById<Button>(R.id.login)
        var registerButton = findViewById<Button>(R.id.register)

        loginButton.setOnClickListener{ view ->
            showMessage(view, "Logging in")
            Log.d("LOGINTEST", "LOGGING IN")
            signIn(view, emailField.text.toString(), passwordField.text.toString())
        }

        registerButton.setOnClickListener{  view ->
            showMessage(view, "Registering")
            register(view, emailField.text.toString(), passwordField.text.toString())
        }

        var user = fbAuth.currentUser
        Log.d("LOGIN", fbAuth.currentUser.toString())
        if(user != null) {
            var intent = Intent(this, LoggedInActivity::class.java)
            intent.putExtra("id", fbAuth.currentUser?.email)
            startActivity(intent)
        } else {
            Log.d("No logged in users", "LOGIN")
        }

    }

    fun signIn(view: View,email: String, password: String){
        showMessage(view,"Authenticating...")

        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                var intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", fbAuth.currentUser?.email)
                startActivity(intent)

            }else{
                showMessage(view,"Error: ${task.exception?.message}")
            }
        })

    }

    fun register(view: View,email: String, password: String){
        showMessage(view,"Authenticating...")

        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                repository.createUser(User(fbAuth.currentUser?.uid!!, email.substringBefore("@"), "user", DEFAULT_IMAGE))
                var intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", fbAuth.currentUser?.email)
                startActivity(intent)

            }else{
                showMessage(view,"Error: ${task.exception?.message}")
            }
        })

    }


    fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}