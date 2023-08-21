package com.example.lawyerapplication.fragments.notifications


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.runner.lifecycle.Stage
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.AlertSwitchFilterBinding
import com.example.lawyerapplication.databinding.FragmentMyBussinesMainBinding
import com.example.lawyerapplication.databinding.FragmentNotificationsMainBinding
import com.example.lawyerapplication.databinding.FragmentNotificationsPageBinding
import com.example.lawyerapplication.db.data.BusinessItem
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.MyBusinessAdapterHorizontal
import com.example.lawyerapplication.fragments.main_screen.situation_adapter.NotificationsAdapter
import com.example.lawyerapplication.fragments.my_business.BussinesViewModel
import com.example.lawyerapplication.fragments.my_business.FMyBussines_page
import com.example.lawyerapplication.fragments.single_chat.asMap
import com.example.lawyerapplication.fragments.single_chat.convert
import com.example.lawyerapplication.fragments.single_chat.serializeToMap
import com.example.lawyerapplication.fragments.single_chat.toDataClass
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.utils.*
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import org.xml.sax.Parser
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FNotifications_page : Fragment() {

    private lateinit var binding: FragmentNotificationsPageBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private var stageBussines: String = String()
    private lateinit var  stBussines: StageBussines

    private lateinit var navController: NavController

    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            context = requireActivity()
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
            viewModel.getStageItem(stBussines.idBussines.toString(), stBussines.id.toString())

            goToLead()
    }




    private fun goToLead() {
        binding.enterButton.setOnClickListener {
            val params = Bundle().apply {
                putString(FMyBussines_page.BUSINESS_ITEM_ID, stBussines.idBussines.toString())
            }
            navController.navigate(R.id.FMyBussines_page, params)
        }
    }


    private fun parseParams() {
        val args = requireArguments()
        stageBussines = args.getString(FNotifications_page.STAGE_ITEM_DATA).toString()
        stBussines =  Gson().fromJson(stageBussines, StageBussines::class.java)
        //Toast.makeText(getActivity(),"item" + item, Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val STAGE_ITEM_DATA = ""
    }




}