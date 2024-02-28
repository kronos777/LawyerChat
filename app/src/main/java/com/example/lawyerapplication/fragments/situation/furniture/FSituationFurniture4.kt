package com.example.lawyerapplication.fragments.situation.furniture

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import com.example.lawyerapplication.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FSituationFurniture4 : Fragment() {

    private lateinit var binding: FragmentSituationFurnitureS4Binding

    private val viewModelSituation: SituationViewModel by activityViewModels()

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }


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

    private val PICK_IMAGE_MULTIPLE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationFurnitureS4Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareFileField()

        binding.enterButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if(checkChoiceFile()) {
                    addLeadAndGoNext()
                } else {
                    Toast.makeText(activity, "Не было выбрано не одного файла.", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun prepareFileField() {
        listUrlFileFirst = ArrayList<Uri>()
        listUrlFileTwo = ArrayList<Uri>()
        listUrlFileFree = ArrayList<Uri>()
        listUrlFileFour = ArrayList<Uri>()
        listUrlFileFive = ArrayList<Uri>()

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
    }

    private fun checkChoiceFile(): Boolean {
        return !(listUrlFileFirst.size == 0 && listUrlFileTwo.size == 0 && listUrlFileFree.size == 0 && listUrlFileFour.size == 0 && listUrlFileFive.size == 0)
    }

    private suspend fun addLeadAndGoNext() {
        /*set data for lead*/
        val leadId = viewModelSituation.lastLeadInDb.await()
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())
        val lead = LeadItem(viewModelSituation.valueQuestionData[0].toString(),
            viewModelSituation.valueQuestionData[1].toString(),
            viewModelSituation.valueQuestionData[2].toString(),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            binding.etMessageData.text.toString(),
            viewModelSituation.getUid(), "", "furniture",
            "newLead", currentDate, "", leadId)
        viewModelSituation.addLead(lead)
        getReadyImagesForUpload(lead.id.toString())
        launchFragmentNext(lead.id.toString())
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


        }

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



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImageUtils.onImagePerResult(this, *grantResults)
    }




    fun launchFragmentNext(idLead: String) {
        navController.navigate(FSituationFurniture4Directions.actionFSituationFurniture4ToFSituationFurniture5(idLead))
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
                viewModelSituation.uploadImages(paramsUpload, listAll[index], categoryFile)
            }

        }

    }

}