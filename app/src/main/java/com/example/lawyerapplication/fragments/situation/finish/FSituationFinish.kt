package com.example.lawyerapplication.fragments.situation.finish

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentCreateLeadOkMessageBinding
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.fragments.situation.auto.FSituationAuto10
import com.example.lawyerapplication.utils.ImageUtils
import com.example.lawyerapplication.utils.MPreference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FSituationFinish : Fragment() {

    private lateinit var binding: FragmentCreateLeadOkMessageBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var navController: NavController
    private var situation: String = String()
    private var situationFile: String = String()

    private var lastIdLead: Int = 0

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var listUrlFile: ArrayList<Uri>

    var PICK_IMAGE_MULTIPLE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentCreateLeadOkMessageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()

        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)

        //listUrlFile = ArrayList<Uri>()
        //storage = FirebaseStorage.getInstance()
        //storageReference = storage.getReference()
//        parseParams()
      //  val testStringParams = "Иное (вы опишите кратко ситуацию, загрузите скан или фотодокументов, специалисты проконсультируют вас устно или письменно, уточнив предварительно информацию по телефону) &Комплексная защита гражданской ответственности при ДТП (ОСАГО, КАСКО)&Взыскание ущерба с причинителя вреда в судебном порядке (в случае если причинитель вреда установлен)&Недостаток обнаружен по истечении 15 дней со дня передачи автомобиля покупателю и недостаток является существенным&Предъявить требование о замене на автомобиль этой же марки или на такой же автомобиль другой марки с соответствующим перерасчетом покупной цены&Предъявить требование о замене на автомобиль этой же марки или на такой же автомобиль другой марки с соответствующим перерасчетом покупной цены&Обращение в суд с иском к продавцу (автосалон) о расторжении договора на дополнительное оборудование (дополнительные услуги) и возвращении уплаченных денежных средств, а также возмещении убытков&Потребовать с автодилера выплаты неустойки за несоблюдение срока ремонта автомобиля&500 рублей + 50% от всех взысканных судом и поступивших на Ваш счет денежных средств&auto"
        //var testStringUri = "[content://com.android.providers.media.documents/document/image%3A223746, content://com.android.providers.media.documents/document/image%3A221192, content://com.android.providers.media.documents/document/image%3A221191, content://com.android.providers.media.documents/document/image%3A218703, content://com.android.providers.media.documents/document/image%3A218702, content://com.android.providers.media.documents/document/image%3A218701]"

     //   parseParams()

      //  val category = getCategoryLead(situation)
        //Toast.makeText(getActivity(),"this category" + category, Toast.LENGTH_SHORT).show()

       // Log.d("uid", preference.getUid().toString())

       // Toast.makeText(getActivity(),"this category" + category, Toast.LENGTH_SHORT).show()
      //  if(category == "auto") {
        //    val arrayValue = testStringParams.split("&")
           // Log.d("arrayValue", arrayValue.size.toString())
          //  getReadyImgUri(testStringUri)
           //Log.d("testStringUri", listUrlFile.toString())
             //addLeadDb(arrayValue)

          //  val leadId = 5

            //uploadImages(leadId.toString())
           /* val db = FirebaseFirestore.getInstance()
            db.collection("Leads").document(lead.id.toString())
                .set(lead, SetOptions.merge())
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }*/
        //} else if(category == "medical") {
          //  val arrayValue = situation.split("&")
            //getReadyImgUri(situationFile)
            //addLeadDb(arrayValue)
           // Toast.makeText(getActivity(),"this21 category" + category, Toast.LENGTH_SHORT).show()
        //}


        /*binding.progressCircular.setOnClickListener {
            multipleChoiseImage()
        }
        binding.textSendMessage2.setOnClickListener {
            val leadId = 11

            uploadImages(leadId.toString())
        }*/


    }


    private fun multipleChoiseImage() {
        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture")
                , PICK_IMAGE_MULTIPLE
            )
        } else {
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK
            && null != data
        ) {
            if (data.getClipData() != null) {
                var count = data.clipData?.itemCount
                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                    listUrlFile.add(imageUri)
                    Log.d("imagePath", imageUri.toString())
                    // getPathFromURI(imageUri)
                }
            } else if (data.getData() != null) {
                // var imagePath: String = data.data?.path!!
                val phUri =  ImageUtils.getPhotoUri(data)
                Log.d("imagePath", phUri.toString())
            }

            //  Log.d("imageArrayList", listUrlFile.toString())
            //  uploadImages("diplomdata")
            // displayImageData()
        }

    }
    private fun getReadyImgUri(valueString: String) {
        var valueString = valueString.replace("[", "")
        valueString = valueString.replace("]", "")
        val arrayUriFile = valueString.split(",")
        for(url in arrayUriFile) {
            listUrlFile.add(url.toUri())
            Log.d("imgUri", url.toString())
        }
    }

    fun findMax(list: List<Int>): Int? {
        return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
    }


    private fun addLeadDb(arrayValue: List<String>) {
        val uid = preference.getUid()
        val lastIdLead = getDocumentRef(context)
            lastIdLead.get()
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


                    val lead = LeadItem(arrayValue.get(0).toString(), arrayValue.get(1).toString(), "", "", "", "", "", "", "", "", "",
                        uid.toString(), "", arrayValue.last().toString(), "newLead",  "dsafsdfdas", leadId)


                    val db = FirebaseFirestore.getInstance()
                    db.collection("Leads").document(lead.id.toString())
                        .set(lead, SetOptions.merge())
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

                    uploadImages(leadId.toString())
                   /* */
                    /*for (document in result) {
                        Log.d("TAG", "${document.id} => ${document.data}")
                    }*/
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "Error getting documents: ", exception)
                }
    }

    private fun createLead(arrayValue: List<String>): LeadItem {
        TODO()
    }


    fun getDocumentRef(context: Context): CollectionReference {
        val preference = MPreference(context)
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }

    private fun uploadImages(paramsUpload: String) {

        for (index in listUrlFile.indices) {
            val imageUri = listUrlFile[index]
            //contentResolver.takePersistableUriPermission(imageUri, takeFlags)
            if (imageUri != null) {
                val progressDialog = ProgressDialog(getActivity())
                progressDialog.setTitle("Uploading...")
                progressDialog.show()
                val ref: StorageReference =
                    storageReference.child("Leads/" + paramsUpload + "/" + "image" + index)
                ref.putFile(imageUri!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        val downloadUri = it.task.snapshot.metadata?.path?.toUri()
                        //val downloadUri2 = it.task.snapshot.storage.downloadUrl
                        // val downloadUri = it.task.snapshot.storage.downloadUrl
                        Toast.makeText(getActivity(), "Uploaded" + downloadUri.toString(), Toast.LENGTH_SHORT).show()
                       // Log.d("uploadIri", downloadUri.toString())
                        //val downloadUri = it.d

                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Log.d("uploadIri", e.message.toString())
                        //Toast.makeText(getActivity(), "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot) {
                            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                .totalByteCount
                            progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                        }
                    })

            }
        }
    }



    private fun getCategoryLead(str: String) : String {
        val arrayValue = str.split("&")

        when(arrayValue.last()) {
            "auto" -> return "auto"
            "medical" -> return "medical"

        }
        return "empty"
    }


    private fun parseParams() {
        val args = requireArguments()
        situation = args.getString(FSituationFinish.SITUATION_ITEM).toString()
        situationFile = args.getString(FSituationFinish.SITUATION_ITEM_FILE).toString()
        Log.d("allDataSituation", situation)
        Log.d("allDataSituationFile", situationFile)
        // Toast.makeText(getActivity(),"all choice" + situation9, Toast.LENGTH_SHORT).show()
        // Toast.makeText(getActivity(),"all choice file" + situation9File, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImageUtils.onImagePerResult(this, *grantResults)
    }

    companion object {
        const val SITUATION_ITEM = "situation_item"
        const val SITUATION_ITEM_FILE = "situation_item_file"
    }

}