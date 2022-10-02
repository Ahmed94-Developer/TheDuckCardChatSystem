package com.example.theduckcardchatsystem.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.theduckcardchatsystem.repository.AddContactsRepository
import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.repository.ChatsRepository
import com.example.theduckcardchatsystem.repository.ContactsRepository
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.example.theduckcardchatsystem.ui.model.CommonModel
import com.example.theduckcardchatsystem.repository.ContactsRepository.ContactsHolder
import com.example.theduckcardchatsystem.repository.EnterCodeRepository
import com.example.theduckcardchatsystem.ui.login.LoginActivity
import com.example.theduckcardchatsystem.repository.EnterPhoneRepository
import com.example.theduckcardchatsystem.repository.GroupChatRepository

class GroupChatViewModel(private val application: Application) : AndroidViewModel(
    application
) {
    private val groupChatRepository: GroupChatRepository
    fun sendMessage(message: String?, id: String?, typeText: String?) {
        groupChatRepository.sendMessage(message, id, typeText)
    }

    fun sendImageAsMessage(imageUrl: String?, id: String?) {
        groupChatRepository.sendMessageAsImage(imageUrl, id)
    }

    fun initRecyclerView(id: String?, recyclerView: RecyclerView?, context: Context?) {
        groupChatRepository.initRecyclerView(id, recyclerView, context)
    }

    fun saveToMainList(id: String?, type: String?) {
        groupChatRepository.saveToMainList(id, type)
    }

    fun ClearChat(id: String?) {
        groupChatRepository.clearChat(id)
    }

    fun DeleteChat(id: String?) {
        groupChatRepository.DeleteChat(id)
    }

    init {
        groupChatRepository = GroupChatRepository(application)
    }
}