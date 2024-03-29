package com.example.lawyerapplication.fragments.single_chat

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.InstrumentationRegistry.getContext
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.utils.Events.EventAudioMsg
import com.example.lawyerapplication.utils.ItemClickListener
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.gone
import com.example.lawyerapplication.utils.show
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.IOException


class AdChat(private val context: Context, private val msgClickListener: ItemClickListener) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DiffCallbackMessages()) {

    private val preference = MPreference(context)

    companion object {
        private const val TYPE_TXT_SENT = 0
        private const val TYPE_TXT_RECEIVED = 1
        private const val TYPE_IMG_SENT = 2
        private const val TYPE_IMG_RECEIVE = 3
        private const val TYPE_STICKER_SENT = 4
        private const val TYPE_STICKER_RECEIVE = 5
        private const val TYPE_AUDIO_SENT = 6
        private const val TYPE_AUDIO_RECEIVE = 7
        private const val TYPE_FILE_SENT = 8
        private const val TYPE_FILE_RECEIVE = 9
        private var lastPlayedHolder: RowAudioSentBinding?=null
        private var lastReceivedPlayedHolder: RowAudioReceiveBinding?=null
        private var lastPlayedAudioId : Long=-1
        private var player = MediaPlayer()
        lateinit var messageList: MutableList<Message>

        fun stopPlaying() {
            if(player.isPlaying) {
                lastReceivedPlayedHolder?.progressBar?.abandon()
                lastPlayedHolder?.progressBar?.abandon()
                lastReceivedPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_action_play)
                lastPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_action_play)
                player.apply {
                    stop()
                    reset()
                    EventBus.getDefault().post(EventAudioMsg(false))
                }
            }
        }

     fun isPlaying() = player.isPlaying
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TXT_SENT -> {
                val binding = RowSentMessageBinding.inflate(layoutInflater, parent, false)
                TxtSentVHolder(binding)
            }
            TYPE_TXT_RECEIVED -> {
                val binding = RowReceiveMessageBinding.inflate(layoutInflater, parent, false)
                TxtReceiveVHolder(binding)
            }
            TYPE_IMG_SENT -> {
                val binding = RowImageSentBinding.inflate(layoutInflater, parent, false)
                ImageSentVHolder(binding)
            }
            TYPE_IMG_RECEIVE -> {
                val binding = RowImageReceiveBinding.inflate(layoutInflater, parent, false)
                ImageReceiveVHolder(binding)
            }
            TYPE_STICKER_SENT -> {
                val binding = RowStickerSentBinding.inflate(layoutInflater, parent, false)
                StickerSentVHolder(binding)
            }
            TYPE_STICKER_RECEIVE -> {
                val binding = RowStickerReceiveBinding.inflate(layoutInflater, parent, false)
                StickerReceiveVHolder(binding)
            }
            TYPE_AUDIO_SENT -> {
                val binding = RowAudioSentBinding.inflate(layoutInflater, parent, false)
                AudioSentVHolder(binding)
            }
            TYPE_FILE_SENT -> {
                val binding = RowFileSentBinding.inflate(layoutInflater, parent, false)
                FileSentVHolder(binding)
            }
            TYPE_FILE_RECEIVE -> {
                val binding = RowFileReceiveBinding.inflate(layoutInflater, parent, false)
                FileReceiveVHolder(binding)
            }

            else-> {
                val binding = RowAudioReceiveBinding.inflate(layoutInflater, parent, false)
                AudioReceiveVHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is TxtSentVHolder ->
                holder.bind(context,getItem(position))
            is TxtReceiveVHolder ->
                holder.bind(context,getItem(position))
            is ImageSentVHolder ->
                holder.bind(getItem(position),msgClickListener)
            is ImageReceiveVHolder ->
                holder.bind(getItem(position),msgClickListener)
            is StickerSentVHolder ->
                holder.bind(getItem(position))
            is StickerReceiveVHolder ->
                holder.bind(getItem(position))
            is AudioSentVHolder ->
                holder.bind(context,getItem(position))
            is AudioReceiveVHolder ->
                holder.bind(context,getItem(position))
            is FileSentVHolder ->
                holder.bind(getItem(position),msgClickListener)
            is FileReceiveVHolder ->
                holder.bind(getItem(position),msgClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        val fromMe=message.from.substring(0, 28) == preference.getUid()
        if (fromMe && message.type == "text")
            return TYPE_TXT_SENT
        else if (!fromMe && message.type == "text")
            return TYPE_TXT_RECEIVED
        else if (fromMe && message.type == "image" && message.imageMessage?.imageType=="image")
            return TYPE_IMG_SENT
        else if (!fromMe && message.type == "image" && message.imageMessage?.imageType=="image")
            return TYPE_IMG_RECEIVE
        else if (fromMe && message.type == "image" && (message.imageMessage?.imageType=="sticker"
                    || message.imageMessage?.imageType=="gif"))
            return TYPE_STICKER_SENT
        else if (!fromMe && message.type == "image"  && (message.imageMessage?.imageType=="sticker"
                    || message.imageMessage?.imageType=="gif"))
            return TYPE_STICKER_RECEIVE
        else if (fromMe && message.type == "audio")
            return TYPE_AUDIO_SENT
        else if (!fromMe && message.type == "audio")
            return TYPE_AUDIO_RECEIVE
        else if (fromMe && message.type == "file")
            return TYPE_FILE_SENT
        else if (!fromMe && message.type == "file")
            return TYPE_FILE_RECEIVE
        return super.getItemViewType(position)
    }

    class TxtSentVHolder(val binding: RowSentMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context,item: Message) {
            binding.message = item
            binding.messageList= messageList as ArrayList<Message>
            if (bindingAdapterPosition>0) {
                val message = messageList[bindingAdapterPosition - 1]
                if (message.from == item.from)
                    binding.txtMsg.setBackgroundResource(R.drawable.shape_send_msg_corned_chat)
            }
            binding.executePendingBindings()
        }
    }

    class TxtReceiveVHolder(val binding: RowReceiveMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context:Context,item: Message) {
            binding.message = item
            if (bindingAdapterPosition>0) {
                val message = messageList[bindingAdapterPosition - 1]
                //Timber.v("chatUserSingleChatAdapter messageFrom ${message.from} itemFrom ${item.from}")
                if (message.from == item.from)
                    binding.txtMsg.setBackgroundResource(R.drawable.shape_receive_msg_corned_chat)
            }
            binding.executePendingBindings()
        }
    }

    class ImageSentVHolder(val binding: RowImageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, msgClickListener: ItemClickListener) {
            binding.message = item
            binding.imageMsg.setOnClickListener {
                msgClickListener.onItemClicked(it,bindingAdapterPosition)
            }
            binding.executePendingBindings()
        }
    }

    class ImageReceiveVHolder(val binding: RowImageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, msgClickListener: ItemClickListener) {
            binding.message = item
            binding.imageMsg.setOnClickListener {
                msgClickListener.onItemClicked(it,bindingAdapterPosition)
            }
            binding.executePendingBindings()
        }
    }


    /*for file*/
    class FileSentVHolder(val binding: RowFileSentBinding) :
        RecyclerView.ViewHolder(binding.root)  {
        fun bind(item: Message, msgClickListener: ItemClickListener) {
            binding.message = item
            binding.fileMsg.setOnClickListener {
                msgClickListener.onItemClicked(it,bindingAdapterPosition)

                //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.fileMessage!!.uri))
                //val i = Intent.parseUri(Uri.parse(item.fileMessage!!.uri).toString(), Intent.URI_INTENT_SCHEME)
               // startActivity(getContext(), intent)
               /* CoroutineScope(Dispatchers.Main).launch {
                    startActivity(intent)
                }*/
               // Timber.v("this file message url" + item.fileMessage!!.uri)
            }
            binding.executePendingBindings()
        }
    }

    class FileReceiveVHolder(val binding: RowFileReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message, msgClickListener: ItemClickListener) {
            binding.message = item
            binding.fileMsg.setOnClickListener {
                msgClickListener.onItemClicked(it,bindingAdapterPosition)
            }
            binding.executePendingBindings()
        }
    }
    /*for file*/

    class StickerSentVHolder(val binding: RowStickerSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.message = item
            binding.executePendingBindings()
        }
    }

    class StickerReceiveVHolder(val binding: RowStickerReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.message = item
            binding.executePendingBindings()
        }
    }

    class AudioReceiveVHolder(val binding: RowAudioReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context,item: Message) {
            binding.message = item
            binding.progressBar.setStoriesCountDebug(1,0)
            binding.progressBar.setAllStoryDuration(item.audioMessage?.duration!!.toLong()*1000)
            binding.imgPlay.setOnClickListener {
                startPlaying(
                    context,
                    item,
                    binding)
            }
            binding.executePendingBindings()
        }

        private fun startPlaying(
            context: Context,
            item: Message,
            currentHolder: RowAudioReceiveBinding) {
            if (player.isPlaying){
                stopPlaying()
                lastReceivedPlayedHolder?.progressBar?.abandon()
                lastReceivedPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_action_play)
                lastPlayedHolder?.imgPlay?.setImageResource(R.drawable.ic_action_play)
                lastPlayedHolder?.progressBar?.abandon()
                if (lastPlayedAudioId ==item.createdAt)
                    return

            }
            player = MediaPlayer()
            lastReceivedPlayedHolder =currentHolder
            lastPlayedAudioId =item.createdAt
            currentHolder.progressBuffer.show()
            currentHolder.imgPlay.gone()
            player.apply {
                try {
                    setDataSource(context, Uri.parse(item.audioMessage?.uri))
                    prepareAsync()
                    setOnPreparedListener {
                        Timber.v("Started..")
                        start()
                        currentHolder.progressBuffer.gone()
                        currentHolder.imgPlay.setImageResource(R.drawable.ic_action_stop)
                        currentHolder.imgPlay.show()
                        currentHolder.progressBar.startStories()
                        EventBus.getDefault().post(EventAudioMsg(true))
                    }
                    setOnCompletionListener {
                        currentHolder.progressBar.abandon()
                        currentHolder.imgPlay.setImageResource(R.drawable.ic_action_play)
                        EventBus.getDefault().post(EventAudioMsg(false))
                    }
                } catch (e: IOException) {
                    println("ChatFragment.startPlaying:prepare failed")
                }
            }
        }
    }

    class AudioSentVHolder(val binding: RowAudioSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            context: Context,
            item: Message,
        ) {
            binding.message = item
            binding.progressBar.setStoriesCountDebug(1,0)
            binding.progressBar.setAllStoryDuration(item.audioMessage?.duration!!.toLong()*1000)
            binding.imgPlay.setOnClickListener {
               startPlaying(
                    context,
                    item,
                    binding)
            }
            binding.executePendingBindings()
        }

        private fun startPlaying(
            context: Context,
            item: Message,
            currentHolder: RowAudioSentBinding) {
            if (player.isPlaying){
                stopPlaying()
                if (lastPlayedAudioId ==item.createdAt)
                    return
            }
            player = MediaPlayer()
            lastPlayedHolder =currentHolder
            lastPlayedAudioId =item.createdAt
            currentHolder.progressBuffer.show()
            currentHolder.imgPlay.gone()
            player.apply {
                try {
                    setDataSource(context, Uri.parse(item.audioMessage?.uri))
                    prepareAsync()
                    setOnPreparedListener {
                        Timber.v("Started..")
                        start()
                        currentHolder.progressBuffer.gone()
                        currentHolder.imgPlay.setImageResource(R.drawable.ic_action_stop)
                        currentHolder.imgPlay.show()
                        currentHolder.progressBar.startStories()
                        EventBus.getDefault().post(EventAudioMsg(true))
                    }
                    setOnCompletionListener {
                        currentHolder.progressBar.abandon()
                        currentHolder.imgPlay.setImageResource(R.drawable.ic_action_play)
                        EventBus.getDefault().post(EventAudioMsg(false))
                    }
                } catch (e: IOException) {
                    println("ChatFragment.startPlaying:prepare failed")
                }
            }
        }
    }
}

class DiffCallbackMessages : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.createdAt == newItem.createdAt
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
