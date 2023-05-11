package com.example.lawyerapplication.fragments.mycards

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
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
import com.example.lawyerapplication.databinding.FragmentMyCardsBinding
import com.example.lawyerapplication.db.data.BanksCardItem
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.mycards.adapter.BanksCardAdapter
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FMyCards : Fragment() {

    private lateinit var binding: FragmentMyCardsBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var banksCardAdapter: BanksCardAdapter
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMyCardsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        val listArrayCards: ArrayList<BanksCardItem> = ArrayList()
        setupRecyclerView()
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)

        val urlImg1 = getURLForResource(R.drawable.auto_s1)
        val urlImg2 = getURLForResource(R.drawable.appliances_s2)
        val urlImg3 = getURLForResource(R.drawable.new_buildings_s3)
        val urlImg4 = getURLForResource(R.drawable.furniture_s4)
        val urlImg5 = getURLForResource(R.drawable.medical_services_s5)
        val urlImg6 = getURLForResource(R.drawable.clothing_s6)
        listArrayCards.add(BanksCardItem(0, "Автомобили", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg1.toString()))
        listArrayCards.add(BanksCardItem(1, "Бытовая техника", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg2.toString()))
        listArrayCards.add(BanksCardItem(2, "Новостройки", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg3.toString()))
        listArrayCards.add(BanksCardItem(3, "Мебель", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg4.toString()))
        listArrayCards.add(BanksCardItem(4, "Медицинские услуги", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg5.toString()))
        listArrayCards.add(BanksCardItem(5, "Одежда", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg6.toString()))
/*
        for (item in 6..85) {
            val situation = SituationItem(item, "cars name"+ item.toString(), urlImg5.toString())
            listArraySituation.add(situation)
        }*/
        banksCardAdapter.submitList(listArrayCards)

        binding.addBanksCard.setOnClickListener {
            navController.navigate(R.id.action_FMyCards_to_FAddCards)
        }
    }

    fun getURLForResource(resourceId: Int): String? {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + R::class.java.getPackage().getName() + "/" + resourceId
        ).toString()
    }

    private fun setupRecyclerView() {
        with(binding.rvCardsList) {
            banksCardAdapter = BanksCardAdapter()
            adapter = banksCardAdapter
            recycledViewPool.setMaxRecycledViews(
                BanksCardAdapter.VIEW_TYPE_ENABLED,
                BanksCardAdapter.MAX_POOL_SIZE
            )

        }
        setupClickListener()
    }

    private fun setupClickListener() {
        banksCardAdapter.onPaymentItemClickListener = {
            Toast.makeText(context, "we are here", Toast.LENGTH_SHORT).show()
           // navController.navigate(R.id.action_FSituation_to_FSituationMedicalServices1)
          /* when(it.id) {
                0 -> navController.navigate(R.id.action_FSituation_to_FSituationAuto1)
                1 -> navController.navigate(R.id.action_FSituation_to_FSituationAppliances1)
                2 -> navController.navigate(R.id.action_FSituation_to_FSituationNewBuildings1)
                3 -> navController.navigate(R.id.action_FSituation_to_FSituationFurniture1)
                4 -> navController.navigate(R.id.action_FSituation_to_FSituationMedicalServices1)
                5 -> navController.navigate(R.id.action_FSituation_to_FSituationClothing1)

            }*/
        }
    }

}