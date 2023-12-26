package com.example.lawyerapplication.fragments.situation

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.utils.LoadState
import com.example.lawyerapplication.utils.MPreference
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.StringFormat
import java.text.SimpleDateFormat
import java.util.*
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



    val userIdUpdateState = MutableLiveData<LoadState>()
    var situationId: String = String()


    fun setDataSituationValue(key: Int, value: String) {
        valueQuestionData.set(key, value)
        //_situationValue.value?.set(key, value)
    }


    fun setTestDataSituationValue(value: Int) {
        _situationValue.value = listOf(value)
        //_situationValue.value?.set(key, value)
    }

    fun getLastLead() {
        val lastIdLead = getDocumentLeadRef()
        val progressDialog = ProgressDialog(context)
        lastIdLead.get()
            .addOnSuccessListener { result ->
                var leadId: Int
                if (result.isEmpty) {
                    leadId = 0
                    situationId = leadId.toString()
                } else {
                    if((result.last().id).toInt() >= 0){
                        val arrayListInt = ArrayList<Int>()
                        for (document in result) {
                            //Log.d("TAG", "${document.id} => ${document.data}")
                            arrayListInt.add(document.id.toInt())
                        }
                        leadId = findMax(arrayListInt)!! + 1
                        situationId = leadId.toString()
                    } else {
                        leadId = 0
                        situationId = leadId.toString()
                    }
                }
                progressDialog.dismiss()
            }
            .addOnFailureListener { _ ->
                //Log.d("TAG", "Error getting documents: ", exception)
            }

    }



    private fun uploadImages(paramsUpload: String, dataUrl: ArrayList<Uri>, category: String) {
        try {
            if (dataUrl.isNotEmpty()) {
                storage = FirebaseStorage.getInstance()
                storageReference = storage.reference
                for (index in dataUrl.indices) {
                    val imageUri = dataUrl[index]
                    //contentResolver.takePersistableUriPermission(imageUri, takeFlags)
                    val progressDialog = ProgressDialog(context)
                    progressDialog.setTitle("Загрузка...")
                    progressDialog.show()
                    val ref: StorageReference =
                        storageReference.child("Leads/" + paramsUpload + "/" + category + "_image" + index)
                    ref.putFile(imageUri)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            val downloadUri = it.task.snapshot.metadata?.path?.toUri()
                            //val downloadUri2 = it.task.snapshot.storage.downloadUrl
                            // val downloadUri = it.task.snapshot.storage.downloadUrl
                            // Toast.makeText(getActivity(), "Uploaded" + downloadUri.toString(), Toast.LENGTH_SHORT).show()
                            // Log.d("uploadIri", downloadUri.toString())
                            //val downloadUri = it.d

                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                        }
                        .addOnProgressListener { taskSnapshot ->
                            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                .totalByteCount
                            progressDialog.setMessage("Загрузка " + progress.toInt() + "%")
                        }

                }
            }
        } catch (e: Exception) {
            val nothing = null
        }

    }

    private fun getDocumentLeadRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }

    fun findMax(list: List<Int>): Int? {
        return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
    }


}

