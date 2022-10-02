package com.example.theduckcardchatsystem

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.theduckcardchatsystem.R
import butterknife.ButterKnife
import android.graphics.drawable.ColorDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.theduckcardchatsystem.ui.model.States
import android.os.Build
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.theduckcardchatsystem.ui.model.CommonModel
import android.provider.ContactsContract
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import android.widget.Toast
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class HomeActivity : AppCompatActivity() {
    private val settingsViewModelSnapShot: SettingsViewModel? = null
    private var mAuth: FirebaseAuth? = null
    private var uid: String? = null
    private val NODE_PHONE_CONTACTS = "phones_contacts"
    private var reference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)
        mAuth = FirebaseAuth.getInstance()
        uid = mAuth!!.currentUser!!.uid

//        getSupportActionBar().setTitle("");
        //       getSupportActionBar().setDisplayShowTitleEnabled(false);
        val colorDrawable = ColorDrawable(Color.parseColor("#133E3E"))
        //        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_contacts,
            R.id.navigation_group,
            R.id.navigation_settings
        )
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController)
        reference =
            FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference
        Thread { initContacts() }.start()
    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        uid = mAuth!!.currentUser!!.uid
        States.UpdateStates(States.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        States.UpdateStates(States.OFFLINE)
    }

    private fun checkPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@HomeActivity, arrayOf(permission), 200
                    )
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(
                this@HomeActivity, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }

    private fun initContacts() {
        if (checkPermissions(this@HomeActivity, Manifest.permission.READ_CONTACTS)) {
//           Toast.makeText(ChatActivity.this, "Contacts Opened", Toast.LENGTH_SHORT).show();
            val arrayContacts = ArrayList<CommonModel>()
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (cursor!!.moveToNext()) {
                @SuppressLint("Range") val fullName =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                @SuppressLint("Range") val phone =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val commonModel = CommonModel()
                commonModel.fullname = fullName
                commonModel.phone = phone
                arrayContacts.add(commonModel)
            }
            cursor.close()
            updatePhonesToDataBase(arrayContacts)
        }
    }

    private fun updatePhonesToDataBase(arraycontacts: ArrayList<CommonModel>) {
        if (mAuth!!.currentUser != null) {
            reference!!.child("phones").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(it: DataSnapshot) {
                    for (snapshot in it.children) {
                        for (contact in arraycontacts) {
                            Log.d("key", snapshot.key!!)
                            Log.d("okey", contact.phone)
                            if (snapshot.key!!.contains(contact.phone)) {
                                reference!!.child(NODE_PHONE_CONTACTS).child(uid!!)
                                    .child(snapshot.value.toString())
                                    .child("id")
                                    .setValue(snapshot.value.toString())
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@HomeActivity,
                                            "" + e.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                reference!!.child(NODE_PHONE_CONTACTS).child(uid!!)
                                    .child(snapshot.value.toString())
                                    .child("fullname")
                                    .setValue(contact.fullname)
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@HomeActivity,
                                            "" + e.message.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}