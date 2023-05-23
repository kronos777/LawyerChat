package com.example.lawyerapplication.fragments.about_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lawyerapplication.databinding.FragmentLicenseAgreementBinding

class LicenseAgreement: Fragment() {

    private lateinit var binding: FragmentLicenseAgreementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentLicenseAgreementBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}