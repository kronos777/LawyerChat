package com.example.lawyerapplication.fragments.mycards

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImage
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.*
import com.example.lawyerapplication.db.data.BanksCardItem
import com.example.lawyerapplication.db.data.SituationItem
import com.example.lawyerapplication.fragments.mycards.adapter.BanksCardAdapter
import com.example.lawyerapplication.fragments.situation.main_list.SearchBySituationAdapter
import com.example.lawyerapplication.models.UserStatus
import com.example.lawyerapplication.utils.*
import com.example.lawyerapplication.views.CustomProgressView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class FMyCards : Fragment() {

    private lateinit var binding: FragmentMyCardsBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private lateinit var dialog: Dialog

    private lateinit var banksCardAdapter: BanksCardAdapter
    private lateinit var navController: NavController
    private val viewModel: ViewModelMyCards by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMyCardsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()

        setupRecyclerView()
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
        setDataInView()


      /*  val urlImg1 = getURLForResource(R.drawable.auto_s1)
        val urlImg2 = getURLForResource(R.drawable.appliances_s2)
        val urlImg3 = getURLForResource(R.drawable.new_buildings_s3)
        val urlImg4 = getURLForResource(R.drawable.furniture_s4)
        val urlImg5 = getURLForResource(R.drawable.medical_services_s5)
        val urlImg6 = getURLForResource(R.drawable.clothing_s6)
        listArrayCards.add(BanksCardItem(0, "Автомобили", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg1.toString()))
        listArrayCards.add(BanksCardItem(1, "Бытовая техника", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg2.toString()))
        listArrayCards.add(BanksCardItem(2, "Новостройки", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg3.toString()))
        listArrayCards.add(BanksCardItem(3, "Мебель", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg4.toString()))
        listArrayCards.add(BanksCardItem(4, "Медицинские услуги", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg5.toString()))
        listArrayCards.add(BanksCardItem(5, "Одежда", "Поддержание высокого уровня сервиса — одна из наших главных задач.", urlImg6.toString()))
*/


        binding.addBanksCard.setOnClickListener {
            navController.navigate(R.id.FAddCards)
        }
    }

    fun setDataInView() {
        val listArrayCards: ArrayList<BanksCardItem> = ArrayList()
        viewModel.getResponseFromFirestoreUsingLiveData().observe(context as FragmentActivity) {
            val urlImg1 = getURLForResource(R.drawable.img_sber_ex)
            for (index in it.products!!.indices) {
                //Toast.makeText(context, it.products!![index].number.toString(), Toast.LENGTH_SHORT).show()
                listArrayCards.add(
                    BanksCardItem(
                        it.products!![index].id,
                        it.products!![index].number,
                        it.products!![index].validity,
                        urlImg1.toString()
                    )
                )
            }
            banksCardAdapter.submitList(listArrayCards)
        }
    }

    fun getURLForResource(resourceId: Int): String? {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + R::class.java.getPackage().getName() + "/" + resourceId
        ).toString()
    }

    private fun setupRecyclerView() {
        with(binding.rvCardsList) {
            banksCardAdapter = BanksCardAdapter()
            adapter = banksCardAdapter
            recycledViewPool.setMaxRecycledViews(
                BanksCardAdapter.VIEW_TYPE_ENABLED,
                BanksCardAdapter.MAX_POOL_SIZE
            )

        }
        setupClickListener()
    }

    private fun setupClickListener() {
        banksCardAdapter.onPaymentItemClickListener = {
            //Toast.makeText(context, "we are here"+ it.nameString, Toast.LENGTH_SHORT).show()
            initDialog(it.nameString)
            dialog.show()
        }
    }


    private fun initDialog(numberCard: String) {
        try {
            dialog = Dialog(requireContext())
            val layoutBinder = AlertDeleteCardBinding.inflate(layoutInflater)

            dialog.setContentView(layoutBinder.root)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutBinder.txtSubTitle.text = "Карта ${numberCard} не будет отображаться в списке способов оплаты. Вы сможете в любой момент снова добавить её данные в приложение"
            layoutBinder.txtOk.setOnClickListener {
                dialog.dismiss()
                //удалить карту
                viewModel.deleteCardUser(numberCard)
                setDataInView()
            }
            layoutBinder.txtCancel.setOnClickListener {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}