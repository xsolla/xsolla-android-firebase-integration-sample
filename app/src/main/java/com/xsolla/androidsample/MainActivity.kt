package com.xsolla.androidsample

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            registerUser()
        }
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = findViewById<EditText>(R.id.emailText).text.toString()
        val password = findViewById<EditText>(R.id.passwordText).text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if(user != null) updateUI(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInUserWithEmail:failure", task.exception)
                    showNotificationMessage("unsuccessful registration. Error: ${task.exception?.message}")
                }
            }
    }

    private fun registerUser() {
        val email = findViewById<EditText>(R.id.emailText).text.toString()
        val password = findViewById<EditText>(R.id.passwordText).text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if(user != null) updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    // unsuccessful registration
                    showNotificationMessage("unsuccessful registration. Error: ${task.exception?.message}")
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null) return

        val intent = Intent(this, StoreActivity::class.java)
        intent.putExtra("uid", user.uid)
        intent.putExtra("email", user.email)
        startActivity(intent)
    }

    private fun showNotificationMessage(message: String) {
        Toast.makeText(
            baseContext,
            message,
            Toast.LENGTH_SHORT,
        ).show()
    }
}