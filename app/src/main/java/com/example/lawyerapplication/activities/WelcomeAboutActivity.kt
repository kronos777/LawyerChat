package com.example.lawyerapplication.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ActivityWelcomeAboutBinding
import com.example.lawyerapplication.utils.WelcomeAboutViewPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeAboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeAboutBinding
    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_onboarding_example1)
        binding = ActivityWelcomeAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (this as AppCompatActivity).supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.statusBarColor = this.resources.getColor(R.color.top_panel_welcome)
        }

        val pageIndicator = binding.pageIndicator
        mViewPager = binding.viewPager
        mViewPager.adapter = WelcomeAboutViewPagerAdapter(this, this)
        TabLayoutMediator(pageIndicator, mViewPager) { _, _ -> }.attach()
        textSkip = binding.textSkip2
        textSkip.setOnClickListener {
            /*finish()
             Animatoo.animateSlideLeft(this)
             */
            val intent =
                Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

        }

        val btnNextStep: FloatingActionButton = binding.btnNextStep

        btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount) {
                val intent =
                    Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            //finish()
                /*val intent =
                    Intent(applicationContext, OnboardingFinishActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)*/
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }
}