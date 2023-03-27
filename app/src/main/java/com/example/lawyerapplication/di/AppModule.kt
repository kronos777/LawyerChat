package com.example.lawyerapplication.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.DefaultDbRepo
import com.example.lawyerapplication.db.daos.ChatUserDao
import com.example.lawyerapplication.db.daos.GroupDao
import com.example.lawyerapplication.db.daos.GroupMessageDao
import com.example.lawyerapplication.db.daos.MessageDao
import com.example.lawyerapplication.activities.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MessageCollection

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GroupCollection

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFireStoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideUsersCollectionRef(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection("Users")
    }

    @MessageCollection
    @Singleton
    @Provides
    fun provideMessagesCollectionRef(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection("Messages")
    }

    @GroupCollection
    @Singleton
    @Provides
    fun provideGroupCollectionRef(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection("Groups")
    }

    @Provides
    fun provideMainActivity(): MainActivity {
        return MainActivity()
    }

    @Provides
    fun provideDefaultDbRepo(userDao: ChatUserDao,
                             groupDao: GroupDao,
                             groupMsgDao: GroupMessageDao,
                             messageDao: MessageDao
    ): DefaultDbRepo {
        return DbRepository(userDao, groupDao, groupMsgDao, messageDao)
    }

}