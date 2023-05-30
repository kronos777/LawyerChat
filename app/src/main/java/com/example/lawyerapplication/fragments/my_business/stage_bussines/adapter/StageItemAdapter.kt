package com.example.lawyerapplication.fragments.mycards.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ItemStageBinding
import com.example.lawyerapplication.db.data.StageBussines

class StageItemAdapter: ListAdapter<StageBussines, StageItemViewHolder>(
    StageItemDiffCallback()
) {

    var onPaymentItemClickListener: ((StageBussines) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageItemViewHolder {
        val layout = R.layout.item_stage

        val binding = DataBindingUtil.inflate<ItemStageBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return StageItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: StageItemViewHolder, position: Int) {
        val stageItem = getItem(position)
        val binding = viewHolder.binding

        /*binding.root.setOnClickListener {
            onPaymentItemClickListener?.invoke(cardItem)
        }*/

        binding.showDescription.setOnClickListener {
            //onPaymentItemClickListener?.invoke(stageItem)
            val deg = binding.showDescription.rotation
            if(deg == (90).toFloat()) {
                binding.stageDescriptionH1.visibility = View.VISIBLE
                binding.showDescription.rotation = 270f
            } else {
                binding.stageDescriptionH1.visibility = View.GONE
                binding.showDescription.rotation = 90f
            }

        }

       // Log.d("itemsit", cardItem.nameString)
        /*       when (binding) {
            is ItemStudentDisabledBinding -> {
                binding.studentItem = studentItem
            }
            is ItemStudentEnabledBinding -> {
                binding.studentItem = studentItem
            }
        }*/

          binding.stageItem = stageItem



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
