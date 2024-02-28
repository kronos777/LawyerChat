package com.example.lawyerapplication.fragments.situation.new_buidings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import com.example.lawyerapplication.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FSituationNewBuildings2 : Fragment() {

    private lateinit var binding: FragmentSituationNewBuildingsS2Binding

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private var radioSelect: String = String()
    private val viewModelSituation: SituationViewModel by activityViewModels()

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
    private lateinit var listUrlFileSix: ArrayList<Uri>
    private var boolFileSix: Boolean = false
    private lateinit var listUrlFileSeven: ArrayList<Uri>
    private var boolFileSeven: Boolean = false
    private lateinit var listUrlFileEight: ArrayList<Uri>
    private var boolFileEight: Boolean = false



    var PICK_IMAGE_MULTIPLE = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationNewBuildingsS2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listUrlFileFirst = ArrayList<Uri>()
        listUrlFileTwo = ArrayList<Uri>()
        listUrlFileFree = ArrayList<Uri>()
        listUrlFileFour = ArrayList<Uri>()
        listUrlFileFive = ArrayList<Uri>()
        listUrlFileSix = ArrayList<Uri>()
        listUrlFileSeven = ArrayList<Uri>()
        listUrlFileEight = ArrayList<Uri>()


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
        binding.textD5Attachment.setOnClickListener {
            boolFileFive = true
            multipleChoiseImage()
        }

        binding.imageD5AttachmentReady.setOnClickListener {
            listUrlFileFive.clear()
            showFiveFieldReady()
        }
        //five field
        //six field
        binding.textD6Attachment.setOnClickListener {
            boolFileSix = true
            multipleChoiseImage()
        }

        binding.imageD6AttachmentReady.setOnClickListener {
            listUrlFileSix.clear()
            showSixFieldReady()
        }
        //six field
        //seven field
        binding.textD7Attachment.setOnClickListener {
            boolFileSeven = true
            multipleChoiseImage()
        }

        binding.imageD7AttachmentReady.setOnClickListener {
            listUrlFileSeven.clear()
            showSevenFieldReady()
        }
        //seven field
        //eight field
        binding.textD8Attachment.setOnClickListener {
            boolFileEight = true
            multipleChoiseImage()
        }

        binding.imageD8AttachmentReady.setOnClickListener {
            listUrlFileEight.clear()
            showEightFieldReady()
        }
        //eight field

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


    private fun checkChoiceFile(): Boolean {
        return !(listUrlFileFirst.size == 0 && listUrlFileTwo.size == 0 && listUrlFileFree.size == 0 && listUrlFileFour.size == 0 && listUrlFileFive.size == 0
                && listUrlFileSix.size == 0 && listUrlFileSeven.size == 0 && listUrlFileEight.size == 0)
    }

    private suspend fun addLeadAndGoNext() {
        /*set data for lead*/
        val leadId = viewModelSituation.lastLeadInDb.await()
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())
        val lead = LeadItem(viewModelSituation.valueQuestionData[0].toString(),
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            binding.etMessageData.text.toString(),
            viewModelSituation.getUid(), "", "newBuildings",
            "newLead", currentDate, "", leadId)
        viewModelSituation.addLead(lead)
        getReadyImagesForUpload(lead.id.toString())
        launchFragmentNext(lead.id.toString())
    }


    private fun getReadyImagesForUpload(paramsUpload: String) {
        val listAll: ArrayList<ArrayList<Uri>> = ArrayList<ArrayList<Uri>>()
        listAll.add(listUrlFileFirst)
        listAll.add(listUrlFileTwo)
        listAll.add(listUrlFileFree)
        listAll.add(listUrlFileFour)
        listAll.add(listUrlFileFive)
        listAll.add(listUrlFileSix)
        listAll.add(listUrlFileSeven)
        listAll.add(listUrlFileEight)
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
                    5 -> categoryFile = "sixGroup"
                    6 -> categoryFile = "sevenGroup"
                    7 -> categoryFile = "eightGroup"
                }
                viewModelSituation.uploadImages(paramsUpload, listAll[index], categoryFile)
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

    private fun showSixFieldReady() {
        if(listUrlFileSix.size > 0) {
            binding.imageD6AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD6AttachmentReady.visibility = View.GONE
        }
    }

    private fun showSevenFieldReady() {
        if(listUrlFileSeven.size > 0) {
            binding.imageD7AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD7AttachmentReady.visibility = View.GONE
        }
    }

    private fun showEightFieldReady() {
        if(listUrlFileEight.size > 0) {
            binding.imageD8AttachmentReady.visibility = View.VISIBLE
        } else {
            binding.imageD8AttachmentReady.visibility = View.GONE
        }
    }


    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
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
                    } else if(boolFileSix) {
                        listUrlFileSix.add(imageUri)
                    } else if(boolFileSeven) {
                        listUrlFileSeven.add(imageUri)
                    } else if(boolFileEight) {
                        listUrlFileEight.add(imageUri)
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
                    } else if(boolFileSix) {
                        listUrlFileSix.add(phUri)
                    } else if(boolFileSeven) {
                        listUrlFileSeven.add(phUri)
                    } else if(boolFileEight) {
                        listUrlFileEight.add(phUri)
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
            } else if(boolFileSix) {
                showSixFieldReady()
                boolFileSix = false
            } else if(boolFileSeven) {
                showSevenFieldReady()
                boolFileSeven = false
            } else if(boolFileEight) {
                showEightFieldReady()
                boolFileEight = false
            }


           }

    }

    fun launchFragmentNext(idLead: String) {
        navController.navigate(FSituationNewBuildings2Directions.actionFSituationNewBuildings2ToFSituationNewBuildings3(idLead))
    }

}