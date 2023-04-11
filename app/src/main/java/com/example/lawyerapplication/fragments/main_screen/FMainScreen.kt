package com.example.lawyerapplication.fragments.main_screen

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImage
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.AlertLogoutBinding
import com.example.lawyerapplication.databinding.FMainScreenBinding
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.SearchBySituationAdapterHorizontal
import com.example.lawyerapplication.fragments.myprofile.FMyProfileViewModel
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FMainScreen : Fragment(R.layout.f_main_screen) {

    private lateinit var binding: FMainScreenBinding

    @Inject
    lateinit var preferenec: MPreference

    @Inject
    lateinit var db: ChatUserDatabase

    private lateinit var dialog: Dialog

    private val viewModel: FMyProfileViewModel by viewModels()

    private lateinit var context: Activity

    private var progressView: CustomProgressView? = null

    private lateinit var navController: NavController

    private lateinit var situationListAdapter: SearchBySituationAdapterHorizontal


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FMainScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(activity, "this is main screen", Toast.LENGTH_SHORT).show()
        context = requireActivity()
        progressView = CustomProgressView(context)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)


        val listArraySituation: ArrayList<SituationItem> = ArrayList()
        binding.rvSituationList.setLayoutManager(LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false))
        //binding.rvSituationList.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
        setupRecyclerView()

        val urlImg1 = getURLForResource(R.drawable.auto_main)
        val urlImg2 = getURLForResource(R.drawable.nov_main)
        val urlImg3 = getURLForResource(R.drawable.new_buildings_s3)
        val urlImg4 = getURLForResource(R.drawable.furniture_s4)
        val urlImg5 = getURLForResource(R.drawable.medical_services_s5)
        val urlImg6 = getURLForResource(R.drawable.clothing_s6)
        listArraySituation.add(SituationItem(0, "Автомобили", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg1.toString()))
        listArraySituation.add(SituationItem(1, "Бытовая техника", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg2.toString()))
        listArraySituation.add(SituationItem(2, "Новостройки", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg3.toString()))
        listArraySituation.add(SituationItem(3, "Мебель", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg4.toString()))
        listArraySituation.add(SituationItem(4, "Медицинские услуги", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg5.toString()))
        listArraySituation.add(SituationItem(5, "Одежда", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg6.toString()))
/*
        for (item in 6..85) {
            val situation = SituationItem(item, "cars name"+ item.toString(), urlImg5.toString())
            listArraySituation.add(situation)
        }*/
        situationListAdapter.submitList(listArraySituation)

        /*binding.btnSaveChanges.setOnClickListener {
            val newName = viewModel.userName.value
            val about = viewModel.about.value
            val image=viewModel.imageUrl.value
            when {
                viewModel.isUploading.value!! -> context.toast("Profile picture is uploading!")
                newName.isNullOrBlank() -> context.toast("User name can't be empty!")
                else -> {
                    context.window.decorView.clearFocus()
                    viewModel.saveChanges(newName,about ?: "" ,image ?: "")
                }
            }
        }*/

       // initDialog()
        //subscribeObservers()
        binding.allServicesMainScreen.setOnClickListener {
            navController.navigate(R.id.action_FMainScreen_to_FSituation)
        }


    }

    private fun subscribeObservers() {
        viewModel.profileUpdateState.observe(viewLifecycleOwner, {
            if (it is LoadState.OnLoading) {
                progressView?.show()
            } else
                progressView?.dismiss()
        })
    }

    private fun initDialog() {
        try {
            dialog = Dialog(requireContext())
            val layoutBinder = AlertLogoutBinding.inflate(layoutInflater)
            dialog.setContentView(layoutBinder.root)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutBinder.txtOk.setOnClickListener {
                dialog.dismiss()
                UserUtils.logOut(requireActivity(), preferenec, db)
            }
            layoutBinder.txtCancel.setOnClickListener {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getURLForResource(resourceId: Int): String? {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + R::class.java.getPackage().getName() + "/" + resourceId
        ).toString()
    }

    private fun setupRecyclerView() {
        with(binding.rvSituationList) {
            situationListAdapter = SearchBySituationAdapterHorizontal()
            adapter = situationListAdapter
            recycledViewPool.setMaxRecycledViews(
                SearchBySituationAdapter.VIEW_TYPE_ENABLED,
                SearchBySituationAdapter.MAX_POOL_SIZE
            )

        }

        setupClickListener()
    }

    private fun setupClickListener() {
        situationListAdapter.onPaymentItemClickListener = {

            /*when(it.id) {
                0 -> launchFragment(CreateSituationAutoOneFragment())
                1 -> launchFragment(CreateSituationAppliancesOneFragment())
                2 -> launchFragment(CreateSituationNewBuildingsOneFragment())
                3 -> launchFragment(CreateSituationFurnitureOneFragment())
                4 -> launchFragment(CreateSituationMedicalServicesOneFragment())
                5 -> launchFragment(CreateSituationClothingOneFragment())
            }*/
        }
    }

    private fun launchFragment() {
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        navController.navigate(R.id.action_FMainScreen_to_FSituation)
    }

}