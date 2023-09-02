package com.example.lawyerapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lawyerapplication.db.daos.ChatUserDao
import com.example.lawyerapplication.db.daos.GroupDao
import com.example.lawyerapplication.db.daos.GroupMessageDao
import com.example.lawyerapplication.db.daos.MessageDao
import com.example.lawyerapplication.db.daos.StageDao
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Group
import com.example.lawyerapplication.db.data.GroupMessage
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.db.data.StageBussinesLocal

@Database(entities = [ChatUser::class, Message::class, Group::class, GroupMessage::class, StageBussinesLocal::class],
    version = 5, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class ChatUserDatabase : RoomDatabase()  {
    abstract fun getChatUserDao(): ChatUserDao
    abstract fun getMessageDao(): MessageDao
    abstract fun getGroupDao(): GroupDao
    abstract fun getGroupMessageDao(): GroupMessageDao
    abstract fun getStageDao(): StageDao
}