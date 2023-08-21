package com.example.lawyerapplication.db.data

data class StageBussines(
    val id: Int,
    val idBussines: Int,
    val title: String,
    val description: String,
    val dateTime: String,
    val status: Int=0,//0=sending,1=sent,2=delivered,3=seen,4=failed
)