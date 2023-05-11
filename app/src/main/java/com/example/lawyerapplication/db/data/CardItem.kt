package com.example.lawyerapplication.db.data


data class CardItem(
    val number: String,
    val validity: String,
    val cvs: String,
    val id: Int = UNDEFINED_ID
){
    companion object {
        const val UNDEFINED_ID = 0
    }
}