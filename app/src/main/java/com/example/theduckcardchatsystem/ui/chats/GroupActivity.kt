package com.example.theduckcardchatsystem.ui.chats

import androidx.appcompat.app.AppCompatActivity
import com.example.theduckcardchatsystem.viewmodel.GroupChatViewModel
import android.os.Bundle
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import butterknife.OnClick
import android.widget.Toast
import com.example.theduckcardchatsystem.repository.GroupChatRepository
import android.content.Intent
import com.theartofdev.edmodo.cropper.CropImage
import android.app.Activity
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class GroupActivity : AppCompatActivity() {
    private var groupChatViewModel: GroupChatViewModel? = null
    private var bundle: Bundle? = null
    private var id: String? = null
    private var fullName: String? = null
    private var uid: String? = null
    private var photo: String? = null
    private val TYPE_CHAT = "chat"
    private val TYPE_TEXT = "text"

    @JvmField
    @BindView(R.id.chat_input_message)
    var inputMessageTxt: EditText? = null
    private val settingsViewModel: SettingsViewModel? = null
    private val imageSender: String? = null
    private val UserName: String? = null

    @JvmField
    @BindView(R.id.chat_recycle_view)
    var recyclerView: RecyclerView? = null
    private val reference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        ButterKnife.bind(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val fullNameTxt = toolbar.findViewById<TextView>(R.id.contact_fullname)
        val imageView = toolbar.findViewById<CircleImageView>(R.id.toolbar_image)
        toolbar.inflateMenu(R.menu.single_chat_action_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        groupChatViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(
            GroupChatViewModel::class.java
        )
        bundle = intent.extras
        if (bundle != null) {
            id = bundle!!.getString("id")
            fullName = bundle!!.getString("fullName")
            uid = bundle!!.getString("uid")
            photo = bundle!!.getString("photo")
            fullNameTxt.text = fullName
            var imageDataBytes = ""
            val editor = getSharedPreferences("ids", MODE_PRIVATE).edit()
            editor.putString("id", id)
            editor.apply()
            if (photo == "empty") {
                //  imageDataBytes = "empty";
                imageView.setImageResource(R.drawable.ic_grop)
            } else {
                fullNameTxt.text = fullName
                imageDataBytes = photo!!.substring(photo!!.indexOf(",") + 1)
                val stream: InputStream = ByteArrayInputStream(
                    Base64.decode(
                        imageDataBytes.toByteArray(),
                        Base64.DEFAULT
                    )
                )
                val bitmap = BitmapFactory.decodeStream(stream)
                imageView.setImageBitmap(bitmap)
            }
        }
        groupChatViewModel!!.initRecyclerView(id, recyclerView, this)
    }

    @OnClick(R.id.chat_btn_send_message)
    fun ChatSendMessage() {
        val message = inputMessageTxt!!.text.toString()
        if (message.isEmpty()) {
            Toast.makeText(this@GroupActivity, "Enter your Message", Toast.LENGTH_SHORT).show()
        } else {
            groupChatViewModel!!.sendMessage(AESEncryptionMethod(message), id, TYPE_TEXT)
        }
        groupChatViewModel!!.saveToMainList(id, "group")
        inputMessageTxt!!.setText("")
    }

    @OnClick(R.id.chat_btn_attach)
    fun attachImageChatBtn() {
        attachImage()
    }

    fun AESEncryptionMethod(string: String): String? {
        val stringByte = string.toByteArray()
        var encrypteByte: ByteArray? = ByteArray(stringByte.size)
        try {
            GroupChatRepository.cipher!!.init(
                Cipher.ENCRYPT_MODE,
                GroupChatRepository.secretKeySpec
            )
            encrypteByte = GroupChatRepository.cipher!!.doFinal(stringByte)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }
        var returnString: String? = null
        try {
            returnString = String(encrypteByte!!, "ISO-8859-1")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return returnString
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.single_chat_action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> groupChatViewModel!!.ClearChat(id)
            R.id.menu_delete_chat -> groupChatViewModel!!.DeleteChat(id)
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                val path1 = resultUri.path
                val bitmap = BitmapFactory.decodeFile(path1)
                val byteArrayOutputStream = ByteArrayOutputStream()
                // In case you want to compress your image, here it's at 40%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val photoString = Base64.encodeToString(byteArray, Base64.DEFAULT)

                //   String messageKey = reference.child(NODE_MESSAGES).child(CURRENT_UID)
                //         .child(commonModel.getUid()).push().getKey().toString();
                groupChatViewModel!!.sendImageAsMessage(photoString, id)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}