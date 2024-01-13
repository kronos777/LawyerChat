package com.example.lawyerapplication.fragments.situation.appliances

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
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.fragments.situation.medical_services.FSituationMedicalServices2
import com.example.lawyerapplication.fragments.situation.medical_services.FSituationMedicalServices3
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FSituationAppliances2 : Fragment() {

    private lateinit var binding: FragmentSituationAppliancesS2Binding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference


    private val viewModelSituation: SituationViewModel by activityViewModels()

    private lateinit var navController: NavController

    private var situation1: String = String()
    private var situationId: String = String()

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var listUrlFileFirst: ArrayList<Uri>
    private var boolFileFirst: Boolean = false
    private lateinit var listUrlFileTwo: ArrayList<Uri>
    private var boolFileTwo: Boolean = false
    private lateinit var listUrlFileFree: ArrayList<Uri>
    private var boolFileFree: Boolean = false
    private lateinit var listUrlFileFour: ArrayList<Uri>
    private var boolFileFour: Boolean = false
    private lateinit var listUrlFileFive: ArrayList<Uri>
    private var boolFileFive: Boolean = false

    var PICK_IMAGE_MULTIPLE = 1

    private var progressView: CustomProgressView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationAppliancesS2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()



        parseParams()
        listUrlFileFirst = ArrayList<Uri>()
        listUrlFileTwo = ArrayList<Uri>()
        listUrlFileFree = ArrayList<Uri>()
        listUrlFileFour = ArrayList<Uri>()
        listUrlFileFive = ArrayList<Uri>()

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()
        // binding.enterButton.getBackground().setAlpha(160)
        // binding.enterButton.isClickable = false
        //  binding.enterButton.isEnabled = false

        //first field
        binding.textD1Attachment.setOnClickListener {
            boolFileFirst = true
            multipleChoiseImage()
        }

        binding.imageD1AttachmentReady.setOnClickListener {
            listUrlFileFirst.clear()
            showFirstFieldReady()
        }
        //first field

        //two field
        binding.textD2Attachment.setOnClickListener {
            boolFileTwo = true
            multipleChoiseImage()
        }

        binding.imageD2AttachmentReady.setOnClickListener {
            listUrlFileTwo.clear()
            showTwoFieldReady()
        }
        //two field
        //free field
        binding.textD3Attachment.setOnClickListener {
            boolFileFree = true
            multipleChoiseImage()
        }

        binding.imageD3AttachmentReady.setOnClickListener {
            listUrlFileFree.clear()
            showFreeFieldReady()
        }
        //free field
        //four field
        binding.textD4Attachment.setOnClickListener {
            boolFileFour = true
            multipleChoiseImage()
        }

        binding.imageD4AttachmentReady.setOnClickListener {
            listUrlFileFour.clear()
            showFourFieldReady()
        }
        //four field
        //five field
        binding.textD7Attachment.setOnClickListener {
            boolFileFive = true
            multipleChoiseImage()
        }

        binding.imageD5AttachmentReady.setOnClickListener {
            listUrlFileFive.clear()
            showFiveFieldReady()
        }
        //five field


        binding.enterButton.setOnClickListener {
            viewModelSituation.setDataSituationValue(1, binding.etMessageData.text.toString())
            viewModelSituation.getLastLead()
           // viewModelSituation.setTestDataSituationValue(25)
            Toast.makeText(activity, "Вы не выбрали не одного файла2. "+ viewModelSituation.valueQuestionData.toString(), Toast.LENGTH_SHORT).show()
            //sleep(2000)
            /*viewModelSituation.valueQuestionData.observe(viewLifecycleOwner) {
                it.forEach {
                    binding.titleSituationH1.text = it.value.toString()
                }


            }
            */
            /*
            if(listUrlFileFirst.size == 0 && listUrlFileTwo.size == 0 && listUrlFileFree.size == 0 && listUrlFileFour.size == 0 && listUrlFileFive.size == 0) {
                Toast.makeText(getActivity(), "Вы не выбрали не одного файла.", Toast.LENGTH_SHORT).show()
            } else {
                addLeadDb()
            }
            */

        }

    }

    private fun getReadyImagesForUpload(paramsUpload: String) {
        val listAll: ArrayList<ArrayList<Uri>> = ArrayList<ArrayList<Uri>>()
        listAll.add(listUrlFileFirst)
        listAll.add(listUrlFileTwo)
        listAll.add(listUrlFileFree)
        listAll.add(listUrlFileFour)
        listAll.add(listUrlFileFive)
        //проверим все списки файлы
        for (index in listAll.indices) {
            if(listAll[index].size > 0) {
                var categoryFile = ""
                when(index) {
                    0 -> categoryFile = "firstGroup"
                    1 -> categoryFile = "twoGroup"
                    2 -> categoryFile = "freeGroup"
                    3 -> categoryFile = "fourGroup"
                    4 -> categoryFile = "fiveGroup"
                }
                uploadImages(paramsUpload, listAll[index], categoryFile)
                //Log.d("thisIndex ", index.toString())
            }

        }
    }


    private fun uploadImages(paramsUpload: String, dataUrl: ArrayList<Uri>, category: String) {

        for (index in dataUrl.indices) {
            val imageUri = dataUrl[index]
            //contentResolver.takePersistableUriPermission(imageUri, takeFlags)
            if (imageUri != null) {
                val progressDialog = ProgressDialog(getActivity())
                progressDialog.setTitle("Загрузка...")
                progressDialog.show()
                val ref: StorageReference =
                    storageReference.child("Leads/" + paramsUpload + "/" + category + "_image" + index)
                ref.putFile(imageUri!!)
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
                        Log.d("uploadIri", e.message.toString())
                        //Toast.makeText(getActivity(), "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                        progressDialog.setMessage("Загрузка " + progress.toInt() + "%")
                    }

            }
        }
    }

    private fun showFirstFieldReady() {
        if(listUrlFileFirst.size > 0) {
            binding.imageD1AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD1AttachmentReady.visibility = View.GONE
        }
    }

    private fun showTwoFieldReady() {
        if(listUrlFileTwo.size > 0) {
            binding.imageD2AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD2AttachmentReady.visibility = View.GONE
        }
    }


    private fun showFreeFieldReady() {
        if(listUrlFileFree.size > 0) {
            binding.imageD3AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD3AttachmentReady.visibility = View.GONE
        }
    }

    private fun showFourFieldReady() {
        if(listUrlFileFour.size > 0) {
            binding.imageD4AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD4AttachmentReady.visibility = View.GONE
        }
    }

    private fun showFiveFieldReady() {
        if(listUrlFileFive.size > 0) {
            binding.imageD5AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD5AttachmentReady.visibility = View.GONE
        }
    }




    fun getDocumentRef(context: Context): CollectionReference {
        val preference = MPreference(context)
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }



    private fun parseParams() {
        val args = requireArguments()
        situation1 = args.getString(SITUATION_ITEM).toString()
        // Toast.makeText(getActivity(),"all choice file" + situation9File, Toast.LENGTH_SHORT).show()
    }

    fun findMax(list: List<Int>): Int? {
        return list.reduce { a: Int, b: Int -> a.coerceAtLeast(b) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImageUtils.onImagePerResult(this, *grantResults)
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
                    if(boolFileFirst) {
                        listUrlFileFirst.add(imageUri)
                    } else if(boolFileTwo) {
                        listUrlFileTwo.add(imageUri)
                    } else if(boolFileFree) {
                        listUrlFileFree.add(imageUri)
                    } else if(boolFileFour) {
                        listUrlFileFour.add(imageUri)
                    } else if(boolFileFive) {
                        listUrlFileFive.add(imageUri)
                    }
                    // listUrlFile.add(imageUri)

                }
            } else if (data.getData() != null) {
                // var imagePath: String = data.data?.path!!
                val phUri =  ImageUtils.getPhotoUri(data)
                if (phUri != null) {
                    if(boolFileFirst) {
                        listUrlFileFirst.add(phUri)
                    } else if(boolFileTwo) {
                        listUrlFileTwo.add(phUri)
                    } else if(boolFileFree) {
                        listUrlFileFree.add(phUri)
                    } else if(boolFileFour) {
                        listUrlFileFour.add(phUri)
                    } else if(boolFileFive) {
                        listUrlFileFive.add(phUri)
                    }
                    // listUrlFile.add(phUri)
                }

            }

            if(boolFileFirst) {
                showFirstFieldReady()
                boolFileFirst = false
            } else if(boolFileTwo) {
                showTwoFieldReady()
                boolFileTwo = false
            } else if(boolFileFree) {
                showFreeFieldReady()
                boolFileFree = false
            } else if(boolFileFour) {
                showFourFieldReady()
                boolFileFour = false
            } else if(boolFileFive) {
                showFiveFieldReady()
                boolFileFive = false
            }

            //  Log.d("imageArrayList", listUrlFile.toString())
            //  uploadImages("diplomdata")
            // displayImageData()
        }

    }



    private fun addLeadDb() {
        val uid = preference.getUid()
        val lastIdLead = getDocumentRef(context)
        lastIdLead.get()
            .addOnSuccessListener { result ->
                //Log.d("lastid", "${result.last().id}")
                var leadId: Int
                if (result.isEmpty) {
                    leadId = 0
                    situationId = leadId.toString()
                } else {
                    if((result.last().id).toInt() >= 0){
                        val arraListInt = ArrayList<Int>()
                        for (document in result) {
                            //Log.d("TAG", "${document.id} => ${document.data}")
                            arraListInt.add(document.id.toInt())
                        }
                        leadId = findMax(arraListInt)!! + 1
                        situationId = leadId.toString()
                    } else {
                        leadId = 0
                        situationId = leadId.toString()
                    }
                }
                // createLead()

                /*  val lead = LeadItem(arrayValue.get(0).toString(), arrayValue.get(1).toString(), arrayValue.get(2).toString(), arrayValue.get(3).toString(), arrayValue.get(4).toString(),
                      arrayValue.get(5).toString(), arrayValue.get(6).toString(), arrayValue.get(7).toString(), arrayValue.get(8).toString(), arrayValue.get(9).toString(), arrayValue.get(9).toString(),
                      uid.toString(), "", arrayValue.get(9).toString(), "newLead",  leadId)*/
                val messLead = binding.etMessageData.text.toString()

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val currentDate = sdf.format(Date())

                val lead = LeadItem(situation1, "", "", "", "", "", "", "", "", "", messLead,
                    uid.toString(), "", "appliances", "newLead", currentDate, "", leadId)


                val db = FirebaseFirestore.getInstance()
                db.collection("Leads").document(lead.id.toString())
                    .set(lead, SetOptions.merge())
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }

                //uploadImages(leadId.toString())
                getReadyImagesForUpload(leadId.toString())
                launchFragmentNext()
                /* */
                /*for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }*/
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    fun launchFragmentNext() {
        val btnArgsLessons = Bundle().apply {
            putString(FSituationAppliances3.SITUATION_ITEM, situationId)
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.navigate(R.id.action_FSituationAppliances2_to_FSituationAppliances3, btnArgsLessons)
    }


    companion object {
        const val SITUATION_ITEM = "situation_item"
    }


}