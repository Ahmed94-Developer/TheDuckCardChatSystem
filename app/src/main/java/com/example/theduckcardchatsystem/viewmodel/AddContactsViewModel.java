package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.repository.AddContactsRepository;

public class AddContactsViewModel extends AndroidViewModel {
    private Application application;
    private AddContactsRepository addContactsRepository;
    public AddContactsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        addContactsRepository = new AddContactsRepository(application);

    }
    public void initRecyclerView(RecyclerView recyclerView, Context context){
        addContactsRepository.initRecyclerView(recyclerView, context);
    }
}
