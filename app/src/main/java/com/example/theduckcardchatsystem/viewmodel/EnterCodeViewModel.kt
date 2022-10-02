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
import com.google.firebase.auth.PhoneAuthCredential

class EnterCodeViewModel(private val application: Application) : AndroidViewModel(
    application
) {
    private val context: Context
    private val repository: EnterCodeRepository
    fun EnterCode(
        credential: PhoneAuthCredential?,
        phoneNumber: String?,
        activity: LoginActivity?
    ) {
        repository.EnterCode(credential, phoneNumber, activity)
    }

    init {
        context = application.applicationContext
        repository = EnterCodeRepository(application, context)
    }
}