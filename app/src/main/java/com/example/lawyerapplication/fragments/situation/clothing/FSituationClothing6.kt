package com.example.lawyerapplication.fragments.situation.clothing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentSituationClothingS6Binding
import com.example.lawyerapplication.fragments.situation.SituationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FSituationClothing6 : Fragment() {

    private lateinit var binding: FragmentSituationClothingS6Binding

    private val args by navArgs<FSituationClothing6Args>()
    private var radioSelect: String = String()

    private var situationId: String = String()

    private val viewModelSituation: SituationViewModel by activityViewModels()

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationClothingS6Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = binding.radioGroupSituation
        parseParams()
        binding.enterButton.getBackground().setAlpha(160)
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
        navController.navigate(R.id.action_FSituationClothing6_to_FSituationFinish)
    }


}