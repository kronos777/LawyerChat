package com.example.lawyerapplication.fragments.mycards.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ItemCardBinding
import com.example.lawyerapplication.databinding.ItemSituationBinding
import com.example.lawyerapplication.db.data.BanksCardItem

import com.example.lawyerapplication.db.data.SituationItem

class BanksCardAdapter: ListAdapter<BanksCardItem, BanksCardViewHolder>(
    BanksCardDiffCallback()
) {

    var onPaymentItemClickListener: ((BanksCardItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BanksCardViewHolder {
        val layout = R.layout.item_card

        val binding = DataBindingUtil.inflate<ItemCardBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return BanksCardViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: BanksCardViewHolder, position: Int) {
        val cardItem = getItem(position)
        val binding = viewHolder.binding

        binding.root.setOnClickListener {
            onPaymentItemClickListener?.invoke(cardItem)
        }

        Log.d("itemsit", cardItem.nameString)
        /*       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }*/

          binding.cardItem = cardItem



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
