package com.example.theduckcardchatsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.theduckcardchatsystem.repository.SettingsRepository
import androidx.lifecycle.LiveData
import com.example.theduckcardchatsystem.repository.SingleChatRepository
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter
import androidx.recyclerview.widget.RecyclerView

class SingleChatViewModel(private val application: Application) : AndroidViewModel(
    application
) {
    private val singleChatRepository: SingleChatRepository
    fun sendMessage(message: String?, recevingUserId: String?, typeText: String?) {
        singleChatRepository.sendMessage(message, recevingUserId, typeText)
    }

    fun sendMessageAsIimage(receivingUserID: String?, imageUrl: String?, messageKey: String?) {
        singleChatRepository.sendMessageAsImage(receivingUserID, imageUrl, messageKey)
    }

    fun saveToMainList(id: String?, type: String?) {
        singleChatRepository.saveToMainList(id, type)
    }

    fun initRecyclerView(uid: String?, adapter: SingleChatAdapter?, recyclerView: RecyclerView?) {
        singleChatRepository.initRecyclerView(uid, adapter, recyclerView)
    }

    fun ClearChat(uid: String?) {
        singleChatRepository.clearChat(uid)
    }

    fun DeleteChat(uid: String?) {
        singleChatRepository.DeleteChat(uid)
    }

    init {
        singleChatRepository = SingleChatRepository(application)
    }
}