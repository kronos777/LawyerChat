package com.example.lawyerapplication.fragments.mycards.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.lawyerapplication.db.data.BanksCardItem
import com.example.lawyerapplication.db.data.SituationItem


class BanksCardDiffCallback: DiffUtil.ItemCallback<BanksCardItem>() {

    override fun areItemsTheSame(oldItem: BanksCardItem, newItem: BanksCardItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BanksCardItem, newItem: BanksCardItem): Boolean {
        return oldItem == newItem
    }
}