package com.example.theduckcardchatsystem.ui.room.repository

import android.app.Application
import com.example.theduckcardchatsystem.ui.room.database.MembersDataBase.Companion.getInstance
import com.example.theduckcardchatsystem.ui.room.database.MembersDataBase.membersDao
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao.membersId
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao.insertId
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao.updateId
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao.DeleteId
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao.deleteAll
import com.example.theduckcardchatsystem.ui.room.dao.MembersDao
import androidx.lifecycle.LiveData
import com.example.theduckcardchatsystem.ui.model.MembersId
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.InsertAsyncTask
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.UpdateAsyncTask
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.deleteAsyncTask
import com.example.theduckcardchatsystem.ui.room.repository.MembersIdRepository.deleteAllAsyncTask
import android.os.AsyncTask
import com.example.theduckcardchatsystem.ui.room.database.MembersDataBase

class MembersIdRepository(application: Application?) {
    private val membersDao: MembersDao
    val liveMembersId: LiveData<List<MembersId?>?>?
    fun insert(membersId: MembersId?) {
        InsertAsyncTask(membersDao).execute(membersId)
    }

    fun Update(membersId: MembersId?) {
        UpdateAsyncTask(membersDao).execute(membersId)
    }

    fun delete(membersId: MembersId?) {
        deleteAsyncTask(membersDao).execute(membersId)
    }

    fun deleteAll() {
        deleteAllAsyncTask(membersDao).execute()
    }

    private class InsertAsyncTask(private val membersDao: MembersDao) :
        AsyncTask<MembersId?, Void?, Void?>() {
        protected override fun doInBackground(vararg membersIds: MembersId): Void? {
            membersDao.insertId(membersIds[0])
            return null
        }
    }

    private class UpdateAsyncTask(private val membersDao: MembersDao) :
        AsyncTask<MembersId?, Void?, Void?>() {
        protected override fun doInBackground(vararg membersIds: MembersId): Void? {
            membersDao.updateId(membersIds[0])
            return null
        }
    }

    private class deleteAsyncTask(private val membersDao: MembersDao) :
        AsyncTask<MembersId?, Void?, Void?>() {
        protected override fun doInBackground(vararg membersIds: MembersId): Void? {
            membersDao.DeleteId(membersIds[0])
            return null
        }
    }

    private class deleteAllAsyncTask(private val membersDao: MembersDao) :
        AsyncTask<Void?, Void?, Void?>() {
        protected override fun doInBackground(vararg voids: Void): Void? {
            membersDao.deleteAll()
            return null
        }
    }

    init {
        val membersDataBase = getInstance(application!!)
        membersDao = membersDataBase!!.membersDao()
        liveMembersId = membersDao.membersId
    }
}