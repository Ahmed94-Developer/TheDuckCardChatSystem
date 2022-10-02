package com.example.theduckcardchatsystem.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theduckcardchatsystem.HomeActivity;
import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.home.HomeFragment;
import com.example.theduckcardchatsystem.ui.login.LoginActivity;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.ui.model.States;
import com.example.theduckcardchatsystem.ui.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    private SettingsViewModel settingsViewModel;
    private com.example.theduckcardchatsystem.viewmodel.SettingsViewModel settingsViewModelSnapShot;
    @BindView(R.id.settings_phone_number)
    TextView settingsPhoneNumber;
    @BindView(R.id.settings_user_login)
    TextView settingsUserLogin;
    @BindView(R.id.settings_user_name)
    TextView settingsUserName;
    @BindView(R.id.user_bio_label)
    TextView userBioLabel;
    @BindView(R.id.settings_user_photo)
    CircleImageView imageView;
    private FirebaseAuth mAuth;
    private Cipher cipher,decipher;
    private SecretKeySpec secretKeySpec;
    private byte encryptionKey [] = {9,117,51,87,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    @BindView(R.id.settings_toolbar)
    Toolbar settingsToolBar;
    private DatabaseReference recyclerviewRef;
    private DatabaseReference mRefMessage;
    private String CURRENT_UID;
    private ValueEventListener valueEventListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this,root);
        ((AppCompatActivity)getActivity()).setSupportActionBar(settingsToolBar);
        ((AppCompatActivity)getActivity()).setTitle("");
        mAuth = FirebaseAuth.getInstance();
       CURRENT_UID = mAuth.getCurrentUser().getUid().toString();

        settingsViewModelSnapShot = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(com.example.theduckcardchatsystem.viewmodel.SettingsViewModel.class);
        recyclerviewRef = FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        mRefMessage = recyclerviewRef.child("groups");


        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey,"AES");


        return root;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.settings_action_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        getUserData();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_menu_exit:
               States.UpdateStates(States.OFFLINE);
               mAuth.signOut();
               startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.settings_menu_change_name:
               startActivity(new Intent(getActivity(),ChangeNameActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void changePhoto(){
        HomeActivity activity = (HomeActivity) getActivity();
        CropImage.activity()
                .setAspectRatio(1,1)
                .setRequestedSize(600,600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(activity,this);

    }
    @OnClick(R.id.settings_change_photo)
    public void ChangeImage(){
        changePhoto();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path = resultUri.getPath();
                Log.d("path",path);

                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // In case you want to compress your image, here it's at 40%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT);
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
                mRefMessage.addValueEventListener(valueEventListener);*/

                settingsViewModelSnapShot.changePhotoViewModel(photoString);
                settingsViewModelSnapShot.photoSnapShotLiveData().observe(this, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null){
                            User user = dataSnapshot.getValue(User.class);
                            String imageDataBytes = user.getPhotourl().substring(user.getPhotourl().indexOf(",")+1);
                            InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            imageView.setImageBitmap(bitmap);
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("photo",photoString);
                            editor.apply();
                        }
                    }
                });

            }
        }
    }
    public void getUserData(){
        settingsViewModelSnapShot.setSettingsRepository();
        settingsViewModelSnapShot.dataSnapshotLiveData().observe(getActivity(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                User user = new User();
                user = dataSnapshot.getValue(User.class);
                settingsPhoneNumber.setText(user.getPhone());
                settingsUserName.setText(user.getFullname());
                settingsUserLogin.setText(user.getUsername());
                try {
                    userBioLabel.setText(AESDecryptionMethod(user.getBio()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (user.getPhotourl() == null){
                    imageView.setImageResource(R.drawable.ic_avatar);
                }else {
                    String imageDataBytes = user.getPhotourl().substring(user.getPhotourl().indexOf(",")+1);
                    InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    imageView.setImageBitmap(bitmap);


                }
            }
        });
    }
    @OnClick(R.id.btn_settings_change_user_name)
    void bthChangeUserName(){
        startActivity(new Intent(getActivity(),ChangeUserNameActivity.class));
    }
    @OnClick(R.id.btn_settings_change_bio)
    void btnChangeBio(){
        startActivity(new Intent(getActivity(),ChangeBioActivity.class));
    }
    private String AESDecryptionMethod(String string) throws UnsupportedEncodingException {
        byte [] EncryptedByte = string.getBytes("ISO-8859-1");
        String decryptionString = string;

        byte [] decryption;
        try {
            decipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            decryption = decipher.doFinal(EncryptedByte);
            decryptionString = new String(decryption);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptionString;
    }

}
