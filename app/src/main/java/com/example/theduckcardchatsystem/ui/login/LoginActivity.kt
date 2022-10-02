package com.example.theduckcardchatsystem.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.theduckcardchatsystem.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportFragmentManager().beginTransaction().replace(R.id.login_data_container,new EnterPhoneFragment()).commit();

    }
    public void ReplaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.login_data_container,fragment).commit();
    }
}
