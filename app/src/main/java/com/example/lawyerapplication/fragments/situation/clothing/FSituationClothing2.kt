package com.example.lawyerapplication.fragments.situation.clothing

import android.app.Activity
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
import com.example.lawyerapplication.fragments.situation.furniture.FSituationFurniture4
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FSituationClothing2 : Fragment() {

    private lateinit var binding: FragmentSituationClothingS2Binding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var navController: NavController
    private var radioSelect: String = String()

    private var situation1: String = String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSituationClothingS2Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        val radioGroup = binding.radioGroupSituation

        parseParams()

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

    private fun parseParams() {
        val args = requireArguments()
        situation1 = args.getString(SITUATION_ITEM).toString()
    }

    private fun getMaterialButtom() {
        binding.enterButton.isClickable = true
        binding.enterButton.isEnabled = true
    }


    fun launchFragmentNext() {
        val btnArgsLessons = Bundle().apply {
            putString(SITUATION_ITEM, situation1 +"&"+ radioSelect)
        }
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_FSituationClothing2_to_FSituationClothing3, btnArgsLessons)
    }

    companion object {
        const val SITUATION_ITEM = "situation_item"

    }


}