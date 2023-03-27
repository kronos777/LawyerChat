package com.example.lawyerapplication.db.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.lawyerapplication.db.daos.ChatUserDao
import com.google.common.truth.Truth.assertThat
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.models.UserProfile
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class ChatUserDaoTest {

    @get:Rule
    var hiltRule=HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule=InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: ChatUserDatabase
    private lateinit var chatUserDao: ChatUserDao

    @Before
    fun setUp(){
        hiltRule.inject()
        chatUserDao=database.getChatUserDao()
    }


    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insert_ChatUser() = runBlockingTest {
         val chatUser=
             ChatUser("testUser1","Gowtham", UserProfile("testUser1",13232113L,123321321L),)
         chatUserDao.insertUser(chatUser)
         val chatUsers=chatUserDao.getChatUserList()
         assertThat(chatUsers).contains(chatUser)
    }

    @Test
    fun get_ChatUser_ById() = runBlockingTest {
        val user= ChatUser("testId","Gowtham", UserProfile("testId",13232113L,123321321L),)
        chatUserDao.insertUser(user)
        val chatUser=chatUserDao.getChatUserById("testId")
        assertThat(chatUser).isNotNull()
    }

    @Test
    fun delete_User_ById() = runBlockingTest {
        val user= ChatUser("testDeleteUserId","Gowtham", UserProfile("testDeleteUserId",13232113L,123321321L),)
        chatUserDao.insertUser(user)
        chatUserDao.deleteUserById("testDeleteUserId")
        val chatUsers=chatUserDao.getChatUserList()
        assertThat(chatUsers).doesNotContain(user)
    }


}