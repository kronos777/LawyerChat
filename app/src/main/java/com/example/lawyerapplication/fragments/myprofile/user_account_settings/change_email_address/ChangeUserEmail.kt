package com.example.lawyerapplication.fragments.myprofile.user_account_settings.change_email_address

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
import com.example.lawyerapplication.databinding.FMyProfileBinding
import com.example.lawyerapplication.databinding.FragmentChangeUserEmailBinding
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.fragments.myprofile.FMyProfileViewModel
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class ChangeUserEmail : Fragment(R.layout.fragment_change_user_email) {

    private lateinit var binding: FragmentChangeUserEmailBinding

    @Inject
    lateinit var preferenec: MPreference

    @Inject
    lateinit var db: ChatUserDatabase

    private lateinit var dialog: Dialog

    private val viewModel: FMyProfileViewModel by viewModels()

    private lateinit var context: Activity

    private var progressView: CustomProgressView? = null

    private lateinit var navController: NavController

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeUserEmailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  Toast.makeText(activity, "this is profile", Toast.LENGTH_SHORT).show()
        context = requireActivity()
        progressView = CustomProgressView(context)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.enterButton.setOnClickListener {
            if(viewModel.email != binding.etEmail.text!!.toString().trim()) {
                binding.tilEmail.error = ""
                //ImageUtils.askPermission(this)
                if (isValidString(binding.etEmail.text.toString())) {
                    binding.tilEmail.error = ""
                   // Toast.makeText(activity, "isValid" + binding.etEmail.text, Toast.LENGTH_SHORT).show()
                    val newEmail = binding.etEmail.text.toString()
                    viewModel.updateProfileEmailData(newEmail)
                   // if(viewModel.profileUpdateState.value == LoadState.OnSuccess()){
                      //  context.toast("Vse ok mogem idti")
                        val navOptions =
                            NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                        Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.FMyProfile, null, navOptions)
                 //   }
                } else {
                    binding.tilEmail.error = "введите корректный адрес почты."
                   // Toast.makeText(activity, "not Valid" + binding.etEmail.text, Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.tilEmail.error = "введите новый адрес электронной почты."
            }



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
     /*   binding.aboutApplicationHref2Delete.setOnClickListener {
        //binding.btnLogout.setOnClickListener {
            dialog.show()
        }

        binding.aboutApplicationInfo.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.aboutApplication, null, navOptions)
        }

        initDialog()
        subscribeObservers()*/
    }

    fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

}