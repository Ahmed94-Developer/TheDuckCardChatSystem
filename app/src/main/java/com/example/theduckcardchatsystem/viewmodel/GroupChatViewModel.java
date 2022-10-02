package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.HomeActivity;
import com.example.theduckcardchatsystem.repository.GroupChatRepository;

public class GroupChatViewModel extends AndroidViewModel {
    private Application application;
    private GroupChatRepository groupChatRepository;

    public GroupChatViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        groupChatRepository = new GroupChatRepository(application);
    }
    public void sendMessage(String message, String id, String typeText){
        groupChatRepository.sendMessage(message, id, typeText);
    }
    public void sendImageAsMessage(String imageUrl,String id){
        groupChatRepository.sendMessageAsImage(imageUrl, id);

    }
    public void initRecyclerView(String id, RecyclerView recyclerView, Context context){
       groupChatRepository.initRecyclerView(id, recyclerView, context);

    }
    public void saveToMainList(String id ,String type){
        groupChatRepository.saveToMainList(id, type);
    }
    public void ClearChat(String id){
        groupChatRepository.clearChat(id);
    }
    public void DeleteChat(String id){
        groupChatRepository.DeleteChat(id);
    }
}
