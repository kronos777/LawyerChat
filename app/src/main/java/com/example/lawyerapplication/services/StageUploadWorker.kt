package com.example.lawyerapplication.services

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lawyerapplication.TYPE_NEW_MESSAGE
import com.example.lawyerapplication.core.MessageSender
import com.example.lawyerapplication.core.OnMessageResponse
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.daos.StageDao
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.di.MessageCollection
import com.example.lawyerapplication.utils.Constants
import com.example.lawyerapplication.utils.UserUtils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.UploadTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.CountDownLatch


@HiltWorker
class StageUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val dbRepository: DbRepository,
    val stageDao: StageDao

):
    Worker(appContext, workerParams) {

    private val params=workerParams

    override fun doWork(): Result {

        stageDao.getAllListStages().filter { it.status == 0 }.forEach {
            getStageItem(it.idBussines.toString(), it.fireBaseId.toString())
            val current = dbRepository.getStageById(it.fireBaseId, it.idBussines)
            val newCurrent = current!!.copy(status = 1)
            dbRepository.updateStage(newCurrent)
            //Timber.v("StageUploadWorker ${it}  newNotify ${newCurrent}")
        }


        return Result.success()
    }

    private fun getStageItem(idBussines: String, idStage: String) {
        getDocumentStageRef().document(idBussines).collection("stages").document(idStage).get().addOnSuccessListener { stage ->
        val status = stage.data!!.get("status").toString()
            if(status.toInt() == 0) {
                setSeenStatus(idBussines, idStage)
            }

        }

    }

    private fun getDocumentStageRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Stage")
    }



    private fun setSeenStatus(idBussines: String, idStage: String) {
        val data = hashMapOf("status" to 1)
        val docRef =  getDocumentStageRef().document(idBussines).collection("stages").document(idStage)
        docRef.set(data, SetOptions.merge())

    }


}
