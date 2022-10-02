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

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val application: Application? = null
    private val contactsRepository: ContactsRepository
    fun initRecyclerView(
        mAdapter: FirebaseRecyclerAdapter<CommonModel?, ContactsHolder?>?,
        rv_contacts: RecyclerView?,
        context: Context?
    ) {
        contactsRepository.initRecyclerView(mAdapter, rv_contacts, context)
    }

    init {
        contactsRepository = ContactsRepository(application)
    }
}