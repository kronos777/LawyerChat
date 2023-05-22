package com.example.lawyerapplication.fragments.my_business

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.lawyerapplication.databinding.FragmentMyBussinesPageBinding
import com.example.lawyerapplication.models.MyImage
import com.example.lawyerapplication.utils.ImageUtils
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.show
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FMyBussines_page : Fragment() {

    private lateinit var binding: FragmentMyBussinesPageBinding

    private lateinit var context: Activity

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference


    private lateinit var navController: NavController

    private var item: String = String()

    private var arrayListImgeData = mutableMapOf<Int, Uri?>()

    private var category: String = String()
    private var originFieldCategory: String = String()
    private var accompanyingText: String = String()
    private val viewModelProfile: BussinesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMyBussinesPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        binding.viewModel = viewModelProfile

        val role = viewModelProfile.isLawyer()
        Log.d("CurrentRole", "Current role user: ${role}")


        navController = Navigation.findNavController(activity!!, com.example.lawyerapplication.R.id.nav_host_fragment)

        //Log.d("TAG", "Current data id: ${item}")
        (activity as AppCompatActivity).findViewById<Toolbar>(com.example.lawyerapplication.R.id.toolbar).title = "Дело № ${item}"
                val docRef = getDocumentRef(context).document(item)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("Snapshotdata", "DocumentSnapshot data: ${document.data}")
                            originFieldCategory = document.data!!.get("category") as String
                            accompanyingText = document.data!!.get("messageLead") as String

                            category = getCategory(originFieldCategory)
                            setFieldTitleCategory(originFieldCategory)
                            var firstText = document.data!!.get("firstText") as String
                            val paymentInfo =  document.data!!.get("paymentInfo") as String
                            val lawyer = (document.data!!.get("idLawyer") as String).trim()
                            val client = (document.data!!.get("idClient") as String).trim()
                            if(!role && lawyer != "") {
                                //user bus
                                    val lawyer = viewModelProfile.getDataUser(context, lawyer)
                                    lawyer.get().addOnSuccessListener { documentUser ->
                                            if (documentUser != null) {
                                                val nameLawyer = documentUser.data!!.get("userName") as String
                                                val serNameLawyer = documentUser.data!!.get("serName") as String
                                               //val userRole = documentUser.data!!.get("role") as String
                                                val userImg = documentUser.data!!.get("image") as String

                                                binding.aboutUserH1.text = nameLawyer + " " + serNameLawyer
                                                //binding.imageProfile.setImageURI(userImg.toUri())
                                                Glide.with(context)
                                                    .load(userImg.toUri())
                                                    .into(binding.imageProfile)
                                                binding.roleApplicationH1.text = "Юрист"
                                                binding.cardProfileData.visibility = View.VISIBLE
                                               // Log.d("dataUser", "name data: ${userImg.toUri()}")
                                                //Log.d("dataUser", "sername data: ${serNameLawyer}")
                                            }
                                        }

                                binding.buttonForClient.visibility = View.VISIBLE

                            //user bus
                            } else if(role) {
                                val user = viewModelProfile.getDataUser(context, client)
                                user.get().addOnSuccessListener { documentUser ->
                                    if (documentUser != null) {
                                        val nameLawyer = documentUser.data!!.get("userName") as String
                                        val serNameLawyer = documentUser.data!!.get("serName") as String
                                        //val userRole = documentUser.data!!.get("role") as String
                                        val userImg = documentUser.data!!.get("image") as String

                                        binding.aboutUserH1.text = nameLawyer + " " + serNameLawyer
                                        //binding.imageProfile.setImageURI(userImg.toUri())
                                        Glide.with(context)
                                            .load(userImg.toUri())
                                            .into(binding.imageProfile)
                                        binding.roleApplicationH1.text = "Пользователь"
                                        binding.cardProfileData.visibility = View.VISIBLE
                                    }
                                }

                                binding.buttonsForLawyer.visibility = View.VISIBLE

                                if(lawyer == "") {
                                    binding.buttonForLawyer4.visibility = View.GONE
                                    binding.buttonForLawyer2.setOnClickListener {
                                        takeBusiness(item)
                                        showButtonLawyer()
                                    }

                                } else {
                                    binding.buttonForLawyer2.visibility = View.GONE
                                    binding.buttonForLawyer4.visibility = View.GONE
                                }

                            }



                            if(originFieldCategory == "clothing") {
                                firstText += "\n" + "\n" + document.data!!.get("twoText") as String
                                firstText += "\n" + "\n" + document.data!!.get("freeText") as String
                                firstText += "\n" + "\n" + document.data!!.get("fourText") as String
                            } else if (originFieldCategory == "furniture") {
                                firstText += "\n" + "\n" + document.data!!.get("twoText") as String
                                firstText += "\n" + "\n" + document.data!!.get("freeText") as String
                            } else if (originFieldCategory == "auto") {
                                firstText += "\n" + "\n" + document.data!!.get("twoText") as String
                                firstText += "\n" + "\n" + document.data!!.get("freeText") as String
                                firstText += "\n" + "\n" + document.data!!.get("fourText") as String
                                firstText += "\n" + "\n" + document.data!!.get("fiveText") as String
                                firstText += "\n" + "\n" + document.data!!.get("sixText") as String
                                firstText += "\n" + "\n" + document.data!!.get("sevenText") as String
                                firstText += "\n" + "\n" + document.data!!.get("eightText") as String
                            }


                            binding.titleSituationH1.text = category
                            binding.accordionDescription1.text = firstText
                            binding.accordionDescription3.text = paymentInfo

                        } else {
                            Log.d("TAG", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }

        val storage = Firebase.storage
        val listRef = storage.reference.child("Leads").child(item)

// You'll need to import com.google.firebase.storage.ktx.component1 and
// com.google.firebase.storage.ktx.component2
        listRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                   // Log.d("fileStoragePrefix", prefix.name)
                }

               // items.forEach { item ->
                    for (index in items.indices) {
                    // All the items under listRef.
                        //item.downloadUrl.addOnSuccessListener { urlTask ->
                        items[index].downloadUrl.addOnSuccessListener { urlTask ->
                        // download URL is available here

                            val url = urlTask.path

                            if(url!!.contains("firstGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear1
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            } else if(url!!.contains("twoGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear2
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            } else if(url!!.contains("freeGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear3
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            } else if(url!!.contains("fourGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear4
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            } else if(url!!.contains("fiveGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear5
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            } else if(url!!.contains("sixGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear6
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            }  else if(url!!.contains("sevenGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear7
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            }  else if(url!!.contains("eightGroup")) {
                                val mainLayout: LinearLayout = binding.accordionDescriptionLinear8
                                arrayListImgeData[index] = urlTask
                                mainLayout.addView(setImageView(index))
                            }



                    }.addOnFailureListener { e ->
                        // Handle any errors
                    }
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }

        /*
        val expansionLayout: ExpansionLayout = binding.expansionLayout
        expansionLayout.addListener { expansionLayout, expanded ->
            Log.d("accordion", "expansionLayout expanded" + expanded.toString())
            Log.d("accordion", "expansionLayout " + expansionLayout.toString())
        }

        val expansionLayout2: ExpansionLayout = binding.expansionLayout2
        expansionLayout2.addListener { expansionLayout, expanded ->
            Log.d("accordion", "expansionLayout expanded2" + expanded.toString())
            Log.d("accordion", "expansionLayout2 " + expansionLayout.toString())
        }*/
        val expansionLayout: ExpansionLayout = binding.expansionLayout
        val expansionLayout2: ExpansionLayout = binding.expansionLayout2
        val expansionLayout3: ExpansionLayout = binding.expansionLayout3
        val expansionLayoutCollection = ExpansionLayoutCollection()
        expansionLayoutCollection.add(expansionLayout)
        expansionLayoutCollection.add(expansionLayout2)
        expansionLayoutCollection.add(expansionLayout3)

   //     expansionLayoutCollection.openOnlyOne(true)


    }

    private fun showButtonLawyer() {
        binding.buttonForLawyer2.visibility = View.GONE
        binding.buttonForLawyer3.visibility = View.GONE
        binding.buttonForLawyer4.visibility = View.VISIBLE
    }

    private fun takeBusiness(item: String) {
        val data = hashMapOf("idLawyer" to preference.getUid())
        val docRef = getDocumentRef(context).document(item)
        docRef.set(data, SetOptions.merge())
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
                listOf(MyImage(arrayListImgeData[id].toString()))
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


    fun getDocumentRef(context: Context): CollectionReference {
        val preference = MPreference(context)
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
        val args = requireArguments()
        item = args.getString(BUSINESS_ITEM_ID).toString()
         //Toast.makeText(getActivity(),"item" + item, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val BUSINESS_ITEM_ID = ""
    }

}