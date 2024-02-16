package com.example.lawyerapplication.fragments.situation.appliances

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
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.situation.SituationViewModel
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
class FSituationAppliances3 : Fragment() {

    private lateinit var binding: FragmentSituationAppliancesS3Binding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private val viewModelSituation: SituationViewModel by activityViewModels()

    private lateinit var navController: NavController
    private var radioSelect: String = String()
    private var situationId: String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationAppliancesS3Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
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


        goCalendarFragmentBackPressed()
        binding.enterButton.setOnClickListener {
            if(binding.checkboxRememberMe.isChecked) {
                viewModelSituation.editLeadPaymentInfo(situationId, radioSelect)
                launchFragmentNext()
                /*val data = hashMapOf("paymentInfo" to radioSelect)
                val docRef = getDocumentRef(context).document(situationId)
                docRef.set(data, SetOptions.merge())
                launchFragmentNext()*/
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

    private fun goCalendarFragmentBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Toast.makeText(activity, "he are here", Toast.LENGTH_SHORT).show()
           // navController.popBackStack(R.id.calendarItemFragment, true)
            //navController.navigate(R.id.calendarItemFragment, null, NavigationOptions().invoke())
        }
    }
    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
    }


    private fun parseParams() {
        val args = requireArguments()
        situationId = args.getString(SITUATION_ITEM).toString()
        Toast.makeText(activity, situationId, Toast.LENGTH_SHORT).show()
    }

    fun launchFragmentNext() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.navigate(R.id.action_FSituationAppliances3_to_FSituationFinish)
    }


    companion object {
        const val SITUATION_ITEM = "situation_item"
    }

}