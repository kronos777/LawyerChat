package com.example.lawyerapplication.fragments.my_business.my_bussines_adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.SituationItem


class BusinessItemDiffCallback: DiffUtil.ItemCallback<BusinessItem>() {

    override fun areItemsTheSame(oldItem: BusinessItem, newItem: BusinessItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BusinessItem, newItem: BusinessItem): Boolean {
        return oldItem == newItem
    }
}