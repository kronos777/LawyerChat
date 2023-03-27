package com.example.lawyerapplication.core

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.example.lawyerapplication.db.data.Group
import com.example.lawyerapplication.db.data.GroupMessage

interface OnGrpMessageResponse{
    fun onSuccess(message: GroupMessage)
    fun onFailed(message: GroupMessage)
}

class GroupMsgSender(private val groupCollection: CollectionReference) {

    fun sendMessage(message: GroupMessage, group: Group, listener: OnGrpMessageResponse){
        message.status[0]=1
        groupCollection.document(group.id).collection("group_messages")
            .document(message.createdAt.toString()).set(message, SetOptions.merge())
            .addOnSuccessListener {
                listener.onSuccess(message)
            }.addOnFailureListener {
                message.status[0]=4
                listener.onFailed(message)
            }
    }

}