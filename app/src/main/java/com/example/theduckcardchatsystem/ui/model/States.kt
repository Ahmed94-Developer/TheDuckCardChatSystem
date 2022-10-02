package com.example.theduckcardchatsystem.ui.model;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public enum States {
    ONLINE("online"),
    @SuppressLint("StaticFieldLeak") OFFLINE("offline"),
    TYPING("typing");
    private String state;
    private Context context;


    States(String state) {
        this.state = state;
    }
    public final String getState() {
        return this.state;
    }
    public static void UpdateStates(States states){
      try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
          DatabaseReference reference = FirebaseDatabase.getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                  .getReference();
            if (mAuth.getCurrentUser() != null){
                reference.child("users").child(uid).child("state").setValue(states.getState())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                User user = new User();
                                user.setState(states.getState());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Toast.makeText(, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }catch (Exception e){
            e.printStackTrace();
        }



    }

}
