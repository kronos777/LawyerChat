package com.example.lawyerapplication.fragments.mycards.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.lawyerapplication.db.data.BanksCardItem
import com.example.lawyerapplication.db.data.StageBussines


class StageItemDiffCallback: DiffUtil.ItemCallback<StageBussines>() {

    override fun areItemsTheSame(oldItem: StageBussines, newItem: StageBussines): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StageBussines, newItem: StageBussines): Boolean {
        return oldItem == newItem
    }
}