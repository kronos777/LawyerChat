package com.example.lawyerapplication.fragments.single_chat_home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.lawyerapplication.MApplication
import com.example.lawyerapplication.R
import com.example.lawyerapplication.core.ChatHandler
import com.example.lawyerapplication.core.ChatUserProfileListener
import com.example.lawyerapplication.core.GroupChatHandler
import com.example.lawyerapplication.databinding.FSingleChatHomeBinding
import com.example.lawyerapplication.db.daos.ChatUserDao
import com.example.lawyerapplication.db.data.ChatUserWithMessages
import com.example.lawyerapplication.db.daos.MessageDao
import com.example.lawyerapplication.models.UserProfile
import com.example.lawyerapplication.activities.SharedViewModel
import com.example.lawyerapplication.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class FSingleChatHome : Fragment(), ItemClickListener {

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var chatUserDao: ChatUserDao

    @Inject
    lateinit var messageDao: MessageDao

    private lateinit var activity: Activity

    private lateinit var profile: UserProfile

    private var chatList = mutableListOf<ChatUserWithMessages>()

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var binding: FSingleChatHomeBinding

    private val viewModel: SingleChatHomeViewModel by viewModels()

    private val adChat: AdSingleChatHome by lazy {
        AdSingleChatHome(requireContext())
    }

    @Inject
    lateinit var chatHandler: ChatHandler

    @Inject
    lateinit var groupChatHandler: GroupChatHandler

    @Inject
    lateinit var chatUsersListener: ChatUserProfileListener

    private lateinit var navController: NavController
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FSingleChatHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity = requireActivity()
        binding.lifecycleOwner = viewLifecycleOwner
        chatHandler.initHandler()
        groupChatHandler.initHandler()
        chatUsersListener.initListener()
        profile = preference.getUserProfile()!!
        setDataInView()
        subScribeObservers()
       /* CoroutineScope(Dispatchers.IO).launch {
              val chatUsers = MApplication.userDaoo.getChatUserList()
              for (user in chatUsers.indices) {
                  Timber.v("ChatUser $user ${chatUsers[user]}")
              }
        }*/
         /*CoroutineScope(Dispatchers.IO).launch {
              chatUserDao.deleteUserById("URJAVFqIRJTZBgAVKFPYoY5QhMg140")
              Toast.makeText(context, "URJAVFqIRJTZBgAVKFPYoY5QhMg14040", Toast.LENGTH_SHORT).show()
          }*/
        //current destination
        /*navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Toast.makeText(context, "destination id "+ destination.navigatorName.toString(), Toast.LENGTH_SHORT).show()

        }*/
        //current destination


    }

    private fun subScribeObservers() {
        lifecycleScope.launch {
            viewModel.getChatUsers().collect { list ->
                for (i in list.indices) {
                    //Log.d("chatUser f", list[i].toString())
                    //Log.d("chatUser f ${i}", list[i].user.toString())
                    Log.d("chatUserMessage ${i}", list[i].messages.toString())
                }
                updateList(list)
            }
        }

        sharedViewModel.getState().observe(viewLifecycleOwner,{state->
            if (state is ScreenState.IdleState){
                CoroutineScope(Dispatchers.IO).launch {
                    //Log.d("chatUserMessage", viewModel.getChatUsersAsList().toString())
                     updateList(viewModel.getChatUsersAsList())

                }
            }
        })
        sharedViewModel.lastQuery.observe(viewLifecycleOwner,{
           if (sharedViewModel.getState().value is ScreenState.SearchState)
                adChat.filter(it)
        })
    }

    private suspend fun updateList(list: List<ChatUserWithMessages>) {
        withContext(Dispatchers.Main){
            val filteredList = list.filter { it.messages.isNotEmpty() }
            if (filteredList.isNotEmpty()) {
                binding.imageEmpty.gone()
                chatList = filteredList as MutableList<ChatUserWithMessages>
                //sort by recent message
                chatList = filteredList.sortedByDescending { it.messages.last().createdAt }
                    .toMutableList()

            /*    for (item in chatList) {
                    Timber.v("chatList ${item}")
                    Timber.v("chatList ${item.user}")
                    Timber.v("chatList ${item.messages}")
                }*/

                AdSingleChatHome.allChatList =chatList
                adChat.submitList(chatList)
                if(sharedViewModel.getState().value is ScreenState.SearchState)
                    adChat.filter(sharedViewModel.lastQuery.value.toString())
            }else
                binding.imageEmpty.show()
        }
    }

    private fun setDataInView() {
        binding.listChat.itemAnimator = null
        binding.listChat.adapter = adChat
       // Timber.v("FSingleChatHome init chat {$adChat}")
        AdSingleChatHome.itemClickListener = this
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Utils.isPermissionOk(*grantResults)){
            if (findNavController().isValidDestination(R.id.FSingleChatHome))
                findNavController().navigate(R.id.action_FSingleChatHome_to_FContacts)
        }
        else
            activity.toast("Требуется разрешение!")
    }

    override fun onItemClicked(v: View, position: Int) {
        sharedViewModel.setState(ScreenState.IdleState)
        val chatUser=adChat.currentList[position]
        preference.setCurrentUser(chatUser.user.id)
        val action= FSingleChatHomeDirections.actionFSingleChatToFChat(chatUser.user)
        findNavController().navigate(action)
    }

    private fun getLeadContactMessage() {
        TODO()
    }



}