package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.repository.ChatsRepository;
import com.example.theduckcardchatsystem.ui.model.CommonModel;

public class ChatsViewModel extends AndroidViewModel {
    private Application application;
    private ChatsRepository chatsRepository;
    public ChatsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        chatsRepository = new ChatsRepository(application);
    }
    public void initRecyclerViewChats(Context context, RecyclerView mainlistRecycler){
        chatsRepository.initRecyclerViewChats(context,mainlistRecycler);
    }
}
