package com.example.lawyerapplication.fragments.notifications


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.runner.lifecycle.Stage
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.AlertSwitchFilterBinding
import com.example.lawyerapplication.databinding.FragmentMyBussinesMainBinding
import com.example.lawyerapplication.databinding.FragmentNotificationsMainBinding
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.MyBusinessAdapterHorizontal
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.NotificationsAdapter
import com.example.lawyerapplication.fragments.my_business.BussinesViewModel
import com.example.lawyerapplication.fragments.my_business.FMyBussines_page
import com.example.lawyerapplication.fragments.single_chat.asMap
import com.example.lawyerapplication.fragments.single_chat.serializeToMap
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.services.StageUploadWorker
import com.example.lawyerapplication.services.UploadWorker
import com.example.lawyerapplication.utils.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FNotifications_main : Fragment() {

    private lateinit var binding: FragmentNotificationsMainBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var myNotificationsAdapter: NotificationsAdapter
    private lateinit var navController: NavController

    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        setupRecyclerView()
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val listArrayStages: ArrayList<StageBussines> = ArrayList()

        /*WorkManager*/
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<StageUploadWorker>()
                //.setInputData(data)
                .build()
        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
        /*WorkManager*/

       // viewModel.updateStageLocalDb()
        viewModel.getAllStagesLocalDb().observe(context as FragmentActivity) {
           // Log.d("CURRENTDATA", it.size.toString())
            for (index in it.indices) {

                listArrayStages.add(
                    StageBussines(
                        it[index].fireBaseId,
                        it[index].idBussines,
                        substrTitle(it[index].title.capitalize()),
                        substrDescription(it[index].description),
                        it[index].dateTime,
                        it[index].status
                    )
                )

                //val newStage = it[index].copy(status = 1)
                //viewModel.insertStage(newStage)
            }

            val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val sortStages = listArrayStages.sortedByDescending {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.parse(it.dateTime, formatter)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }

            if(sortStages.size > 0) {
                myNotificationsAdapter.submitList(sortStages)
            }
        }

        //val listArrayStagesPage: ArrayList<StageBussines> = ArrayList()


        /*viewModel.getStagesLiveData().observe(context as FragmentActivity) {
            for (index in it.products!!.indices) {

                listArrayStages.add(
                    StageBussines(
                        it.products!![index].id,
                        it.products!![index].idBussines,
                        substrTitle(it.products!![index].title.capitalize()),
                        substrDescription(it.products!![index].description),
                        it.products!![index].dateTime,
                        it.products!![index].status
                    )
                )

            }

            if(listArrayStages.size > 0) {
                myNotificationsAdapter.submitList(listArrayStages)
            }

        }*/
       // viewModel.getStatesLiveDataNew()

    }

    private fun setDataInView() {
        val listArrayStages: ArrayList<StageBussines> = ArrayList()
        viewModel.getStagesLiveData().observe(context as FragmentActivity) {
            for (index in it.products!!.indices) {

                listArrayStages.add(
                    StageBussines(
                        it.products!![index].id,
                        it.products!![index].idBussines,
                        it.products!![index].title,
                        it.products!![index].description,
                        it.products!![index].dateTime,
                        it.products!![index].status
                    )
                )
            }
            Log.d("CURRENTDATA", listArrayStages.toString())
            if(listArrayStages.size > 0) {
                myNotificationsAdapter.submitList(listArrayStages)
            }

        }
    }

    private fun setDataInViewTest() {
        val arrStage: ArrayList<StageBussines> = ArrayList<StageBussines>()
        for (i in 0..100) {
            arrStage.add(StageBussines(i.toInt(), (i + 3).toInt(), "title {i}", "description {i}", "10/08/2023 15:18", 0))
        }
        myNotificationsAdapter.submitList(arrStage)
    }


    private fun substrDescription(str: String): String {
        if(str.count() > 68) {
            return str.substring(0, 68) + " ..."
        } else {
            return str
        }
    }

    private fun substrTitle(str: String): String {
        if(str.count() > 15) {
            return str.substring(0, 15) + " ..."
        } else {
            return str
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvSituationList) {
            myNotificationsAdapter = NotificationsAdapter()
            adapter = myNotificationsAdapter
            recycledViewPool.setMaxRecycledViews(
                SearchBySituationAdapter.VIEW_TYPE_ENABLED,
                SearchBySituationAdapter.MAX_POOL_SIZE
            )

        }
        setupClickListener()
    }

    private fun setupClickListener() {
        myNotificationsAdapter.onPaymentItemClickListener = {
            launchFragment(it)
        }
    }


    fun launchFragment(it: Any) {
        var gson = Gson()
        val btnArgsBusines = Bundle().apply {
            // putString(FNotifications_page.STAGE_ITEM_DATA, id.toString())
            putString(FNotifications_page.STAGE_ITEM_DATA, gson.toJson(it))
            //putString(FNotifications_page.STAGE_ITEM_DATA, id.serializeToMap().toString())
        }
        navController.navigate(R.id.FNotifications_page, btnArgsBusines)
        // navController.navigate(R.id.FMyBussines_page)
    }

}