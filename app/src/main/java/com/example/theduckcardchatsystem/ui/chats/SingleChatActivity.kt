package com.example.theduckcardchatsystem.ui.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.adapters.SingleChatAdapter;
import com.example.theduckcardchatsystem.repository.SingleChatRepository;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.example.theduckcardchatsystem.viewmodel.SingleChatViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.theduckcardchatsystem.repository.SingleChatRepository.cipher;

public class SingleChatActivity extends AppCompatActivity {

    private String fullName,uid,status,photo;
    @BindView(R.id.chat_input_message)
    EditText chatInputMessage;
    @BindView(R.id.chat_recycle_view)
    RecyclerView chatRecyclerView;
    @BindView(R.id.chat_btn_send_message)
    FloatingActionButton chatBtnSend;
    private String TYPE_TEXT = "text";
    private SingleChatViewModel singleChatViewModel;
    private String TYPE_CHAT = "chat";
    private SingleChatAdapter adapter;
    private DatabaseReference reference;
    private String NODE_MESSAGES = "Messages";
    private String CURRENT_UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView fullNameTxt = toolbar.findViewById(R.id.contact_fullname);
        TextView statusTxt = toolbar.findViewById(R.id.contact_status);
        CircleImageView imageView = toolbar.findViewById(R.id.toolbar_image);
        toolbar.inflateMenu(R.menu.single_chat_action_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reference = FirebaseDatabase.
                getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        CURRENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        singleChatViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication())).get(SingleChatViewModel.class);
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null){
            fullName = bundle.getString("fullName");
            uid = bundle.getString("uid");
            status = bundle.getString("status");
            photo = bundle.getString("photo");
            fullNameTxt.setText(fullName);
            statusTxt.setText(status);
            String imageDataBytes = "";
            if (photo == null || fullName.isEmpty()){
                imageDataBytes = "empty";
                fullNameTxt.setText("User Name");

            }else {
                imageDataBytes = photo.substring(photo.indexOf(",")+1);
                InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                imageView.setImageBitmap(bitmap);
            }


        }
        singleChatViewModel.initRecyclerView(uid,adapter,chatRecyclerView);
        chatInputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // chatBtnSend.setImageResource(R.drawable.ic_sendicon);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String message = chatInputMessage.getText().toString();
                if (TextUtils.isEmpty(message)){
                    chatBtnSend.setImageResource(R.drawable.ic_sendhidden);
                }else {
                    chatBtnSend.setImageResource(R.drawable.ic_sendicon);
                }

            }
        });
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
              singleChatViewModel.ClearChat(uid);
                finish();
                break;
            case R.id.menu_delete_chat:
              singleChatViewModel.DeleteChat(uid);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    private void attachImage(){
        CropImage.activity()
                .setAspectRatio(1,1)
                .setRequestedSize(1000,1000)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(SingleChatActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

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

                String messageKey = reference.child(NODE_MESSAGES).child(CURRENT_UID)
                        .child(uid).push().getKey().toString();

                singleChatViewModel.sendMessageAsIimage(uid,photoString,messageKey);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @OnClick(R.id.chat_btn_attach)
    void attachImageChatBtn(){
        attachImage();
    }
    @OnClick(R.id.chat_btn_send_message)
    void chatBtnSendMessage(){
        String message = chatInputMessage.getText().toString();
        if (message.isEmpty()){
            Toast.makeText(SingleChatActivity.this, "Enter your Message", Toast.LENGTH_SHORT).show();

        }else {
            singleChatViewModel.sendMessage(AESEncryptionMethod(message),uid,TYPE_TEXT);
        }
         singleChatViewModel.saveToMainList(uid,TYPE_CHAT);
        chatInputMessage.setText("");
    }
    public String AESEncryptionMethod(String string){
        byte [] stringByte = string.getBytes();
        byte [] encrypteByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, SingleChatRepository.secretKeySpec);
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
}
