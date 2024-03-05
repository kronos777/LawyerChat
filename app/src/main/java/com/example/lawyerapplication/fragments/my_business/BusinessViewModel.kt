package com.example.lawyerapplication.fragments.my_business

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.db.data.ResponseBussines
import com.example.lawyerapplication.models.UserProfile
import com.example.lawyerapplication.utils.LoadState
import com.example.lawyerapplication.utils.LogMessage
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.UserUtils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.json.JSONObject
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val preference: MPreference
) : ViewModel() {

    val businessListUpdateState = MutableLiveData<LoadState>()

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
    private var isFirstQuery = false
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

    fun isLawyer(): Boolean {
        return role.value == "Lawyer"
    }

    fun getDataUser(uid: String): DocumentReference {
        return UserUtils.getDocumentRefBussines(uid)
    }

    override fun onCleared() {
        LogMessage.v("ProfileViewModel Cleared")
        super.onCleared()
    }

    fun getDocumentRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }
    fun getBusinessData(idLead: String): Deferred<LeadItem?> = CoroutineScope(Dispatchers.IO).async {
        val gson = Gson()
        var busData: LeadItem? = null
        getDocumentRef().document(idLead).get()
            .addOnSuccessListener { result ->
                val obj = JSONObject(result.data)
                busData = gson.fromJson(obj.toString(), LeadItem::class.java)

            }
            .addOnFailureListener { error ->
                Timber.v("TAG  $error")
            }
        delay(1500)
        return@async busData

    }

    fun getUserData(idUser: String): Deferred<UserProfile?> = CoroutineScope(Dispatchers.IO).async {
        val gson = Gson()
        var usData: UserProfile? = null
        getDataUser(idUser).get()
            .addOnSuccessListener { result ->
                val obj = JSONObject(result.data)
                usData = gson.fromJson(obj.toString(), UserProfile::class.java)

            }
            .addOnFailureListener { error ->
                Timber.v("TAG  $error")
            }
        delay(1500)
        return@async usData

    }

    fun getLeadImageData(idLead: String): Deferred<List<Uri>> = CoroutineScope(Dispatchers.IO).async {
        val listUri = mutableListOf<Uri>()
        val storage = Firebase.storage
        val listRef = storage.reference.child("Leads").child(idLead)
        listRef.listAll()
            .addOnSuccessListener { (items, _) ->
                for (index in items.indices) {
                    items[index].downloadUrl.addOnSuccessListener { urlTask ->
                        listUri.add(urlTask)
                    }.addOnFailureListener { e ->
                        // Handle any errors
                    }
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
        delay(1500)
        return@async listUri
    }

    fun getBusinessLiveDataRealTime(uid: String, role: Boolean, sort: Boolean, typeSorting: String?, tabPosition: String?) : MutableLiveData<ResponseBussines> {
        businessListUpdateState.value = LoadState.OnLoading
        val mutableLiveData = MutableLiveData<ResponseBussines>()
        val resultFunction = ArrayList<BusinessItem>()
        if(!isFirstQuery) {
            getDocumentRef().addSnapshotListener { snapShots, _ ->
                val response = ResponseBussines()
                snapShots?.forEach { item ->
                    if(uid == item.data["idClient"] as String && role == false) {
                        val id = item.data["id"]
                        val lead = BusinessItem(id, "Дело номер $id", item.data["status"] as String, getCategory(
                            item.data["category"] as String), item.data["dateTime"] as String, item.data["idLawyer"] as String)
                        resultFunction.add(lead)
                    } else if(role && (uid == item.data["idLawyer"] || item.data["idLawyer"] == "")) {
                        val id = item.data["id"]
                        val lead = BusinessItem(id, "Дело номер $id", item.data["status"] as String, getCategory(item.data["category"] as String), item.data["dateTime"] as String, item.data["idLawyer"] as String)
                        resultFunction.add(lead)
                    }

                    response.products = filteringContent(resultFunction, uid, role, sort, typeSorting)
                    businessListUpdateState.value = LoadState.OnSuccess()

                }
                mutableLiveData.value = response
            }
        }
        return mutableLiveData
    }

    fun getBusinessLiveData(uid: String, role: Boolean, sort: Boolean, typeSorting: String?, tabPosition: String?) : MutableLiveData<ResponseBussines> {
        businessListUpdateState.value = LoadState.OnLoading
        val mutableLiveData = MutableLiveData<ResponseBussines>()
        val resultFunction = ArrayList<BusinessItem>()
        //getDocumentRef().get().addOnCompleteListener { task ->
        getDocumentRef().get().addOnCompleteListener { task ->
            val response = ResponseBussines()
            if (task.isSuccessful) {
                for (item in task.result!!.documents){
                    if(uid == item.data!!.get("idClient") as String && role == false) {
                        val id = item.data!!["id"]
                        val lead = BusinessItem(id, "Дело номер $id", item.data!!.get("status") as String, getCategory(item.data!!.get("category") as String), item.data!!.get("dateTime") as String, item.data!!.get("idLawyer") as String)
                        resultFunction.add(lead)
                    } else if(role == true && (uid == item.data!!.get("idLawyer") || item.data!!.get("idLawyer") == "")) {
                        val id = item.data!!["id"]
                        val lead = BusinessItem(id, "Дело номер $id", item.data!!.get("status") as String, getCategory(item.data!!.get("category") as String), item.data!!.get("dateTime") as String, item.data!!.get("idLawyer") as String)
                        resultFunction.add(lead)
                    }

                }

                if(role && (typeSorting == "" || typeSorting == "noactive")) {
                    val sortBussines = resultFunction.filter {  it.idLaywer == "" }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines
                } else if (role && typeSorting == "active") {
                    val sortBussines = resultFunction.filter {  it.idLaywer == preference.getUid() }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines
                } else if (role && typeSorting == "dateTime" && sort) {
                    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                    val sortBussines = resultFunction.filter {  it.idLaywer == "" }.sortedByDescending {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDate.parse(it.dateTimeLead, formatter)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                    }

                    response.products = sortBussines
                } else if (role && typeSorting == "activeDateTime" && !sort) {
                    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                    val sortBussines = resultFunction.filter {  it.idLaywer == preference.getUid() }.sortedByDescending {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDate.parse(it.dateTimeLead, formatter)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                    }

                    response.products = sortBussines
                } else if (role && typeSorting == "noActiveDateTime" && sort) {
                    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                    val sortBussines = resultFunction.filter {  it.idLaywer == preference.getUid() }.sortedBy {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDate.parse(it.dateTimeLead, formatter)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }
                    }

                    response.products = sortBussines
                } else if (role && typeSorting == "applications" && sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead != "consultation" }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines

                }  else if (role && typeSorting == "applications" && !sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead != "consultation" }.sortedBy {
                        it.categoryLead
                    }

                    response.products = sortBussines

                }else if (role && typeSorting == "activeApplications" && !sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead != "consultation" && it.idLaywer == preference.getUid()}.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines

                }  else if (role && typeSorting == "noActiveApplications" && sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead != "consultation" && it.idLaywer == preference.getUid()}.sortedBy {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "consultation" && sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead == "consultation" }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "consultation" && !sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead == "consultation" }.sortedBy {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "activeConsultation" && !sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead == "consultation" && it.idLaywer == preference.getUid()}.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines

                }  else if (role && typeSorting == "noActiveConsultation" && sort) {
                    val sortBussines = resultFunction.filter {  it.categoryLead == "consultation" && it.idLaywer == preference.getUid()}.sortedBy {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "themes" && sort) {
                    val sortBussines = resultFunction.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "themes" && !sort) {
                    val sortBussines = resultFunction.sortedBy {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "ActiveThemes" && sort) {
                    val sortBussines = resultFunction.filter {  it.idLaywer == preference.getUid() }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (role && typeSorting == "ActiveThemes" && !sort) {
                    val sortBussines = resultFunction.filter {  it.idLaywer == preference.getUid() }.sortedBy {
                        it.categoryLead
                    }

                    response.products = sortBussines

                } else if (!role && (typeSorting == "" || typeSorting == "active")) {
                    //for client
                    val sortBussines = resultFunction.filter {  it.typeLead != "close" }.sortedByDescending {
                        it.id.toString().toInt()
                    }
                    response.products = sortBussines
                } else if (!role && typeSorting == "closed" && sort) {
                    //for client
                    val sortBussines = resultFunction.filter {  it.typeLead == "close" }.sortedByDescending {
                        it.id.toString().toInt()
                    }
                    response.products = sortBussines
                } else if (!sort && typeSorting == ""){
                    response.products = resultFunction
                }


                businessListUpdateState.value = LoadState.OnSuccess()
                //response.products = resultFunction
            } else {
                businessListUpdateState.value = task.exception?.let { LoadState.OnFailure(it) }
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
    }


    private fun filteringContent(arrFilter: ArrayList<BusinessItem>,uid: String, role: Boolean, sort: Boolean, typeSorting: String?): List<BusinessItem> {
        var resultFilter = mutableListOf<BusinessItem>()

        if(role && (typeSorting == "" || typeSorting == "noactive")) {
            val sortBusiness = arrFilter.filter {  it.idLaywer == "" }.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()
        } else if (role && typeSorting == "active") {
            val sortBusiness = arrFilter.filter {  it.idLaywer == preference.getUid() }.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()
        } else if (role && typeSorting == "dateTime" && sort) {
            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val sortBusiness = arrFilter.filter {  it.idLaywer == "" }.sortedByDescending {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.parse(it.dateTimeLead, formatter)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }

            resultFilter = sortBusiness.toList().toMutableList()
        } else if (role && typeSorting == "activeDateTime" && !sort) {
            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val sortBusiness = arrFilter.filter {  it.idLaywer == preference.getUid() }.sortedByDescending {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.parse(it.dateTimeLead, formatter)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }

            resultFilter = sortBusiness.toList().toMutableList()
        } else if (role && typeSorting == "noActiveDateTime" && sort) {
            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val sortBusiness = arrFilter.filter {  it.idLaywer == preference.getUid() }.sortedBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.parse(it.dateTimeLead, formatter)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }

            resultFilter = sortBusiness.toList().toMutableList()
        } else if (role && typeSorting == "applications" && sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead != "consultation" }.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        }  else if (role && typeSorting == "applications" && !sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead != "consultation" }.sortedBy {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        }else if (role && typeSorting == "activeApplications" && !sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead != "consultation" && it.idLaywer == preference.getUid()}.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        }  else if (role && typeSorting == "noActiveApplications" && sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead != "consultation" && it.idLaywer == preference.getUid()}.sortedBy {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "consultation" && sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead == "consultation" }.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "consultation" && !sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead == "consultation" }.sortedBy {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "activeConsultation" && !sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead == "consultation" && it.idLaywer == preference.getUid()}.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        }  else if (role && typeSorting == "noActiveConsultation" && sort) {
            val sortBusiness = arrFilter.filter {  it.categoryLead == "consultation" && it.idLaywer == preference.getUid()}.sortedBy {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "themes" && sort) {
            val sortBusiness = arrFilter.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "themes" && !sort) {
            val sortBusiness = arrFilter.sortedBy {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "ActiveThemes" && sort) {
            val sortBusiness = arrFilter.filter {  it.idLaywer == preference.getUid() }.sortedByDescending {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (role && typeSorting == "ActiveThemes" && !sort) {
            val sortBusiness = arrFilter.filter {  it.idLaywer == preference.getUid() }.sortedBy {
                it.categoryLead
            }

            resultFilter = sortBusiness.toList().toMutableList()

        } else if (!role && (typeSorting == "" || typeSorting == "active")) {
            //for client
            val sortBusiness = arrFilter.filter {  it.typeLead != "close" }.sortedByDescending {
                it.id.toString().toInt()
            }
            resultFilter = sortBusiness.toList().toMutableList()
        } else if (!role && typeSorting == "closed" && sort) {
            //for client
            val sortBusiness = arrFilter.filter {  it.typeLead == "close" }.sortedByDescending {
                it.id.toString().toInt()
            }
            resultFilter = sortBusiness.toList().toMutableList()
        } else if (!sort && typeSorting == ""){
            resultFilter = arrFilter.toList().toMutableList()
        }

        return resultFilter
    }


    private fun getCategory(str: Any): String {
        return when(str) {
            "medical" -> "Медицинские услуги"
            "auto" -> "Авто"
            "appliances" -> "Бытовая техника"
            "newBuildings" -> "Новостройки"
            "furniture" -> "Мебель"
            "clothing" -> "Одежда"
            "consultation" -> "Консультация"
            else -> "услуга не определена"
        }
    }



}