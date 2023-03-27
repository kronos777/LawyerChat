package com.example.lawyerapplication.models

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@IgnoreExtraProperties
@Parcelize
data class UserProfile(var uId: String?=null, var createdAt: Long?=null,
                       var updatedAt: Long?=null,
                       var image: String="", var userName: String="",
                       var about: String="",
                       var token :String="",
                       /*m dt*/
                       var password: String="",
                       var role: String="",
                       var lastName: String="",
                       var serName: String="",
                       //var patronymic: String="",
                       var email: String="",
                       //var passportData: List<String>,
                       //var diplomOfHigherEducation: List<String>,

                        /*m dt*/
                       var mobile: ModelMobile?=null,
                       @get:PropertyName("device_details")
                       @set:PropertyName("device_details")
                       var deviceDetails: ModelDeviceDetails?=null) : Parcelable