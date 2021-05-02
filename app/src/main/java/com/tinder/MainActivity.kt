package com.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstancesState: Bundle?) {
        super.onCreate(savedInstancesState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}