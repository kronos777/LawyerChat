package com.example.lawyerapplication.fragments.my_business

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
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
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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
    private val viewModelProfile: BussinesViewModel by viewModels()
    private var sortingData: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        setupRecyclerView()
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        setDataInView(sortingData)

        binding.imageSorting.setOnClickListener {
            if(sortingData) {
                //Toast.makeText(context, "Click sorting "+sortingData.toString(), Toast.LENGTH_SHORT).show()
                sortingData = false
                setDataInView(sortingData)
            } else {
                sortingData = true
                setDataInView(sortingData)
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
            //Toast.makeText(context, "this is ${it.id}", Toast.LENGTH_SHORT).show()
            launchFragment(it.id!!)
            /*when(it.id) {
                 0 -> navController.navigate(R.id.action_FSituation_to_FSituationAuto1)
                 1 -> navController.navigate(R.id.FSituationFinish)
                 4 -> navController.navigate(R.id.action_FSituation_to_FSituationMedicalServices1)
                 3 -> launchFragment(CreateSituationFurnitureOneFragment())
                 4 -> launchFragment(CreateSituationMedicalServicesOneFragment())
                 5 -> launchFragment(CreateSituationClothingOneFragment())
            }*/
        }
    }

    private fun setDataInView(sort: Boolean) {
        val uid = preference.getUid()
        val role = viewModelProfile.isLawyer()
        val listArrayBusiness: ArrayList<BusinessItem> = ArrayList()
        viewModelProfile.getBussinesLiveData(uid!!, role, sort).observe(context as FragmentActivity) {

            for (index in it.products!!.indices) {
                //Toast.makeText(context, it.products!![index].number.toString(), Toast.LENGTH_SHORT).show()
                listArrayBusiness.add(
                    BusinessItem(
                        it.products!![index].id,
                        it.products!![index].title,
                        it.products!![index].typeLead,
                        it.products!![index].categoryLead,
                        it.products!![index].dateTimeLead
                    )
                )
            }
            myBusinessListAdapter.submitList(listArrayBusiness)

            if (listArrayBusiness.isNotEmpty()) {
                //binding.rvSituationList.smoothScrollToPosition(listArrayBusiness.size - 1)
                binding.rvSituationList.smoothScrollToPosition(0)
            }
        }
    }


    fun launchFragment(id: Any) {
        val btnArgsBusines = Bundle().apply {
            putString(FMyBussines_page.BUSINESS_ITEM_ID, id.toString())
        }
        navController.navigate(R.id.FMyBussines_page, btnArgsBusines)
        // navController.navigate(R.id.FMyBussines_page)
    }

}