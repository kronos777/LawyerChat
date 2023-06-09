package com.example.lawyerapplication.core

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.utils.LogMessage
import timber.log.Timber


class LeadMessageSender(private val msgCollection: CollectionReference,
                    private val dbRepo: DbRepository, private val chatUser: ChatUser,
                    private val listener: OnMessageResponse
) {

    fun checkAndSend(fromUser: String, toUser: String, message: Message, idLead: String) {
        val docId = chatUser.documentId
        if (!docId.isNullOrEmpty()){
            Timber.v("Case 0 ${chatUser.documentId}")
            send(docId, message)
       } else {
            //so we don't create multiple nodes for same chat
            msgCollection.document("${fromUser}_${toUser}_${idLead}").get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        //this node exists send your message
                        Timber.v("Case 1")
                        send("${fromUser}_${toUser}_${idLead}", message)
                    } else {
                        //senderId_receiverId node doesn't exist check receiverId_senderId
                        msgCollection.document("${fromUser}_${toUser}_${idLead}").get()
                            .addOnSuccessListener { documentSnapshot2 ->
                                if (documentSnapshot2.exists()) {
                                    Timber.v("Case 2")
                                    send("${fromUser}_${toUser}_${idLead}", message)
                                } else {
                                    //no previous chat history(senderId_receiverId & receiverId_senderId both don't exist)
                                    //so we create document senderId_receiverId then messages array then add messageMap to messages
                                    //this node exists send your message
                                    //add ids of chat members
                                    Timber.v("Case 3")
                                    msgCollection.document("${fromUser}_${toUser}_${idLead}")
                                        .set(mapOf("chat_members" to FieldValue.arrayUnion(fromUser, toUser)),
                                            SetOptions.merge()
                                        ).addOnSuccessListener {
                                            LogMessage.v("chat member update successfully")
                                            send("${fromUser}_${toUser}_${idLead}", message)
                                        }.addOnFailureListener {
                                            LogMessage.v("chat member update failed ${it.message}")
                                        }
                                }
                            }
                    }
                }
        }
    }

    private fun send(doc: String, message: Message){
        try {
            chatUser.documentId=doc
            dbRepo.insertUser(chatUser)
            val chatUserId=message.chatUserId
            message.chatUserId=null  //chatUserId field is being used only for relation query,changing to null will ignore this field
            message.status=1
            message.chatUsers= arrayListOf(message.from,message.to)
            msgCollection.document(doc).collection("messages").document(message.createdAt.toString()).set(
                message,
                SetOptions.merge()
            ).addOnSuccessListener {
                    LogMessage.v("Message sender Sucesss ${message.createdAt}")
                    message.chatUserId=chatUserId
                    listener.onSuccess(message)
                }.addOnFailureListener {
                    message.chatUserId=chatUserId
                    message.status=4
                LogMessage.v("Message sender Failed ${it.message}")
                    listener.onFailed(message)
                }
  /*          msgCollection.document(doc)
                .update("messages",
                    FieldValue.arrayUnion(message.serializeToMap())).addOnSuccessListener {
                    LogMessage.v("Message sender Sucesss ${message.textMessage?.text}")
                    listener.onSuccess(message)
                }.addOnFailureListener {
                    message.status=4
                    LogMessage.v("Message sender Failed ${it.message}")
                    listener.onFailed(message)
                }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun checkAndSendId(fromUser: String, toUser: String, message: Message, idLead: String) {
        val docId = chatUser.documentId
        if (!docId.isNullOrEmpty()){
            Timber.v("Case 0 ${chatUser.documentId}")
            send(docId, message)
        } else {
            //so we don't create multiple nodes for same chat
            msgCollection.document(idLead).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        //this node exists send your message
                        Timber.v("Case 1")
                        send(idLead, message)
                    } else {
                        //senderId_receiverId node doesn't exist check receiverId_senderId
                        msgCollection.document(idLead).get()
                            .addOnSuccessListener { documentSnapshot2 ->
                                if (documentSnapshot2.exists()) {
                                    Timber.v("Case 2")
                                    send((idLead), message)
                                } else {
                                    //no previous chat history(senderId_receiverId & receiverId_senderId both don't exist)
                                    //so we create document senderId_receiverId then messages array then add messageMap to messages
                                    //this node exists send your message
                                    //add ids of chat members
                                    Timber.v("Case 3")
                                    msgCollection.document(idLead)
                                        .set(mapOf("chat_members" to FieldValue.arrayUnion(fromUser, toUser)),
                                            SetOptions.merge()
                                        ).addOnSuccessListener {
                                            LogMessage.v("chat member update successfully")
                                            send(idLead, message)
                                        }.addOnFailureListener {
                                            LogMessage.v("chat member update failed ${it.message}")
                                        }
                                }
                            }
                    }
                }
        }
    }

}