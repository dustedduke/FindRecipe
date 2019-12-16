package com.dustedduke.findrecipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.dustedduke.findrecipe.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity: AppCompatActivity() {
    var fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var emailField = findViewById<EditText>(R.id.email)
        var passwordField = findViewById<EditText>(R.id.password)

        var loginButton = findViewById<Button>(R.id.login)
        var registerButton = findViewById<Button>(R.id.register)

        loginButton.setOnClickListener{ view ->
            showMessage(view, "Logging in")
            Log.d("LOGINTEST", "LOGGING IN")
            signIn(view, emailField.text.toString(), passwordField.text.toString())
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


}