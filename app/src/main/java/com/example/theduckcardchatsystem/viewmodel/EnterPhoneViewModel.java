package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.theduckcardchatsystem.repository.EnterPhoneRepository;
import com.example.theduckcardchatsystem.ui.login.LoginActivity;

public class EnterPhoneViewModel extends AndroidViewModel {
    private EnterPhoneRepository repository;
    private Application application;
    private Context context;

    public EnterPhoneViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.context = application.getApplicationContext();
        repository = new EnterPhoneRepository(application,context);
    }

    public void EnterPhone(String phoneNumber, LoginActivity activity){
        repository.EnterPhoneAuthData(phoneNumber,activity);
    }
}
