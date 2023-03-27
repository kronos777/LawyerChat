package com.example.lawyerapplication.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lawyerapplication.R
import com.example.lawyerapplication.fragments.welcome_about.WelcomeAboutFragment

class WelcomeAboutViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WelcomeAboutFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_1),
                context.resources.getString(R.string.description_onboarding_1),
                R.drawable.welcomeimg1
                //R.raw.lottie_delivery_boy_bumpy_ride
            )
            1 -> WelcomeAboutFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_2),
                //R.raw.lottie_developer
                R.drawable.welcomeimg2
            )
            else -> WelcomeAboutFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_3),
                context.resources.getString(R.string.description_onboarding_3),
                R.drawable.welcomeimg3
                //R.raw.lottie_girl_with_a_notebook
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}