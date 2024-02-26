package com.example.lawyerapplication.fragments.situation

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.utils.LoadState
import com.example.lawyerapplication.utils.MPreference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@HiltViewModel
open class SituationViewModel @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val dbRepository: DbRepository,//before change stay private
    private val preference: MPreference,
    private val firebaseFireStore: FirebaseFirestore
) : ViewModel() {

    private val _situationValue = MutableLiveData<List<Int>>()
    val situationValue: LiveData<List<Int>>
        get() = _situationValue

    val valueQuestionData: HashMap<Int, String> by lazy {
        HashMap()
    }
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private val dbFireStore by lazy {
        FirebaseFirestore.getInstance()
    }

    val userIdUpdateState = MutableLiveData<LoadState>()
    var situationId: String = String()


    fun getUid(): String {
        return preference.getUid().toString()
    }

    fun setDataSituationValue(key: Int, value: String) {
        //if(valueQuestionData[0] != null) valueQuestionData.clear()
        valueQuestionData[key] = value
        //_situationValue.value?.set(key, value)
    }


    fun clearValueQuestionData() {
        valueQuestionData.clear()
    }


    fun addLead(leadItem: LeadItem) {
        dbFireStore.collection("Leads").document(leadItem.id.toString())
            .set(leadItem, SetOptions.merge())
            .addOnSuccessListener { Timber.tag(ContentValues.TAG)
                .d("DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Timber.tag(ContentValues.TAG)
                .w(e, "Error writing document") }
    }


    val lastLeadInDb: Deferred<Int> = CoroutineScope(Dispatchers.IO).async {
        var leadId = -1
            getDocumentLeadRef().get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    leadId = 0
                } else {
                    if((result.last().id).toInt() > 0){
                        val arrayListInt = ArrayList<Int>()
                        for (document in result) {
                            arrayListInt.add(document.id.toInt())
                        }
                        leadId = findMax(arrayListInt)!! + 1
                    } else {
                        leadId = 0

                    }

                }

            }
            .addOnFailureListener { _ ->
                //Log.d("TAG", "Error getting documents: ", exception)
            }
        delay(1500)
        return@async leadId

    }

    fun deleteLeadInDb(idLeads: String) {
        dbFireStore.collection("Leads").document(idLeads)
            .delete()
            .addOnSuccessListener { Timber.tag(TAG).d("DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { _ -> Timber.tag(TAG).d("Error deleting document") }

        /*
        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val desertRef = storageReference.child("Leads/$idLeads")
        // Delete the file
        desertRef.delete().addOnSuccessListener {
            // File deleted successfully
            Timber.tag(TAG).d("DocumentSnapshot successfully deleted!")
        }.addOnFailureListener {
            // Uh-oh, an error occurred!
        }*/
    }



    fun editLeadPaymentInfo(leadId: String, radioSelect: String) {
        val data = hashMapOf("paymentInfo" to radioSelect)
        getDocumentLeadRef().document(leadId).set(data, SetOptions.merge())
    }

    fun uploadImages(paramsUpload: String, dataUrl: ArrayList<Uri>, category: String) {
                storage = FirebaseStorage.getInstance()
                storageReference = storage.reference
                for (index in dataUrl.indices) {
                    val imageUri = dataUrl[index]
                    val ref: StorageReference =
                    storageReference.child("Leads/" + paramsUpload + "/" + category + "_image" + index)
                    ref.putFile(imageUri)
                        .addOnSuccessListener {
                        //val downloadUri = it.task.snapshot.metadata?.path?.toUri()
                        }
                        .addOnFailureListener { _ -> }
                        .addOnProgressListener { _ -> }

                }
    }

    private fun getDocumentLeadRef(): CollectionReference {
        return dbFireStore.collection("Leads")
    }

    private fun findMax(list: List<Int>): Int? {
        return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
    }


}

