package com.example.lawyerapplication.fragments.notifications


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentNotificationsPageBinding
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.fragments.my_business.FMyBussines_page
import com.example.lawyerapplication.utils.*
import com.google.firebase.firestore.CollectionReference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


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

            /*runBlocking {
                val job = launch(Dispatchers.Default) {
                    val st = viewModel.getStageLocal(stBussines.id, stBussines.idBussines)
                    val nst = st!!.copy(status = 1)
                    viewModel.updateStage(nst)

                    Log.d("CURRENTDATA", st.toString())
                }
            }*/

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