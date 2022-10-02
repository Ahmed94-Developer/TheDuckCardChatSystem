package com.example.theduckcardchatsystem.ui.room.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.theduckcardchatsystem.ui.model.MembersId;
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao;

@Database(entities = {MembersId.class},version = 1, exportSchema = false)
public abstract class MembersDataBase extends RoomDatabase {
    public static MembersDataBase instance;
    public abstract MembersDao membersDao();

    public static synchronized MembersDataBase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext()
                    ,MembersDataBase.class,"members_data")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }
    public static Callback roomCallBack = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateAsyncTask(instance).execute();
        }
    };
    private static class PopulateAsyncTask extends AsyncTask<Void,Void,Void> {
        private MembersDao membersDao;
        private PopulateAsyncTask(MembersDataBase db){
            membersDao = db.membersDao();
        }

        public PopulateAsyncTask(MembersDao membersDao) {
            this.membersDao = membersDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
