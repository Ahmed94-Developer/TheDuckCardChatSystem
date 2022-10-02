package com.example.theduckcardchatsystem.ui.room.viewmodel

import android.app.Application
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.liveMembersId
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.insert
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.Update
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.delete
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.deleteAll
import androidx.lifecycle.AndroidViewModel
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository
import androidx.lifecycle.LiveData
import com.example.theduckcardchatsystem.ui.model.MembersId

class MembersViewModel(application: Application) : AndroidViewModel(application) {
    private val application: Application? = null
    private val membersIdRepository: MembersIdRepository
    val membersIdLiveData: LiveData<List<MembersId?>?>?
    fun insert(membersId: MembersId?) {
        membersIdRepository.insert(membersId)
    }

    fun Update(membersId: MembersId?) {
        membersIdRepository.Update(membersId)
    }

    fun Delete(membersId: MembersId?) {
        membersIdRepository.delete(membersId)
    }

    fun DeleteAll() {
        membersIdRepository.deleteAll()
    }

    init {
        membersIdRepository = MembersIdRepository(application)
        membersIdLiveData = membersIdRepository.liveMembersId
    }
}