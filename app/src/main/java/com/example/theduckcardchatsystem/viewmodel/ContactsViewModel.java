package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.repository.ContactsRepository;
import com.example.theduckcardchatsystem.ui.model.CommonModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class ContactsViewModel extends AndroidViewModel {
    private Application application;
    private ContactsRepository contactsRepository;
    public ContactsViewModel(@NonNull Application application) {
        super(application);
        contactsRepository = new ContactsRepository(application);
    }
    public void initRecyclerView(FirebaseRecyclerAdapter<CommonModel,ContactsRepository.ContactsHolder> mAdapter
            , RecyclerView rv_contacts, Context context){
        contactsRepository.initRecyclerView(mAdapter,rv_contacts,context);
    }
}
