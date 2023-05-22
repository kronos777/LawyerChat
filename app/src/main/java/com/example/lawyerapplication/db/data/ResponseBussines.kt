package com.example.lawyerapplication.db.data

data class ResponseBussines(
    var products: List<BusinessItem>? = null,
    var exception: Exception? = null
)