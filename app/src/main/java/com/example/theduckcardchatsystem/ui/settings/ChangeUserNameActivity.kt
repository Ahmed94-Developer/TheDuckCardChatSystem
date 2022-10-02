package com.example.theduckcardchatsystem.ui.settings

import android.graphics.Color
import com.example.theduckcardchatsystem.ui.model.User.username
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.EditText
import android.os.Bundle
import butterknife.ButterKnife
import android.graphics.drawable.ColorDrawable
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.example.theduckcardchatsystem.ui.model.User
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec
import kotlin.Throws

class ChangeUserNameActivity : AppCompatActivity() {
    private var settingsViewModel: SettingsViewModel? = null

    @JvmField
    @BindView(R.id.settings_input_user_name)
    var input_user_txt: EditText? = null
    private var cipher: Cipher? = null
    private var decipher: Cipher? = null
    private var secretKeySpec: SecretKeySpec? = null
    private val encryptionKey =
        byteArrayOf(9, 117, 51, 87, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_name)
        ButterKnife.bind(this)
        val colorDrawable = ColorDrawable(Color.parseColor("#133E3E"))
        supportActionBar!!.setBackgroundDrawable(colorDrawable)
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        try {
            cipher = Cipher.getInstance("AES")
            decipher = Cipher.getInstance("AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        secretKeySpec = SecretKeySpec(encryptionKey, "AES")
        settingsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(
            SettingsViewModel::class.java
        )
        settingsViewModel!!.initUser()
        settingsViewModel!!.initUserLiveData.observe(this) { dataSnapshot ->
            if (dataSnapshot != null) {
                val user = dataSnapshot.getValue(
                    User::class.java
                )
                try {
                    input_user_txt!!.setText(AESDecryptionMethod(user!!.username))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_confirm_changes -> {
                val userName = input_user_txt!!.text.toString()
                settingsViewModel!!.changeUserName(AESEncryptionMethod(userName))
            }
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu_confirm, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun AESEncryptionMethod(string: String): String? {
        val stringByte = string.toByteArray()
        var encrypteByte: ByteArray? = ByteArray(stringByte.size)
        try {
            cipher!!.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            encrypteByte = cipher!!.doFinal(stringByte)
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