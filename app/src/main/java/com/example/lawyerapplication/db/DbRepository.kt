package com.example.lawyerapplication.db

import com.example.lawyerapplication.db.daos.ChatUserDao
import com.example.lawyerapplication.db.daos.GroupDao
import com.example.lawyerapplication.db.daos.GroupMessageDao
import com.example.lawyerapplication.db.daos.MessageDao
import com.example.lawyerapplication.db.daos.StageDao
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Group
import com.example.lawyerapplication.db.data.GroupMessage
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.db.data.StageBussinesLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbRepository @Inject constructor(
    private val userDao: ChatUserDao,
    private val groupDao: GroupDao,
    private val groupMsgDao: GroupMessageDao,
    private val messageDao: MessageDao,
    private val stageDao: StageDao
) : DefaultDbRepo {

    override fun insertUser(user: ChatUser) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertUser(user)
        }
    }

    fun insertStage(stageBussinesLocal: StageBussinesLocal) {
        CoroutineScope(Dispatchers.IO).launch {
            if(!stageDao.ifExists(stageBussinesLocal.fireBaseId, stageBussinesLocal.idBussines)) {
                stageDao.insertStage(stageBussinesLocal)
            }

        }
    }

    fun updateStage(stageBussinesLocal: StageBussinesLocal) {
        CoroutineScope(Dispatchers.IO).launch {
                stageDao.insertStage(stageBussinesLocal)
        }
    }

    fun deleteItemStage(fireBaseId: Int, idBussines: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            stageDao.deleteStagesByStageIdBussinesId(fireBaseId, idBussines)
        }
    }

    fun deleteTableStage() = stageDao.nukeTable()
    fun getAllStages() = stageDao.getAllStages()

    fun deleteAllStages() = stageDao.nukeTable()

    fun getStageById(fireBaseId: Int, idBussines: Int) = stageDao.getStageById(fireBaseId, idBussines)

    override fun insertMultipleUser(users: List<ChatUser>) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertMultipleUser(users)
        }
    }

    override fun getChatUserWithMessages() = userDao.getChatUserWithMessages()

    override fun getChatUserList() = userDao.getChatUserList()

    override fun getChatUserWithMessagesList() = userDao.getChatUserWithMessagesList()

    override fun getChatUserById(id: String) = userDao.getChatUserById(id)

    override fun getAllChatUser() = userDao.getAllChatUser()

    override fun nukeTable() {
    }

    override fun deleteUserById(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.deleteUserById(userId)
        }
    }

    fun insertMultipleUser(finalList: ArrayList<ChatUser>) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertMultipleUser(finalList)
        }
    }

    suspend fun insertMultipleUsers(users: ArrayList<ChatUser>){
       userDao.insertMultipleUser(users)
   }

    fun insertGroup(group: Group) {
        CoroutineScope(Dispatchers.IO).launch {
            groupDao.insertGroup(group)
        }
    }

    suspend fun insertMultipleMessage(messagesList: MutableList<Message>) =
            messageDao.insertMultipleMessage(messagesList)


    suspend fun insertMultipleGroupMessage(messagesList: List<GroupMessage>) =
            groupMsgDao.insertMultipleMessage(messagesList)

    fun getAllNonSeenMessage() =
        messageDao.getAllNotSeenMessages()

    fun insertMessage(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            messageDao.insertMessage(message)
        }
    }

    fun insertMessage(message: GroupMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            groupMsgDao.insertMessage(message)
        }
    }

   suspend fun insertMultipleGroup(groups: List<Group>) =
            groupDao.insertMultipleGroup(groups)


    fun getGroupWithMessages() = groupDao.getGroupWithMessages()

    fun getMessagesByChatUserId(chatUserId: String) = messageDao.getMessagesByChatUserId(chatUserId)

    fun getMessagesByChatUserIdForLead(chatUserId: String, chatUserIdForLead: String) = messageDao.getMessagesByChatUserIdForLead(chatUserId, chatUserIdForLead)

    fun getChatsOfFriend(toUser: String) = messageDao.getChatsOfFriend(toUser)

    fun getGroupById(groupId: String) = groupDao.getGroupById(groupId)

    fun getChatsOfGroupList(groupId: String) = groupMsgDao.getChatsOfGroupList(groupId)

    fun getChatsOfGroup(groupId: String) = groupMsgDao.getChatsOfGroup(groupId)

    fun getGroupWithMessagesList() = groupDao.getGroupWithMessagesList()

    fun getMessageList() = messageDao.getMessageList()

    fun getGroupList() = groupDao.getGroupList()
}