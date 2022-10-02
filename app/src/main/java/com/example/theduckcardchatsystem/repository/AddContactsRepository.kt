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
import com.example.theduckcardchatsystem.adapters.AddContactsAdapter
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

class AddContactsRepository(private val applicationl: Application) {
    private val reference: DatabaseReference
    private val mAdapter: AddContactsAdapter
    private val mRefMainList: DatabaseReference
    private val mRefUser: DatabaseReference
    private val mRefMessages: DatabaseReference
    private val NODE_MAIN_LIST = "main_list"
    private val NODE_USERS = "users"
    private val NODE_MESSAGES = "Messages"
    private val CURRENT_UID: String
    private var commonModelList: List<CommonModel> = ArrayList()
    fun initRecyclerView(recyclerView: RecyclerView, context: Context?) {
        mRefMainList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var iterable: Iterable<*> = snapshot.children
                val iterator = iterable.iterator()
                var var10000 = this@AddContactsRepository
                val var11 = var10000
                val `destination$iv$iv` =
                    ArrayList<Any?>(iterable.collectionSizeOrDefault<Any>(10)) as MutableCollection<*>
                while (iterator.hasNext()) {
                    val `item$iv$iv` = iterator.next()!!
                    val it = `item$iv$iv` as DataSnapshot
                    var10000 = this@AddContactsRepository
                    Intrinsics.checkExpressionValueIsNotNull(it, "it")
                    val var13 = var10000.getCommonModel(it)
                    `destination$iv$iv`.add(var13)
                }
                val var12 = `destination$iv$iv` as List<*>
                var11.commonModelList = var12
                iterable = commonModelList
                val var4 = iterable.iterator()
                while (var4.hasNext()) {
                    val `element$iv` = var4.next()!!
                    val model = `element$iv` as CommonModel
                    Log.d("id", model.id)
                    mRefUser.child(model.id)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val newModel = snapshot.getValue(
                                    CommonModel::class.java
                                )
                                // Log.d("id",newModel.getId());
                                mRefMessages.child(model.id).limitToLast(1)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val var10000: Iterable<*> = snapshot.children
                                            //  Intrinsics.checkExpressionValueIsNotNull(var10000, "dataSnapshot2.children");
                                            val iterable1 = var10000
                                            val `destination$iv$iv` = ArrayList<Any?>(
                                                iterable1.collectionSizeOrDefault<Any>(10)
                                            ) as MutableCollection<*>
                                            val var7 = iterable1.iterator()
                                            while (var7.hasNext()) {
                                                val `item$iv$iv` = var7.next()!!
                                                val it = `item$iv$iv` as DataSnapshot
                                                val var15 = this@AddContactsRepository
                                                Intrinsics.checkExpressionValueIsNotNull(it, "it")
                                                val var12 = var15.getCommonModel(it)
                                                `destination$iv$iv`.add(var12 as Nothing)
                                            }
                                            val tempList = `destination$iv$iv` as List<*>
                                            if (tempList.isEmpty()) {
                                                newModel!!.lastMessage = "Deleted Conversation"
                                            } else {
                                                try {
                                                    newModel!!.lastMessage =
                                                        (tempList[0] as CommonModel)
                                                            .text
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                            /*  try {
                                                newModel.setLastMessage(((CommonModel)tempList.get(0)).getText());
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }*/if (newModel!!.fullname == null) {
                                                newModel.fullname = "Unknown User"
                                            } else {
                                                val var16 = newModel.fullname
                                                Intrinsics.checkExpressionValueIsNotNull(
                                                    var16,
                                                    "newModel.fullname"
                                                )
                                                val var14 = var16 as CharSequence
                                                if (var14.length == 0) {
                                                    newModel.fullname = newModel.phone
                                                }
                                            }
                                            mAdapter.updateListItems(newModel)
                                        }

                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        val manager = LinearLayoutManager(context)
        recyclerView.layoutManager = manager
        recyclerView.adapter = mAdapter
    }

    fun getCommonModel(`$this$getCommonModel`: DataSnapshot): CommonModel {
        Intrinsics.checkParameterIsNotNull(`$this$getCommonModel`, "\$this\$getCommonModel")
        var var10000 = `$this$getCommonModel`.getValue(CommonModel::class.java) as CommonModel
        if (var10000 == null) {
            var10000 = CommonModel()
        }
        return var10000
    }

    companion object {
        var cipher: Cipher? = null
        @JvmField
        var decipher: Cipher? = null

        lateinit var secretKeySpec: SecretKeySpec
        var encryptionKey =
            byteArrayOf(9, 117, 51, 87, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    }

    init {
        reference = FirebaseDatabase
            .getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        CURRENT_UID = FirebaseAuth.getInstance().currentUser!!.uid
        mAdapter = AddContactsAdapter()
        mRefMainList = reference.child("phones_contacts").child(CURRENT_UID)
        mRefUser = reference.child(NODE_USERS)
        mRefMessages = reference.child(NODE_MESSAGES).child(CURRENT_UID)
        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
    }
}