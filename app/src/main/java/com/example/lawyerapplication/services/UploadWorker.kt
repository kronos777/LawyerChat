package com.example.lawyerapplication.services

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lawyerapplication.TYPE_NEW_MESSAGE
import com.example.lawyerapplication.core.MessageSender
import com.example.lawyerapplication.core.OnMessageResponse
import com.example.lawyerapplication.db.DbRepository
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.di.MessageCollection
import com.example.lawyerapplication.utils.Constants
import com.example.lawyerapplication.utils.UserUtils
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.UploadTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @MessageCollection
    val msgCollection: CollectionReference,
    val dbRepository: DbRepository
):
    Worker(appContext, workerParams) {

    private val params=workerParams

    override fun doWork(): Result {
        val stringData=params.inputData.getString(Constants.MESSAGE_DATA) ?: ""
        val message= Json.decodeFromString<Message>(stringData)

        var url=params.inputData.getString(Constants.MESSAGE_FILE_URI)!!

        val realPath: String?
        val sourceName: String
        //Timber.v("getSourceName message ${message.type}")
        //Timber.v("getSourceName ${realPath}")
       // Timber.v("getSourceName ${url}")
        if(message.type == "audio") {
            sourceName = getSourceName(message, url) //до стоял url
        } else if(message.type == "file"){
            url = getRealPathFromURI(Uri.parse(url), applicationContext).toString()
            sourceName = getSourceName(message, url)
        } else {
            realPath = getRealPathFromURI(Uri.parse(url), applicationContext) //поставил функцию для извлечения пути корректно
            sourceName = getSourceName(message, realPath!!)
        }

        //Timber.v("TaskResult url ${getRealPathFromURI(Uri.parse(url), applicationContext)}")

        val storageRef= UserUtils.getStorageRef(applicationContext)

        val child = storageRef.child(
            "chats/${message.to}/$sourceName")
        val task: UploadTask
        task = if(url.contains(".mp3") || message.type == "file") {
            val stream = FileInputStream(url)  //audio message
            child.putStream(stream)
        }else
            child.putFile(Uri.parse(message.imageMessage?.uri))

        val countDownLatch = CountDownLatch(1)
        val result= arrayOf(Result.failure())
        task.addOnSuccessListener {
            child.downloadUrl.addOnCompleteListener { taskResult ->
                Timber.v("TaskResult ${taskResult.result.toString()}")
                val downloadUrl=taskResult.result.toString()
                sendMessage(message,downloadUrl,result,countDownLatch)
            }.addOnFailureListener { e ->
                Timber.v("TaskResult Failed ${e.message}")
                result[0]= Result.failure()
                message.status=4
                dbRepository.insertMessage(message)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return result[0]
    }



    private fun getSourceName(message: Message, url: String): String {
        val createdAt=message.createdAt.toString()
        val num=createdAt.substring(createdAt.length - 5)
        val extension=url.substring(url.lastIndexOf('.'))

        return "${message.type}_$num$extension"
    }


    fun getRealPathFromURI(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)

        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }

    private fun sendMessage(message: Message, downloadUrl: String, result: Array<Result>,
                            countDownLatch: CountDownLatch) {
        val chatUser=Json.decodeFromString<ChatUser>(params.inputData.getString(Constants.CHAT_USER_DATA)!!)
        setUrl(message,downloadUrl)
        val messageSender = MessageSender(
            msgCollection,
            dbRepository,
            chatUser,object : OnMessageResponse{
                override fun onSuccess(message: Message) {
                    UserUtils.sendPush(applicationContext, TYPE_NEW_MESSAGE, Json.encodeToString(message)
                        , chatUser.user.token,message.to)
                    result[0]= Result.success()
                    countDownLatch.countDown()
                }

                override fun onFailed(message: Message) {
                    result[0]= Result.failure()
                    dbRepository.insertMessage(message)
                    countDownLatch.countDown()
                }
            }
        )
        messageSender.checkAndSend(message.from, message.to, message)
    }

    private fun setUrl(message: Message, imgUrl: String) {
        if (message.type=="audio")
            message.audioMessage?.uri=imgUrl
        else if (message.type=="file")
            message.fileMessage?.uri=imgUrl
        else
            message.imageMessage?.uri=imgUrl
    }


}
