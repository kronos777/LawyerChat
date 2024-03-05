package com.example.lawyerapplication.fragments.my_business


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.AlertSwitchFilterBinding
import com.example.lawyerapplication.databinding.FragmentMyBussinesMainBinding
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.MyBusinessAdapterHorizontal
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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
    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private val viewModelProfile: BusinessViewModel by viewModels()
    private var sortingData: Boolean = false
    private var sortingString: String = ""
    private var role: Boolean = false
    private var tabPos: Boolean = false
    private lateinit var dialog: Dialog
    private var progressView: CustomProgressView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMyBussinesMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()

        //viewModelProfile.getBusinessLiveDataRealTime()

        progressView = CustomProgressView(context)
        subscribeObservers()
        role = viewModelProfile.isLawyer()
        setupRecyclerView()

        setDataInView(sortingData, "")
        imageSortingListClick()

        binding.sortingValue.setOnClickListener {
            initDialog()
            dialog.show()
        }
        tabClick()
    }

    private fun imageSortingListClick() {
        binding.imageSorting.setOnClickListener {
            if(!sortingData && sortingString == "") {
                filterDataDateTime()
            } else if(sortingData && (sortingString == "active" || sortingString == "noActiveDateTime") && tabPos){
                filterDataDateTimeActive()
            } else if(!sortingData && sortingString == "activeDateTime" && tabPos){
                sortingData = true
                sortingString = "noActiveDateTime"
                setDataInView(sortingData, sortingString)
            } else if(!sortingData && sortingString == "applications"){
                filterDataApplications()
            } else if(sortingData && sortingString == "applications"){
                sortingData = false
                sortingString = "applications"
                setDataInView(sortingData, sortingString)
            } else if(sortingData && (sortingString == "applications" || sortingString == "noActiveApplications") && tabPos){
                filterDataApplicationsActive()
            } else if(!sortingData && sortingString == "activeApplications" && tabPos){
                sortingData = true
                sortingString = "noActiveApplications"
                setDataInView(sortingData, sortingString)
            } else if(!sortingData && sortingString == "consultations"){
                filterDataConsultations()
            } else if(sortingData && sortingString == "consultations"){
                sortingData = false
                sortingString = "consultations"
                setDataInView(sortingData, sortingString)
            } else if(sortingData && (sortingString == "consultations" || sortingString == "noActiveConsultations") && tabPos){
                filterDataConsultationsActive()
            } else if(!sortingData && sortingString == "activeApplications" && tabPos){
                sortingData = true
                sortingString = "noActiveConsultations"
                setDataInView(sortingData, sortingString)
            }else if(!sortingData && sortingString == "themes"){
                filterDataThemes()
            } else if(sortingData && sortingString == "themes"){
                sortingData = false
                sortingString = "themes"
                setDataInView(sortingData, sortingString)
            } else if(!sortingData && (sortingString == "themes" || sortingString == "ActiveThemes") && tabPos){
                filterDataThemesActive()
            } else if(sortingData && (sortingString == "themes" || sortingString == "ActiveThemes") && tabPos){
                sortingData = false
                sortingString = "ActiveThemes"
                setDataInView(sortingData, sortingString)
            }else {
                sortingData = false
                sortingString = ""
                setDataInView(sortingData, "")
            }
        }
    }


    private fun filterDataDateTime() {
        sortingData = true
        sortingString = "dateTime"
        setDataInView(sortingData, sortingString)
    }

    private fun filterDataDateTimeActive() {
        sortingData = false
        sortingString = "activeDateTime"
        setDataInView(sortingData, sortingString)
    }

    private fun filterDataApplicationsActive() {
        sortingData = false
        sortingString = "activeApplications"
        setDataInView(sortingData, sortingString)
    }
    private fun filterDataApplications() {
        sortingData = true
        sortingString = "applications"
        setDataInView(sortingData, sortingString)
    }



    private fun filterDataConsultations() {
        sortingData = true
        sortingString = "consultations"
        setDataInView(sortingData, sortingString)
    }

    private fun filterDataConsultationsActive() {
        sortingData = false
        sortingString = "activeConsultations"
        setDataInView(sortingData, sortingString)
    }

    private fun filterDataThemes() {
        sortingData = true
        sortingString = "themes"
        setDataInView(sortingData, sortingString)
    }

    private fun filterDataThemesActive() {
        sortingData = true
        sortingString = "ActiveThemes"
        setDataInView(sortingData, sortingString)
    }


    private fun tabClick(){
        val tabLayout = binding.tabMode
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                //Toast.makeText(context, "Tab item select"+ tab!!.position, Toast.LENGTH_SHORT).show()
                if(role){
                    if(tab!!.position == 0) {
                        tabPos = false
                        sortingData = true
                        sortingString = "noactive"
                        setDataInView(sortingData, sortingString)
                    } else if(tab!!.position == 1) {
                        tabPos = true
                        sortingData = true
                        sortingString = "active"
                        setDataInView(sortingData, sortingString)
                    }
                } else {
                    if(tab!!.position == 0) {
                        sortingData = true
                        sortingString = "active"
                        setDataInView(sortingData, sortingString)
                    } else if(tab!!.position == 1) {
                        sortingData = true
                        sortingString = "closed"
                        setDataInView(sortingData, sortingString)
                    }
                }

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
                Toast.makeText(context, "Tab item reselect"+ tab!!.id, Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }


    fun getDocumentRef(context: Context): CollectionReference {
        val preference = MPreference(context)
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }


    fun getURLForResource(resourceId: Int): String? {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + R::class.java.getPackage().name + "/" + resourceId
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
    private fun subscribeObservers() {
        viewModelProfile.businessListUpdateState.observe(viewLifecycleOwner, {
            if (it is LoadState.OnLoading) {
                progressView?.show()
            } else
                progressView?.dismiss()
        })
    }
    private fun setDataInView(sort: Boolean, typeSorting: String?) {
        val uid = preference.getUid()
        val listArrayBusiness: ArrayList<BusinessItem> = ArrayList()

           if(!role){
               binding.sortingDataMain.visibility = View.GONE
               binding.tabMode.getTabAt(0)!!.text = "Активные"
               binding.tabMode.getTabAt(1)!!.text = "Закрытые"

               //viewModelProfile.getBusinessLiveData(uid!!, role, sort, typeSorting, "").observe(context as FragmentActivity) {
               viewModelProfile.getBusinessLiveDataRealTime(uid!!, role, sort, typeSorting, "").observe(context as FragmentActivity) {

                   for (index in it.products!!.indices) {
                       //Toast.makeText(context, it.products!![index].number.toString(), Toast.LENGTH_SHORT).show()
                       listArrayBusiness.add(
                           BusinessItem(
                               it.products!![index].id,
                               it.products!![index].title,
                               it.products!![index].typeLead,
                               it.products!![index].categoryLead,
                               it.products!![index].dateTimeLead,
                               it.products!![index].idLaywer,
                           )
                       )
                   }
                   myBusinessListAdapter.submitList(listArrayBusiness)

                   if (listArrayBusiness.isNotEmpty()) {
                       //binding.rvSituationList.smoothScrollToPosition(listArrayBusiness.size - 1)
                       binding.rvSituationList.smoothScrollToPosition(0)
                   }
               }


           } else {
               viewModelProfile.getBusinessLiveDataRealTime(uid!!, role, sort, typeSorting, "").observe(context as FragmentActivity) {

                   for (index in it.products!!.indices) {
                       //Toast.makeText(context, it.products!![index].number.toString(), Toast.LENGTH_SHORT).show()
                       listArrayBusiness.add(
                           BusinessItem(
                               it.products!![index].id,
                               it.products!![index].title,
                               it.products!![index].typeLead,
                               it.products!![index].categoryLead,
                               it.products!![index].dateTimeLead,
                               it.products!![index].idLaywer,
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

    }

    private fun setSortingData(strSort: String) {
        sortingString = strSort
        when (strSort) {
            "dateTime" ->  binding.sortingValue.text = "По дате"
            "applications" ->  binding.sortingValue.text = "Заявки"
            "consultations" ->  binding.sortingValue.text = "Консультации"
            "themes" ->  binding.sortingValue.text = "По темам"
        }

        if(!tabPos) {
            when (strSort) {
                "dateTime" ->  filterDataDateTime()
                "applications" ->  filterDataApplications()
                "consultations" ->  filterDataConsultations()
                "themes" ->  filterDataThemes()
            }
        } else {
            when (strSort) {
                "dateTime" -> filterDataDateTimeActive()
                "applications" -> filterDataApplicationsActive()
                "consultations" -> filterDataConsultationsActive()
                "themes" -> filterDataThemesActive()
            }
        }

    }

    private fun initDialog() {
        //Toast.makeText(context, "init dialog", Toast.LENGTH_SHORT).show()
        try {
            dialog = Dialog(requireContext())
            val layoutBinder = AlertSwitchFilterBinding.inflate(layoutInflater)

            dialog.setContentView(layoutBinder.root)
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
           // val mInfoTextView = binding.titleSorting
            layoutBinder.radioBtn.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    com.example.lawyerapplication.R.id.radio_button_1 -> setSortingData("dateTime")
                    com.example.lawyerapplication.R.id.radio_button_2 -> setSortingData("applications")
                    com.example.lawyerapplication.R.id.radio_button_3 -> setSortingData("consultations")
                    com.example.lawyerapplication.R.id.radio_button_4 -> setSortingData("themes")
                }
            })
           /* layoutBinder.radioBtn.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    //val radio: RadioButton = findViewById(checkedId)
                    //Toast.makeText(context," On checked change :"+ " ${radio.text}",  Toast.LENGTH_SHORT).show()

                })*/
           /*
            layoutBinder.enterButton.setOnClickListener {
                //Log.d("logDialog", )
                //Log.d("logDialog", layoutBinder.etDescription.text.toString())
                val title = layoutBinder.etTitle.text.toString()
                val description  = layoutBinder.etDescription.text.toString()
                viewModelStage.addStageBussines(item.toInt(), title, description)

                Handler().postDelayed({
                    dialog.dismiss()
                    setDataInView()
                }, 1500)

                //addStageBussines


            }
            layoutBinder.imageClose.setOnClickListener {
                dialog.dismiss()
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onClickedLead(view: View) {
        Toast.makeText(context," On checked change :"+
                " ${view.id}",
            Toast.LENGTH_SHORT).show()
    }


    fun launchFragment(id: Any) {
        navController.navigate(FMyBussines_mainDirections.actionFmyBussinesMainToFMyBussinesPage(id.toString()))
    }

}