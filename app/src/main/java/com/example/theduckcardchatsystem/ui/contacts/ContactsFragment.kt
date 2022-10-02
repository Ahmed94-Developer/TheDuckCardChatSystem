package com.example.theduckcardchatsystem.ui.contacts

import butterknife.BindView
import com.example.theduckcardchatsystem.R
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.example.theduckcardchatsystem.ui.model.CommonModel
import com.example.theduckcardchatsystem.repository.ContactsRepository.ContactsHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import butterknife.ButterKnife
import androidx.lifecycle.ViewModelProvider
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.fragment.app.Fragment
import com.example.theduckcardchatsystem.viewmodel.ContactsViewModel
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.HashMap

class ContactsFragment : Fragment() {
    @JvmField
    @BindView(R.id.rv_contacts)
    var contactsRecyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.profile_id_contacts)
    var circleProfile: CircleImageView? = null
    private var contactsViewModel: ContactsViewModel? = null
    private val mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>? = null
    private val mapListener = HashMap<DatabaseReference, ValueEventListener>()
    private var settingsViewModel: SettingsViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)
        ButterKnife.bind(this, root)
        contactsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                activity!!.application
            )
        ).get(
            ContactsViewModel::class.java
        )
        settingsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                activity!!.application
            )
        ).get(
            SettingsViewModel::class.java
        )
        settingsViewModel!!.setSettingsRepository()
        settingsViewModel!!.dataSnapshotLiveData().observe(activity!!) { dataSnapshot ->
            if (dataSnapshot != null) {
                val photo = dataSnapshot.child("photourl").getValue(
                    String::class.java
                )
                if (photo == null) {
                    circleProfile!!.setImageResource(R.drawable.ic_profile)
                } else {
                    val imageDataBytes = photo.substring(photo.indexOf(",") + 1)
                    val stream: InputStream = ByteArrayInputStream(
                        Base64.decode(
                            imageDataBytes.toByteArray(),
                            Base64.DEFAULT
                        )
                    )
                    val bitmap = BitmapFactory.decodeStream(stream)
                    circleProfile!!.setImageBitmap(bitmap)
                }
            }
        }
        contactsViewModel!!.initRecyclerView(mAdapter, contactsRecyclerView, activity)
        return root
    }

    override fun onPause() {
        super.onPause()
        //   mAdapter.stopListening();
        for ((key, value) in mapListener) {
            key.removeEventListener(value)
        }
    }
}