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
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.HashMap
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

class SingleChatRepository(private val application: Application) {
    private val reference: DatabaseReference
    private val TYPE_TEXT = "text"
    private val NODE_MESSAGES = "Messages"
    private val CURRENT_UID: String
    private val CHILD_TEXT = "text"
    private val CHILD_TYPE = "type"
    private val CHILD_FROM = "from"
    private val CHILD_TIMESTAMP = "timeStamp"
    private val CHILD_ID = "id"
    private val TYPE_CHAT = "chat"
    private val NODE_MAIN_LIST = "main_list"
    private val SENDER_IMG = "senderImg"
    private val mRefUser: DatabaseReference? = null
    private var mRefMessage: DatabaseReference? = null
    private var mMessageListener: ChildEventListener? = null
    private val commonModelList: List<CommonModel> = ArrayList()
    fun sendMessage(message: String, recevingUserId: String, typeText: String) {
        val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$recevingUserId"
        val refDialogRecevingUser = "$NODE_MESSAGES/$recevingUserId/$CURRENT_UID"
        val messageKey = reference.child(refDialogUser).push().key
        val mapMessage = HashMap<String, Any>()
        mapMessage[CHILD_FROM] = CURRENT_UID
        mapMessage[CHILD_TYPE] = typeText
        mapMessage[CHILD_TEXT] = message
        mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
        val mapDialog = HashMap<String, Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogRecevingUser/$messageKey"] = mapMessage
        reference.updateChildren(mapDialog)
            .addOnSuccessListener { }
            .addOnFailureListener { e ->
                Toast.makeText(
                    application.applicationContext, "" + e.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun sendMessageAsImage(receivingUserID: String, imageUrl: String, messageKey: String) {
        val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
        val refDialogRecevingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
        val mapMessage = HashMap<String, Any>()
        mapMessage[CHILD_FROM] = CURRENT_UID
        mapMessage[CHILD_TYPE] = "image"
        mapMessage[CHILD_ID] = messageKey
        mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
        mapMessage["imageUrl"] = imageUrl
        val mapDialog = HashMap<String, Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogRecevingUser/$messageKey"] = mapMessage
        reference.updateChildren(mapDialog)
            .addOnSuccessListener { }
            .addOnFailureListener { e ->
                Toast.makeText(
                    application.applicationContext, "" + e.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun saveToMainList(id: String, type: String) {
        val refUser = "$NODE_MAIN_LIST/$CURRENT_UID/$id"
        val refReiceved = "$NODE_MAIN_LIST/$id/$CURRENT_UID"
        val mapUser = HashMap<String, Any>()
        val mapReceived = HashMap<String, Any>()
        mapUser[CHILD_ID] = id
        mapUser[CHILD_TYPE] = type
        mapReceived[CHILD_ID] = CURRENT_UID
        mapReceived[CHILD_TYPE] = type
        val commonMap = HashMap<String, Any>()
        commonMap[refUser] = mapUser
        commonMap[refReiceved] = mapReceived
        reference.updateChildren(commonMap)
            .addOnFailureListener { e ->
                Toast.makeText(
                    application.applicationContext, "" + e.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun DeleteChat(id: String?) {
        reference.child(NODE_MAIN_LIST).child(CURRENT_UID).child(id!!).removeValue()
            .addOnFailureListener { e ->
                Toast.makeText(
                    application.applicationContext, "" + e.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                Toast.makeText(
                    application.applicationContext, "Chat Deleted Successfully", Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun clearChat(id: String?) {
        reference.child(NODE_MESSAGES).child(CURRENT_UID).child(id!!)
            .removeValue()
            .addOnFailureListener { e ->
                Toast.makeText(
                    application.applicationContext, "" + e.message.toString(), Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener {
                reference.child(NODE_MESSAGES).child(id).child(CURRENT_UID).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(
                            application.applicationContext,
                            "Chat Cleared Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    application.applicationContext,
                    "" + e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun initRecyclerView(uid: String?, adapter: SingleChatAdapter?, recyclerView: RecyclerView) {
        var adapter = adapter
        mRefMessage = reference.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(uid!!)
        adapter = SingleChatAdapter(application.applicationContext, commonModelList)
        val finalAdapter: SingleChatAdapter = adapter
        mMessageListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val commonModel = snapshot.getValue(CommonModel::class.java)
                finalAdapter.addItem(commonModel)
                recyclerView.smoothScrollToPosition(finalAdapter.itemCount)
                finalAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        mRefMessage!!.addChildEventListener(mMessageListener)
        recyclerView.adapter = finalAdapter
    }

    companion object {
        @JvmField
        var cipher: Cipher? = null
        @JvmField
        var decipher: Cipher? = null
        @JvmField
        var secretKeySpec: SecretKeySpec
        var encryptionKey =
            byteArrayOf(9, 117, 51, 87, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    }

    init {
        CURRENT_UID = FirebaseAuth.getInstance().currentUser!!.uid
        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
        reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    }
}