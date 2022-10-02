package com.example.theduckcardchatsystem.ui.groups

import com.example.theduckcardchatsystem.viewmodel.AddContactsViewModel
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.ui.model.CommonModel
import com.example.theduckcardchatsystem.ui.room.viewmodel.MembersViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import butterknife.ButterKnife
import androidx.lifecycle.ViewModelProvider
import com.example.theduckcardchatsystem.ui.groups.AddContactsFragment
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import butterknife.OnClick
import android.widget.Toast
import com.example.theduckcardchatsystem.ui.model.MembersId
import android.content.Intent
import android.util.Base64
import android.view.View
import androidx.fragment.app.Fragment
import com.example.theduckcardchatsystem.ui.groups.CreateGroupActivity
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.ArrayList

class AddContactsFragment : Fragment() {
    private var addContactsViewModel: AddContactsViewModel? = null

    @JvmField
    @BindView(R.id.add_contacts_recycle_view)
    var addContactsRecycler: RecyclerView? = null

    @JvmField
    @BindView(R.id.profile_photo_create_group)
    var profile_photo_image: CircleImageView? = null
    private val commonModelList: List<CommonModel> = ArrayList()
    private var membersViewModel: MembersViewModel? = null
    private var settingsViewModel: SettingsViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_contacts, container, false)
        ButterKnife.bind(this, root)
        addContactsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                activity!!.application
            )
        ).get(
            AddContactsViewModel::class.java
        )
        addContactsViewModel!!.initRecyclerView(addContactsRecycler, activity)
        membersViewModel = ViewModelProvider(
            activity!!,
            ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
        ).get(
            MembersViewModel::class.java
        )
        contactsList.clear()
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
                    profile_photo_image!!.setImageResource(R.drawable.ic_profile)
                } else {
                    val imageDataBytes = photo.substring(photo.indexOf(",") + 1)
                    val stream: InputStream = ByteArrayInputStream(
                        Base64.decode(
                            imageDataBytes.toByteArray(),
                            Base64.DEFAULT
                        )
                    )
                    val bitmap = BitmapFactory.decodeStream(stream)
                    profile_photo_image!!.setImageBitmap(bitmap)
                }
            }
        }
        return root
    }

    @OnClick(R.id.add_contacts_btn_next)
    fun btnNext() {
        if (contactsList.isEmpty()) {
            Toast.makeText(activity, "Add a participant", Toast.LENGTH_SHORT).show()
        } else {
            for (it in contactsList) {
                val membersId = MembersId(it.uid)
                membersViewModel!!.insert(membersId)
                val membersId1 = MembersId(it.uid)
                membersId.id = membersId1.id
                membersViewModel!!.Update(membersId1)
            }
            val i = Intent(activity, CreateGroupActivity::class.java)
            i.putExtra("size", contactsList.size)
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        membersViewModel!!.DeleteAll()
    }

    companion object {
        @JvmField
        var contactsList: MutableList<CommonModel> = ArrayList()
    }
}