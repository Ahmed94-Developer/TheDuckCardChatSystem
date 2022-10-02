package com.example.theduckcardchatsystem.ui.settings

import com.example.theduckcardchatsystem.ui.model.States.Companion.UpdateStates
import com.example.theduckcardchatsystem.ui.model.User.photourl
import com.example.theduckcardchatsystem.ui.model.User.phone
import com.example.theduckcardchatsystem.ui.model.User.fullname
import com.example.theduckcardchatsystem.ui.model.User.username
import com.example.theduckcardchatsystem.ui.model.User.bio
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.TextView
import android.os.Bundle
import butterknife.ButterKnife
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.theduckcardchatsystem.ui.model.States
import android.content.Intent
import com.example.theduckcardchatsystem.ui.login.LoginActivity
import com.example.theduckcardchatsystem.ui.settings.ChangeNameActivity
import com.example.theduckcardchatsystem.HomeActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import butterknife.OnClick
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.theduckcardchatsystem.ui.model.User
import com.example.theduckcardchatsystem.ui.settings.ChangeUserNameActivity
import com.example.theduckcardchatsystem.ui.settings.ChangeBioActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import kotlin.Throws

class SettingsFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel? = null
    private var settingsViewModelSnapShot: com.example.theduckcardchatsystem.viewmodel.SettingsViewModel? =
        null

    @JvmField
    @BindView(R.id.settings_phone_number)
    var settingsPhoneNumber: TextView? = null

    @JvmField
    @BindView(R.id.settings_user_login)
    var settingsUserLogin: TextView? = null

    @JvmField
    @BindView(R.id.settings_user_name)
    var settingsUserName: TextView? = null

    @JvmField
    @BindView(R.id.user_bio_label)
    var userBioLabel: TextView? = null

    @JvmField
    @BindView(R.id.settings_user_photo)
    var imageView: CircleImageView? = null
    private var mAuth: FirebaseAuth? = null
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null
    private val encryptionKey =
        byteArrayOf(9, 117, 51, 87, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)

    @JvmField
    @BindView(R.id.settings_toolbar)
    var settingsToolBar: Toolbar? = null
    private var recyclerviewRef: DatabaseReference? = null
    private var mRefMessage: DatabaseReference? = null
    private var CURRENT_UID: String? = null
    private val valueEventListener: ValueEventListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        ButterKnife.bind(this, root)
        (activity as AppCompatActivity?)!!.setSupportActionBar(settingsToolBar)
        (activity as AppCompatActivity?)!!.title = ""
        mAuth = FirebaseAuth.getInstance()
        CURRENT_UID = mAuth!!.currentUser!!.uid
        settingsViewModelSnapShot = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                activity!!.application
            )
        )
            .get(com.example.theduckcardchatsystem.viewmodel.SettingsViewModel::class.java)
        recyclerviewRef =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        mRefMessage = recyclerviewRef!!.child("groups")
        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        userData
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                UpdateStates(States.OFFLINE)
                mAuth!!.signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity!!.finish()
            }
            R.id.settings_menu_change_name -> startActivity(
                Intent(
                    activity,
                    ChangeNameActivity::class.java
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changePhoto() {
        val activity = activity as HomeActivity?
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!, this)
    }

    @OnClick(R.id.settings_change_photo)
    fun ChangeImage() {
        changePhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                val path = resultUri.path
                Log.d("path", path!!)
                val bitmap = BitmapFactory.decodeFile(path)
                val byteArrayOutputStream = ByteArrayOutputStream()
                // In case you want to compress your image, here it's at 40%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val photoString = Base64.encodeToString(byteArray, Base64.DEFAULT)
                /*   valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot datas: snapshot.getChildren()){
                            String key= datas.getKey();
                            //Log.d("key",key);
                            for(DataSnapshot booksSnapshot : datas.child("Messages").getChildren()){
                                //loop 2 to go through all the child nodes of books node
                                String bookskey = booksSnapshot.getKey();
                                Log.d("book",bookskey);
                               CommonModel commonModel = booksSnapshot.getValue(CommonModel.class);
                               Log.d("from",commonModel.getFrom());
                               if (commonModel.getFrom().equals(CURRENT_UID)){
                                   booksSnapshot.child("senderImg").getRef().setValue(photoString);
                               }


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                mRefMessage.addValueEventListener(valueEventListener);*/settingsViewModelSnapShot!!.changePhotoViewModel(
                    photoString
                )
                settingsViewModelSnapShot!!.photoSnapShotLiveData().observe(this) { dataSnapshot ->
                    if (dataSnapshot != null) {
                        val user = dataSnapshot.getValue(
                            User::class.java
                        )
                        val imageDataBytes =
                            user!!.photourl!!.substring(user.photourl!!.indexOf(",") + 1)
                        val stream: InputStream = ByteArrayInputStream(
                            Base64.decode(
                                imageDataBytes.toByteArray(),
                                Base64.DEFAULT
                            )
                        )
                        val bitmap = BitmapFactory.decodeStream(stream)
                        imageView!!.setImageBitmap(bitmap)
                        val editor =
                            activity!!.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                        editor.putString("photo", photoString)
                        editor.apply()
                    }
                }
            }
        }
    }

    val userData: Unit
        get() {
            settingsViewModelSnapShot!!.setSettingsRepository()
            settingsViewModelSnapShot!!.dataSnapshotLiveData().observe(activity!!) { dataSnapshot ->
                var user: User? = User()
                user = dataSnapshot.getValue(User::class.java)
                settingsPhoneNumber!!.text = user!!.phone
                settingsUserName!!.text = user.fullname
                settingsUserLogin!!.text = user.username
                try {
                    userBioLabel!!.text = AESDecryptionMethod(user.bio)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                if (user.photourl == null) {
                    imageView!!.setImageResource(R.drawable.ic_avatar)
                } else {
                    val imageDataBytes = user.photourl!!.substring(user.photourl!!.indexOf(",") + 1)
                    val stream: InputStream = ByteArrayInputStream(
                        Base64.decode(
                            imageDataBytes.toByteArray(),
                            Base64.DEFAULT
                        )
                    )
                    val bitmap = BitmapFactory.decodeStream(stream)
                    imageView!!.setImageBitmap(bitmap)
                }
            }
        }

    @OnClick(R.id.btn_settings_change_user_name)
    fun bthChangeUserName() {
        startActivity(Intent(activity, ChangeUserNameActivity::class.java))
    }

    @OnClick(R.id.btn_settings_change_bio)
    fun btnChangeBio() {
        startActivity(Intent(activity, ChangeBioActivity::class.java))
    }

    @Throws(UnsupportedEncodingException::class)
    private fun AESDecryptionMethod(string: String): String {
        val EncryptedByte = string.toByteArray(charset("ISO-8859-1"))
        var decryptionString = string
        val decryption: ByteArray
        try {
            decipher!!.init(Cipher.DECRYPT_MODE, secretKeySpec)
            decryption = decipher!!.doFinal(EncryptedByte)
            decryptionString = String(decryption)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }
        return decryptionString
    }
}