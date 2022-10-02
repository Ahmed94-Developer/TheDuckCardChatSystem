package com.example.theduckcardchatsystem.ui.model

import com.example.theduckcardchatsystem.ui.model.CommonModel.timeStamp
import com.example.theduckcardchatsystem.ui.model.CommonModel
import androidx.recyclerview.widget.DiffUtil

class DiffUtilCalback(
    private val oldList: List<CommonModel>,
    private val newList: List<CommonModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timeStamp === newList[newItemPosition].timeStamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}