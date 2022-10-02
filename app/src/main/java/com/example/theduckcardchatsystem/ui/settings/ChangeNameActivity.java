package com.example.theduckcardchatsystem.ui.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.theduckcardchatsystem.HomeActivity;
import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.ui.model.User;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class ChangeNameActivity extends AppCompatActivity {
    private SettingsViewModel settingsViewModel;
    @BindView(R.id.settings_input_name)
    EditText settingsInputName;
    @BindView(R.id.settings_input_sur_name)
    EditText settingsInputSurName;
    private ValueEventListener valueEventListener;
    private DatabaseReference recyclerviewRef;
    private DatabaseReference mRefMessage;
    private String CURRENT_UID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        ButterKnife.bind(this);
        settingsViewModel = new ViewModelProvider(this, ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication())).get(SettingsViewModel.class);
        initUser();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#133E3E"));
        CURRENT_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerviewRef = FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        mRefMessage = recyclerviewRef.child("groups");
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_confirm_changes:
                String name = settingsInputName.getText().toString();
                String surName = settingsInputSurName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(this, "Insert Your Name", Toast.LENGTH_SHORT).show();
                } else {
                    String fullName = name + " " + surName;
                    settingsViewModel.changeName(fullName);
                    /*valueEventListener = new ValueEventListener() {
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
                                        booksSnapshot.child("fullname").getRef().setValue(fullName);
                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    mRefMessage.addValueEventListener(valueEventListener);*/
                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initUser() {
        settingsViewModel.initUser();
        settingsViewModel.getInitUserLiveData().observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = new User();
                    user = dataSnapshot.getValue(User.class);

                    String[] fullNameList = user.getFullname().split(" ");

                    try {
                        settingsInputName.setText(fullNameList[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        settingsInputSurName.setText(fullNameList[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
