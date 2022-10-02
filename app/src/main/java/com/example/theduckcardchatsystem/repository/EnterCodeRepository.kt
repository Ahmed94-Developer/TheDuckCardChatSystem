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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class EnterCodeRepository(private val application: Application, context: Context) {
    private val reference: DatabaseReference
    private val mAuth: FirebaseAuth
    private val credential: PhoneAuthCredential? = null
    private val MODEL_USERS = "users"
    private val CHILD_FULLNAME = "fullname"
    private val NODE_PHONES = "phones"
    private val context: Context
    fun EnterCode(credential: PhoneAuthCredential?, phoneNumber: String, activity: LoginActivity) {
        mAuth.signInWithCredential(credential!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser!!
                        .uid
                    val datemaps: MutableMap<String, Any> = HashMap()
                    datemaps["uid"] = uid
                    datemaps["phone"] = phoneNumber
                    datemaps["username"] = uid
                    reference.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "" + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnSuccessListener {
                            reference.child(MODEL_USERS).child(uid)
                                .updateChildren(datemaps)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context, "login successfully", Toast.LENGTH_SHORT
                                    ).show()
                                    context.startActivity(Intent(context, HomeActivity::class.java))
                                    activity.finish()
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        context, ""
                                                + task.exception, Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                } else {
                    Toast.makeText(context, "" + task.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    init {
        reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        mAuth = FirebaseAuth.getInstance()
        this.context = context
    }
}