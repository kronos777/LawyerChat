package com.example.lawyerapplication.fragments.main_screen.situation_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapplication.fragments.situation.main_list.SituationItemDiffCallback
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ItemMyBusinessBinding
import com.example.lawyerapplication.databinding.ItemNotificationBinding
import com.example.lawyerapplication.databinding.ItemSituationHorizontalBinding
import com.example.lawyerapplication.db.data.BusinessItem

import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.fragments.my_business.my_bussines_adapter.BusinessItemDiffCallback
import com.example.lawyerapplication.fragments.notifications.adapter.StageItemDiffCallback

class NotificationsAdapter: ListAdapter<StageBussines, NotificationsItemHorizontalViewHolder>(
    StageItemDiffCallback()
) {

    var onPaymentItemClickListener: ((StageBussines) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsItemHorizontalViewHolder {
        val layout = R.layout.item_notification

        val binding = DataBindingUtil.inflate<ItemNotificationBinding>(
            LayoutInflater.from(parent.context),
            layout,
            parent,
            false
        )
        return NotificationsItemHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: NotificationsItemHorizontalViewHolder, position: Int) {
        val stageItem = getItem(position)
        val binding = viewHolder.binding

        binding.root.setOnClickListener {
            onPaymentItemClickListener?.invoke(stageItem)
        }

        binding.stageItem= stageItem



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
