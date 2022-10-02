package com.example.theduckcardchatsystem.ui.groups

import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.ui.model.CommonModel
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.theduckcardchatsystem.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import kotlin.Throws
import com.example.theduckcardchatsystem.ui.groups.CreateGroupActivity
import android.widget.TextView
import android.widget.LinearLayout
import com.example.theduckcardchatsystem.ui.groups.AddContactsFragment
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.util.ArrayList
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class AddContactsAdapter : RecyclerView.Adapter<AddContactsAdapter.AddContactsViewHolder>() {
    var commonModelList: MutableList<CommonModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item, parent, false)
        return AddContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddContactsViewHolder, position: Int) {
        val commonModel = commonModelList[position]
        holder.itemName.text = commonModel.fullname
        if (commonModel.lastMessage == null) {
            holder.itemLastMessage.visibility = View.VISIBLE
            holder.itemLastMessage.text = ""
        } else if (commonModel.lastMessage.isEmpty() && !commonModel.imageUrl.isEmpty()) {
            holder.itemLastMessage.visibility = View.GONE
            holder.mainListNoTificationImage.visibility = View.VISIBLE
        } else if (!commonModel.lastMessage.isEmpty()) {
            try {
                holder.itemLastMessage.text = AESDecryptionMethod(commonModel.lastMessage)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            holder.mainListNoTificationImage.visibility = View.GONE
        }
        if (commonModel.photourl == null) {
            holder.itemPhoto.setImageResource(R.drawable.ic_avatar)
        } else {
            val imageDataBytes = commonModelList[position].photourl
                .substring(commonModelList[position].photourl.indexOf(",") + 1)
            val stream: InputStream =
                ByteArrayInputStream(Base64.decode(imageDataBytes.toByteArray(), Base64.DEFAULT))
            val bitmap = BitmapFactory.decodeStream(stream)
            holder.itemPhoto.setImageBitmap(bitmap)
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun AESDecryptionMethod(string: String): String {
        val EncryptedByte = string.toByteArray(charset("ISO-8859-1"))
        var decryptionString = string
        val decryption: ByteArray
        try {
            CreateGroupActivity.decipher.init(
                Cipher.DECRYPT_MODE,
                CreateGroupActivity.secretKeySpec
            )
            decryption = CreateGroupActivity.decipher.doFinal(EncryptedByte)
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

    override fun getItemCount(): Int {
        return commonModelList.size
    }

    fun updateListItems(item: CommonModel) {
        commonModelList.add(item)
        notifyItemInserted(commonModelList.size)
    }

    inner class AddContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemLastMessage: TextView
        var itemPhoto: CircleImageView
        var itemChoice: CircleImageView
        private val mainListNoTificationImage: LinearLayout

        init {
            itemName = itemView.findViewById(R.id.add_contacts_item_name)
            itemLastMessage = itemView.findViewById(R.id.add_contacts_last_message)
            itemPhoto = itemView.findViewById(R.id.add_contacts_item_photo)
            itemChoice = itemView.findViewById(R.id.add_contacts_item_choice)
            mainListNoTificationImage = itemView.findViewById(R.id.imageView29)
            itemView.setOnClickListener {
                if (commonModelList[absoluteAdapterPosition].choice) {
                    itemChoice.visibility = View.INVISIBLE
                    commonModelList[absoluteAdapterPosition].choice = false
                    AddContactsFragment.contactsList.remove(commonModelList[absoluteAdapterPosition])
                } else {
                    itemChoice.visibility = View.VISIBLE
                    commonModelList[absoluteAdapterPosition].choice = true
                    AddContactsFragment.contactsList.add(commonModelList[absoluteAdapterPosition])
                }
            }
        }
    }
}