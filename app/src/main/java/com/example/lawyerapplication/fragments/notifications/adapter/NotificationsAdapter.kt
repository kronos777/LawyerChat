package com.example.lawyerapplication.fragments.main_screen.situation_adapter


import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lawyerapplication.databinding.ItemNotificationBinding
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.fragments.notifications.NotificationsViewModel
import com.example.lawyerapplication.fragments.notifications.adapter.StageItemDiffCallback


class NotificationsAdapter(
    private val showMenuDelete: (Boolean) -> Unit
): ListAdapter<StageBussines, NotificationsItemHorizontalViewHolder>(
    StageItemDiffCallback()
) {
    var onPaymentItemClickListener: ((StageBussines) -> Unit)? = null

    private var isEnabled = false
    private var itemSelectedList = mutableListOf<Int>()
    val pairList = ArrayList<Pair<Int, Int>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsItemHorizontalViewHolder {
        val layout = com.example.lawyerapplication.R.layout.item_notification

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
            if(stageItem.status != 5 && itemSelectedList.isEmpty()){
                onPaymentItemClickListener?.invoke(stageItem)
            } else if(itemSelectedList.contains(position)) {
                itemSelectedList.removeAt(position)
                stageItem!!.status = 0
               viewHolder.binding.checkImage.visibility = View.GONE
                if (itemSelectedList.isEmpty()) {
                    showMenuDelete(false)
                }
            } else if(stageItem.status != 5 && itemSelectedList.isNotEmpty()) {
                selectItem(viewHolder, stageItem, position)
            }

        }
        //here
        viewHolder.itemView.setOnLongClickListener {
            selectItem(viewHolder, stageItem, position)
            true
        }
        //here

        binding.stageItem= stageItem



    }

    private fun selectItem(viewHolder: NotificationsItemHorizontalViewHolder, stageItem: StageBussines?, position: Int) {
        isEnabled = true
        viewHolder.binding.checkImage.visibility = View.VISIBLE
        itemSelectedList.add(position)
        stageItem!!.status = 5
        showMenuDelete(true)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return VIEW_TYPE_ENABLED

    }

    fun deleteChoiceItem() {
        if (itemSelectedList.isNotEmpty()) {
            currentList.filter{ it.status == 5 }.forEach {
                val pair = Pair(it.id, it.idBussines)
                pairList.add(pair)
            }
            itemSelectedList.clear()
            isEnabled = false
        }
    }


    companion object {
        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = 101
        const val MAX_POOL_SIZE = 30
    }

}


