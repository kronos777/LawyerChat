package com.example.lawyerapplication.fragments.situation.new_buidings

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
import com.example.lawyerapplication.fragments.situation.medical_services.FSituationMedicalServices3
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FSituationNewBuildings3 : Fragment() {

    private lateinit var binding: FragmentSituationNewBuildingsS3Binding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var navController: NavController
    private var radioSelect: String = String()
    private var situationId: String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationNewBuildingsS3Binding.inflate(layoutInflater, container, false)
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
                radioSelect = radio.text.toString()
            })



        binding.enterButton.setOnClickListener {
            if(binding.checkboxRememberMe.isChecked) {
                val data = hashMapOf("paymentInfo" to radioSelect)
                val docRef = getDocumentRef(context).document(situationId)
                docRef.set(data, SetOptions.merge())
                launchFragmentNext()
            } else {
                Toast.makeText(getContext(), "Дайте свое согласие на обработку данных", Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun getDocumentRef(context: Context): CollectionReference {
        val preference = MPreference(context)
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }


    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
    }


    private fun parseParams() {
        val args = requireArguments()
        situationId = args.getString(SITUATION_ITEM).toString()
        //Toast.makeText(getActivity(),"situationId" + situationId, Toast.LENGTH_SHORT).show()
        // Toast.makeText(getActivity(),"all choice file" + situation9File, Toast.LENGTH_SHORT).show()
    }

    fun launchFragmentNext() {
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_FSituationNewBuildings3_to_FSituationFinish)
    }

    companion object {
        const val SITUATION_ITEM = "situation_item"
    }
}