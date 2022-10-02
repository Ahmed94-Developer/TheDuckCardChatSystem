package com.example.theduckcardchatsystem.ui.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theduckcardchatsystem.R;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.example.theduckcardchatsystem.ui.model.MembersId;
import com.example.theduckcardchatsystem.ui.room.viewmodel.MembersViewModel;
import com.example.theduckcardchatsystem.viewmodel.SettingsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.theduckcardchatsystem.ui.groups.AddContactsFragment.contactsList;

public class CreateGroupActivity extends AppCompatActivity {

    private Map<String, Object> namesMap;
    private MembersViewModel membersViewModel;
    @BindView(R.id.create_group_counts)
    TextView groupCountTxt;
    @BindView(R.id.create_group_recycle_view)
    RecyclerView createGroupRecycler;
    public byte encryptionKey [] = {9,117,51,87,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    public static Cipher cipher,decipher;
    public static SecretKeySpec secretKeySpec;
    Bundle bundle;
    int count;
    private HashMap<String,Object> mapData;
    private  Map<String,Object> map;
    private String CHILD_ID = "id";
    private String CHILD_FUllNAME = "fullName";
    private String USER_CREATOR = "creator";
    private String uid,keyGroup,nameGroup;
    private DatabaseReference path;
    private String NODE_GROUPS = "groups";
    private String CHILD_TYPE = "type";
    private String NODE_MAIN_LIST = "main_list";
    private DatabaseReference mainListRefrence;
    private Uri mUri;
    public DatabaseReference reference;
    @BindView(R.id.create_group_btn_complete)
    FloatingActionButton createGroupBtn;
    @BindView(R.id.create_group_photo)
    CircleImageView createGroupPhoto;
    @BindView(R.id.create_group_input_name)
    EditText createGroupInputName;
    String photoString;
    private SettingsViewModel settingsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        settingsViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication())).get(SettingsViewModel.class);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        Toolbar toolbar = findViewById(R.id.toolbar_create_group);
        setSupportActionBar(toolbar);
        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey,"AES");
        reference = FirebaseDatabase
                .getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        mainListRefrence = FirebaseDatabase
                .getInstance("https://the-duck-card-chat-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
        keyGroup = reference.child(NODE_GROUPS).push().getKey().toString();
        Log.d("key",keyGroup);
        map = new HashMap<String,Object>();
        path = reference.child(NODE_GROUPS).child(keyGroup);
        bundle = getIntent().getExtras();
        membersViewModel = new ViewModelProvider(this,ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication())).get(MembersViewModel.class);
        if (bundle != null){
            count = bundle.getInt("size");
        }
        getResources().getQuantityString(R.plurals.count_members,count);
        groupCountTxt.setText(getResources()
                .getQuantityString(R.plurals.count_members,count
                        ,count));
        membersViewModel.getMembersIdLiveData().observe(this, new Observer<List<MembersId>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<MembersId> membersIds) {
                namesMap =  membersIds.stream().distinct()
                        .collect(Collectors.toMap(s->s.getMemberId().toString(), s->"member"));
            }
        });
        initRecyclerView();
        mapData = new HashMap<String,Object>();
    }
    private void initRecyclerView(){
        AddContactsAdapter adapter = new AddContactsAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        createGroupRecycler.setAdapter(adapter);
        createGroupRecycler.setLayoutManager(manager);
        for (CommonModel it : contactsList){
            adapter.updateListItems(it);
        }
    }
    private void createGroupToDataBase(String nameGroup, Uri mUri, List<CommonModel> contactsList) {
        mapData = new HashMap<String,Object>();
        mapData.put(CHILD_ID,keyGroup);

        mapData.put(CHILD_FUllNAME,nameGroup);
        if (photoString == null){
            mapData.put("photourl","empty");
        }else {
            mapData.put("photourl",photoString);
        }


        namesMap.put(uid,USER_CREATOR);

        mapData.put("members",namesMap);

        path.updateChildren(mapData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateGroupActivity.this, "Group Created Successfully"
                        , Toast.LENGTH_SHORT).show();
                addGroupToMainList(mapData,contactsList);

            }
        });


    }
    public void addGroupToMainList(HashMap<String,Object> mapData,List<CommonModel>commonModelList){
        ((Map)map).put(CHILD_ID, String.valueOf(mapData.get(CHILD_ID)));
        ((Map)map).put(CHILD_TYPE, "group");
        Iterable $this$forEach$iv = (Iterable)commonModelList;

        Iterator var8 = $this$forEach$iv.iterator();

        while(var8.hasNext()) {
            Object element$iv = var8.next();
            CommonModel it = (CommonModel) element$iv;

            mainListRefrence.child(NODE_MAIN_LIST).child(it.getUid())
                    .child(String.valueOf(map.get(CHILD_ID))).updateChildren((Map)map);
        }

        mainListRefrence.child(NODE_MAIN_LIST).child(uid)
                .child(String.valueOf(map.get(CHILD_ID)))
                .updateChildren((Map)map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    private void addPhoto() {
        CropImage.activity()
                .setAspectRatio(1,1)
                .setRequestedSize(600,600)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mUri = result.getUri();
                Log.d("uri", mUri.toString() + "");
                createGroupPhoto.setImageURI(mUri);
                Uri resultUri = result.getUri();
                String path1 = resultUri.getPath();

                Bitmap bitmap = BitmapFactory.decodeFile(path1);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // In case you want to compress your image, here it's at 40%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                photoString = Base64.encodeToString(byteArray, Base64.DEFAULT);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    @OnClick(R.id.create_group_photo)
    void createGroupPhoto(){
        addPhoto();
    }
    @OnClick(R.id.create_group_btn_complete)
    void createGroupBtnComplete(){
         nameGroup = createGroupInputName.getText().toString();
        if (nameGroup.isEmpty()){
            Toast.makeText(this, "Enter Group Name", Toast.LENGTH_SHORT).show();
        }else {

                createGroupToDataBase(nameGroup, mUri, contactsList);

        }

    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @OnClick(R.id.back_btn)
    void OnClickBack(){
        onSupportNavigateUp();
    }
}
