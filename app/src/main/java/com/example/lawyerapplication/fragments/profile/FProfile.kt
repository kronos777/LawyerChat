package com.example.lawyerapplication.fragments.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FProfileBinding
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FProfile : Fragment() {

    private lateinit var binding: FProfileBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private var progressView: CustomProgressView? = null

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var PICK_IMAGE_MULTIPLE = 1
    lateinit var imagePath: String
    var imagesPathList: MutableList<String> = arrayListOf()
    private val PICK_IMAGE_CODE = 2
    private var imageUri: Uri? = null
    private var isLawyer: Boolean? = false

    private lateinit var listUrlFileFirst: ArrayList<Uri>
    private var boolFileFirst: Boolean = false
    private lateinit var listUrlFileTwo: ArrayList<Uri>
    private var boolFileTwo: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context = requireActivity()
        UserUtils.updatePushToken(context,userCollection,true)
        EventBus.getDefault().post(UserStatus())
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel
        progressView = CustomProgressView(context)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()



      //  binding.imgProPic.setOnClickListener { ImageUtils.askPermission(this) }


        hideLawyerFields()

        val items = listOf("Клиент", "Юрист")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_role, items)
        (binding.etRole as? AutoCompleteTextView)?.setAdapter(adapter)

        // Минимальное число символов для показа выпадающего списка
        binding.etRole.threshold = 2

        // Обработчик щелчка
        binding.etRole.onItemClickListener = AdapterView.OnItemClickListener { parent, _,
                                                                               position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            // Выводим выбранное слово
            //Toast.makeText(getContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            if(selectedItem == "Юрист") {
                showLawyerFields()
                isLawyer = true
              /*  binding.etDiplomData.setOnClickListener {
                    multipleChoiseImage()
                }*/
            } else {
                hideLawyerFields()
                isLawyer = false
            }


        }

        subscribeObservers()

        listUrlFileFirst = ArrayList<Uri>()
        listUrlFileTwo = ArrayList<Uri>()
        //first field
        binding.imageViewPassport.setOnClickListener {
            boolFileFirst = true
            multipleChoiseImage()
        }

        binding.imageD1AttachmentReady.setOnClickListener {
            listUrlFileFirst.clear()
            showFirstFieldReady()
        }
        //first field

        //two field
        binding.imageViewDiplom.setOnClickListener {
            boolFileTwo = true
            multipleChoiseImage()
        }

        binding.imageD2AttachmentReady.setOnClickListener {
            listUrlFileTwo.clear()
            showTwoFieldReady()
        }
        //two field


        binding.saveButton.setOnClickListener {
            if(binding.checkboxRememberMe.isChecked) {

                if(isLawyer == true) {
                    if(listUrlFileFirst.size == 0 && listUrlFileTwo.size == 0 && isLawyer == true) {
                        Toast.makeText(getContext(), "Загрузите данные пасспорта и диплома."+preference.getUid(), Toast.LENGTH_SHORT).show()
                    } else if(listUrlFileFirst.size > 0 || listUrlFileTwo.size > 0) {
                        getReadyImagesForUpload()
                        validate()
                    }
                } else if(isLawyer == false) {
                    validate()
                }

            } else {
                Toast.makeText(getContext(), "Дайте свое согласие на обработку данных", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun subscribeObservers() {
        viewModel.progressProPic.observe(viewLifecycleOwner, { uploaded ->
        //    binding.progressPro.toggle(uploaded)
        })

        viewModel.profileUpdateState.observe(viewLifecycleOwner, {
            when (it) {
                is LoadState.OnSuccess -> {
                    if (findNavController().isValidDestination(R.id.FProfile)) {
                        progressView?.dismiss()
                        findNavController().navigate(R.id.action_FProfile_to_FMainScreen)
                        //findNavController().navigate(R.id.action_FProfile_to_FSingleChatHome)
                    }
                }
                is LoadState.OnFailure -> {
                    progressView?.dismiss()
                }
                is LoadState.OnLoading -> {
                    progressView?.show()
                }
            }
        })

        viewModel.checkUserNameState.observe(viewLifecycleOwner,{
            when (it) {
                is LoadState.OnFailure -> {
                    progressView?.dismiss()
                }
                is LoadState.OnLoading -> {
                    progressView?.show()
                }
            }
        })
    }

    private fun validate() {
        val name = viewModel.name.value
       if(isLawyer == true){
            viewModel.role.value = "Lawyer"
        } else {
            viewModel.role.value = "Client"
        }

        /*viewModel.role.value = "Client"*/
        if (!name.isNullOrEmpty() && name.length > 1 && !viewModel.progressProPic.value!!)
            viewModel.storeProfileData()
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
                    }

                }
            } else if (data.getData() != null) {
                // var imagePath: String = data.data?.path!!
                val phUri =  ImageUtils.getPhotoUri(data)
                if (phUri != null) {
                    if(boolFileFirst) {
                        listUrlFileFirst.add(phUri)
                    } else if(boolFileTwo) {
                        listUrlFileTwo.add(phUri)
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

    private fun onCropResult(data: Intent?) {
        try {
            val imagePath: Uri? = ImageUtils.getCroppedImage(data)
            imagePath?.let {
                viewModel.uploadProfileImage(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImageUtils.onImagePerResult(this, *grantResults)
    }

    override fun onDestroy() {
        try {
            progressView?.dismissIfShowing()
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideLawyerFields() {
        binding.tilLastname.visibility = (View.GONE)
        binding.tilPassportData.visibility = (View.GONE)
        binding.textPassportData.visibility = (View.GONE)
        binding.tilDiplomData.visibility = (View.GONE)
        binding.textDiplomData.visibility = (View.GONE)
        binding.imageViewPassport.visibility = (View.GONE)
        binding.imageViewDiplom.visibility = (View.GONE)
        binding.textDiplomData.visibility = (View.GONE)
        binding.textPassportData.visibility = (View.GONE)/**/
    }

    private fun showLawyerFields() {
        binding.tilLastname.visibility = (View.VISIBLE)
        binding.tilPassportData.visibility = (View.VISIBLE)
        //binding.etPassportData.visibility = (View.VISIBLE)
        binding.textPassportData.visibility = (View.VISIBLE)
        binding.tilDiplomData.visibility = (View.VISIBLE)
        //binding.etDiplomData.visibility = (View.VISIBLE)
        binding.textDiplomData.visibility = (View.VISIBLE)
        binding.imageViewPassport.visibility =  (View.VISIBLE)
        binding.imageViewDiplom.visibility =  (View.VISIBLE)
        binding.textDiplomData.visibility =  (View.VISIBLE)
        binding.textPassportData.visibility =  (View.VISIBLE)/**/
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

    private fun getReadyImagesForUpload() {
        val listAll: ArrayList<ArrayList<Uri>> = ArrayList<ArrayList<Uri>>()
        listAll.add(listUrlFileFirst)
        listAll.add(listUrlFileTwo)
        //проверим все списки файлы
        for (index in listAll.indices) {
            if(listAll[index].size > 0) {
                var categoryFile = ""
                when(index) {
                    0 -> categoryFile = "passportGroup"
                    1 -> categoryFile = "diplomGroup"
                }
                uploadImages(listAll[index], categoryFile)
                //Log.d("thisIndex ", index.toString())
            }

        }

    }
    private fun uploadImages(dataUrl: ArrayList<Uri>, category: String) {

        for (index in dataUrl.indices) {
            val imageUri = dataUrl[index]
            //contentResolver.takePersistableUriPermission(imageUri, takeFlags)
            if (imageUri != null) {
                val progressDialog = ProgressDialog(getActivity())
                progressDialog.setTitle("Загрузка...")
                progressDialog.show()
                val ref: StorageReference =
                    storageReference.child("Users/" + preference.getUid() + "/diplomPassport/" + category + "_image" + index)
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
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot) {
                            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                .totalByteCount
                            progressDialog.setMessage("Загрузка " + progress.toInt() + "%")
                        }
                    })

            }
        }
    }

}