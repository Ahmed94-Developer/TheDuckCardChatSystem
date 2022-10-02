package com.example.theduckcardchatsystem.repository

import android.app.Application
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
import com.google.firebase.database.*

class SettingsRepository(private val application: Application) {
    private val reference: DatabaseReference
    private val uid: String
    private val dataSnapshotMutableLiveData: MutableLiveData<DataSnapshot>
    private val photoMutableLiveData: MutableLiveData<DataSnapshot>
    private val changeNameLiveData: MutableLiveData<DataSnapshot>? = null
    private val initUserLiveData: MutableLiveData<DataSnapshot>
    private val changeUserNameLiveData: MutableLiveData<DataSnapshot>
    fun setSettingsData() {
        reference.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataSnapshotMutableLiveData.postValue(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        application.applicationContext,
                        "" + error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun changePhoto(path: String?) {
        reference.child("users").child(uid).child("photourl")
            .setValue(path).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.child("users").child(uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                photoMutableLiveData.postValue(snapshot)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }
    }

    fun ChangeName(fullName: String?) {
        reference.child("users")
            .child(uid).child("fullname").setValue(fullName)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        application.applicationContext,
                        "Name Changed Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun initUser() {
        reference.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    initUserLiveData.postValue(dataSnapshot)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun UpdatechangeUserName(userName: String?) {
        reference.child("users").child(uid)
            .child("username").setValue(userName).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        application.applicationContext,
                        "User Name Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                    //   deleteOldUserName();
                } else {
                    Toast.makeText(
                        application.applicationContext,
                        "" + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun ChangeUserName(userName: String?) {
        reference.child("usernames").child(userName!!)
            .setValue(uid).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UpdatechangeUserName(userName)
                }
            }
    }

    fun changeBio(bio: String?) {
        reference.child("users").child(uid).child("bio").setValue(bio)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        application.applicationContext,
                        "Bio Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        application.applicationContext,
                        "" + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun initUserLiveData(): LiveData<DataSnapshot> {
        return initUserLiveData
    }

    fun dataSnapshotLiveData(): LiveData<DataSnapshot> {
        return dataSnapshotMutableLiveData
    }

    fun photoSnapShotLiveData(): LiveData<DataSnapshot> {
        return photoMutableLiveData
    }

    fun ChangeNameLiveData(): LiveData<DataSnapshot>? {
        return changeNameLiveData
    }

    init {
        reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        dataSnapshotMutableLiveData = MutableLiveData()
        photoMutableLiveData = MutableLiveData()
        initUserLiveData = MutableLiveData()
        changeUserNameLiveData = MutableLiveData()
    }
}