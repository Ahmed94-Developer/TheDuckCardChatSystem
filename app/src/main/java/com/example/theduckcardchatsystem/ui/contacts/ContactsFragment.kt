package com.example.theduckcardchatsystem.ui.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.repository.ContactsRepository;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.viewmodel.ContactsViewModel;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsFragment extends Fragment {
    @BindView(R.id.rv_contacts)
    RecyclerView contactsRecyclerView;
    @BindView(R.id.profile_id_contacts)
    CircleImageView circleProfile;
    private com.example.theduckcardchatsystem.viewmodel.ContactsViewModel contactsViewModel;
    private FirebaseRecyclerAdapter<CommonModel, ContactsRepository.ContactsHolder> mAdapter;
    private HashMap<DatabaseReference,ValueEventListener> mapListener = new
            HashMap<DatabaseReference,ValueEventListener>();
    private SettingsViewModel settingsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this,root);
        contactsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(ContactsViewModel.class);
        settingsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(SettingsViewModel.class);
        settingsViewModel.setSettingsRepository();
        settingsViewModel.dataSnapshotLiveData().observe(getActivity(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    String photo = dataSnapshot.child("photourl").getValue(String.class);
                    if (photo == null){
                        circleProfile.setImageResource(R.drawable.ic_profile);
                    }else {
                        String imageDataBytes = photo.substring(photo.indexOf(",")+1);
                        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        circleProfile.setImageBitmap(bitmap);
                    }
                }
            }
        });

        contactsViewModel.initRecyclerView(mAdapter,contactsRecyclerView,getActivity());
        return root;
    }
    @Override
    public void onPause() {
        super.onPause();
     //   mAdapter.stopListening();
        for (Map.Entry<DatabaseReference, ValueEventListener> it : mapListener.entrySet()) {
            it.getKey().removeEventListener(it.getValue());
        }

    }

}
