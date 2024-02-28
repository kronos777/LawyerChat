package com.example.lawyerapplication.fragments.situation.medical_services

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FProfileBinding
import com.example.lawyerapplication.databinding.FragmentChoiceBySituationBinding
import com.example.lawyerapplication.databinding.FragmentSituationAutoS1Binding
import com.example.lawyerapplication.databinding.FragmentSituationMedicalServicesS1Binding
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
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
import javax.inject.Inject

@AndroidEntryPoint
class FSituationMedicalServices1 : Fragment() {

    private lateinit var binding: FragmentSituationMedicalServicesS1Binding

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private var radioSelect: String = String()
    private val viewModelSituation: SituationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentSituationMedicalServicesS1Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = binding.radioGroupSituation

        binding.enterButton.background.alpha = 160
        binding.enterButton.isClickable = false
        binding.enterButton.isEnabled = false


        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.enterButton.background.alpha = 255
            getMaterialButtom()
            val radio: RadioButton = group.findViewById(checkedId)
            radioSelect = radio.text.toString()
        }

        binding.enterButton.setOnClickListener {
            launchFragmentNext()
        }

    }

    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
    }


    fun launchFragmentNext() {
        viewModelSituation.clearValueQuestionData()
        viewModelSituation.setDataSituationValue(0, radioSelect)
        navController.navigate(R.id.action_FSituationMedicalServices1_to_FSituationMedicalServices2)
    }

}