package com.example.theduckcardchatsystem.ui.chats

import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.theduckcardchatsystem.viewmodel.SingleChatViewModel
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter
import android.os.Bundle
import butterknife.ButterKnife
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextWatcher
import android.text.Editable
import android.text.TextUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.content.Intent
import android.app.Activity
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import butterknife.OnClick
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.theduckcardchatsystem.repository.SingleChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class SingleChatActivity : AppCompatActivity() {
    private var fullName: String? = null
    private var uid: String? = null
    private var status: String? = null
    private var photo: String? = null

    @JvmField
    @BindView(R.id.chat_input_message)
    var chatInputMessage: EditText? = null

    @JvmField
    @BindView(R.id.chat_recycle_view)
    var chatRecyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.chat_btn_send_message)
    var chatBtnSend: FloatingActionButton? = null
    private val TYPE_TEXT = "text"
    private var singleChatViewModel: SingleChatViewModel? = null
    private val TYPE_CHAT = "chat"
    private val adapter: SingleChatAdapter? = null
    private var reference: DatabaseReference? = null
    private val NODE_MESSAGES = "Messages"
    private var CURRENT_UID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat)
        ButterKnife.bind(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val fullNameTxt = toolbar.findViewById<TextView>(R.id.contact_fullname)
        val statusTxt = toolbar.findViewById<TextView>(R.id.contact_status)
        val imageView = toolbar.findViewById<CircleImageView>(R.id.toolbar_image)
        toolbar.inflateMenu(R.menu.single_chat_action_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        CURRENT_UID = FirebaseAuth.getInstance().currentUser!!.uid
        singleChatViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(
            SingleChatViewModel::class.java
        )
        var bundle: Bundle? = Bundle()
        bundle = intent.extras
        if (bundle != null) {
            fullName = bundle.getString("fullName")
            uid = bundle.getString("uid")
            status = bundle.getString("status")
            photo = bundle.getString("photo")
            fullNameTxt.text = fullName
            statusTxt.text = status
            var imageDataBytes = ""
            if (photo == null || fullName!!.isEmpty()) {
                imageDataBytes = "empty"
                fullNameTxt.text = "User Name"
            } else {
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
        singleChatViewModel!!.initRecyclerView(uid, adapter, chatRecyclerView)
        chatInputMessage!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // chatBtnSend.setImageResource(R.drawable.ic_sendicon);
            }

            override fun afterTextChanged(editable: Editable) {
                val message = chatInputMessage!!.text.toString()
                if (TextUtils.isEmpty(message)) {
                    chatBtnSend!!.setImageResource(R.drawable.ic_sendhidden)
                } else {
                    chatBtnSend!!.setImageResource(R.drawable.ic_sendicon)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.single_chat_action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat -> {
                singleChatViewModel!!.ClearChat(uid)
                finish()
            }
            R.id.menu_delete_chat -> {
                singleChatViewModel!!.DeleteChat(uid)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(1000, 1000)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(this@SingleChatActivity)
    }

    override fun onResume() {
        super.onResume()
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
                val messageKey = reference!!.child(NODE_MESSAGES).child(CURRENT_UID!!)
                    .child(uid!!).push().key.toString()
                singleChatViewModel!!.sendMessageAsIimage(uid, photoString, messageKey)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    @OnClick(R.id.chat_btn_attach)
    fun attachImageChatBtn() {
        attachImage()
    }

    @OnClick(R.id.chat_btn_send_message)
    fun chatBtnSendMessage() {
        val message = chatInputMessage!!.text.toString()
        if (message.isEmpty()) {
            Toast.makeText(this@SingleChatActivity, "Enter your Message", Toast.LENGTH_SHORT).show()
        } else {
            singleChatViewModel!!.sendMessage(AESEncryptionMethod(message), uid, TYPE_TEXT)
        }
        singleChatViewModel!!.saveToMainList(uid, TYPE_CHAT)
        chatInputMessage!!.setText("")
    }

    fun AESEncryptionMethod(string: String): String? {
        val stringByte = string.toByteArray()
        var encrypteByte: ByteArray? = ByteArray(stringByte.size)
        try {
            SingleChatRepository.cipher!!.init(
                Cipher.ENCRYPT_MODE,
                SingleChatRepository.secretKeySpec
            )
            encrypteByte = SingleChatRepository.cipher!!.doFinal(stringByte)
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
}