package com.example.lawyerapplication.fragments.notifications.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.db.data.StageBussines


class StageItemDiffCallback: DiffUtil.ItemCallback<StageBussines>() {

    override fun areItemsTheSame(oldItem: StageBussines, newItem: StageBussines): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StageBussines, newItem: StageBussines): Boolean {
        return oldItem == newItem
    }
}