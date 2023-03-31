package com.example.lawyerapplication.fragments.main_screen

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.canhub.cropper.CropImage
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.AlertLogoutBinding
import com.example.lawyerapplication.databinding.FMainScreenBinding
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.fragments.myprofile.FMyProfileViewModel
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FMainScreen : Fragment(R.layout.f_main_screen) {

    private lateinit var binding: FMainScreenBinding

    @Inject
    lateinit var preferenec: MPreference

    @Inject
    lateinit var db: ChatUserDatabase

    private lateinit var dialog: Dialog

    private val viewModel: FMyProfileViewModel by viewModels()

    private lateinit var context: Activity

    private var progressView: CustomProgressView? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FMainScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(activity, "this is profile", Toast.LENGTH_SHORT).show()
        context = requireActivity()
        progressView = CustomProgressView(context)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.imageProfile.setOnClickListener {
             ImageUtils.askPermission(this)
        }
        /*binding.btnSaveChanges.setOnClickListener {
            val newName = viewModel.userName.value
            val about = viewModel.about.value
            val image=viewModel.imageUrl.value
            when {
                viewModel.isUploading.value!! -> context.toast("Profile picture is uploading!")
                newName.isNullOrBlank() -> context.toast("User name can't be empty!")
                else -> {
                    context.window.decorView.clearFocus()
                    viewModel.saveChanges(newName,about ?: "" ,image ?: "")
                }
            }
        }*/
        binding.aboutApplicationHref2Delete.setOnClickListener {
        //binding.btnLogout.setOnClickListener {
            dialog.show()
        }

        binding.cardAboutCard.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.aboutApplication, null, navOptions)
        }

        binding.changeUserSettings.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.userAccountSettings, null, navOptions)
        }

        initDialog()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.profileUpdateState.observe(viewLifecycleOwner, {
            if (it is LoadState.OnLoading) {
                progressView?.show()
            } else
                progressView?.dismiss()
        })
    }

    private fun initDialog() {
        try {
            dialog = Dialog(requireContext())
            val layoutBinder = AlertLogoutBinding.inflate(layoutInflater)
            dialog.setContentView(layoutBinder.root)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutBinder.txtOk.setOnClickListener {
                dialog.dismiss()
                UserUtils.logOut(requireActivity(), preferenec, db)
            }
            layoutBinder.txtCancel.setOnClickListener {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImageUtils.onImagePerResult(this, *grantResults)
    }
}