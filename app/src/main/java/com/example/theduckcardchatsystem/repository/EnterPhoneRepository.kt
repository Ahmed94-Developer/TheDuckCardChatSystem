package com.example.theduckcardchatsystem.repository

import android.app.Application
import android.content.Context
import kotlin.collections.collectionSizeOrDefault
import com.example.theduckcardchatsystem.ui.model.CommonModel
import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.repository.AddContactsRepository
import kotlin.jvm.internal.Intrinsics
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theduckcardchatsystem.adapters.MainListAdapter
import com.example.theduckcardchatsystem.repository.ChatsRepository
import com.example.theduckcardchatsystem.HomeActivity
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.example.theduckcardchatsystem.repository.ContactsRepository.ContactsHolder
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Intent
import com.example.theduckcardchatsystem.ui.chats.SingleChatActivity
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.theduckcardchatsystem.R
import android.widget.TextView
import com.example.theduckcardchatsystem.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.example.theduckcardchatsystem.ui.login.EnterCodeFragment
import com.example.theduckcardchatsystem.adapters.GroupChatAdapter
import com.example.theduckcardchatsystem.repository.GroupChatRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter
import com.example.theduckcardchatsystem.repository.SingleChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class EnterPhoneRepository(private val application: Application, context: Context) {
    private val mAuth: FirebaseAuth
    private val context: Context
    fun EnterPhoneAuthData(phoneNumber: String?, activity: LoginActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            activity,  // Activity (for callback binding)
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    mAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (mAuth.currentUser!!.uid == null) {
                                    Toast.makeText(
                                        context, "Enter Your Phone Number", Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    context
                                        .startActivity(
                                            Intent(
                                                context, HomeActivity::class.java
                                            )
                                        )
                                    Toast.makeText(
                                        context, "Login Successfully", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context, ""
                                            + task.exception.toString(), Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(
                        context, "" + e.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    id: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    activity.ReplaceFragment(EnterCodeFragment(phoneNumber, id))
                }
            })
    }

    init {
        mAuth = FirebaseAuth.getInstance()
        this.context = context
    }
}