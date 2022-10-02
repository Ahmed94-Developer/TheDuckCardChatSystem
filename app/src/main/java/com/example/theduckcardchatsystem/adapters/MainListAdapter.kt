package com.example.theduckcardchatsystem.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.ui.model.CommonModel
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.theduckcardchatsystem.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.Throws
import com.example.theduckcardchatsystem.repository.AddContactsRepository
import android.widget.TextView
import android.widget.LinearLayout
import com.example.theduckcardchatsystem.ui.groups.AddContactsFragment
import com.example.theduckcardchatsystem.adapters.GroupChatAdapter.GroupChatViewHolder
import androidx.recyclerview.widget.DiffUtil.DiffResult
import com.example.theduckcardchatsystem.adapters.GroupChatAdapter
import android.text.method.LinkMovementMethod
import androidx.recyclerview.widget.DiffUtil
import com.example.theduckcardchatsystem.ui.model.DiffUtilCalback
import com.example.theduckcardchatsystem.repository.GroupChatRepository
import android.graphics.RectF
import android.graphics.PorterDuffXfermode
import android.graphics.PorterDuff
import com.example.theduckcardchatsystem.adapters.MainListAdapter.MainListViewHolder
import com.example.theduckcardchatsystem.repository.ChatsRepository
import android.content.Intent
import android.util.Base64
import android.util.Log
import android.view.View
import com.example.theduckcardchatsystem.ui.chats.SingleChatActivity
import com.example.theduckcardchatsystem.ui.chats.GroupActivity
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter.SingleChatViewHolder
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter
import com.example.theduckcardchatsystem.repository.SingleChatRepository
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.security.InvalidKeyException
import java.util.ArrayList
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class MainListAdapter(private val context: Context) : RecyclerView.Adapter<MainListViewHolder>() {
    private val commonModelList: MutableList<CommonModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        return MainListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
        val commonModel = commonModelList[position]
        holder.itemName.text = commonModel.fullname
        try {
            if (commonModel.lastMessage.isEmpty()) {
                holder.itemLastMessage.visibility = View.VISIBLE
                holder.itemLastMessage.text = ""
            } else if (commonModel.lastMessage.isEmpty() && !commonModel.imageUrl.isEmpty()) {
                holder.itemLastMessage.visibility = View.GONE
                holder.mainListNoTificationImage.visibility = View.VISIBLE
            } else if (!commonModel.lastMessage.isEmpty()) {
                holder.itemLastMessage.text = AESDecryptionMethod(commonModel.lastMessage)
                holder.mainListNoTificationImage.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val imagesDataBytes = ""
        if (commonModelList[position].photourl == null) {
            holder.mainListItemPhoto.setImageResource(R.drawable.ic_avatar)
        } else if (commonModelList[position].photourl == "empty") {
            holder.mainListItemPhoto.setImageResource(R.drawable.ic_groups)
        } else {
            val imageDataBytes = commonModelList[position].photourl
                .substring(commonModelList[position].photourl.indexOf(",") + 1)
            val stream: InputStream =
                ByteArrayInputStream(Base64.decode(imageDataBytes.toByteArray(), Base64.DEFAULT))
            val bitmap = BitmapFactory.decodeStream(stream)
            holder.mainListItemPhoto.setImageBitmap(bitmap)
        }
    }

    fun updateListItems(item: CommonModel) {
        commonModelList.add(item)
        notifyItemInserted(commonModelList.size)
        Log.d("size", commonModelList.size.toString() + "")
    }

    override fun getItemCount(): Int {
        return commonModelList.size
    }

    @Throws(UnsupportedEncodingException::class)
    private fun AESDecryptionMethod(string: String): String {
        val EncryptedByte = string.toByteArray(charset("ISO-8859-1"))
        var decryptionString = string
        val decryption: ByteArray
        try {
            ChatsRepository.decipher!!.init(Cipher.DECRYPT_MODE, ChatsRepository.secretKeySpec)
            decryption = ChatsRepository.decipher!!.doFinal(EncryptedByte)
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

    inner class MainListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var itemLastMessage: TextView
        var mainListItemPhoto: CircleImageView
        var mainListNoTificationImage: LinearLayout

        init {
            itemName = itemView.findViewById(R.id.main_list_item_name)
            itemLastMessage = itemView.findViewById(R.id.main_list_last_message)
            mainListItemPhoto = itemView.findViewById(R.id.main_list_item_photo)
            mainListNoTificationImage = itemView.findViewById(R.id.imageView29)
            itemView.setOnClickListener {
                try {
                    when (commonModelList[adapterPosition].type) {
                        "chat" -> {
                            val i = Intent(context, SingleChatActivity::class.java)
                            i.putExtra("uid", commonModelList[absoluteAdapterPosition].uid)
                            i.putExtra(
                                "fullName",
                                commonModelList[absoluteAdapterPosition].fullname
                            )
                            i.putExtra("status", commonModelList[absoluteAdapterPosition].state)
                            i.putExtra("photo", commonModelList[absoluteAdapterPosition].photourl)
                            context.startActivity(i)
                        }
                        "group" -> {
                            val intent = Intent(context, GroupActivity::class.java)
                            intent.putExtra("id", commonModelList[absoluteAdapterPosition].id)
                            intent.putExtra(
                                "fullName",
                                commonModelList[absoluteAdapterPosition].fullname
                            )
                            intent.putExtra("uid", commonModelList[absoluteAdapterPosition].uid)
                            intent.putExtra(
                                "photo",
                                commonModelList[absoluteAdapterPosition].photourl
                            )
                            context.startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}