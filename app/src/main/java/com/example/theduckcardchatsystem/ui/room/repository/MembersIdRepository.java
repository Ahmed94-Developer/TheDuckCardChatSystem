package com.example.theduckcardchatsystem.ui.room.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.theduckcardchatsystem.ui.model.MembersId;
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao;
import com.example.theduckcardchatsystem.ui.room.database.MembersDataBase;

import java.util.List;

public class MembersIdRepository {
    private MembersDao membersDao;
    private LiveData<List<MembersId>> membersIdLiveData;

    public MembersIdRepository(Application application){
       MembersDataBase membersDataBase = MembersDataBase.getInstance(application);
        membersDao = membersDataBase.membersDao();
        membersIdLiveData = membersDao.getMembersId();
    }
    public void insert(MembersId membersId){
        new InsertAsyncTask(membersDao).execute(membersId);

    }
    public void Update(MembersId membersId){
        new UpdateAsyncTask(membersDao).execute(membersId);
    }
    public void delete(MembersId membersId){
        new deleteAsyncTask(membersDao).execute(membersId);
    }
    public void deleteAll(){
        new deleteAllAsyncTask(membersDao).execute();
    }
    public LiveData<List<MembersId>> getLiveMembersId(){
        return membersIdLiveData;
    }
    private static class InsertAsyncTask extends AsyncTask<MembersId,Void,Void> {
        private MembersDao membersDao;

        public InsertAsyncTask(MembersDao membersDao) {
            this.membersDao = membersDao;
        }

        @Override
        protected Void doInBackground(MembersId... membersIds) {
            membersDao.insertId(membersIds[0]);
            return null;
        }
    }
    private static class UpdateAsyncTask extends AsyncTask<MembersId,Void,Void>{
        private MembersDao membersDao;

        public UpdateAsyncTask(MembersDao membersDao) {
            this.membersDao = membersDao;
        }

        @Override
        protected Void doInBackground(MembersId... membersIds) {
            membersDao.updateId(membersIds[0]);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<MembersId,Void,Void>{
        private MembersDao membersDao;

        public deleteAsyncTask(MembersDao membersDao) {
            this.membersDao = membersDao;
        }

        @Override
        protected Void doInBackground(MembersId... membersIds) {
            membersDao.DeleteId(membersIds[0]);
            return null;
        }
    }
    private static class deleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private MembersDao membersDao;

        public deleteAllAsyncTask(MembersDao membersDao) {
            this.membersDao= membersDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            membersDao.deleteAll();
            return null;
        }
    }

}
