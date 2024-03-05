package com.example.lawyerapplication.fragments.my_business

import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.AlertAddStageBinding
import com.example.lawyerapplication.databinding.FragmentMyBussinesPageBinding
import com.example.lawyerapplication.db.data.ChatUser
import com.example.lawyerapplication.db.data.LeadItem
import com.example.lawyerapplication.db.data.Message
import com.example.lawyerapplication.db.data.StageBussines
import com.example.lawyerapplication.fragments.my_business.stage_bussines.StageBussinesViewModel
import com.example.lawyerapplication.fragments.mycards.adapter.StageItemAdapter
import com.example.lawyerapplication.fragments.single_chat.SingleChatViewModel
import com.example.lawyerapplication.models.MyImage
import com.example.lawyerapplication.models.UserProfile
import com.example.lawyerapplication.utils.ImageUtils
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.UserUtils
import com.example.lawyerapplication.utils.show
import com.example.lawyerapplication.views.CustomProgressView
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FMyBussines_page : Fragment() {

    private lateinit var binding: FragmentMyBussinesPageBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    private val args by navArgs<FMyBussines_pageArgs>()

    private val navController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private var item: String = String()

    private var arrayListImgData = mutableMapOf<Int, Uri?>()

    private var category: String = String()
    private var originFieldCategory: String = String()
    private var accompanyingText: String = String()
    private val viewModelProfile: BusinessViewModel by viewModels()
    private val viewModelStage: StageBussinesViewModel by viewModels()
    private lateinit var dialog: Dialog
    private lateinit var stageItemAdapter: StageItemAdapter

    private val viewModel: SingleChatViewModel by viewModels()
    private var progressView: CustomProgressView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentMyBussinesPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        binding.viewModel = viewModelProfile

        setupRecyclerView()
        val role = viewModelProfile.isLawyer()
        progressView = CustomProgressView(context)

        (activity as AppCompatActivity).findViewById<Toolbar>(R.id.toolbar).title = "Дело № ${item}"

        CoroutineScope(Dispatchers.Main).launch {
            progressView?.show()
            val businessItem = viewModelProfile.getBusinessData(item).await()
            setCustomUi(businessItem)
            setRoleUi(businessItem, role)
            val listImageLead = viewModelProfile.getLeadImageData(item).await()
            for (index in listImageLead.indices) {
                val mainLayout: LinearLayout = whatsGroupFile(listImageLead[index].path.toString())
                arrayListImgData[index] = listImageLead[index]
                mainLayout.addView(setImageView(index))
            }
            progressView?.dismiss()
        }


        binding.cardAddStage.setOnClickListener {
            initDialog()
            dialog.show()
        }

        setDataStageInView()


    }

    private suspend fun setRoleUi(businessItem: LeadItem?, role: Boolean) {
        if (businessItem != null) {
            if(!role && businessItem.idLawyer != "") {
                val dataUser = viewModelProfile.getUserData(businessItem.idLawyer).await()
                if (dataUser != null) {
                    setUserRoleData(dataUser)
                }
            } else if(role) {
                val dataUser = viewModelProfile.getUserData(businessItem.idClient).await()
                if (dataUser != null) {
                    setUserRoleData(dataUser)
                }
            }
        }
    }


    private fun setUserRoleData(dataUser: UserProfile, businessItem: LeadItem? = null) {
        binding.aboutUserH1.text = dataUser.userName.capitalize() + " " + dataUser.lastName.capitalize()
        Glide.with(context)
            .load(dataUser.image.toUri())
            .into(binding.imageProfile)
        if(dataUser.role == "Lawyer") {
            binding.roleApplicationH1.text = "Юрист"
            if (businessItem != null) {
                activeButtonForMessagingLawyer(dataUser.uId!!, businessItem.idLawyer)
            }
        } else {
            binding.roleApplicationH1.text = "Клиент"
            activeButtonForMessagingClient(dataUser.uId!!)
        }
        binding.cardProfileData.visibility = View.VISIBLE
    }

    private fun activeButtonForMessagingLawyer(uId: String, idLawyer: String) {
        if(idLawyer == "") {
            binding.buttonForLawyerCloseLead.visibility = View.GONE
            binding.buttonForLawyer2.setOnClickListener {
                takeBusiness(item)
                showButtonLawyer()
            }

        } else {
            binding.buttonForLawyer2.visibility = View.GONE
            binding.buttonForLawyerCloseLead.visibility = View.VISIBLE
            binding.buttonForLawyerCloseLead.setOnClickListener {
                closeLead()
            }
            binding.buttonForLawyerRefuseService.setOnClickListener {
                refuseBusiness(item)
            }
            binding.buttonChat.setOnClickListener {
                initChatUser(item, uId)
                //Toast.makeText(context, "new msg lawyer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun activeButtonForMessagingClient(uid: String) {
        binding.buttonForClient.visibility = View.VISIBLE
        binding.buttonForClient.setOnClickListener {
            initChatUser(item, uid)
        }
    }

    private fun setCustomUi(businessItem: LeadItem?) {
        if (businessItem != null) {
            originFieldCategory = businessItem.category
            accompanyingText = businessItem.messageLead
            category = getCategory(originFieldCategory)
            setFieldTitleCategory(originFieldCategory)
            var firstText = businessItem.firstText
            val paymentInfo =  businessItem.paymentInfo
            if(originFieldCategory == "clothing") {
                firstText += "\n" + "\n" + businessItem.twoText
                firstText += "\n" + "\n" + businessItem.freeText
                firstText += "\n" + "\n" + businessItem.fourText
            } else if (originFieldCategory == "furniture") {
                firstText += "\n" + "\n" + businessItem.twoText
                firstText += "\n" + "\n" + businessItem.freeText
            } else if (originFieldCategory == "auto") {
                firstText += "\n" + "\n" + businessItem.twoText
                firstText += "\n" + "\n" + businessItem.freeText
                firstText += "\n" + "\n" + businessItem.fourText
                firstText += "\n" + "\n" + businessItem.fiveText
                firstText += "\n" + "\n" + businessItem.sixText
                firstText += "\n" + "\n" + businessItem.sevenText
                firstText += "\n" + "\n" + businessItem.eightText
            }


            binding.titleSituationH1.text = category
            binding.accordionDescription1.text = firstText
            binding.accordionDescription3.text = paymentInfo

        }

        val expansionLayout: ExpansionLayout = binding.expansionLayout
        val expansionLayout2: ExpansionLayout = binding.expansionLayout2
        val expansionLayout3: ExpansionLayout = binding.expansionLayout3
        val expansionLayoutCollection = ExpansionLayoutCollection()
        expansionLayoutCollection.add(expansionLayout)
        expansionLayoutCollection.add(expansionLayout2)
        expansionLayoutCollection.add(expansionLayout3)

    }

    fun whatsGroupFile(groupName: String): LinearLayout {
        return when {
            groupName.contains("firstGroup") -> binding.accordionDescriptionLinear1
            groupName.contains("twoGroup") -> binding.accordionDescriptionLinear2
            groupName.contains("freeGroup") -> binding.accordionDescriptionLinear3
            groupName.contains("fourGroup") -> binding.accordionDescriptionLinear4
            groupName.contains("fiveGroup") -> binding.accordionDescriptionLinear5
            groupName.contains("sixGroup") -> binding.accordionDescriptionLinear6
            groupName.contains("sevenGroup") -> binding.accordionDescriptionLinear7
            groupName.contains("eightGroup") -> binding.accordionDescriptionLinear8
            else -> binding.accordionDescriptionLinear1
        }
    }


    /*пробуем создать новый чат */
    private fun initChatUser(idLead: String, to: String) {
       // Timber.v("userProfile {$to}")
        val usersCollection = UserUtils.getDocumentRefBussines(to)
        usersCollection.get().addOnSuccessListener { profile ->
            if (profile.exists()) {
                val userProfile = profile.toObject(UserProfile::class.java)
                val mobile = userProfile?.mobile?.country + " " +
                        userProfile?.mobile?.number
                val chatUser= ChatUser(
                    //id= "${preference.getUid()}_${to}_${idLead}",localName = "Дело ${idLead}",user = userProfile!!,
                    id= to + idLead,localName = "Дело ${idLead}",user = UserProfile(to + idLead,13232113L,123321321L, "", "Дело №" + idLead),
                    documentId = idLead)
                    //documentId = "${preference.getUid()}_${to}_${idLead}")
                Timber.v("userProfile {$chatUser}")
                viewModel.setChatUser(chatUser)

                val action = FMyBussines_pageDirections.actionFMyBussinesPageToFSingleChat(chatUser)
                findNavController().navigate(action)
               /* val message = createMessage(to, idLead).apply {
                    textMessage= TextMessage("msg ${to}")
                    chatUsers= ArrayList()
                }*/
                //viewModel.sendMessageLead(message, idLead)
                //viewModel.dbRepository.insertMessage(message)
                //viewModel.removeTypingCallbacks()
            } else {
                Timber.v("userProfile not exists")
            }
        }


    }


    private fun createMessage(to: String, idLead: String): Message {
        //val chatUserId = to
        val chatUserId = to

        return Message(
            System.currentTimeMillis(),
            //from = preference.getUid().toString() + "_" + idLead,
            from = preference.getUid().toString() + idLead,
            chatUserId=chatUserId,
            to = to, senderName = preference.getUserProfile()!!.userName,
            senderImage = preference.getUserProfile()!!.image
        )
    }



    /*пробуем создать новый чат */

    fun setDataStageInView() {
        val listArrayStages: ArrayList<StageBussines> = ArrayList()
        viewModelStage.getStageBussinesLiveData(item).observe(context as FragmentActivity) {
            //Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            for (index in it.products!!.indices) {
                listArrayStages.add(
                    StageBussines(
                        it.products!![index].id,
                        item.toInt(),
                        it.products!![index].title,
                        it.products!![index].description,
                        it.products!![index].dateTime
                    )
                )
            }
            stageItemAdapter.submitList(listArrayStages)
        }
    }

    private fun showButtonLawyer() {
        binding.buttonForLawyer2.visibility = View.GONE
        binding.buttonForLawyerRefuseService.visibility = View.GONE
        binding.buttonForLawyerCloseLead.visibility = View.VISIBLE
    }

    private fun refuseBusiness(item: String) {
        val data = hashMapOf("idLawyer" to "")
        val docRef = getDocumentRef().document(item)
        docRef.set(data, SetOptions.merge())
        viewModelStage.deleteAllStage(item)
    }


    private fun takeBusiness(item: String) {
        val data = hashMapOf("idLawyer" to preference.getUid())
        val docRef = getDocumentRef().document(item)
        docRef.set(data, SetOptions.merge())
        //добавляем первый этап в стадии
        addFirstStage()
    }

    private fun closeLead() {
            val data = hashMapOf("status" to "close")
            val docRef = getDocumentRef().document(item)
            docRef.set(data, SetOptions.merge())
            //добавляем первый этап в стадии
    }


    private fun addFirstStage() {
        val nameLawyer = firstUpperCase(viewModelProfile.name.value.toString()) + " " + firstUpperCase(viewModelProfile.serName.value.toString())
        val title = "Дело принято"
        val description  = "Юрист " + nameLawyer + " взял дело в работу."
        viewModelStage.addStageBussines(item.toInt(), title, description)

    }


    fun firstUpperCase(word: String?): String? {
        return if (word == null || word.isEmpty()) "" else word.substring(0, 1)
            .uppercase(Locale.getDefault()) + word.substring(1) //или return word;
    }

    private fun setFieldTitleCategory(categoryOrigin: Any?) {

        when(categoryOrigin) {
            "medical" -> setStandartField()
            "auto" -> setAutoField()
            "appliances" -> setStandartField()
            "newBuildings" -> setNewBuildingsField()
            "furniture" -> setStandartField()
            "clothing" -> setStandartField()
            else -> setStandartField()
        }

    }

    private fun setNewBuildingsField() {
            binding.accordionDescriptionTitle1.text = "Договор долевого участия"
            binding.accordionDescriptionTitle2.text = "Разрешение на ввод объекта в эксплуатацию"
            binding.accordionDescriptionTitle3.text = "Уведомление от застройщика о готовности объекта и необходимости его принятия участником"
            binding.accordionDescriptionTitle4.text = "Акт приема-передачи (при наличии)"
            binding.accordionDescriptionTitle5.text = "Документы об оплате цены объекта"
            binding.accordionDescriptionTitle6.text = "Акт осмотра объекта (акт о несоответствиях, перечень несоответствий)"
            binding.accordionDescriptionTitle7.text = "Претензионная переписка"
            binding.accordionDescriptionTitle8.text = "Сопроводительный текст"

            val mainLayout: LinearLayout = binding.accordionDescriptionLinear8
            val tv_dynamic = TextView(context)
            tv_dynamic.textSize = 14f
            tv_dynamic.text = accompanyingText
            mainLayout.addView(tv_dynamic)

    }

    private fun setAutoField() {
        binding.accordionDescriptionTitle1.text = "Договор купли-продажи ТС"
        binding.accordionDescriptionTitle2.text = "Документы об оплате ТС"
        binding.accordionDescriptionTitle3.text = "Паспорт ТС"
        binding.accordionDescriptionTitle4.text = "Свидетельство о регистрации ТС"
        binding.accordionDescriptionTitle5.text = "Акт осмотра ТС (акт о несоответствиях, перечень несоответствий)"
        binding.accordionDescriptionTitle6.text = "Претензионная переписка"
        binding.accordionDescriptionTitle7.text = "Сопроводительный текст"
        binding.accordionDescriptionTitle8.visibility = View.GONE
        binding.accordionDescriptionLinear8.visibility = View.GONE

        val mainLayout: LinearLayout = binding.accordionDescriptionLinear7
        val tv_dynamic = TextView(context)
        tv_dynamic.textSize = 14f
        tv_dynamic.text = accompanyingText
        mainLayout.addView(tv_dynamic)

    }

    private fun setStandartField() {
        binding.accordionDescriptionTitle6.visibility = View.GONE
        binding.accordionDescriptionLinear6.visibility = View.GONE
        binding.accordionDescriptionTitle7.visibility = View.GONE
        binding.accordionDescriptionLinear7.visibility = View.GONE
        binding.accordionDescriptionTitle8.visibility = View.GONE
        binding.accordionDescriptionLinear8.visibility = View.GONE

        val mainLayout: LinearLayout = binding.accordionDescriptionLinear5
        val tv_dynamic = TextView(context)
        tv_dynamic.textSize = 14f
        tv_dynamic.text = accompanyingText
        mainLayout.addView(tv_dynamic)

    }


    fun setImageView(id: Int) : ImageView {
        val imageView = ImageView(context)
        imageView.setImageResource(com.example.lawyerapplication.R.drawable.foto_add_2)
        imageView.id = id

        imageView.setOnClickListener {
           // Toast.makeText(context, "this is ${arrayListImgeData[id]}", Toast.LENGTH_SHORT).show()

            binding.fullSizeImageView.show()
            StfalconImageViewer.Builder(
                context,
                listOf(MyImage(arrayListImgData[id].toString()))
            ) { imageView, myImage ->
                ImageUtils.loadGalleryImage(myImage.url, imageView)
            }
                .withDismissListener { binding.fullSizeImageView.visibility = View.GONE }
                .show()


        }
             val imageViewLayoutParams =
                ViewGroup.LayoutParams(
                   ViewGroup.LayoutParams.WRAP_CONTENT,
                    // 220,
                    //35
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )


            imageView.setLayoutParams(imageViewLayoutParams)

/*
            imageView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(0,0,15,0)
            }*/

            return imageView
}


    private fun getCategory(str: Any): String {
        return when(str) {
            "medical" -> "Медицинские услуги"
            "auto" -> "Медицинские услуги"
            "appliances" -> "Бытовая техника"
            "newBuildings" -> "Новостройки"
            "furniture" -> "Мебель"
            "clothing" -> "Одежда"
            else -> "услуга не определена"
        }
    }


    fun getDocumentRef(): CollectionReference {
        val db = FirebaseFirestore.getInstance()
        return db.collection("Leads")
    }


    fun getURLForResource(resourceId: Int): String? {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + com.example.lawyerapplication.R::class.java.getPackage().getName() + "/" + resourceId
        ).toString()
    }

    private fun parseParams() {
        item = args.leadId
    }

    private fun initDialog() {
        //Toast.makeText(context, "init dialog", Toast.LENGTH_SHORT).show()
        try {
            dialog = Dialog(requireContext())
            val layoutBinder = AlertAddStageBinding.inflate(layoutInflater)

            dialog.setContentView(layoutBinder.root)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            //layoutBinder.txtSubTitle.text = "Карта ${numberCard} не будет отображаться в списке способов оплаты. Вы сможете в любой момент снова добавить её данные в приложение"
            layoutBinder.enterButton.setOnClickListener {
                //Log.d("logDialog", )
                //Log.d("logDialog", layoutBinder.etDescription.text.toString())
                val title = layoutBinder.etTitle.text.toString()
                val description  = layoutBinder.etDescription.text.toString()
                viewModelStage.addStageBussines(item.toInt(), title, description)

                Handler().postDelayed({
                    dialog.dismiss()
                    setDataStageInView()
                }, 1500)

                //addStageBussines


            }
            layoutBinder.imageClose.setOnClickListener {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvStageList) {
            stageItemAdapter = StageItemAdapter()
            adapter = stageItemAdapter
            recycledViewPool.setMaxRecycledViews(
                com.example.lawyerapplication.fragments.mycards.adapter.StageItemAdapter.VIEW_TYPE_ENABLED,
                com.example.lawyerapplication.fragments.mycards.adapter.StageItemAdapter.MAX_POOL_SIZE
            )

        }
//        setupClickListener()
    }

    private fun setupClickListener() {
        TODO("Not yet implemented")
    }

    companion object {
        const val BUSINESS_ITEM_ID = ""
    }

}