package com.example.lawyerapplication.fragments.situation.appliances

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentSituationAppliancesS1Binding
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import com.example.lawyerapplication.utils.MPreference
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FSituationAppliances1 : Fragment() {

    private lateinit var binding: FragmentSituationAppliancesS1Binding

    private lateinit var context: Activity

    private val viewModelSituation: SituationViewModel by activityViewModels()

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }


    private var radioSelect: String = String()


   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationAppliancesS1Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
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
        navController.navigate(R.id.action_FSituationAppliances1_to_FSituationAppliances2)
    }

}