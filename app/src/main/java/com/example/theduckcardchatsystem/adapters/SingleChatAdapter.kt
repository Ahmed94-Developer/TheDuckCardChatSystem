package com.example.theduckcardchatsystem.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.theduckcardchatsystem.ui.model.CommonModel
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.theduckcardchatsystem.R
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
import com.example.theduckcardchatsystem.adapters.MainListAdapter.MainListViewHolder
import com.example.theduckcardchatsystem.repository.ChatsRepository
import android.content.Intent
import android.graphics.*
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.theduckcardchatsystem.ui.chats.SingleChatActivity
import com.example.theduckcardchatsystem.ui.chats.GroupActivity
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter.SingleChatViewHolder
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter
import com.example.theduckcardchatsystem.repository.SingleChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec

class SingleChatAdapter(var context: Context, commonModelList: List<CommonModel>) :
    RecyclerView.Adapter<SingleChatViewHolder>() {
    var commonModelList: List<CommonModel> = ArrayList()
    private val cipher: Cipher? = null
    private val decipher: Cipher? = null
    private val secretKeySpec: SecretKeySpec? = null
    private var diffResult: DiffResult? = null
    private val CURRENT_UID = FirebaseAuth.getInstance().currentUser!!
        .uid
    private val reference: DatabaseReference? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SingleChatViewHolder, position: Int) {
        val commonModel = commonModelList[position]
        Log.d("uid", CURRENT_UID)
        when (commonModel.type) {
            "text" -> DrawMessageText(holder, position)
            "image" -> DrawMessageImage(holder, position)
        }
    }

    private fun DrawMessageImage(holder: SingleChatViewHolder, position: Int) {
        holder.blocUserMessage.visibility = View.GONE
        holder.blocReceiveMessage.visibility = View.GONE
        holder.receiverImage.visibility = View.GONE
        val reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        val imageDataBytes = commonModelList[position].imageUrl.substring(
            commonModelList[position].imageUrl.indexOf(",") + 1
        )
        val stream: InputStream =
            ByteArrayInputStream(Base64.decode(imageDataBytes.toByteArray(), Base64.DEFAULT))
        val bitmap = BitmapFactory.decodeStream(stream)
        if (commonModelList[position].from == CURRENT_UID) {
//        holder.chat_received_image_txt.setVisibility(View.GONE);
            holder.message_receiver_image_view.visibility = View.GONE
            holder.message_image_profile_image.visibility = View.GONE

//            holder.received_image_message_time.setVisibility(View.GONE);
            holder.message_sender_image_view.visibility = View.VISIBLE
            holder.message_sender_image_view.setImageBitmap(getRoundedCornerBitmap(bitmap))

            //   holder.user_image_time_txt.setText(asTime(commonModelList.get(position).getTimeStamp().toString()));
            ///  holder.user_image_time_txt.setVisibility(View.VISIBLE);
        } else {
            holder.message_receiver_image_view.visibility = View.VISIBLE
            holder.receiverImage.visibility = View.VISIBLE
            holder.message_sender_image_view.visibility = View.GONE
            holder.receiverImage.visibility = View.GONE
            // holder.user_image_time_txt.setVisibility(View.GONE);
            holder.message_receiver_image_view.setImageBitmap(getRoundedCornerBitmap(bitmap))
            holder.message_image_profile_image.visibility = View.VISIBLE
            reference.child("users").child(commonModelList[position].from)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val photo = snapshot.child("photourl").getValue(
                            String::class.java
                        )
                        if (photo == null) {
                            holder.message_image_profile_image.setImageResource(R.drawable.ic_avatar)
                        } else {
                            holder.message_image_profile_image.visibility = View.VISIBLE
                            val imageDataBytes = photo.substring(photo.indexOf(",") + 1)
                            val stream: InputStream = ByteArrayInputStream(
                                Base64.decode(
                                    imageDataBytes.toByteArray(),
                                    Base64.DEFAULT
                                )
                            )
                            val bitmap = BitmapFactory.decodeStream(stream)
                            holder.message_image_profile_image.setImageBitmap(bitmap)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


            //  holder.chat_received_image_txt.setVisibility(View.VISIBLE);
            // holder.chat_received_image_txt.setText(commonModelList.get(position).getFullname());
//            holder.received_image_message_time.setText(commonModelList.get(position).getTimeStamp().toString());
        }
    }

    private fun DrawMessageText(holder: SingleChatViewHolder, position: Int) {
        holder.message_receiver_image_view.visibility = View.GONE
        holder.message_sender_image_view.visibility = View.GONE
        holder.blocUserMessage.visibility = View.GONE
        holder.message_image_profile_image.visibility = View.GONE
        val reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        //   holder.chat_received_image_txt.setVisibility(View.GONE);
        //holder.user_image_time_txt.setVisibility(View.GONE);
//        holder.received_image_message_time.setVisibility(View.GONE);
        holder.blocReceiveMessage.visibility = View.GONE
        if (commonModelList[position].from == CURRENT_UID) {
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceiveMessage.visibility = View.GONE
            holder.receiverImage.visibility = View.GONE
            try {
                holder.chatUserTxt.text =
                    AESDecryptionMethod(commonModelList[position].text.toString())
                holder.chatUserTxt.movementMethod = LinkMovementMethod.getInstance()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            holder.timeUserTxt.text = asTime(commonModelList[position].timeStamp.toString())
        } else {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceiveMessage.visibility = View.VISIBLE
            reference.child("users").child(commonModelList[position].from)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val photo = snapshot.child("photourl").getValue(
                            String::class.java
                        )
                        if (photo == null) {
                            holder.receiverImage.setImageResource(R.drawable.ic_avatar)
                        } else {
                            holder.receiverImage.visibility = View.VISIBLE
                            val imageDataBytes = photo.substring(photo.indexOf(",") + 1)
                            val stream: InputStream = ByteArrayInputStream(
                                Base64.decode(
                                    imageDataBytes.toByteArray(),
                                    Base64.DEFAULT
                                )
                            )
                            val bitmap = BitmapFactory.decodeStream(stream)
                            holder.receiverImage.setImageBitmap(bitmap)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            try {
                holder.chatReceivedTxt.text =
                    AESDecryptionMethod(commonModelList[position].text.toString())
                holder.chatReceivedTxt.movementMethod = LinkMovementMethod.getInstance()
                holder.chatReceivedTxt.setLinkTextColor(Color.BLACK)
                holder.chatReceivedTxt.setTextColor(Color.BLACK)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            //        holder.fullNameTxt.setVisibility(View.GONE);
            holder.chatReceivedTime.text = asTime(commonModelList[position].timeStamp.toString())
        }
    }

    fun addItem(item: CommonModel) {
        val newList: MutableList<CommonModel> = ArrayList()
        newList.addAll(commonModelList)
        newList.add(item)
        diffResult = DiffUtil.calculateDiff(DiffUtilCalback(commonModelList, newList))
        diffResult!!.dispatchUpdatesTo(this)
        commonModelList = newList
    }

    private fun asTime(string: String): String {
        val time1 = string.toLong()
        val time = Date(time1)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(time)
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
            SingleChatRepository.decipher!!.init(
                Cipher.DECRYPT_MODE,
                SingleChatRepository.secretKeySpec
            )
            decryption = SingleChatRepository.decipher!!.doFinal(EncryptedByte)
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

    inner class SingleChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var chatUserTxt: TextView
        var chatReceivedTxt: TextView
        var timeUserTxt: TextView
        var chatReceivedTime: TextView
        var blocUserMessage: LinearLayout
        var blocReceiveMessage: LinearLayout
        var receiverImage: CircleImageView
        var message_image_profile_image: CircleImageView
        var message_sender_image_view: ImageView
        var message_receiver_image_view: ImageView

        init {
            chatUserTxt = itemView.findViewById(R.id.sender_messsage_text)
            timeUserTxt = itemView.findViewById(R.id.sender_message_time)
            chatReceivedTxt = itemView.findViewById(R.id.receiver_message_text)
            chatReceivedTime = itemView.findViewById(R.id.receiver_time)
            blocUserMessage = itemView.findViewById(R.id.sender_messsage_block)
            blocReceiveMessage = itemView.findViewById(R.id.receiver_message_text_block)
            receiverImage = itemView.findViewById(R.id.message_profile_image)
            message_sender_image_view = itemView.findViewById(R.id.message_sender_image_view)
            message_receiver_image_view = itemView.findViewById(R.id.message_receiver_image_view)
            message_image_profile_image = itemView.findViewById(R.id.message_image_profile_image)
            //  user_image_time_txt = itemView.findViewById(R.id.chat_user_image_message_time);
            //   received_image_message_time = itemView.findViewById(R.id.chat_received_image_message_time);
            /*   blockUserImage = itemView.findViewById(R.id.bloc_user_image_message);
        blockReceiveImage = itemView.findViewById(R.id.bloc_received_image_message);
        fullNameTxt = itemView.findViewById(R.id.chat_received_message_name);
        chat_received_image_txt = itemView.findViewById(R.id.textView75);*/
        }
    }

    companion object {
        fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = 14f
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }

    init {
        this.commonModelList = commonModelList
    }
}