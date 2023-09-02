package com.example.lawyerapplication.fragments.my_business.stage_bussines

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.data.LastIdStage
import com.example.lawyerapplication.db.data.ResponseStage
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.utils.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StageBussinesViewModel @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val preference: MPreference
) : ViewModel() {

    private val storageRef = UserUtils.getStorageRef(context)

    private val docuRef = UserUtils.getDocumentRef(context)

    val lastIdStageBussines = MutableLiveData("")

    private var createdAt: Long = System.currentTimeMillis()


    override fun onCleared() {
        LogMessage.v("ProfileViewModel Cleared")
        super.onCleared()
    }

    fun getDocumentRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Stage")
    }

    fun getLastIdStageBussines(idBussines: String): MutableLiveData<LastIdStage> {
        val mutableLiveData = MutableLiveData<LastIdStage>()
        getDocumentRef().document(idBussines).collection("stages").get()
                .addOnSuccessListener { result ->
                    var response: LastIdStage
                    if (result.isEmpty) {
                        response = LastIdStage(0)
                        Log.d("responseStage", "${response}")
                    } else {
                        if((result.last().id).toInt() >= 0){
                            val arraListInt = ArrayList<Int>()
                            for (document in result) {
                                //Log.d("TAG", "${document.id} => ${document.data}")
                                arraListInt.add(document.id.toInt())
                            }
                            response = LastIdStage(findMax(arraListInt)!! + 1)

                        } else {
                            response = LastIdStage(0)
                        }
                    }
                    mutableLiveData.value = response
        }
        return mutableLiveData
    }

    @SuppressLint("SimpleDateFormat")
    fun addStageBussines(idBussines: Int, title: String, description: String) {

        getDocumentRef().document(idBussines.toString()).collection("stages").get()
            .addOnSuccessListener { result ->
                var leadId: Int
                if (result.isEmpty) {
                    leadId = 0
                } else {
                    if((result.last().id).toInt() >= 0){
                        val arraListInt = ArrayList<Int>()
                        for (document in result) {
                            arraListInt.add(document.id.toInt())
                        }
                        leadId = findMax(arraListInt)!! + 1
                    } else {
                        leadId = 0
                    }
                }
                //Log.d("responseStage", "${leadId}")
                //add new stage
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val currentDate = sdf.format(Date())
                val stageBussines = StageBussines(leadId, idBussines, title, description, currentDate)
                getDocumentRef().document(idBussines.toString()).collection("stages").document(stageBussines.id.toString())
                    .set(stageBussines, SetOptions.merge())
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
                //add new stage

            }

    }


    fun getStageBussinesLiveData(idBussines: String) : MutableLiveData<ResponseStage> {
        val mutableLiveData = MutableLiveData<ResponseStage>()
        val resultFunction = ArrayList<StageBussines>()
        getDocumentRef().document(idBussines).collection("stages").get().addOnCompleteListener { task ->
            val response = ResponseStage()
            if (task.isSuccessful) {
                for (item in task.result!!.documents){
                    val id = item.data!!.get("id").toString().toInt()
                    val idBussines = item.data!!.get("idBussines").toString().toInt()
                    val title = item.data!!.get("title").toString()
                    val description = item.data!!.get("description").toString()
                    val dateTime = item.data!!.get("dateTime").toString()
                    val status = item.data!!.get("status").toString()

                    val stage = StageBussines(id, idBussines, title, description, dateTime, status.toInt())

                    resultFunction.add(stage)
                }
                response.products = resultFunction.reversed()
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
       // Log.d("stageItem", "id ${response.products}")
        return mutableLiveData
    }

    fun deleteAllStage(idBussines: String) {
        getDocumentRef().document(idBussines)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun findMax(list: List<Int>): Int? {
        return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
    }

}