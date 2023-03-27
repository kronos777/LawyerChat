package com.example.lawyerapplication.core

import com.google.firebase.firestore.FirebaseFirestore
import com.example.lawyerapplication.models.UserProfile
import com.example.lawyerapplication.utils.UserUtils
import timber.log.Timber

interface QueryCompleteListener{
    fun onQueryCompleted(queriedList: ArrayList<UserProfile>)
}

class ContactsQuery(val list: ArrayList<String>,val position: Int,val listener: QueryCompleteListener){

    private val usersCollection = FirebaseFirestore.getInstance().collection("Users")

    fun makeQuery() {
        try {
           // Timber.v("Query contact: {${usersCollection.toString()}}")
           // Timber.v("Contacts contact ${list}")
            val correctList = getCorrectList(list)
            Timber.v("Contacts contact1 ${correctList}")
            usersCollection.whereIn("mobile.number", correctList).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val contact = document.toObject(UserProfile::class.java)
                        //Timber.v("Contacts contact ${contact}")
                        UserUtils.queriedList.add(contact)
                    }
                    UserUtils.resultCount += 1
                    if(UserUtils.resultCount == UserUtils.totalRecursionCount){
                        listener.onQueryCompleted(UserUtils.queriedList)
                        Timber.v("Contacts all ${UserUtils.queriedList}")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.wtf("Error getting documents: ${exception.message}")
                    Timber.v("Error getting documents: ${exception.message}")
                    UserUtils.resultCount += 1
                    if(UserUtils.resultCount == UserUtils.totalRecursionCount)
                        listener.onQueryCompleted(UserUtils.queriedList)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCorrectList(listNumber: ArrayList<String>) : ArrayList<String> {
        val retList : ArrayList<String> = ArrayList<String>()
        for (number in listNumber) {
            val noTire = number.replace("-", "")
            val noTire1 = noTire.replace("(", "")
            val noTire2 = noTire1.replace(")", "")
            if(noTire2.length >= 11) {
                retList.add(noTire2.drop(1))
                //val tval = noTire2.drop(1)
               // val tval2 = noTire2.length
                //Timber.v("new1 contact ${tval}")
               // Timber.v("new1 contact ${tval2}")
            } else if (noTire2.length == 10) {
                retList.add(noTire2)
            }
        }

        return retList
    }


}