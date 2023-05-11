package com.example.lawyerapplication.fragments.mycards

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.data.CardItem
import com.example.lawyerapplication.db.data.LeadItem
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


    fun addUserCard(numberCard: String, validity: String, cvs: String): String {
        val fieldsValid = validateInput(numberCard, validity, cvs)
        val uId = preference.getUid()
        if (fieldsValid) {
            addCardDbCard(numberCard, validity, cvs)
            return "ok" + uId
        } else {
            return "hui mulnii" + uId
        }
    }

    fun validateInput(numberCard: String, validity: String, cvs: String): Boolean {
        var result = true
        if (numberCard.isBlank()) {
            _errorInputCardNumber.value = true
            result = false
        }

        if (validity.isBlank()) {
              _errorInputValidity.value = true
              result = false
         }

        if (cvs.isBlank()) {
            _errorInputCvs.value = true
            result = false
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


                val db = FirebaseFirestore.getInstance()
                db.collection("Cards").document(preference.getUid().toString())
                    .set(card, SetOptions.merge())
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }


            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }




fun findMax(list: List<Int>): Int? {
    return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
}


}