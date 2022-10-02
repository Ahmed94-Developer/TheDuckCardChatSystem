package com.example.theduckcardchatsystem.ui.model;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffUtilCalback extends DiffUtil.Callback {
    private List<CommonModel> oldList;
    private List<CommonModel> newList;

    public DiffUtilCalback(List<CommonModel> oldList, List<CommonModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }


    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        return oldList.get(oldItemPosition).getTimeStamp() == newList.get(newItemPosition).getTimeStamp();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
