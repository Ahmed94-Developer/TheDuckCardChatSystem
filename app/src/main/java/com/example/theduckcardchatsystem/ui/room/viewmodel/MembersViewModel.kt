package com.example.theduckcardchatsystem.ui.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.theduckcardchatsystem.ui.model.MembersId;
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository;

import java.util.List;

public class MembersViewModel extends AndroidViewModel {
    private Application application;
    private MembersIdRepository membersIdRepository;
    private LiveData<List<MembersId>> membersIdLiveData;
    public MembersViewModel(@NonNull Application application) {
        super(application);
        membersIdRepository = new MembersIdRepository(application);
        membersIdLiveData = membersIdRepository.getLiveMembersId();
    }

    public void insert(MembersId membersId) {
        membersIdRepository.insert(membersId);
    }

    public void Update(MembersId membersId) {
        membersIdRepository.Update(membersId);
    }

    public void Delete(MembersId membersId) {
        membersIdRepository.delete(membersId);
    }

    public void DeleteAll() {
        membersIdRepository.deleteAll();
    }

    public LiveData<List<MembersId>> getMembersIdLiveData() {
        return membersIdLiveData;
    }
}
