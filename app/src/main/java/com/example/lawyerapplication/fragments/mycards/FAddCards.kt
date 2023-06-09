package com.example.lawyerapplication.fragments.mycards

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentAddBanksCardBinding
import com.example.lawyerapplication.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FAddCards : Fragment() {

    private lateinit var binding: FragmentAddBanksCardBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private val viewModel: ViewModelMyCards by viewModels()

    private lateinit var navController: NavController
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement OnEditingFinishedListener")
        }
    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
        }
    }
    interface OnEditingFinishedListener {

        fun onEditingFinished()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentAddBanksCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        observeViewModel()

        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        //binding.etCardNumber.addTextChangedListener(PhoneTextFormatter(binding.etCardNumber, "+7 (###) ###-####"))
        binding.etCvs.addTextChangedListener(PhoneTextFormatter(binding.etCvs, "###"))
        binding.etValidity.addTextChangedListener(PhoneTextFormatter(binding.etValidity, "##/##"))
        binding.etCardNumber.addTextChangedListener(PhoneTextFormatter(binding.etCardNumber, "#### #### #### ####"))



        binding.enterButton.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val validity = binding.etValidity.text.toString()
            val cvs = binding.etCvs.text.toString()
            val res = viewModel.addUserCard(cardNumber, validity, cvs)
            setHideErrorInput()
            if (res == "ok") {
                navController.navigate(R.id.FMyCards)
            }
        }


    }

    private fun setHideErrorInput() {

        if(viewModel.errorInputCardNumber.value == true) {
            binding.tilCardNumber.error = "Проверьте правиальность ввода"
        } else {
            binding.tilCardNumber.error = ""
        }
        if(viewModel.errorInputValidity.value == true) {
            binding.tilValidity.error = "Проверьте правиальность ввода"
        } else {
            binding.tilValidity.error = ""
        }
        if(viewModel.errorInputCvs.value == true) {
            binding.tilCvs.error = "Проверьте правиальность ввода"
        } else {
            binding.tilCvs.error = ""
        }

    }



}