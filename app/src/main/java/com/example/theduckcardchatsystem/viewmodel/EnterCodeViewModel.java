package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.theduckcardchatsystem.repository.EnterCodeRepository;
import com.example.theduckcardchatsystem.ui.login.LoginActivity;
import com.google.firebase.auth.PhoneAuthCredential;

public class EnterCodeViewModel extends AndroidViewModel {
    private Application application;
    private Context context;
    private EnterCodeRepository repository;
    public EnterCodeViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        context = application.getApplicationContext();
        repository = new EnterCodeRepository(application,context);
    }
    public void EnterCode(PhoneAuthCredential credential, String phoneNumber, LoginActivity activity){
        repository.EnterCode(credential, phoneNumber, activity);
    }
}
