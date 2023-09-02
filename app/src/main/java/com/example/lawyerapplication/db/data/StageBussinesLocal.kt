package com.example.lawyerapplication.db.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
@kotlinx.parcelize.Parcelize
@Entity

data class StageBussinesLocal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fireBaseId: Int,
    val idBussines: Int,
    val title: String,
    val description: String,
    val dateTime: String,
    val status: Int=0,
): Parcelable