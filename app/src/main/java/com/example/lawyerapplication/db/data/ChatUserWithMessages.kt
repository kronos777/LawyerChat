package com.example.lawyerapplication.db.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation

@kotlinx.parcelize.Parcelize
class ChatUserWithMessages(
    @Embedded
    val user: ChatUser,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatUserId"
    )
    val messages: List<Message>) : Parcelable