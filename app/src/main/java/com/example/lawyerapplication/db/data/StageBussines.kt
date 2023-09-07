package com.example.lawyerapplication.db.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

data class StageBussines(
    val id: Int,
    val idBussines: Int,
    val title: String,
    val description: String,
    val dateTime: String,
    var status: Int=0,
)