package com.example.lawyerapplication.fragments.main_screen.situation_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapplication.fragments.situation.main_list.SituationItemDiffCallback
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ItemSituationHorizontalBinding

import com.example.lawyerapplication.db.data.SituationItem

class SearchBySituationAdapterHorizontal: ListAdapter<SituationItem, SituationItemHorizontalViewHolder>(
    SituationItemDiffCallback()
) {

    var onPaymentItemClickListener: ((SituationItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SituationItemHorizontalViewHolder {
        val layout = R.layout.item_situation_horizontal

        val binding = DataBindingUtil.inflate<ItemSituationHorizontalBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return SituationItemHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SituationItemHorizontalViewHolder, position: Int) {
        val situationItem = getItem(position)
        val binding = viewHolder.binding

        binding.root.setOnClickListener {
            onPaymentItemClickListener?.invoke(situationItem)
        }

        Log.d("itemsit", situationItem.nameString)
        /*       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }*/

          binding.situationItem = situationItem



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
