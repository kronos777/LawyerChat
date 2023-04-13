package com.example.lawyerapplication.fragments.situation.auto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FSituationAuto9 : Fragment() {

    private lateinit var binding: FragmentSituationAutoS9Binding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var navController: NavController

    private lateinit var listUrlFile: ArrayList<Uri>
    var PICK_IMAGE_MULTIPLE = 1
    lateinit var imagePath: String
    var imagesPathList: MutableList<String> = arrayListOf()

    private var radioSelect: String = String()
    private var situation8: String = String()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationAutoS9Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()

        parseParams()
        listUrlFile = ArrayList<Uri>()

       /* binding.enterButton.getBackground().setAlpha(160)
        binding.enterButton.isClickable = false
        binding.enterButton.isEnabled = false*/

        //first field upload
        binding.textD1Attachment.setOnClickListener {
            //ImageUtils.askPermission(this)
            multipleChoiseImage()
        }
        //first field upload




        binding.enterButton.setOnClickListener {

            if(listUrlFile.size > 0) {
                launchFragmentNext()
            }

        }

    }

    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true

    }


    private fun parseParams() {
        val args = requireArguments()
        situation8 = args.getString(SITUATION_ITEM).toString()
      // Toast.makeText(getActivity(),"first choice" + situation8, Toast.LENGTH_SHORT).show()
    }


    fun launchFragmentNext() {

        val btnArgsAuto = Bundle().apply {
            putString(FSituationAuto10.SITUATION_ITEM, situation8)
            putString(FSituationAuto10.SITUATION_ITEM_FILE,  listUrlFile.toString())
        }
        Log.d("allFile", listUrlFile.toString())
/*
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_FSituationAuto9_to_FSituationAuto10, btnArgsAuto)*/
    }


   // override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       // super.onActivityResult(requestCode, resultCode, data)
      /*  if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            onCropResult(data)
        else*/
            //ImageUtils.cropImage(context, data, true)
          //val phUri =  ImageUtils.getPhotoUri(data)
        //Toast.makeText(getActivity(),"img info" + phUri.toString(), Toast.LENGTH_SHORT).show()
   // }

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

    private fun getPathFromURI(uri: Uri) {
        var path: String = uri.path!! // uri = any content Uri

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/image:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.DATE_TAKEN
            ) // some example data you can query
            val cursor = getActivity()?.contentResolver?.query(
                databaseUri,
                projection, selection, selectionArgs, null
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(projection[0])
                    imagePath = cursor.getString(columnIndex)
                    // Log.e("path", imagePath);
                    imagesPathList.add(imagePath)
                }
            }
            if (cursor != null) {
                cursor.close()
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message, e)
        }
    }


    private fun onCropResult(data: Intent?) {
        try {
            val imagePath: Uri? = ImageUtils.getCroppedImage(data)
            imagePath?.let {
              //  viewModel.uploadProfileImage(it)
                Toast.makeText(getActivity(),"img info" + it.toString(), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    companion object {
        const val SITUATION_ITEM = "situation_item"
    }

}