package com.example.lawyerapplication.fragments.search

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.models.UserProfile
import com.example.lawyerapplication.utils.Constants
import com.example.lawyerapplication.utils.DataStorePreference
import com.example.lawyerapplication.utils.LoadState
import com.example.lawyerapplication.utils.MPreference
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepo  @Inject constructor(
    private val usersCollection: CollectionReference, private val dataStore: DataStorePreference,
    private val preference: MPreference
){

    fun makeQuery(query: String, loadState: MutableLiveData<LoadState>) {
        try {
            loadState.value= LoadState.OnLoading
            usersCollection.whereEqualTo("userName", query).get()
                .addOnSuccessListener { documents ->
                    val list= arrayListOf<ChatUser>()
                    for (document in documents) {
                        val profile = document.toObject(UserProfile::class.java)
                        if (profile.uId==preference.getUid())
                            continue
                        val chatUser=
                            ChatUser(profile.uId.toString(),profile.userName,profile,locallySaved = false,
                            isSearchedUser = true)
                        list.add(chatUser)
                    }
                    loadState.value= LoadState.OnSuccess(list)
                    dataStore.storeList(Constants.KEY_LAST_QUERIED_LIST,list)
                }
                .addOnFailureListener { exception ->
                    loadState.value= LoadState.OnFailure(exception)
                    Timber.wtf("Error getting documents: ${exception.message}")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}