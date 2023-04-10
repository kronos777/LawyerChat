package com.example.lawyerapplication.fragments.situation.main_list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapp.presentation.adapter.viewholders.SituationItemDiffCallback
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ItemSituationBinding

import com.example.lawyerapplication.db.data.SituationItem

class SearchBySituationAdapter: ListAdapter<SituationItem, SituationItemViewHolder>(
    SituationItemDiffCallback()
) {

    var onPaymentItemClickListener: ((SituationItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SituationItemViewHolder {
        val layout = R.layout.item_situation

        val binding = DataBindingUtil.inflate<ItemSituationBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return SituationItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SituationItemViewHolder, position: Int) {
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
