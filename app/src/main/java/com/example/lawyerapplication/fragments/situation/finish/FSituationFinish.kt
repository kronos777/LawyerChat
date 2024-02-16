package com.example.lawyerapplication.fragments.situation.finish

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock.sleep
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentCreateLeadOkMessageBinding
import com.example.lawyerapplication.utils.MPreference
import com.google.firebase.firestore.CollectionReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FSituationFinish : Fragment() {

    private lateinit var binding: FragmentCreateLeadOkMessageBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentCreateLeadOkMessageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        //sleep(5000)
       // navController.navigate(R.id.FMainScreen)
        Handler().postDelayed({
            navController.navigate(R.id.FMainScreen)
        }, 5000)

    }





}