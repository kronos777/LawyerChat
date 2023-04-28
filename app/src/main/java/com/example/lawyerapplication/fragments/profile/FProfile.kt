package com.example.lawyerapplication.fragments.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
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


    var PICK_IMAGE_MULTIPLE = 1
    lateinit var imagePath: String
    var imagesPathList: MutableList<String> = arrayListOf()
    private val PICK_IMAGE_CODE = 2
    private var imageUri: Uri? = null
    private var isLawyer: Boolean? = null


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
      //  binding.imgProPic.setOnClickListener { ImageUtils.askPermission(this) }
        binding.saveButton.setOnClickListener {
            if(binding.checkboxRememberMe.isChecked) {
                validate()
            } else {
                Toast.makeText(getContext(), "Дайте свое согласие на обработку данных", Toast.LENGTH_SHORT).show()
            }

        }

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
  /*      if(isLawyer == true){
            viewModel.role.value = "Lawyer"
        } else {
            viewModel.role.value = "Client"
        }
*/
        viewModel.role.value = "Client"
        if (!name.isNullOrEmpty() && name.length > 1 && !viewModel.progressProPic.value!!)
            viewModel.storeProfileData()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            onCropResult(data)
        else
            ImageUtils.cropImage(context, data, true)
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
       /* binding.tilPassportData.visibility = (View.GONE)
        binding.textPassportData.visibility = (View.GONE)
        binding.tilDiplomData.visibility = (View.GONE)
        binding.textDiplomData.visibility = (View.GONE)
        binding.imageViewPassport.visibility = (View.GONE)
        binding.imageViewDiplom.visibility = (View.GONE)
        binding.textDiplomData.visibility = (View.GONE)
        binding.textPassportData.visibility = (View.GONE)*/
    }

    private fun showLawyerFields() {
        binding.tilLastname.visibility = (View.VISIBLE)
        /*binding.tilPassportData.visibility = (View.VISIBLE)
        binding.textPassportData.visibility = (View.VISIBLE)
        binding.tilDiplomData.visibility = (View.VISIBLE)
        binding.textDiplomData.visibility = (View.VISIBLE)
        binding.imageViewPassport.visibility =  (View.VISIBLE)
        binding.imageViewDiplom.visibility =  (View.VISIBLE)
        binding.textDiplomData.visibility =  (View.VISIBLE)
        binding.textPassportData.visibility =  (View.VISIBLE)*/
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

}