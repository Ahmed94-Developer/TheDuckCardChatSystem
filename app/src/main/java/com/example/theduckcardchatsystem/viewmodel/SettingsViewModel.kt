package com.example.theduckcardchatsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.theduckcardchatsystem.repository.SettingsRepository
import androidx.lifecycle.LiveData
import com.example.theduckcardchatsystem.repository.SingleChatRepository
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot

class SettingsViewModel(private val application: Application) : AndroidViewModel(
    application
) {
    private val settingsRepository: SettingsRepository
    private val dataSnapshotLiveData: LiveData<DataSnapshot>
    private val photoSnapShotLiveData: LiveData<DataSnapshot>
    private val changeNameLiveData: LiveData<DataSnapshot>
    val initUserLiveData: LiveData<DataSnapshot>
    fun setSettingsRepository() {
        settingsRepository.setSettingsData()
    }

    fun changePhotoViewModel(path: String?) {
        settingsRepository.changePhoto(path)
    }

    fun changeName(fullName: String?) {
        settingsRepository.ChangeName(fullName)
    }

    fun initUser() {
        settingsRepository.initUser()
    }

    fun changeUserName(userName: String?) {
        settingsRepository.ChangeUserName(userName)
    }

    fun changeBio(bio: String?) {
        settingsRepository.changeBio(bio)
    }

    fun dataSnapshotLiveData(): LiveData<DataSnapshot> {
        return dataSnapshotLiveData
    }

    fun photoSnapShotLiveData(): LiveData<DataSnapshot> {
        return photoSnapShotLiveData
    }

    init {
        settingsRepository = SettingsRepository(application)
        dataSnapshotLiveData = settingsRepository.dataSnapshotLiveData()
        photoSnapShotLiveData = settingsRepository.photoSnapShotLiveData()
        changeNameLiveData = settingsRepository.ChangeNameLiveData()
        initUserLiveData = settingsRepository.initUserLiveData()
    }
}