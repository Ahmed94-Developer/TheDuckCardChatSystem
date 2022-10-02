package com.example.theduckcardchatsystem.ui.groups

import com.example.theduckcardchatsystem.ui.groups.AddContactsAdapter.updateListItems
import androidx.appcompat.app.AppCompatActivity
import com.example.theduckcardchatsystem.ui.room.viewmodel.MembersViewModel
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.EditText
import butterknife.ButterKnife
import androidx.lifecycle.ViewModelProvider
import com.example.theduckcardchatsystem.ui.groups.CreateGroupActivity
import com.example.theduckcardchatsystem.ui.model.MembersId
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theduckcardchatsystem.ui.model.CommonModel
import com.example.theduckcardchatsystem.ui.groups.AddContactsFragment
import com.google.android.gms.tasks.OnSuccessListener
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.content.Intent
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.appcompat.widget.Toolbar
import butterknife.OnClick
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.security.NoSuchAlgorithmException
import java.util.HashMap
import java.util.function.Function
import java.util.stream.Collectors
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

class CreateGroupActivity : AppCompatActivity() {
    private var namesMap: MutableMap<String?, Any>? = null
    private var membersViewModel: MembersViewModel? = null

    @JvmField
    @BindView(R.id.create_group_counts)
    var groupCountTxt: TextView? = null

    @JvmField
    @BindView(R.id.create_group_recycle_view)
    var createGroupRecycler: RecyclerView? = null
    var encryptionKey =
        byteArrayOf(9, 117, 51, 87, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)
    var bundle: Bundle? = null
    var count = 0
    private var mapData: HashMap<String, Any?>? = null
    private var map: MutableMap<String, Any>? = null
    private val CHILD_ID = "id"
    private val CHILD_FUllNAME = "fullName"
    private val USER_CREATOR = "creator"
    private var uid: String? = null
    private var keyGroup: String? = null
    private var nameGroup: String? = null
    private var path: DatabaseReference? = null
    private val NODE_GROUPS = "groups"
    private val CHILD_TYPE = "type"
    private val NODE_MAIN_LIST = "main_list"
    private var mainListRefrence: DatabaseReference? = null
    private var mUri: Uri? = null
    var reference: DatabaseReference? = null

    @JvmField
    @BindView(R.id.create_group_btn_complete)
    var createGroupBtn: FloatingActionButton? = null

    @BindView(R.id.create_group_photo)
    var createGroupPhoto: CircleImageView? = null

    @JvmField
    @BindView(R.id.create_group_input_name)
    var createGroupInputName: EditText? = null
    var photoString: String? = null
    private var settingsViewModel: SettingsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        ButterKnife.bind(this)
        settingsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(
            SettingsViewModel::class.java
        )
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val toolbar = findViewById<Toolbar>(R.id.toolbar_create_group)
        setSupportActionBar(toolbar)
        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
        reference = FirebaseDatabase
            .getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        mainListRefrence = FirebaseDatabase
            .getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference
        keyGroup = reference!!.child(NODE_GROUPS).push().key.toString()
        Log.d("key", keyGroup!!)
        map = HashMap()
        path = reference!!.child(NODE_GROUPS).child(keyGroup!!)
        bundle = intent.extras
        membersViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(
            MembersViewModel::class.java
        )
        if (bundle != null) {
            count = bundle!!.getInt("size")
        }
        resources.getQuantityString(R.plurals.count_members, count)
        groupCountTxt!!.text = resources
            .getQuantityString(
                R.plurals.count_members, count, count
            )
        membersViewModel!!.membersIdLiveData.observe(this) { membersIds ->
            namesMap = membersIds.stream().distinct()
                .collect(
                    Collectors.toMap(
                        Function { s: MembersId -> s.memberId.toString() },
                        Function<MembersId, Any> { s: MembersId? -> "member" })
                )
        }
        initRecyclerView()
        mapData = HashMap()
    }

    private fun initRecyclerView() {
        val adapter = AddContactsAdapter()
        val manager = LinearLayoutManager(this)
        createGroupRecycler!!.adapter = adapter
        createGroupRecycler!!.layoutManager = manager
        for (it in AddContactsFragment.contactsList) {
            adapter.updateListItems(it)
        }
    }

    private fun createGroupToDataBase(
        nameGroup: String,
        mUri: Uri?,
        contactsList: List<CommonModel?>
    ) {
        mapData = HashMap()
        mapData!![CHILD_ID] = keyGroup
        mapData!![CHILD_FUllNAME] = nameGroup
        if (photoString == null) {
            mapData!!["photourl"] = "empty"
        } else {
            mapData!!["photourl"] = photoString
        }
        namesMap!![uid] = USER_CREATOR
        mapData!!["members"] = namesMap
        path!!.updateChildren(mapData!!).addOnSuccessListener {
            Toast.makeText(
                this@CreateGroupActivity, "Group Created Successfully", Toast.LENGTH_SHORT
            ).show()
            addGroupToMainList(mapData!!, contactsList)
        }
    }

    fun addGroupToMainList(mapData: HashMap<String, Any?>, commonModelList: List<CommonModel?>) {
        (map as MutableMap<*, *>?)!![CHILD_ID] = mapData[CHILD_ID].toString()
        (map as MutableMap<*, *>?)!![CHILD_TYPE] = "group"
        val `$this$forEach$iv` = commonModelList as Iterable<*>
        val var8 = `$this$forEach$iv`.iterator()
        while (var8.hasNext()) {
            val `element$iv` = var8.next()!!
            val it = `element$iv` as CommonModel
            mainListRefrence!!.child(NODE_MAIN_LIST).child(it.uid)
                .child(map!![CHILD_ID].toString()).updateChildren(map as Map<*, *>?)
        }
        mainListRefrence!!.child(NODE_MAIN_LIST).child(uid!!)
            .child(map!![CHILD_ID].toString())
            .updateChildren(map as Map<*, *>?).addOnSuccessListener { }
            .addOnFailureListener { }
    }

    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                mUri = result.uri
                Log.d("uri", mUri.toString() + "")
                createGroupPhoto!!.setImageURI(mUri)
                val resultUri = result.uri
                val path1 = resultUri.path
                val bitmap = BitmapFactory.decodeFile(path1)
                val byteArrayOutputStream = ByteArrayOutputStream()
                // In case you want to compress your image, here it's at 40%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                photoString = Base64.encodeToString(byteArray, Base64.DEFAULT)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    @OnClick(R.id.create_group_photo)
    fun createGroupPhoto() {
        addPhoto()
    }

    @OnClick(R.id.create_group_btn_complete)
    fun createGroupBtnComplete() {
        nameGroup = createGroupInputName!!.text.toString()
        if (nameGroup!!.isEmpty()) {
            Toast.makeText(this, "Enter Group Name", Toast.LENGTH_SHORT).show()
        } else {
            createGroupToDataBase(nameGroup!!, mUri, AddContactsFragment.contactsList)
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @OnClick(R.id.back_btn)
    fun OnClickBack() {
        onSupportNavigateUp()
    }

    companion object {
        var cipher: Cipher? = null
        var decipher: Cipher? = null
        var secretKeySpec: SecretKeySpec? = null
    }
}