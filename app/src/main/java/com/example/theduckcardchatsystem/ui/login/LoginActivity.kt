package com.example.theduckcardchatsystem.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.theduckcardchatsystem.R
import com.example.theduckcardchatsystem.ui.login.EnterPhoneFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_data_container, EnterPhoneFragment()).commit()
    }

    fun ReplaceFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction().replace(R.id.login_data_container, fragment!!)
            .commit()
    }
}