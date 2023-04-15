package com.example.lawyerapplication.fragments.my_business

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentMyBussinesMainBinding
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.MyBusinessAdapterHorizontal
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.utils.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FMyBussines_main : Fragment() {

    private lateinit var binding: FragmentMyBussinesMainBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var myBusinessListAdapter: MyBusinessAdapterHorizontal
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //(activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMyBussinesMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        val listArraySituation: ArrayList<BusinessItem> = ArrayList()
        setupRecyclerView()
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)

        Toast.makeText(context, "this fragment my bussines", Toast.LENGTH_SHORT).show()
        //(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)



        //val urlImg1 = getURLForResource(R.drawable.auto_s1)
        //val urlImg2 = getURLForResource(R.drawable.appliances_s2)
        //val urlImg3 = getURLForResource(R.drawable.new_buildings_s3)
        //val urlImg4 = getURLForResource(R.drawable.furniture_s4)
        //val urlImg5 = getURLForResource(R.drawable.medical_services_s5)
       // val urlImg6 = getURLForResource(R.drawable.clothing_s6)
      //  listArraySituation.add(SituationItem(0, "Автомобили", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg1.toString()))
       // listArraySituation.add(SituationItem(1, "Бытовая техника", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg2.toString()))
     //   listArraySituation.add(SituationItem(2, "Новостройки", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg3.toString()))
    //    listArraySituation.add(SituationItem(3, "Мебель", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg4.toString()))
       // listArraySituation.add(SituationItem(4, "Медицинские услуги", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg5.toString()))
       // listArraySituation.add(SituationItem(5, "Одежда", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg6.toString()))


/*
        for (item in 6..85) {
            val situation = SituationItem(item, "cars name"+ item.toString(), urlImg5.toString())
            listArraySituation.add(situation)
        }*/

        val docRef = getDocumentRef(context)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                for (itemLead in snapshot) {
                    Log.d("TAG", "Current data: ${itemLead.data}")
                    //Log.d("TAG", "Current data: ${itemLead.data.get("status")}")
                    //Log.d("TAG", "Current data: ${itemLead.data.get("category")}")
                    val id = itemLead.data.get("id")
                    val lead = BusinessItem(id, "Дело номер $id", itemLead.data.get("status") as String, itemLead.data.get("category") as String, "22.02.2012 20:00")
                    listArraySituation.add(lead)
                    myBusinessListAdapter.submitList(listArraySituation)
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }



    }


    fun getDocumentRef(context: Context): CollectionReference {
        val preference = MPreference(context)
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }


    fun getURLForResource(resourceId: Int): String? {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + R::class.java.getPackage().getName() + "/" + resourceId
        ).toString()
    }

    private fun setupRecyclerView() {
        with(binding.rvSituationList) {
            myBusinessListAdapter = MyBusinessAdapterHorizontal()
            adapter = myBusinessListAdapter
            recycledViewPool.setMaxRecycledViews(
                SearchBySituationAdapter.VIEW_TYPE_ENABLED,
                SearchBySituationAdapter.MAX_POOL_SIZE
            )

        }
        setupClickListener()
    }

    private fun setupClickListener() {
        myBusinessListAdapter.onPaymentItemClickListener = {

           when(it.id) {
                0 -> navController.navigate(R.id.action_FSituation_to_FSituationAuto1)
                1 -> navController.navigate(R.id.FSituationFinish)
                4 -> navController.navigate(R.id.action_FSituation_to_FSituationMedicalServices1)
               /* 3 -> launchFragment(CreateSituationFurnitureOneFragment())
                4 -> launchFragment(CreateSituationMedicalServicesOneFragment())
                5 -> launchFragment(CreateSituationClothingOneFragment())*/
            }
        }
    }


    fun launchFragment(fragment: Fragment) {
        /*fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_item_container, fragment)
            ?.addToBackStack(null)
            ?.commit()*/
    }

}