package com.example.lawyerapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.ActivityMainBinding
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.Group
import com.example.lawyerapplication.fragments.my_business.BussinesViewModel
import com.example.lawyerapplication.fragments.mycards.FAddCards
import com.example.lawyerapplication.fragments.single_chat_home.FSingleChatHomeDirections
import com.example.lawyerapplication.utils.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : ActBase(), FAddCards.OnEditingFinishedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var searchView: SearchView

    private lateinit var searchItem: MenuItem
    private val viewModelProfile: BussinesViewModel by viewModels()
    private var stopped=false

    private var redCircle: FrameLayout? = null
    private var countTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        binding.fab.setOnClickListener {
            if (searchItem.isActionViewExpanded)
               searchItem.collapseActionView()
            if (Utils.askContactPermission(returnFragment()!!)) {
                if (navController.isValidDestination(R.id.FSingleChatHome))
                    navController.navigate(R.id.action_FSingleChatHome_to_FContacts)
                else if (navController.isValidDestination(R.id.FGroupChatHome))
                    navController.navigate(R.id.action_FGroupChatHome_to_FAddGroupMembers)
            }

        }
        setDataInView()
       // subscribeObservers()
        /*message alert*/
       /* val messageBtnAppBarTop = findViewById<View>(R.id.icon_forum)
        messageBtnAppBarTop.setOnClickListener {
            navController.navigate(R.id.FSingleChatHome)
        }*/

        /*val materialToolbar: Toolbar = binding.toolbar
        materialToolbar.setOnMenuItemClickListener {
            //Toast.makeText(this, "this item id" + it, Toast.LENGTH_SHORT).show()
            when (it.itemId) {
                R.id.action_notifications -> {
                //R.id.action_forum -> {
                    navController.navigate(R.id.FSingleChatHome)
                    true
                }
                else -> false
            }
        }*/


        /*message alert*/
    }

    private fun subscribeObservers(notificaitons: View) {
       // val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_home)//nav_chat
       // badge.isVisible = false
        //redCircle = findViewById(R.id.view_alert_red_circle)
       // val notificaitons: View = binding.toolbar.menu.findItem(R.id.action_forum).getActionView()
        redCircle = notificaitons.findViewById(R.id.view_alert_red_circle)
        //redCircle?.visibility = View.VISIBLE
        //Timber.v("this redCircle" + redCircle)
        countTextView = notificaitons.findViewById(R.id.view_alert_count_textview)
       // Timber.v("this redCircle" + redCircle)
        lifecycleScope.launch {
            chatUserDao.getChatUserWithMessages().conflate().collect { list ->
                val count = list.filter { it.user.unRead != 0 && it.messages.isNotEmpty() }
                if(count.isNotEmpty()) { //hide if 0
                    Timber.v("this count unread" + count.size)
                    countTextView?.text = count.size.toString()
                    redCircle?.visibility = View.VISIBLE
                   // countTextView?.visibility = View.VISIBLE

                } else {
                    redCircle?.visibility = View.GONE
                   // countTextView?.visibility = View.GONE
                }
            }
        }
/*
        val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_home)//nav_chat
        badge.isVisible = false
        val groupChatBadge = binding.bottomNav.getOrCreateBadge(R.id.nav_home)//nav_group
        groupChatBadge.isVisible = false
        lifecycleScope.launch {
            groupDao.getGroupWithMessages().conflate().collect { list ->
                val count = list.filter { it.group.unRead != 0 }
                groupChatBadge.isVisible = count.isNotEmpty() //hide if 0
                groupChatBadge.number = count.size
            }
        }

        lifecycleScope.launch {
            chatUserDao.getChatUserWithMessages().conflate().collect { list ->
                val count = list.filter { it.user.unRead != 0 && it.messages.isNotEmpty() }
                badge.isVisible = count.isNotEmpty() //hide if 0
                badge.number = count.size
            }
        }*/

    }

    private fun setDataInView() {
        try {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                onDestinationChanged(destination.id)
            }
            //val appBarConfiguration = AppBarConfiguration(setOf(R.id.FSingleChatHome))
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.FMainScreen, R.id.FSituation, R.id.fmyBussines_main, R.id.FMyProfile))
            binding.toolbar.setupWithNavController(navController, appBarConfiguration)
            binding.bottomNav.setOnNavigationItemSelectedListener(onBottomNavigationListener)

            val isNewMessage = intent.action == Constants.ACTION_NEW_MESSAGE
            val isNewGroupMessage = intent.action == Constants.ACTION_GROUP_NEW_MESSAGE
            val userData = intent.getParcelableExtra<ChatUser>(Constants.CHAT_USER_DATA)
            val groupData = intent.getParcelableExtra<Group>(Constants.GROUP_DATA)

            if (preference.isLoggedIn() && navController.isValidDestination(R.id.FLogIn)) {
                if (preference.getUserProfile() == null)
                    navController.navigate(R.id.action_FLogIn_to_FProfile)
                else
                //navController.navigate(R.id.action_FLogIn_to_FSingleChatHome)
                    navController.navigate(R.id.FMainScreen)
                //navController.navigate(R.id.FProfile)
            }

            //single chat message notification clicked
            if (isNewMessage && navController.isValidDestination(R.id.FSingleChatHome)) {
                preference.setCurrentUser(userData!!.id)
                val action = FSingleChatHomeDirections.actionFSingleChatToFChat(userData)
                navController.navigate(action)
            } else if (isNewGroupMessage && navController.isValidDestination(R.id.FSingleChatHome)) {
                preference.setCurrentGroup(groupData!!.id)
                val action = FSingleChatHomeDirections.actionFSingleChatHomeToFGroupChat(groupData)
                navController.navigate(action)
            }

            //if lawyer hide item menu
            if(viewModelProfile.isLawyer()) {
              //  binding.bottomNav.findViewById<MenuItem>(R.id.nav_services).setVisible(false)
                //BottomNavigationView.findItem(R.id.nav_services).isVisible = false
                val navView: BottomNavigationView = binding.bottomNav
                // Find the menu item and then disable it
               // navView.menu.findItem(R.id.nav_services).isEnabled = false
                navView.menu.findItem(R.id.nav_services).isVisible = false
                navView.menu.findItem(R.id.nav_home).isVisible = false
                navController.navigate(R.id.fmyBussines_main)

            }
            //if lawyer hide item menu

            //navController.navigate(R.id.FMainScreen)

            if (!preference.isSameDevice())
                Utils.showLoggedInAlert(this, preference, db)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onDestinationChanged(currentDestination: Int) {
        try {
            when(currentDestination) {
                R.id.FSingleChatHome -> {
                   // binding.bottomNav.selectedItemId = R.id.nav_home
                    showView()
                    binding.fab.hide()

                // navController.navigate(R.id.FSingleChatHome)
                   // Toast.makeText(this, "single cht home", Toast.LENGTH_SHORT).show()
                }
                R.id.FMainScreen -> {
                    binding.bottomNav.selectedItemId = R.id.nav_home
                    showView()
                    binding.fab.hide()
                  //  this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                   // this.supportActionBar?.setDisplayShowHomeEnabled(false)
                }
                R.id.FGroupChatHome -> {
                    binding.bottomNav.selectedItemId = R.id.nav_home
                    showView()
                    binding.fab.hide()
                }
                R.id.FSearch -> {
                    binding.bottomNav.selectedItemId = R.id.nav_home
                    showView()
                    binding.fab.hide()
                }
                R.id.FMyProfile -> {
                    binding.bottomNav.selectedItemId = R.id.nav_profile
                    showView()
                    binding.fab.hide()
                }
                R.id.fmyBussines_main -> {
                    binding.bottomNav.selectedItemId = R.id.nav_my_business
                    showView()
                    binding.fab.hide()
                   //binding.toolbar.setDisplayHomeAsUpEnabled(false)

                }
                R.id.FMyBussines_page -> {
                   // binding.bottomNav.selectedItemId = R.id.nav_my_business
                    showView()
                    binding.fab.hide()
                    //binding.toolbar.setDisplayHomeAsUpEnabled(false)

                }/**/
                R.id.FSituation -> {
                    binding.bottomNav.selectedItemId = R.id.nav_services
                    showView()
                    binding.fab.hide()
                }
                R.id.aboutApplication -> {
                    binding.bottomNav.gone()
                    binding.fab.gone()
                }
                R.id.userAccountSettings -> {
                    binding.bottomNav.gone()
                    binding.fab.gone()
                }
                R.id.changeUserPassword -> {
                    binding.bottomNav.gone()
                    binding.fab.gone()
                }
                else -> {
                    binding.bottomNav.gone()
                    binding.fab.gone()
                    binding.toolbar.gone()
                }
            }
          /*  Handler(Looper.getMainLooper()).postDelayed({ //delay time for searchview
                if (this::searchItem.isInitialized) {
                    if (currentDestination == R.id.FMyProfile) {
                        searchItem.collapseActionView()
                        searchItem.isVisible = false
                    }else
                        searchItem.isVisible = true
                }
            }, 500)*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        initToolbarItem()
        return true
    }

    private fun initToolbarItem() {
         searchItem = binding.toolbar.menu.findItem(R.id.action_search)

        subscribeObservers(binding.toolbar.menu.findItem(R.id.action_forum).getActionView())
        val paymentBtnAppBarTop = binding.toolbar.menu.findItem(R.id.action_forum).getActionView()
        paymentBtnAppBarTop.setOnClickListener {
            navController.navigate(R.id.FSingleChatHome)

        }


        binding.toolbar.setOnMenuItemClickListener {
                // Toast.makeText(this, "this item id" + it, Toast.LENGTH_SHORT).show()
                 when (it.itemId) {
                    /* R.id.action_forum -> {
                         //R.id.action_forum -> {
                         navController.navigate(R.id.FSingleChatHome)
                         true
                     }*/
                     R.id.action_notifications -> {
                         //R.id.action_forum -> {
                         //navController.navigate(R.id.FSingleChatHome)
                         Toast.makeText(this, "notifications", Toast.LENGTH_SHORT).show()
                         true
                     }
                     else -> false
                 }
             }

          searchView = searchItem.actionView as SearchView
          searchView.apply {
              maxWidth = Integer.MAX_VALUE
              queryHint = getString(R.string.txt_search)
          }

          searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
              override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                  sharedViewModel.setState(ScreenState.SearchState)
                  return true
              }

              override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    if (!stopped)
                      sharedViewModel.setState(ScreenState.IdleState)
                  return true
              }
          })

          sharedViewModel.getState().observe(this, { state ->
              if (state is ScreenState.SearchState && searchView.isIconified) {
                  searchItem.expandActionView()
                  searchView.setQuery(sharedViewModel.getLastQuery().value, false)
              }
          })

          searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
              override fun onQueryTextSubmit(query: String?): Boolean {
                  return true
              }

              override fun onQueryTextChange(newText: String?): Boolean {
                  sharedViewModel.setLastQuery(newText.toString())
                  return true
              }
          })



    }

    private fun showView() {
        binding.bottomNav.show()
        binding.fab.show()
        binding.toolbar.show()
    }

    private fun isNotSameDestination(destination: Int): Boolean {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment)
            .currentDestination!!.id
    }

    private val onBottomNavigationListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val navOptions =
                        NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                    if (isNotSameDestination(R.id.FMainScreen)) {
                      //  searchItem.collapseActionView()
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.FMainScreen, null, navOptions)
                    }
                    true
                }
                R.id.nav_services -> {
                    val navOptions =
                        NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                    if (isNotSameDestination(R.id.FSituation)) {
//                        searchItem.collapseActionView()
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.FSituation, null, navOptions)
                    }
                    true
                }
                R.id.nav_my_business -> {
                    val navOptions =
                        NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                    if (isNotSameDestination(R.id.fmyBussines_main)) {
                     //   searchItem.collapseActionView()
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.fmyBussines_main, null, navOptions)
                    }
                    true
                }
                R.id.nav_profile -> {
                    val navOptions =
                        NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                    if (isNotSameDestination(R.id.FMyProfile)) {
                       // searchItem.collapseActionView()
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.FMyProfile, null, navOptions)
                    }
                    true
                }
                /* R.id.nav_group -> {
                     if (isNotSameDestination(R.id.FGroupChatHome)) {
                         searchItem.collapseActionView()
                         Navigation.findNavController(this, R.id.nav_host_fragment)
                             .navigate(R.id.FGroupChatHome)
                     }
                     true
                 }
                 R.id.nav_search -> {
                     if (isNotSameDestination(R.id.FSearch)) {
                         searchItem.collapseActionView()
                         Navigation.findNavController(this, R.id.nav_host_fragment)
                             .navigate(R.id.FSearch)
                     }
                     true
                 }*/
                else -> {
                    if (isNotSameDestination(R.id.FMyProfile)) {
                        //searchItem.collapseActionView()
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                            .navigate(R.id.FMyProfile)
                    }
                    true
                }
            }

        }


    fun returnFragment(): Fragment? {
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
        if (navHostFragment != null) {
            val childFragments = navHostFragment.childFragmentManager.fragments
            childFragments.forEach { fragment ->
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        /* val navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
         if (navHostFragment != null) {
             val childFragments = navHostFragment.childFragmentManager.fragments
             childFragments.forEach { fragment ->
                 fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
             }
         }*/
    }

    override fun onBackPressed() {
        if (navController.isValidDestination(R.id.FSingleChatHome) || navController.isValidDestination(R.id.FMainScreen)) {
            finish()
        /*else if (navController.isValidDestination(R.id.FMyProfile) ||
            navController.isValidDestination(R.id.FGroupChatHome) ||
            navController.isValidDestination(R.id.FSearch)) {
            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.FSingleChatHome, null, navOptions)*/
        } else
            super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        Timber.v("onSdd")
        stopped=true
    }

    override fun onResume() {
        super.onResume()
        Timber.v("onResime")
        stopped=false
    }

    override fun onEditingFinished() {
        supportFragmentManager.popBackStack()
    }

}