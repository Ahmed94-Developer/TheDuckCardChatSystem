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
import android.util.Base64
import android.util.Log
import com.example.theduckcardchatsystem.ui.chats.SingleChatActivity
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
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
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.HashMap

class ContactsRepository(private val application: Application) {
    private var refrenceContacts: DatabaseReference
    private val reference: DatabaseReference
    private var options: FirebaseRecyclerOptions<CommonModel>
    private val uid: String
    private var mRefUsers: DatabaseReference? = null
    private var mRefUserListener: ValueEventListener? = null
    private val mapListener = HashMap<DatabaseReference, ValueEventListener>()
    fun initRecyclerView(
        mAdapter: FirebaseRecyclerAdapter<CommonModel?, ContactsHolder?>,
        rv_contacts: RecyclerView,
        context: Context
    ) {
        var mAdapter = mAdapter
        refrenceContacts = reference.child("phones_contacts").child(uid)
        options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refrenceContacts, CommonModel::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {
            override fun onBindViewHolder(
                holder: ContactsHolder, position: Int, model: CommonModel
            ) {
                mRefUsers = reference.child("users").child(model.id)
                mRefUserListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val commonModel = getCommonModel(snapshot)
                        if (commonModel.fullname == null) {
                            holder.fullNameTxt.text = "Unknown User"
                            holder.stautsTxt.text = commonModel.state
                        } else {
                            if (commonModel.fullname.isEmpty() || commonModel.fullname == null) {
                                holder.fullNameTxt.text = commonModel.fullname
                                holder.stautsTxt.text = commonModel.state
                            } else {
                                holder.fullNameTxt.text = commonModel.fullname
                                holder.stautsTxt.text = commonModel.state
                            }
                        }
                        var imageDataBytes = ""
                        if (commonModel.photourl == null) {
                            imageDataBytes = "empty"
                        } else {
                            imageDataBytes =
                                commonModel.photourl.substring(commonModel.photourl.indexOf(",") + 1)
                            val stream: InputStream = ByteArrayInputStream(
                                Base64.decode(
                                    imageDataBytes.toByteArray(),
                                    Base64.DEFAULT
                                )
                            )
                            val bitmap = BitmapFactory.decodeStream(stream)
                            holder.circle_photo.setImageBitmap(bitmap)
                        }
                        holder.itemView.setOnClickListener {
                            val i = Intent(context, SingleChatActivity::class.java)
                            i.putExtra("fullName", commonModel.fullname)
                            i.putExtra("uid", commonModel.uid)
                            Log.d("uid", commonModel.uid)
                            i.putExtra("status", commonModel.state)
                            i.putExtra("photo", commonModel.photourl)
                            context.startActivity(i)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                }
                mRefUsers!!.addValueEventListener(mRefUserListener)
                mapListener[mRefUsers!!] = mRefUserListener
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }
        }
        val manager = LinearLayoutManager(context)
        rv_contacts.layoutManager = manager
        rv_contacts.adapter = mAdapter
        mAdapter.startListening()
    }

    private fun getCommonModel(dataSnapshot: DataSnapshot): CommonModel {
        var commonModel = dataSnapshot.getValue(CommonModel::class.java)
        if (commonModel == null) {
            commonModel = CommonModel()
        }
        return commonModel
    }

    inner class ContactsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTxt: TextView
        val stautsTxt: TextView
        val circle_photo: CircleImageView

        init {
            fullNameTxt = itemView.findViewById(R.id.contact_fullname)
            stautsTxt = itemView.findViewById(R.id.contact_status)
            circle_photo = itemView.findViewById(R.id.contact_photo)
        }
    }

    init {
        reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        refrenceContacts = reference.child("phones_contacts").child(uid)
        options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refrenceContacts, CommonModel::class.java)
            .build()
    }
}