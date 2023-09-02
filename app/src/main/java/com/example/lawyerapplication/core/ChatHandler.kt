package com.example.lawyerapplication.core

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.example.lawyerapplication.FirebasePush
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.fragments.single_chat.toDataClass
import com.example.lawyerapplication.models.UserProfile
import com.example.lawyerapplication.utils.LogMessage
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.UserUtils
import com.example.lawyerapplication.utils.getUnreadCount
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.Collections.addAll
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dbRepository: DbRepository,
    private val usersCollection: CollectionReference,
    private val preference: MPreference,
    private val messageStatusUpdater: MessageStatusUpdater
) {

    private val messagesList: MutableList<Message> by lazy { mutableListOf() }

    private var fromUser = preference.getUid()

    val message = MutableLiveData<String>()

    private lateinit var chatUsers: List<ChatUser>

    private lateinit var newChatUsers: ArrayList<ChatUser>

    private val listOfDocs = ArrayList<String>()

    private lateinit var messageCollectionGroup: Query

    private val chatUserUtil = ChatUserUtil(dbRepository, usersCollection, null)

    private var isFirstQuery = false

    companion object {

        private var listenerDoc1: ListenerRegistration? = null
        private var instanceCreated = false

        fun removeListeners() {
            instanceCreated = false
            listenerDoc1?.remove()
        }
    }

    fun initHandler() {
        if (instanceCreated)
            return
        instanceCreated = true
        fromUser = preference.getUid()
        //Timber.v("ChatHandler init")
        Timber.v("ChatHandler init {$fromUser}")
       /* CoroutineScope(Dispatchers.IO).launch {
            chatUsers = dbRepository.getChatUserList()
            Timber.v("ChatHandler chatUsers {$chatUsers}")
        }*/
        messageCollectionGroup = UserUtils.getMessageSubCollectionRef()
        preference.clearCurrentUser()
        //checkExistsChatUser()
      /*  Timber.v("ChatHandler init msgs collection{$messageCollectionGroup}")
        Timber.v("ChatHandler init msgs collection{$fromUser}")*/
       /* CoroutineScope(Dispatchers.IO).launch {
            dbRepository.getChatUserWithMessages().collect {
                //Timber.v("getChatUserWithMessages collection ${it[index].user}")
                for (index in it.indices) {
                    Timber.v("getChatUserWithMessages user ${it[index].user}")
                    Timber.v("getChatUserWithMessages messages ${it[index].messages}")
                }

            }
        }*/


       // fromUser = "06v9Upo82fOvZ9iSsesBQykQMh13"
        listenerDoc1 = messageCollectionGroup//.whereArrayContains("chatUsers", fromUser!!)
            .addSnapshotListener { snapShots, error ->
                /* if (error != null || snapShots == null || snapShots.metadata.isFromCache) {
                      LogMessage.v("Error cache${snapShots?.metadata?.isFromCache}")
                      LogMessage.v("Error dcache${snapShots}")

                      if(snapShots?.metadata?.isFromCache == true)
                          Timber.v("ChatHandler init fetch document{$snapShots}")
                          onFetchDocuments()
                      return@addSnapshotListener
                  }else {
                      onSnapShotChanged(snapShots)
                  }*/
                   snapShots?.let { onSnapShotChanged(it) }

            }
    }

    private fun onFetchDocuments() {
        Timber.v("ChatHandler onFetchDocuments")
        Timber.v("ChatHandler onFetchDocuments fromUser {$fromUser}")
       // fromUser = "06v9Upo82fOvZ9iSsesBQykQMh13"
       val msgs = messageCollectionGroup.whereArrayContains("chatUsers", fromUser!!).get().addOnSuccessListener {
             isFirstQuery=true
            Timber.v("ChatHandler init it {$it}")
            onSnapShotChanged(it)
        }
        Timber.v("ChatHandler onFetchDocuments {$msgs}")

    }

    fun dropElementInNewUsers(uid: String) {
        TODO()
    }

    private fun checkExistsChatUser() {
       // dbRepository.deleteUserById("7hnvdormFcO5U2ReaD9pxuVKo0D3")
        CoroutineScope(Dispatchers.IO).launch {

            val newListOfIds = ArrayList<String>()
            val newListOfIdsHash = HashSet<String>()
            val chatUsers: List<ChatUser> = dbRepository.getChatUserList()
            val filteredChatUsers = mutableListOf<ChatUser>().apply { addAll(newChatUsers) }
            //Timber.v("allChatUsersExists ${chatUsers}")

            if(newChatUsers.size > 0) {
                if (chatUsers.isNotEmpty()) {
                    /*for (i in chatUsers.indices) {
                        val chUsersLocal = chatUsers[i]
                        Timber.v("ChatHandlerChatUserLocal exist ${chUsersLocal.id}")
                         listOfIds.let {
                             for (usId in it) {
                                 if (chUsersLocal.id.substring(28).equals(usId.substring(28))) {
                                     Timber.v("ChatHandlerChatUserLocal exist ${usId}")
                                 } else {
                                     Timber.v("ChatHandlerChatUserLocal no exist ${usId}")
                                 }
                             }
                         }
                    }*/
                    var c = 0
                    Timber.v("ChatHandlerChatUserLocal final before newchatUser  ${newChatUsers}")
                    do {
                        //println(c)
                        //for (c in newChatUsers.indices) {
                            val chatUserId = newChatUsers[c].id
                            Timber.v("ChatHandlerChatUserFireStore exist ${chatUserId}")
                            for (i in chatUsers.indices) {
                                if((chatUserId.substring(28).equals(chatUsers[i].id.substring(28))) || (chatUserId.equals(chatUsers[i].id))) {
                                    Timber.v("ChatHandlerChatUserLocal exist ${chatUserId}  local ${chatUsers[i].id} ")
                                    filteredChatUsers.remove(newChatUsers[c])
                                } /*else {
                                     Timber.v("ChatHandlerChatUserLocal no exist ${chatUserId}  local  ${chatUsers[i].id}")
                                }*/
                            }
                       // }
                        c++
                    } while (c < newChatUsers.size)
                    Timber.v("ChatHandlerChatUserLocal final after newchatUser  ${filteredChatUsers}")
                    dbRepository.insertMultipleUsers(filteredChatUsers as java.util.ArrayList<ChatUser>)
                } else {
                    Timber.v("Local chatUser count zero ${newChatUsers}")
                    //add all users
                    dbRepository.insertMultipleUsers(newChatUsers)
                }

            } else {
                Timber.v("new chat user size zero")
            }

        }
    }

    private fun onSnapShotChanged(snapShots: QuerySnapshot) {
        messagesList.clear()
        listOfDocs.clear()
        val listOfIds = ArrayList<String>()
        newChatUsers = mutableListOf<ChatUser>() as ArrayList<ChatUser>
        Timber.v("ChatHandler init 1 {$snapShots}")
        if (isFirstQuery) {
            snapShots.forEach { doc ->
                val parentDoc = doc.reference.parent.parent?.id!!
                Timber.v("ChatHandler parentDoc {$parentDoc}")
                val message = doc.data.toDataClass<Message>()
                Timber.v("ChatHandler message {$message}")
                message.chatUserId =
                    if (message.from != fromUser) message.from else message.to
                messagesList.add(message)
                //checkExistsChatUser(message.chatUserId!!)
               // Timber.v("ChatHandler init msgs fquery {$message}")
                if (!listOfDocs.contains(parentDoc)) {
                    listOfDocs.add(doc.reference.parent.parent?.id.toString())
                    listOfIds.add(message.chatUserId!!)
                    //create send lead info
                    val toLead = if (fromUser == message.to!!.substring(0, 28)) message.from.substring(0, 28) else message.to.substring(0, 28)
                    newChatUsers.add(ChatUser(
                        id= toLead + doc.reference.parent.parent?.id.toString(),localName = "Дело ${doc.reference.parent.parent?.id.toString()}",user = UserProfile(toLead + doc.reference.parent.parent?.id.toString(),13232113L,123321321L, "", "Дело №" + doc.reference.parent.parent?.id.toString()),
                        documentId = doc.reference.parent.parent?.id.toString()))
                }
            }
            isFirstQuery=false
        } else {
            for (shot in snapShots.documentChanges) {
                if (shot.type == DocumentChange.Type.ADDED ||
                    shot.type == DocumentChange.Type.MODIFIED
                ) {
                    val document = shot.document
                    val parentDoc = document.reference.parent.parent?.id!!
                    Timber.v("ChatHandler parentDoc {$parentDoc}")
                    val message = document.data.toDataClass<Message>()

                    message.chatUserId =
                        if (message.from != fromUser) message.from else message.to
                    Timber.v("message firestore {$message}")
                  //  checkExistsChatUser(message.chatUserId!!)
                    messagesList.add(message)
                    if (!listOfDocs.contains(parentDoc)) {
                        listOfDocs.add(document.reference.parent.parent?.id.toString())
                        listOfIds.add(message.chatUserId!!)
                        val toLead = if (fromUser == message.to!!.substring(0, 28)) message.from.substring(0, 28) else message.to.substring(0, 28)
                        newChatUsers.add(ChatUser(
                            id= toLead + document.reference.parent.parent?.id.toString(),localName = "Дело ${document.reference.parent.parent?.id.toString()}",user = UserProfile(toLead + document.reference.parent.parent?.id.toString(),13232113L,123321321L, "", "Дело №" + document.reference.parent.parent?.id.toString()),
                            documentId = document.reference.parent.parent?.id.toString()))
                    }
                }
            }
        }
        Timber.v("listOfDocs {$listOfDocs}")
        Timber.v("listOfIds {$listOfIds}")
        Timber.v("listOfChatUsers {$newChatUsers}")

        checkExistsChatUser()
        if (!messagesList.isNullOrEmpty())
            Timber.v("insertMessageOnDb {$listOfIds}")
            insertMessageOnDb(listOfIds)
    }

    private fun insertMessageOnDb(listOfIds: ArrayList<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = ArrayList<ChatUser>()
            val newContactIds =
                ArrayList<String>()  //message from new user not saved in localdb yet
            chatUsers = dbRepository.getChatUserList()
            dbRepository.insertMultipleMessage(messagesList)
            for ((index, doc) in listOfDocs.withIndex()) {
                val chatUser = chatUsers.firstOrNull { it.id == listOfIds[index] }
                if (chatUser == null) {
                    newContactIds.add(listOfIds[index])
                    Timber.v("addNewContacts ${listOfIds[index]}")
                    //message from unsaved user
                } else {
                    chatUser.unRead = if (preference.getOnlineUser() == chatUser.id) 0 else
                        dbRepository.getChatsOfFriend(chatUser.id).getUnreadCount(chatUser.id)
                    chatUser.documentId = doc
                    Timber.v("UserId ${chatUser.id}  count ${chatUser.unRead}")
                    contacts.add(chatUser)
                }
            }
            dbRepository.insertMultipleUsers(contacts)
            val currentChatUser = if (preference.getOnlineUser().isNotEmpty())
                contacts.firstOrNull { it.id == preference.getOnlineUser() }
            else null
            val allUnReadMsgs = dbRepository.getAllNonSeenMessage()
            withContext(Dispatchers.Main) {
                updateMsgStatus(newContactIds, currentChatUser, allUnReadMsgs)
            }
        }

    }

    private fun updateMsgStatus(
        newContactIds: ArrayList<String>,
        currentChatUser: ChatUser?,
        allUnReadMsgs: List<Message>
    ) {
        showNotification(newContactIds)
        if (currentChatUser != null) {
            val currentUserMsgs = allUnReadMsgs.filter {
                it.chatUserId == currentChatUser.id
            }
            val otherUserMsgs = allUnReadMsgs.filter {
                it.chatUserId != currentChatUser.id
            }
            messageStatusUpdater.updateToDelivery(otherUserMsgs, *chatUsers.toTypedArray())
            messageStatusUpdater.updateToSeen(
                currentChatUser.id, currentChatUser.documentId!!, currentUserMsgs
            )
        } else {
            messageStatusUpdater.updateToDelivery(allUnReadMsgs, *chatUsers.toTypedArray())
        }
    }

    private fun showNotification(
        newContactIds: ArrayList<String>
    ) {
        if (newContactIds.isEmpty()) {
            val lastMsgId = messagesList.maxOf { it.createdAt }
            val msg = messagesList.find { it.createdAt == lastMsgId }
            if (msg != null && msg.from != fromUser)
                FirebasePush.showNotification(context, dbRepository)
        } else {
            //unsaved new user
            for (i in 0 until newContactIds.size) {
                val userId = newContactIds[i]
                if (userId == preference.getOnlineUser())
                    continue

                val unreadCount = messagesList.getUnreadCount(userId)
                chatUserUtil.queryNewUserProfile(
                    context,
                    userId,
                    listOfDocs.firstOrNull { it.contains(userId) }, unreadCount,
                    showNotification = i == newContactIds.lastIndex
                )
            }
        }
    }
}