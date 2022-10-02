package com.example.theduckcardchatsystem.ui.groups;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.ui.model.MembersId;
import com.example.theduckcardchatsystem.ui.room.viewmodel.MembersViewModel;
import com.example.theduckcardchatsystem.viewmodel.AddContactsViewModel;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactsFragment extends Fragment {
    private AddContactsViewModel addContactsViewModel;
    @BindView(R.id.add_contacts_recycle_view)
    RecyclerView addContactsRecycler;
    @BindView(R.id.profile_photo_create_group)
    CircleImageView profile_photo_image;

    private List<CommonModel> commonModelList = new ArrayList<>();
    public static List<CommonModel> contactsList = new ArrayList<>();
    private MembersViewModel membersViewModel;
    private SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_contacts, container, false);
        ButterKnife.bind(this,root);
        addContactsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AddContactsViewModel.class);
        addContactsViewModel.initRecyclerView(addContactsRecycler,getActivity());
        membersViewModel = new ViewModelProvider(getActivity()
                ,ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(MembersViewModel.class);
        contactsList.clear();
        settingsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(SettingsViewModel.class);
        settingsViewModel.setSettingsRepository();
        settingsViewModel.dataSnapshotLiveData().observe(getActivity(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    String photo = dataSnapshot.child("photourl").getValue(String.class);
                    if (photo == null){
                        profile_photo_image.setImageResource(R.drawable.ic_profile);
                    }else {
                        String imageDataBytes = photo.substring(photo.indexOf(",")+1);
                        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        profile_photo_image.setImageBitmap(bitmap);
                    }
                }
            }
        });

        return root;
    }
    @OnClick(R.id.add_contacts_btn_next)
    void btnNext(){
        if(contactsList.isEmpty()){
            Toast.makeText(getActivity(), "Add a participant", Toast.LENGTH_SHORT).show();
        }else {
            for (CommonModel it : contactsList){

                MembersId membersId = new MembersId(it.getUid());
                membersViewModel.insert(membersId);
                MembersId membersId1 = new MembersId(it.getUid());
                membersId.setId(membersId1.getId());
                membersViewModel.Update(membersId1);
            }

            Intent i  = new Intent(getActivity(),CreateGroupActivity.class);
            i.putExtra("size",contactsList.size());
            startActivity(i);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        membersViewModel.DeleteAll();
    }

}
