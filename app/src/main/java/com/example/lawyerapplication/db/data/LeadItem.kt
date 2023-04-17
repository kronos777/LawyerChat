package com.example.lawyerapplication.db.data


data class LeadItem(
    val firstText: String,
    val twoText: String,
    val freeText: String,
    val fourText: String,
    val fiveText: String,
    val sixText: String,
    val sevenText: String,
    val eightText: String,
    val nineText: String,
    val tenText: String,
    val messageLead: String,
    val idClient: String,
    val idLawyer: String,
    val category: String,
    val status: String,
    val dateTime: String,
    val paymentInfo: String,
    val id: Int = UNDEFINED_ID
){
    companion object {
        const val UNDEFINED_ID = 0
    }
}