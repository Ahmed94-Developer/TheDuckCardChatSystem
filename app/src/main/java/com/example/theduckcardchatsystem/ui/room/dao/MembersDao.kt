package com.example.theduckcardchatsystem.ui.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.theduckcardchatsystem.ui.model.MembersId;

import java.util.List;

@Dao
public interface MembersDao {
    @Insert
    void insertId(MembersId membersId);
    @Update
    void updateId(MembersId membersId);
    @Delete
    void DeleteId(MembersId membersId);
    @Query("SELECT *FROM members_table")
    LiveData<List<MembersId>> getMembersId();
    @Query("DELETE FROM members_table")
    void deleteAll();
}
