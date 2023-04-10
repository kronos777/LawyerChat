package com.example.lawyerapp.presentation.adapter.viewholders

import androidx.recyclerview.widget.DiffUtil
import com.example.lawyerapplication.db.data.SituationItem


class SituationItemDiffCallback: DiffUtil.ItemCallback<SituationItem>() {

    override fun areItemsTheSame(oldItem: SituationItem, newItem: SituationItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SituationItem, newItem: SituationItem): Boolean {
        return oldItem == newItem
    }
}