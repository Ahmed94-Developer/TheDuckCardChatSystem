package com.example.theduckcardchatsystem.ui.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theduckcardchatsystem.HomeActivity;
import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.ui.model.User;
import com.example.theduckcardchatsystem.viewmodel.GroupChatViewModel;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.style.TtsSpan.TYPE_TEXT;
import static com.example.theduckcardchatsystem.repository.GroupChatRepository.cipher;
import static com.example.theduckcardchatsystem.repository.GroupChatRepository.secretKeySpec;

public class GroupActivity extends AppCompatActivity {
    private GroupChatViewModel groupChatViewModel;
    private Bundle bundle;
    private String id,fullName,uid,photo;
    private String TYPE_CHAT = "chat";
    private String TYPE_TEXT = "text";
    @BindView(R.id.chat_input_message)
    EditText inputMessageTxt;
    private SettingsViewModel settingsViewModel;
    private String imageSender,UserName;
    @BindView(R.id.chat_recycle_view)
    RecyclerView recyclerView;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView fullNameTxt = toolbar.findViewById(R.id.contact_fullname);
        CircleImageView imageView = toolbar.findViewById(R.id.toolbar_image);
        toolbar.inflateMenu(R.menu.single_chat_action_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        groupChatViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication())).get(GroupChatViewModel.class);

        bundle = getIntent().getExtras();
        if (bundle != null){
            id = bundle.getString("id");
            fullName = bundle.getString("fullName");
            uid = bundle.getString("uid");
            photo = bundle.getString("photo");
            fullNameTxt.setText(fullName);
            String imageDataBytes = "";
            SharedPreferences.Editor editor = getSharedPreferences("ids",MODE_PRIVATE).edit();
            editor.putString("id",id);
            editor.apply();

            if (photo.equals("empty")){
              //  imageDataBytes = "empty";
                imageView.setImageResource(R.drawable.ic_grop);

            }else {
                fullNameTxt.setText(fullName);

                imageDataBytes = photo.substring(photo.indexOf(",")+1);
                InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                imageView.setImageBitmap(bitmap);
            }

        }
        groupChatViewModel.initRecyclerView(id,recyclerView,this);

    }
    @OnClick(R.id.chat_btn_send_message)
        void ChatSendMessage() {
        String message = inputMessageTxt.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(GroupActivity.this, "Enter your Message", Toast.LENGTH_SHORT).show();

        } else {
            groupChatViewModel.sendMessage(AESEncryptionMethod(message), id, TYPE_TEXT);
        }
        groupChatViewModel.saveToMainList(id,"group");
        inputMessageTxt.setText("");

    }
    @OnClick(R.id.chat_btn_attach)
    void attachImageChatBtn(){
        attachImage();
    }


    public String AESEncryptionMethod(String string){
        byte [] stringByte = string.getBytes();
        byte [] encrypteByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            encrypteByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String returnString = null;
        try {
            returnString = new String(encrypteByte,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnString;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_chat_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_clear_chat:
                groupChatViewModel.ClearChat(id);
               // finish();
                break;
            case R.id.menu_delete_chat:
               groupChatViewModel.DeleteChat(id);
               // finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path1 = resultUri.getPath();

                Bitmap bitmap = BitmapFactory.decodeFile(path1);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // In case you want to compress your image, here it's at 40%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT);

                //   String messageKey = reference.child(NODE_MESSAGES).child(CURRENT_UID)
                //         .child(commonModel.getUid()).push().getKey().toString();

                groupChatViewModel.sendImageAsMessage(photoString,id);



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void attachImage(){
        CropImage.activity()
                .setAspectRatio(1,1)
                .setRequestedSize(600,600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
