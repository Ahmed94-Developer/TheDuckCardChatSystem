package com.example.theduckcardchatsystem.ui.model

import android.content.Context
import com.example.theduckcardchatsystem.ui.model.States
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

enum class States(val state: String) {
    ONLINE("online"), OFFLINE("offline"), TYPING("typing");

    private val context: Context? = null

    companion object {
        @JvmStatic
        fun UpdateStates(states: States) {
            try {
                val uid = FirebaseAuth.getInstance().currentUser!!
                    .uid
                val mAuth = FirebaseAuth.getInstance()
                val reference =
                    FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .reference
                if (mAuth.currentUser != null) {
                    reference.child("users").child(uid).child("state").setValue(states.state)
                        .addOnSuccessListener {
                            val user = User()
                            user.state = states.state
                        }.addOnFailureListener {
                            // Toast.makeText(, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}