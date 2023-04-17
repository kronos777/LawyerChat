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
import com.example.lawyerapplication.db.ChatUserDatabase
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.MyBusinessAdapterHorizontal
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.fragments.situation.medical_services.FSituationMedicalServices3
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

        val uid = preference.getUid()

        val docRef = getDocumentRef(context)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {

                for (itemLead in snapshot) {
                    if(uid == itemLead.data.get("idClient") as String) {
                        Log.d("CURRENTDATA", "Current uid: ${uid}")
                        val id = itemLead.data.get("id")
                        val lead = BusinessItem(id, "Дело номер $id", itemLead.data.get("status") as String, itemLead.data.get("category") as String, itemLead.data.get("dateTime") as String)
                        listArraySituation.add(lead)
                    }
                    //Log.d("TAG", "Current data: ${itemLead.data}")
                    //Log.d("TAG", "Current data: ${itemLead.data.get("status")}")
                    //Log.d("TAG", "Current data: ${itemLead.data.get("category")}")

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


    fun launchFragment(id: Any) {
        val btnArgsBusines = Bundle().apply {
            putString(FMyBussines_page.BUSINESS_ITEM_ID, id.toString())
        }
        navController.navigate(R.id.FMyBussines_page, btnArgsBusines)
        // navController.navigate(R.id.FMyBussines_page)
    }

}