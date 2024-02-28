package com.example.lawyerapplication.fragments.situation.medical_services

import android.app.Activity
import android.content.Context
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
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import com.example.lawyerapplication.fragments.situation.auto.FSituationAuto10
import com.example.lawyerapplication.fragments.situation.finish.FSituationFinish
import com.example.lawyerapplication.fragments.situation.furniture.FSituationFurniture5Args
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FSituationMedicalServices3 : Fragment() {

    private lateinit var binding: FragmentSituationMedicalServicesS3Binding

    private val args by navArgs<FSituationFurniture5Args>()
    private var radioSelect: String = String()

    private var situationId: String = String()

    private val viewModelSituation: SituationViewModel by activityViewModels()

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationMedicalServicesS3Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = binding.radioGroupSituation
        parseParams()
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
            if(binding.checkboxRememberMe.isChecked) {
                viewModelSituation.editLeadPaymentInfo(situationId, radioSelect)
                launchFragmentNext()
            } else {
                Toast.makeText(context, "Дайте свое согласие на обработку данных", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
    }


    private fun parseParams() {
        situationId = args.leadId
    }

    fun launchFragmentNext() {
        navController.navigate(R.id.action_FSituationMedicalServices3_to_FSituationFinish)
    }

}