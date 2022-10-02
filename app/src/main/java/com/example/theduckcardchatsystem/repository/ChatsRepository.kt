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
import android.util.Log
import com.example.theduckcardchatsystem.ui.chats.SingleChatActivity
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
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
import java.lang.Exception
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

class ChatsRepository(private val application: Application) {
    private var adapter: MainListAdapter
    private val mainListRef: DatabaseReference
    private val reference: DatabaseReference
    private val mRefUser: DatabaseReference
    private val mRefMessages: DatabaseReference
    private val NODE_MAIN_LIST = "main_list"
    private val NODE_USERS = "users"
    private val NODE_MESSAGES = "Messages"
    private val uid: String
    private val mLlistItems: List<CommonModel>? = null
    private var inputMethodManager: InputMethodManager? = null
    fun showChat(model: CommonModel) {
        mRefUser.child(model.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commonModel2 = getCommonModel(snapshot)
                mRefMessages.child(model.id).limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val var10000: Iterable<*> = snapshot.children
                            //  Intrinsics.checkExpressionValueIsNotNull(var10000, "dataSnapshot2.children");
                            val `destination$iv$iv` = ArrayList<Any?>(
                                var10000.collectionSizeOrDefault<Any>(
                                    10
                                )
                            ) as MutableCollection<*>
                            val var7 = var10000.iterator()
                            while (var7.hasNext()) {
                                val `item$iv$iv` = var7.next()!!
                                val it = `item$iv$iv` as DataSnapshot
                                val var15 = this@ChatsRepository
                                Intrinsics.checkExpressionValueIsNotNull(it, "it")
                                val var12 = var15.getCommonModel(it)
                                `destination$iv$iv`.add(var12)
                            }
                            val tempList = `destination$iv$iv` as List<*>
                            if (tempList.isEmpty()) {
                                commonModel2.lastMessage = "Deleted Conversation"
                            } else {
                                try {
                                    commonModel2.lastMessage = (tempList[0] as CommonModel).text
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            var var16 = commonModel2.fullname
                            if (var16 == null) {
                                var16 = "User Name"
                                commonModel2.fullname = var16
                            } else {
                                Intrinsics.checkExpressionValueIsNotNull(var16, "newModel.fullname")
                                val var14 = var16 as CharSequence
                                if (var14.length == 0) {
                                    commonModel2.fullname = commonModel2.phone
                                }
                            }
                            commonModel2.type = "chat"
                            adapter.updateListItems(commonModel2)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun initRecyclerViewChats(context: Context?, mainListRecycler: RecyclerView) {
        adapter = MainListAdapter(context)
        mainListRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val var10000 = this@ChatsRepository
                val var10001: Iterable<*> = snapshot.children
                Intrinsics.checkNotNullExpressionValue(var10001, "dataSnapshot.children")
                val `destination$iv$iv` = ArrayList<Any?>(
                    var10001.collectionSizeOrDefault<Any>(
                        10
                    )
                ) as MutableCollection<*>
                val var7 = var10001.iterator()
                while (var7.hasNext()) {
                    val `item$iv$iv` = var7.next()!!
                    val it = `item$iv$iv` as DataSnapshot
                    val var15 = this@ChatsRepository
                    Intrinsics.checkExpressionValueIsNotNull(it, "it")
                    val var12 = var15.getCommonModel(it)
                    `destination$iv$iv`.add(var12)
                }
                val tempList: List<CommonModel> =
                    `destination$iv$iv` as List<*>
                for (it in tempList) {
                    Log.d("type", it.type)
                    when (it.type) {
                        "chat" -> showChat(it)
                        "group" -> showGroup(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val manager = LinearLayoutManager(context)
        mainListRecycler.layoutManager = manager
        mainListRecycler.adapter = adapter
    }

    private fun hideKeyboard(activity: HomeActivity) {
        inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager!!.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    fun getCommonModel(`$this$getCommonModel`: DataSnapshot): CommonModel {
        Intrinsics.checkParameterIsNotNull(`$this$getCommonModel`, "\$this\$getCommonModel")
        var var10000 = `$this$getCommonModel`.getValue(CommonModel::class.java) as CommonModel
        if (var10000 == null) {
            var10000 = CommonModel()
        }
        return var10000
    }

    private fun showGroup(commonModel: CommonModel) {
        reference.child("groups").child(commonModel.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val fullName = snapshot.child("fullName").getValue(
                            String::class.java
                        )
                        val photoUrl = snapshot.child("photourl").getValue(
                            String::class.java
                        )
                        val id = snapshot.child("id").getValue(String::class.java)
                        val commonModel = CommonModel()
                        commonModel.fullname = fullName
                        commonModel.photourl = photoUrl
                        commonModel.id = id
                        Log.d("name", commonModel.fullname)
                        reference.child("groups").child(commonModel.id).child("Messages")
                            .limitToLast(1)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val var10000: Iterable<*> = snapshot.children
                                    //  Intrinsics.checkExpressionValueIsNotNull(var10000, "dataSnapshot2.children");
                                    val `destination$iv$iv` = ArrayList<Any?>(
                                        var10000.collectionSizeOrDefault<Any>(
                                            10
                                        )
                                    ) as MutableCollection<*>
                                    val var7 = var10000.iterator()
                                    while (var7.hasNext()) {
                                        val `item$iv$iv` = var7.next()!!
                                        val it = `item$iv$iv` as DataSnapshot
                                        val var15 = this@ChatsRepository
                                        Intrinsics.checkExpressionValueIsNotNull(it, "it")
                                        val var12 = var15.getCommonModel(it)
                                        `destination$iv$iv`.add(var12)
                                    }
                                    val tempList = `destination$iv$iv` as List<*>
                                    if (tempList.isEmpty()) {
                                        commonModel.lastMessage = "Deleted Conversation"
                                    } else {
                                        commonModel.lastMessage = (tempList[0] as CommonModel).text
                                    }
                                    commonModel.type = "group"
                                    adapter.updateListItems(commonModel)
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    companion object {
        var cipher: Cipher? = null
        @JvmField
        var decipher: Cipher? = null
        @JvmField
        var secretKeySpec: SecretKeySpec
        var encryptionKey =
            byteArrayOf(9, 117, 51, 87, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    }

    init {
        reference = FirebaseDatabase
            .getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        mainListRef = reference.child(NODE_MAIN_LIST).child(uid)
        mRefUser = reference.child(NODE_USERS)
        mRefMessages = reference.child(NODE_MESSAGES).child(uid)
        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
        adapter = MainListAdapter(application.applicationContext)
    }
}