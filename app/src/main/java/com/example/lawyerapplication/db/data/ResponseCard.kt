package com.example.lawyerapplication.db.data

data class ResponseCard(
    var products: List<CardItem>? = null,
    var exception: Exception? = null
)
