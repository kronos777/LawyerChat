package com.example.lawyerapplication.fragments.about_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lawyerapplication.databinding.FragmentAboutApplicationBinding
import com.example.lawyerapplication.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicy: Fragment() {

    private lateinit var binding: FragmentPrivacyPolicyBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentPrivacyPolicyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}