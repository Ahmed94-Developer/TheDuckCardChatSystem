package com.example.theduckcardchatsystem.ui.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "members_table")
public class MembersId {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String memberId;

    public MembersId(String memberId) {
        this.memberId = memberId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }
}
