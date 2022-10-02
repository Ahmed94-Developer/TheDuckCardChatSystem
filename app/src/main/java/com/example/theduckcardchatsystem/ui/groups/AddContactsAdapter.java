package com.example.theduckcardchatsystem.ui.groups;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.repository.GroupChatRepository;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddContactsAdapter extends RecyclerView.Adapter<AddContactsAdapter.AddContactsViewHolder> {
    List<CommonModel> commonModelList = new ArrayList<>();
    @NonNull
    @Override
    public AddContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_contacts_item,parent,false);
        return new AddContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddContactsViewHolder holder, int position) {
        CommonModel commonModel = commonModelList.get(position);
        holder.itemName.setText(commonModel.getFullname());
        if (commonModel.getLastMessage() == null){
            holder.itemLastMessage.setVisibility(View.VISIBLE);
            holder.itemLastMessage.setText("");
        }else if (commonModel.getLastMessage().isEmpty() && !commonModel.getImageUrl().isEmpty()){
            holder.itemLastMessage.setVisibility(View.GONE);
            holder.mainListNoTificationImage.setVisibility(View.VISIBLE);

        }else if (!commonModel.getLastMessage().isEmpty()){

            try {
                holder.itemLastMessage.setText(AESDecryptionMethod(commonModel.getLastMessage()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            holder.mainListNoTificationImage.setVisibility(View.GONE);
        }
        if (commonModel.getPhotourl() == null){
            holder.itemPhoto.setImageResource(R.drawable.ic_avatar);
        }else {
            String imageDataBytes = commonModelList.get(position).getPhotourl()
                    .substring(commonModelList.get(position).getPhotourl().indexOf(",")+1);
            InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            holder.itemPhoto.setImageBitmap(bitmap);
        }



    }
    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte [] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptionString = string;

        byte [] decryption;
        try {
           CreateGroupActivity.decipher.init(Cipher.DECRYPT_MODE,CreateGroupActivity.secretKeySpec);
            decryption = CreateGroupActivity.decipher.doFinal(EncryptedByte);
            decryptionString = new String(decryption);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptionString;
    }

    @Override
    public int getItemCount() {
        return commonModelList.size();
    }
    public void updateListItems(CommonModel item){
        commonModelList.add(item);
        notifyItemInserted(commonModelList.size());
    }

    public class AddContactsViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,itemLastMessage;
        CircleImageView itemPhoto,itemChoice;
        private LinearLayout mainListNoTificationImage;
        public AddContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.add_contacts_item_name);
            itemLastMessage = itemView.findViewById(R.id.add_contacts_last_message);
            itemPhoto = itemView.findViewById(R.id.add_contacts_item_photo);
            itemChoice = itemView.findViewById(R.id.add_contacts_item_choice);
            mainListNoTificationImage = itemView.findViewById(R.id.imageView29);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (commonModelList.get(getAbsoluteAdapterPosition()).getChoice()){
                       itemChoice.setVisibility(View.INVISIBLE);
                       commonModelList.get(getAbsoluteAdapterPosition()).setChoice(false);
                        AddContactsFragment.contactsList.remove(commonModelList.get(getAbsoluteAdapterPosition()));

                    }else {
                        itemChoice.setVisibility(View.VISIBLE);
                        commonModelList.get(getAbsoluteAdapterPosition()).setChoice(true);
                        AddContactsFragment.contactsList.add(commonModelList.get(getAbsoluteAdapterPosition()));





                    }
                }
            });
        }
    }
}
