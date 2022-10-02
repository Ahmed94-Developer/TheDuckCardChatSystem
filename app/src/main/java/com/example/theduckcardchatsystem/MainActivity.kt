package com.example.theduckcardchatsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.theduckcardchatsystem.R
import android.content.Intent
import android.os.Handler
import com.example.theduckcardchatsystem.HomeActivity
import com.example.theduckcardchatsystem.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if (mAuth!!.currentUser != null) {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }, 5000)
    }
}