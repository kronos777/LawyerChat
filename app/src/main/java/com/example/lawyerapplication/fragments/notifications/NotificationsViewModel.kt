package com.example.lawyerapplication.fragments.notifications

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.ResponseBussines
import com.example.lawyerapplication.db.data.ResponseNotifications
import com.example.lawyerapplication.db.data.ResponseStage
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.db.data.StageBussinesLocal
import com.example.lawyerapplication.utils.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val dbRepo: DbRepository,
    private val preference: MPreference
) : ViewModel() {

    private val _stageBussines = MutableLiveData<StageBussines>()
    val stageBussines: LiveData<StageBussines>
        get() = _stageBussines

    val profileUpdateState = MutableLiveData<LoadState>()

    val checkUserNameState = MutableLiveData<LoadState>()

    val name = MutableLiveData("")
    val lastName = MutableLiveData("")
    val serName = MutableLiveData("")
    val email = MutableLiveData("")
    val role = MutableLiveData("")
    val password = MutableLiveData("")

    private val storageRef = UserUtils.getStorageRef(context)

    private val docuRef = UserUtils.getDocumentRef(context)

    val profilePicUrl = MutableLiveData("")

    private var about = ""

    private var createdAt: Long = System.currentTimeMillis()

    init {
        LogMessage.v("ProfileViewModel")
        val userProfile = preference.getUserProfile()
        userProfile?.let {
            name.value = userProfile.userName
            /*md*/
            lastName.value = userProfile.lastName
            serName.value = userProfile.serName
            email.value = userProfile.email
            role.value = userProfile.role
            password.value = userProfile.password
            /*md*/
            profilePicUrl.value = userProfile.image
            about = userProfile.about
            createdAt = userProfile.createdAt ?: System.currentTimeMillis()
        }
    }

    fun setStageBussines(stBussines: StageBussines) {
        viewModelScope.launch {
            _stageBussines.value = stBussines
        }
    }

    override fun onCleared() {
        LogMessage.v("ProfileViewModel Cleared")
        super.onCleared()
    }



    fun getDocumentRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }

    fun updateStageLocalDb() {
        val uid = preference.getUid()
        val resultListIntBussines = ArrayList<Int>()
        getDocumentRef().get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (item in task.result!!.documents){
                    if(uid.equals((item.data!!.get("idClient") as String).trim())) {
                        val id = item.data!!.get("id").toString()
                        resultListIntBussines.add(id.toInt())

                    }
                }

                resultListIntBussines.forEach { id ->

                    getDocumentStageRef().document(id.toString()).collection("stages").get().addOnCompleteListener { taskTwo ->

                        if (taskTwo.isSuccessful) {

                            for (item in taskTwo.result!!.documents){
                                val id = item.data!!.get("id").toString().toInt()
                                val idBussines = item.data!!.get("idBussines").toString().toInt()
                                val title = item.data!!.get("title").toString()
                                val description = item.data!!.get("description").toString()
                                val dateTime = item.data!!.get("dateTime").toString()
                                val status = item.data!!.get("status").toString()

                                val stageLocal = StageBussinesLocal(0, id, idBussines, title, description, dateTime, status.toInt())
                                dbRepo.insertStage(stageLocal)

                            }
                        }

                    }
                  }
              }
         }
    }

    fun getStagesLiveData() : MutableLiveData<ResponseNotifications> {
        val uid = preference.getUid()
        val mutableLiveData = MutableLiveData<ResponseNotifications>()
        val resultFunction = ArrayList<StageBussines>()
        val resultListIntBussines = ArrayList<Int>()

        getDocumentRef().get().addOnCompleteListener { task ->
            val response = ResponseNotifications()
            if (task.isSuccessful) {

                for (item in task.result!!.documents){
                    if(uid.equals((item.data!!.get("idClient") as String).trim())) {
                        val id = item.data!!.get("id").toString()
                        resultListIntBussines.add(id.toInt())

                    }
                }

                resultListIntBussines.forEach { id ->

                    getDocumentStageRef().document(id.toString()).collection("stages").get().addOnCompleteListener { taskTwo ->

                        if (taskTwo.isSuccessful) {
                            resultFunction.clear()
                            for (item in taskTwo.result!!.documents){
                                val id = item.data!!.get("id").toString().toInt()
                                val idBussines = item.data!!.get("idBussines").toString().toInt()
                                val title = item.data!!.get("title").toString()
                                val description = item.data!!.get("description").toString()
                                val dateTime = item.data!!.get("dateTime").toString()
                                val status = item.data!!.get("status").toString()

                                val stage = StageBussines(id, idBussines, title, description, dateTime, status.toInt())
                                val stageLocal = StageBussinesLocal(0, id, idBussines, title, description, dateTime, status.toInt())
                                dbRepo.insertStage(stageLocal)


                                resultFunction.add(stage)
                            }
                            response.products = resultFunction
                            mutableLiveData.value = response
                        }

                    }

                }




               // response.products = resultFunction





                /*if(sort && typeSorting == "dateTime") {
                    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }

                    val sortBussines = resultFunction.sortedByDescending {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDate.parse(it.dateTimeLead, formatter)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                    }
                    response.products = sortBussines
                } else if (sort && typeSorting == "applications"){
                    val sortBussines = resultFunction.filter {  it.categoryLead != "consultation" }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines
                } else if (sort && typeSorting == "consultations"){
                    val sortBussines = resultFunction.sortedByDescending {
                        it.categoryLead == "consultations"
                    }

                    response.products = sortBussines
                } else if (sort && typeSorting == "active"){
                    val sortBussines = resultFunction.filter {  it.idLaywer != "" }.sortedByDescending {
                            it.idLaywer
                        }
                    response.products = sortBussines
                } else if (sort && typeSorting == "noactive"){
                    val sortBussines = resultFunction.filter {  it.idLaywer == "" }.sortedByDescending {
                        it.idLaywer
                    }

                    response.products = sortBussines
                } else if (sort && typeSorting == "themes"){
                    val sortBussines = resultFunction.sortedByDescending {
                            it.categoryLead
                       }

                    response.products = sortBussines
                } */



                //response.products = resultFunction
          /*  } else {
                response.exception = task.exception*/
            }
           // mutableLiveData.value = response

        }

        return mutableLiveData
    }


    fun insertStage(stageLocal: StageBussinesLocal) {
        dbRepo.insertStage(stageLocal)
    }

    fun getStatesLiveDataNew() {
        getDocumentStageRef()
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAGNEWEPT", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAGNEWEPT", "Error getting documents: ", exception)
            }

    }



    fun getStageItem(idBussines: String, idStage: String) {
        getDocumentStageRef().document(idBussines).collection("stages").document(idStage).get().addOnSuccessListener { stage ->

                    val id = stage.data!!.get("id").toString().toInt()
                    val idBuss = stage.data!!.get("idBussines").toString().toInt()
                    val title = stage.data!!.get("title").toString().capitalize()
                    val description = stage.data!!.get("description").toString()
                    val dateTime = stage.data!!.get("dateTime").toString()
                    val status = stage.data!!.get("status").toString()

                    val stBussines = StageBussines(id, idBuss, title, description, dateTime, status.toInt())

                    setStageBussines(stBussines)

                    if(status.toInt() == 0) {
                        setSeenStatus(idBussines, idStage)
                    }

            }

    }


    private fun setSeenStatus(idBussines: String, idStage: String) {
        val data = hashMapOf("status" to 1)
        val docRef =  getDocumentStageRef().document(idBussines).collection("stages").document(idStage)
        docRef.set(data, SetOptions.merge())

    }



    fun getDocumentStageRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Stage")
    }

    fun getStageLocal(fireBaseId: Int, idBussines: Int): StageBussinesLocal? {
        return dbRepo.getStageById(fireBaseId, idBussines)
    }
    fun getAllStagesLocalDb(): LiveData<List<StageBussinesLocal>> {
        return dbRepo.getAllStages()
    }

    fun updateStage(st: StageBussinesLocal) {
        dbRepo.updateStage(st)
    }

    fun deleteTableStage() {
            dbRepo.deleteTableStage()
    }

}