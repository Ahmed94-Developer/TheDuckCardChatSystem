package com.example.theduckcardchatsystem.ui.home

import com.example.theduckcardchatsystem.ui.home.HomeViewModel
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.viewmodel.ChatsViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import butterknife.ButterKnife
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.theduckcardchatsystem.ui.model.User
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel? = null

    @JvmField
    @BindView(R.id.profile_id)
    var profileImage: CircleImageView? = null

    @JvmField
    @BindView(R.id.home_toolbar)
    var homeToolBar: Toolbar? = null

    @JvmField
    @BindView(R.id.recycler_chats)
    var recyclerChats: RecyclerView? = null
    private var chatsViewModel: ChatsViewModel? = null
    private var settingsViewModel: SettingsViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        ButterKnife.bind(this, root)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(homeToolBar)
        (activity as AppCompatActivity?)!!.title = ""
        chatsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                activity!!.application
            )
        )
            .get(ChatsViewModel::class.java)
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
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                var imageDataBytes = ""
                if (user!!.photourl == null) {
                    imageDataBytes = "empty"
                } else {
                    imageDataBytes = user.photourl.substring(user.photourl.indexOf(",") + 1)
                    val stream: InputStream = ByteArrayInputStream(
                        Base64.decode(
                            imageDataBytes.toByteArray(),
                            Base64.DEFAULT
                        )
                    )
                    val bitmap = BitmapFactory.decodeStream(stream)
                    profileImage!!.setImageBitmap(bitmap)
                }
            } else {
                profileImage!!.setImageResource(R.drawable.ic_profile)
            }
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        chatsViewModel!!.initRecyclerViewChats(activity, recyclerChats)
    }
}