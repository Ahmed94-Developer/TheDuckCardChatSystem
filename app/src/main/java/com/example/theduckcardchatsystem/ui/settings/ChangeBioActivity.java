package com.example.theduckcardchatsystem.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.model.User;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.google.firebase.database.DataSnapshot;

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

public class ChangeBioActivity extends AppCompatActivity {
    @BindView(R.id.settings_input_bio)
    EditText bioTxt;
    private SettingsViewModel settingsViewModel;
    private Cipher cipher,decipher;
    private SecretKeySpec secretKeySpec;
    private byte encryptionKey [] = {9,117,51,87,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bio);
        ButterKnife.bind(this);
        settingsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication())).get(SettingsViewModel.class);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#133E3E"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey,"AES");

        settingsViewModel.initUser();
        settingsViewModel.getInitUserLiveData().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    User user = dataSnapshot.getValue(User.class);
                    try {
                        bioTxt.setText(AESDecryptionMethod(user.getBio()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_confirm_changes:
                String bio = bioTxt.getText().toString();
                settingsViewModel.changeBio(AESEncryptionMethod(bio));
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_confirm,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private String AESEncryptionMethod(String string){

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
