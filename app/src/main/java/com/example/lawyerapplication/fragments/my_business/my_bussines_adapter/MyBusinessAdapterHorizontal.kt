package com.example.lawyerapplication.fragments.main_screen.situation_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapplication.fragments.situation.main_list.SituationItemDiffCallback
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ItemMyBusinessBinding
import com.example.lawyerapplication.databinding.ItemSituationHorizontalBinding
import com.example.lawyerapplication.db.data.BusinessItem

import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.my_business.my_bussines_adapter.BusinessItemDiffCallback

class MyBusinessAdapterHorizontal: ListAdapter<BusinessItem, MyBusinessItemHorizontalViewHolder>(
    BusinessItemDiffCallback()
) {

    var onPaymentItemClickListener: ((BusinessItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBusinessItemHorizontalViewHolder {
        val layout = R.layout.item_my_business

        val binding = DataBindingUtil.inflate<ItemMyBusinessBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return MyBusinessItemHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MyBusinessItemHorizontalViewHolder, position: Int) {
        val businessItem = getItem(position)
        val binding = viewHolder.binding

        binding.root.setOnClickListener {
            onPaymentItemClickListener?.invoke(businessItem)
        }

       // Log.d("itemsit", situationItem.nameString)
        /*       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }*/

          binding.businessItem = businessItem



    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return VIEW_TYPE_ENABLED

    }

    companion object {

        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = 101
        const val MAX_POOL_SIZE = 30
    }

}
