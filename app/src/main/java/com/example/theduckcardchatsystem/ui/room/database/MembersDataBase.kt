package com.example.theduckcardchatsystem.ui.room.database

import android.content.Context
import androidx.room.Database
import com.example.theduckcardchatsystem.ui.model.MembersId
import androidx.room.RoomDatabase
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao
import android.os.AsyncTask
import com.example.theduckcardchatsystem.ui.room.database.MembersDataBase
import kotlin.jvm.Synchronized
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.theduckcardchatsystem.ui.room.database.MembersDataBase.PopulateAsyncTask

@Database(entities = [MembersId::class], version = 1, exportSchema = false)
abstract class MembersDataBase : RoomDatabase() {
    abstract fun membersDao(): MembersDao
    private class PopulateAsyncTask : AsyncTask<Void?, Void?, Void?> {
        private var membersDao: MembersDao

        private constructor(db: MembersDataBase?) {
            membersDao = db!!.membersDao()
        }

        constructor(membersDao: MembersDao) {
            this.membersDao = membersDao
        }

        protected override fun doInBackground(vararg voids: Void): Void? {
            return null
        }
    }

    companion object {
        var instance: MembersDataBase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): MembersDataBase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, MembersDataBase::class.java, "members_data"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build()
            }
            return instance
        }

        var roomCallBack: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateAsyncTask(instance).execute()
            }
        }
    }
}