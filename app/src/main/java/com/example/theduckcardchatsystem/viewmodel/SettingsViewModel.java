package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.theduckcardchatsystem.HomeActivity;
import com.example.theduckcardchatsystem.repository.SettingsRepository;
import com.google.firebase.database.DataSnapshot;

public class SettingsViewModel extends AndroidViewModel {
    private Application application;
    private SettingsRepository settingsRepository;
    private LiveData<DataSnapshot> dataSnapshotLiveData;
    private LiveData<DataSnapshot> photoSnapShotLiveData;
    private LiveData<DataSnapshot> changeNameLiveData;
    private LiveData<DataSnapshot> initUserLiveData;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        settingsRepository = new SettingsRepository(application);
        dataSnapshotLiveData = settingsRepository.dataSnapshotLiveData();
        photoSnapShotLiveData = settingsRepository.photoSnapShotLiveData();
        changeNameLiveData = settingsRepository.ChangeNameLiveData();
        initUserLiveData = settingsRepository.initUserLiveData();
    }
    public void setSettingsRepository(){
        settingsRepository.setSettingsData();
    }
    public void changePhotoViewModel(String path){
        settingsRepository.changePhoto(path);
    }
    public void changeName(String fullName){
        settingsRepository.ChangeName(fullName);
    }
    public void initUser(){
        settingsRepository.initUser();
    }
    public void changeUserName(String userName){
        settingsRepository.ChangeUserName(userName);
    }
    public void changeBio(String bio){
        settingsRepository.changeBio(bio);
    }
    public LiveData<DataSnapshot> dataSnapshotLiveData(){
        return  dataSnapshotLiveData;
    }
    public LiveData<DataSnapshot> photoSnapShotLiveData(){
        return photoSnapShotLiveData;
    }
    public LiveData<DataSnapshot> getInitUserLiveData(){
        return initUserLiveData;
    }

}
