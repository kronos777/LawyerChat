package com.example.lawyerapplication.fragments.myprofile.user_account_settings.change_password

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
import com.example.lawyerapplication.databinding.FragmentPasswordChangeBinding
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.fragments.myprofile.FMyProfileViewModel
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class ChangeUserPassword : Fragment(R.layout.fragment_password_change) {

    private lateinit var binding: FragmentPasswordChangeBinding

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
        binding = FragmentPasswordChangeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context = requireActivity()
        progressView = CustomProgressView(context)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.changePasswordButton.setOnClickListener {

            val currentPassword = viewModel.password
            val oldPassword = binding.etOldPassword.text
            val newPassword = binding.etOldPasswordTwice.text
            val newPasswordRepeat = binding.etNewPassword.text

            if(currentPassword.toString() == oldPassword.toString()) {
                val validPasswd = ValidPassword().checkPassword(oldPassword.toString(), newPassword.toString(), newPasswordRepeat.toString())
                if (validPasswd == "ok") {
                    viewModel.updateProfilePasswordData(newPassword.toString())
                  //  if(viewModel.profileUpdateState.value == LoadState.OnSuccess()){
                        context.toast("Vse ok mogem idti")
                        val navOptions =
                            NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                        Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.userAccountSettings, null, navOptions)
                   // }
                } else  {
                    binding.tilNewPassword.error = validPasswd
                    binding.tilNewPassword.error = validPasswd
                    binding.tilNewPassword.error = validPasswd
                }

            } else {
                context.toast("текущий пароль" + currentPassword)
                context.toast("текущий из формы пароль" + oldPassword)
                binding.tilOldPassword.error = "неправильный пароль"
            }



        }

    }



}