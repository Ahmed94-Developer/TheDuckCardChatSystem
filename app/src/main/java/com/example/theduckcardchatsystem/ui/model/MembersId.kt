package com.example.theduckcardchatsystem.ui.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members_table")
class MembersId(val memberId: String) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}