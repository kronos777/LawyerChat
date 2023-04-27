package com.example.lawyerapplication.fragments.myprofile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.utils.LoadState
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.UserUtils
import com.example.lawyerapplication.utils.toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FMyProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preference: MPreference
) : ViewModel() {

    private var userProfile = preference.getUserProfile()

    val userName = MutableLiveData(firstUpperCase(userProfile?.userName.toString()))
    val userSerName = MutableLiveData(firstUpperCase(userProfile?.serName.toString()))
    val userLastName = MutableLiveData(firstUpperCase(userProfile?.lastName.toString()))

    val imageUrl = MutableLiveData(userProfile?.image)

    val about = MutableLiveData(userProfile?.about)
    val email = userProfile?.email
    //val email2 = MutableLiveData(userProfile?.email)
    val password = userProfile?.password

    val isUploading = MutableLiveData(false)

    private val mobileData = userProfile?.mobile

    private val storageRef = UserUtils.getStorageRef(context)

    private val docuRef = UserUtils.getDocumentRef(context)

    val mobile = MutableLiveData("${mobileData?.country} ${mobileData?.number}")

    val profileUpdateState = MutableLiveData<LoadState>()

    private lateinit var uploadTask: UploadTask


    init {
        Timber.v("FMyProfileViewModel init")
    }

    fun uploadProfileImage(imagePath: Uri) {
        try {
            isUploading.value = true
            val child = storageRef.child("profile_picture_${System.currentTimeMillis()}.jpg")
            if (this::uploadTask.isInitialized && uploadTask.isInProgress)
                uploadTask.cancel()
            uploadTask = child.putFile(imagePath)
            uploadTask.addOnSuccessListener {
                child.downloadUrl.addOnCompleteListener { taskResult ->
                    isUploading.value = false
                    imageUrl.value = taskResult.result.toString()
                    context.toast("this new pic url "  + taskResult.result.toString())
                    updateProfileImgData(taskResult.result.toString())
                }.addOnFailureListener {
                    OnFailureListener { e ->
                        isUploading.value = false
                        context.toast(e.message.toString())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveChanges(name: String, strAbout: String, image: String) {
        name.toLowerCase(Locale.getDefault())
        updateProfileData(name, strAbout, image)
    }

    private fun updateProfileData(name: String, strAbout: String, image: String) {
        try {
            profileUpdateState.value = LoadState.OnLoading
            val profile = userProfile!!
            profile.userName = name
            profile.about = strAbout
            profile.image = image
            profile.updatedAt = System.currentTimeMillis()
            docuRef.set(profile, SetOptions.merge()).addOnSuccessListener {
                context.toast("Profile updated!")
                userProfile = profile
                preference.saveProfile(profile)
                profileUpdateState.value = LoadState.OnSuccess()
            }.addOnFailureListener { e ->
                context.toast(e.message.toString())
                profileUpdateState.value = LoadState.OnFailure(e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateProfileEmailData(email: String) {
        try {
            profileUpdateState.value = LoadState.OnLoading
            val profile = userProfile!!
            profile.email = email
            profile.updatedAt = System.currentTimeMillis()
            docuRef.set(profile, SetOptions.merge()).addOnSuccessListener {
                context.toast("Profile updated!")
                userProfile = profile
                preference.saveProfile(profile)
                profileUpdateState.value = LoadState.OnSuccess()
            }.addOnFailureListener { e ->
                context.toast(e.message.toString())
                profileUpdateState.value = LoadState.OnFailure(e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateProfileImgData(img: String) {
        try {
            profileUpdateState.value = LoadState.OnLoading
            val profile = userProfile!!
            profile.image = img
            profile.updatedAt = System.currentTimeMillis()
            docuRef.set(profile, SetOptions.merge()).addOnSuccessListener {
                context.toast("Profile updated!")
                userProfile = profile
                preference.saveProfile(profile)
                profileUpdateState.value = LoadState.OnSuccess()
            }.addOnFailureListener { e ->
                context.toast(e.message.toString())
                profileUpdateState.value = LoadState.OnFailure(e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateProfilePasswordData(password: String) {
        try {
            profileUpdateState.value = LoadState.OnLoading
            val profile = userProfile!!
            profile.password = password
            profile.updatedAt = System.currentTimeMillis()
            docuRef.set(profile, SetOptions.merge()).addOnSuccessListener {
                context.toast("Profile updated!")
                userProfile = profile
                preference.saveProfile(profile)
                profileUpdateState.value = LoadState.OnSuccess()
            }.addOnFailureListener { e ->
                context.toast(e.message.toString())
                profileUpdateState.value = LoadState.OnFailure(e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun firstUpperCase(word: String?): String? {
        return if (word == null || word.isEmpty()) "" else word.substring(0, 1)
            .uppercase(Locale.getDefault()) + word.substring(1) //или return word;
    }


    override fun onCleared() {
        super.onCleared()
        if (this::uploadTask.isInitialized && uploadTask.isInProgress)
            uploadTask.cancel()
    }

}
