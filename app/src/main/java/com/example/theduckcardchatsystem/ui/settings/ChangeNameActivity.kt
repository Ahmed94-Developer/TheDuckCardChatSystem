package com.example.theduckcardchatsystem.ui.settings

import android.graphics.Color
import com.example.theduckcardchatsystem.ui.model.User.fullname
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.example.theduckcardchatsystem.R
import android.widget.EditText
import android.os.Bundle
import butterknife.ButterKnife
import androidx.lifecycle.ViewModelProvider
import android.graphics.drawable.ColorDrawable
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.theduckcardchatsystem.ui.model.User
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class ChangeNameActivity : AppCompatActivity() {
    private var settingsViewModel: SettingsViewModel? = null

    @JvmField
    @BindView(R.id.settings_input_name)
    var settingsInputName: EditText? = null

    @JvmField
    @BindView(R.id.settings_input_sur_name)
    var settingsInputSurName: EditText? = null
    private val valueEventListener: ValueEventListener? = null
    private var recyclerviewRef: DatabaseReference? = null
    private var mRefMessage: DatabaseReference? = null
    private var CURRENT_UID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_name)
        ButterKnife.bind(this)
        settingsViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(
            SettingsViewModel::class.java
        )
        initUser()
        val colorDrawable = ColorDrawable(Color.parseColor("#133E3E"))
        CURRENT_UID = FirebaseAuth.getInstance().currentUser!!.uid
        recyclerviewRef =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        mRefMessage = recyclerviewRef!!.child("groups")
        supportActionBar!!.setBackgroundDrawable(colorDrawable)
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_confirm_changes -> {
                val name = settingsInputName!!.text.toString()
                val surName = settingsInputSurName!!.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Insert Your Name", Toast.LENGTH_SHORT).show()
                } else {
                    val fullName = "$name $surName"
                    settingsViewModel!!.changeName(fullName)
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
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu_confirm, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun initUser() {
        settingsViewModel!!.initUser()
        settingsViewModel!!.initUserLiveData.observe(this) { dataSnapshot ->
            if (dataSnapshot != null) {
                var user: User? = User()
                user = dataSnapshot.getValue(
                    User::class.java
                )
                val fullNameList = user!!.fullname.split(" ".toRegex()).toTypedArray()
                try {
                    settingsInputName!!.setText(fullNameList[0])
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                try {
                    settingsInputSurName!!.setText(fullNameList[1])
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}