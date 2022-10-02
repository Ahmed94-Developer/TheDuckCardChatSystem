package com.example.theduckcardchatsystem.ui.home;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.HomeActivity;
import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.ui.model.User;
import com.example.theduckcardchatsystem.ui.settings.SettingsFragment;
import com.example.theduckcardchatsystem.viewmodel.ChatsViewModel;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    @BindView(R.id.profile_id)
     CircleImageView profileImage;
    @BindView(R.id.home_toolbar)
    Toolbar homeToolBar;
    @BindView(R.id.recycler_chats)
    RecyclerView recyclerChats;
    private ChatsViewModel chatsViewModel;



    private com.example.theduckcardchatsystem.viewmodel.SettingsViewModel settingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,root);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).setSupportActionBar(homeToolBar);
        ((AppCompatActivity)getActivity()).setTitle("");

        chatsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ChatsViewModel.class);

        settingsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(SettingsViewModel.class);
        settingsViewModel.setSettingsRepository();
        settingsViewModel.dataSnapshotLiveData().observe(getActivity(), new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    User user = dataSnapshot.getValue(User.class);
                    String imageDataBytes = "";
                    if (user.getPhotourl() == null){
                        imageDataBytes = "empty";
                    }else {
                       imageDataBytes = user.getPhotourl().substring(user.getPhotourl().indexOf(",")+1);
                        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);

                        profileImage.setImageBitmap(bitmap);

                    }


                }else {
                    profileImage.setImageResource(R.drawable.ic_profile);
                }
            }
        });


        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        chatsViewModel.initRecyclerViewChats(getActivity(),recyclerChats);
    }
}
