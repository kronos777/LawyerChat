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

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var navController: NavController
    private var radioSelect: String = String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationMedicalServicesS1Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        val radioGroup = binding.radioGroupSituation

        binding.enterButton.getBackground().setAlpha(160)
        binding.enterButton.isClickable = false
        binding.enterButton.isEnabled = false


        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                binding.enterButton.getBackground().setAlpha(255)
                getMaterialButtom()
                val radio: RadioButton = group.findViewById(checkedId)
                /*Toast.makeText(getActivity()," On checked change :"+
                        " ${radio.text}",
                    Toast.LENGTH_SHORT).show()*/
                radioSelect = radio.text.toString()
            })

        binding.enterButton.setOnClickListener {
            launchFragmentNext()
        }

    }

    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
    }


    fun launchFragmentNext() {
        val btnArgsLessons = Bundle().apply {
            putString(FSituationMedicalServices2.SITUATION_ITEM, radioSelect)
        }
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_FSituationMedicalServices1_to_FSituationMedicalServices2, btnArgsLessons)
    }

}