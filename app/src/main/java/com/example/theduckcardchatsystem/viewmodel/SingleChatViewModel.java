package com.example.theduckcardchatsystem.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theduckcardchatsystem.adapters.SingleChatAdapter;
import com.example.theduckcardchatsystem.repository.SingleChatRepository;

public class SingleChatViewModel extends AndroidViewModel {
    private Application application;
    private SingleChatRepository singleChatRepository;
    public SingleChatViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        singleChatRepository = new SingleChatRepository(application);
    }
    public void sendMessage(String message, String recevingUserId, String typeText){
     singleChatRepository.sendMessage(message, recevingUserId, typeText);

    }
    public void sendMessageAsIimage(String receivingUserID , String imageUrl,String messageKey){
        singleChatRepository.sendMessageAsImage(receivingUserID, imageUrl, messageKey);

    }
    public void saveToMainList(String id ,String type){
        singleChatRepository.saveToMainList(id,type);
    }
    public void initRecyclerView(String uid, SingleChatAdapter adapter, RecyclerView recyclerView){
        singleChatRepository.initRecyclerView(uid, adapter, recyclerView);
    }
    public void ClearChat(String uid){
        singleChatRepository.clearChat(uid);
    }
    public void DeleteChat(String uid){
        singleChatRepository.DeleteChat(uid);
    }
}
