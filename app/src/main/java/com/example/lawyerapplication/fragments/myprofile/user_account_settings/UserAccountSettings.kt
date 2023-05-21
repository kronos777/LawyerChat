package com.example.lawyerapplication.fragments.myprofile.user_account_settings

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
import com.example.lawyerapplication.databinding.FragmentSettingUserAccountBinding
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.fragments.myprofile.FMyProfileViewModel
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UserAccountSettings : Fragment(R.layout.fragment_setting_user_account) {

    private lateinit var binding: FragmentSettingUserAccountBinding

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
        binding = FragmentSettingUserAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // Toast.makeText(activity, "this is profile", Toast.LENGTH_SHORT).show()
        context = requireActivity()
        progressView = CustomProgressView(context)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.changeEmailBotton.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.changeUserEmail, null, navOptions)
        }
        binding.changeEmailBotton.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.changeUserEmail, null, navOptions)
        }
        binding.changePasswordButton.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.changeUserPassword, null, navOptions)
        }
        binding.changeFotoProfile.setOnClickListener {
            ImageUtils.askPermission(this)
        }
      /*  binding.imageProfile.setOnClickListener {
             ImageUtils.askPermission(this)
        }



        binding.aboutApplicationInfo.setOnClickListener {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.aboutApplication, null, navOptions)
        }*/

    }


}