package com.example.theduckcardchatsystem.ui.room.dao

import com.example.theduckcardchatsystem.ui.model.MembersId
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MembersDao {
    @Insert
    fun insertId(membersId: MembersId?)

    @Update
    fun updateId(membersId: MembersId?)

    @Delete
    fun DeleteId(membersId: MembersId?)

    @get:Query("SELECT *FROM members_table")
    val membersId: LiveData<List<MembersId?>?>?

    @Query("DELETE FROM members_table")
    fun deleteAll()
}