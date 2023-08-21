package com.example.lawyerapplication.fragments.my_business

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.ResponseBussines
import com.example.lawyerapplication.utils.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BussinesViewModel @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val preference: MPreference
) : ViewModel() {

    val progressProPic = MutableLiveData(false)

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

    fun isLawyer(): Boolean {
        return role.value == "Lawyer"
    }

     fun getDataUser(context: Context, uid: String): DocumentReference {
         val user = UserUtils.getDocumentRefBussines(context, uid)
         return user
     }

    override fun onCleared() {
        LogMessage.v("ProfileViewModel Cleared")
        super.onCleared()
    }

    fun getDocumentRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }

    fun getBussinesLiveData(uid: String, role: Boolean, sort: Boolean, typeSorting: String?, tabPosition: String?) : MutableLiveData<ResponseBussines> {
        val mutableLiveData = MutableLiveData<ResponseBussines>()
        val resultFunction = ArrayList<BusinessItem>()
        getDocumentRef().get().addOnCompleteListener { task ->
            val response = ResponseBussines()
            if (task.isSuccessful) {
                /*val result = task.result
                result?.let {
                    response.products = result.documents.mapNotNull { snapShot ->
                        snapShot.toObject(CardItem::class.java)
                    }
                }*/
                for (item in task.result!!.documents){
                    if(uid == item.data!!.get("idClient") as String && role == false) {
                        Log.d("CURRENTDATA", "Current uid: ${uid}")
                        val id = item.data!!.get("id")
                        val lead = BusinessItem(id, "Дело номер $id", item.data!!.get("status") as String, getCategory(item.data!!.get("category") as String), item.data!!.get("dateTime") as String, item.data!!.get("idLawyer") as String)
                        resultFunction.add(lead)
                    } else if(role == true && (uid == item.data!!.get("idLawyer") || item.data!!.get("idLawyer") == "")) {
                        val id = item.data!!.get("id")
                        val lead = BusinessItem(id, "Дело номер $id", item.data!!.get("status") as String, getCategory(item.data!!.get("category") as String), item.data!!.get("dateTime") as String, item.data!!.get("idLawyer") as String)
                        resultFunction.add(lead)
                    }

                }

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
                        it.categoryLead
                    }

                    response.products = sortBussines
                } else if (!role && typeSorting == "closed" && sort) {
                    val sortBussines = resultFunction.filter {  it.typeLead == "close" }.sortedByDescending {
                        it.categoryLead
                    }

                    response.products = sortBussines
                } else if (!sort && typeSorting == ""){
                    response.products = resultFunction
                }


                //response.products = resultFunction
            } else {
                response.exception = task.exception
            }
            mutableLiveData.value = response
        }
        return mutableLiveData
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