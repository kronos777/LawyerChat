package com.example.lawyerapplication.fragments.mycards

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.data.CardItem
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.db.data.ResponseCard
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.UserUtils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@HiltViewModel

class ViewModelMyCards @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val preference: MPreference
) : ViewModel() {


    private val _errorInputCardNumber = MutableLiveData<Boolean>()
    val errorInputCardNumber: LiveData<Boolean>
        get() = _errorInputCardNumber

    private val _errorInputValidity = MutableLiveData<Boolean>()
    val errorInputValidity: LiveData<Boolean>
        get() = _errorInputValidity


    private val _errorInputCvs = MutableLiveData<Boolean>()
    val errorInputCvs: LiveData<Boolean>
        get() = _errorInputCvs



    fun resetErrorInputCardNumber() {
        _errorInputCardNumber.value = false
    }

    fun resetErrorInputValidity() {
        _errorInputValidity.value = false
    }


    fun resetErrorInputCvs() {
        _errorInputCvs.value = false
    }

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen


    fun addUserCard(numberCard: String, validity: String, cvs: String): String {
        val fieldsValid = validateInput(numberCard, validity, cvs)
        val uId = preference.getUid()
        if (fieldsValid) {
            addCardDbCard(numberCard, validity, cvs)
            return "ok" + uId
        } else {
            return "error" + uId
        }
    }

    private fun validateInput(numberCard: String, validity: String, cvs: String): Boolean {
        var result = true


        if (!cvs.isDigitsOnly()) {
            Log.d("numberCard", numberCard)
            Log.d("numberCard", numberCard.length.toString())
            Log.d("validity", validity)
            Log.d("validity", validity.length.toString())
            Log.d("cvs", cvs)
            Log.d("cvs", cvs.length.toString())
        }


        if (numberCard.isBlank() || !numberCard.isDigitsOnly()) {
            _errorInputCardNumber.value = true
            result = false
        } else {
            _errorInputCardNumber.value = false
        }

        if (validity.isBlank() || validity.length < 4 || !validity.isDigitsOnly()) {
              _errorInputValidity.value = true
              result = false
         } else {
            _errorInputValidity.value = false
         }

        if (cvs.isBlank() || cvs.length < 3 || !cvs.isDigitsOnly()) {
            _errorInputCvs.value = true
            result = false
        } else {
            _errorInputCvs.value = false
        }

        return result
    }


    fun getDocumentRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Cards")
    }

    fun addCardDbCard(numberCard: String, validity: String, cvs: String) {
        val lastIdCard = getDocumentRef()
        lastIdCard.get()
            .addOnSuccessListener { result ->
                //Log.d("lastid", "${result.last().id}")
                var leadId: Int
                if (result.isEmpty) {
                    leadId = 0

                } else {
                    if((result.last().id).toInt() >= 0){
                        val arraListInt = ArrayList<Int>()
                        for (document in result) {
                            //Log.d("TAG", "${document.id} => ${document.data}")
                            arraListInt.add(document.id.toInt())
                        }
                        leadId = findMax(arraListInt)!! + 1

                    } else {
                        leadId = 0

                    }
                }
                // createLead()

                /*  val lead = LeadItem(arrayValue.get(0).toString(), arrayValue.get(1).toString(), arrayValue.get(2).toString(), arrayValue.get(3).toString(), arrayValue.get(4).toString(),
                      arrayValue.get(5).toString(), arrayValue.get(6).toString(), arrayValue.get(7).toString(), arrayValue.get(8).toString(), arrayValue.get(9).toString(), arrayValue.get(9).toString(),
                      uid.toString(), "", arrayValue.get(9).toString(), "newLead",  leadId)*/
              /*  val messLead = binding.etMessageData.text.toString()

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val currentDate = sdf.format(Date())

                val lead = LeadItem(situation1, "", "", "", "", "", "", "", "", "", messLead,
                    uid.toString(), "", "appliances", "newLead", currentDate, "", leadId)
*/
                val card = CardItem(numberCard, validity, cvs, leadId)

                lastIdCard.document(preference.getUid().toString()).collection("cards").document(card.number)
                    .set(card, SetOptions.merge())
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                finishWork()
                /*
                val db = FirebaseFirestore.getInstance()
                db.collection("Cards").document(preference.getUid().toString())
                    .set(card, SetOptions.merge())
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                */

            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    fun deleteCardUser(numberCard: String) {
        /*
        * db.collection("cities").document("DC")
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        * */
        getDocumentRef().document(preference.getUid().toString()).collection("cards").document(numberCard)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }
/*
    fun getCardUserInfo(): MutableLiveData<ResponseCard> {
        val mutableLiveData = MutableLiveData<ResponseCard>()
        //val resultFunction = ArrayList<CardItem>()
        getDocumentRef().document(preference.getUid().toString()).collection("cards").get()
            .addOnSuccessListener { result ->
                val response = ResponseCard()
                for (item in result){
                    val id = item.data.get("id").toString()
                    val cvs = item.data.get("cvs").toString()
                    val number = item.data.get("number").toString()
                    val validity = item.data.get("validity").toString()
                    val card = CardItem(number, validity, cvs, id.toInt())
                 }



            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }
*/
    fun getResponseFromFirestoreUsingLiveData() : MutableLiveData<ResponseCard> {
        val mutableLiveData = MutableLiveData<ResponseCard>()
        val resultFunction = ArrayList<CardItem>()
        getDocumentRef().document(preference.getUid().toString()).collection("cards").get().addOnCompleteListener { task ->
            val response = ResponseCard()
            if (task.isSuccessful) {
                /*val result = task.result
                result?.let {
                    response.products = result.documents.mapNotNull { snapShot ->
                        snapShot.toObject(CardItem::class.java)
                    }
                }*/
                for (item in task.result!!.documents){
                    val id = item.data!!.get("id").toString()
                    val cvs = item.data!!.get("cvs").toString()
                    val number = item.data!!.get("number").toString()
                    val validity = item.data!!.get("validity").toString()
                    val card = CardItem(number, validity, cvs, id.toInt())
                    resultFunction.add(card)
                }
                response.products = resultFunction
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
    }


    fun findMax(list: List<Int>): Int? {
        return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
    }

    private fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}