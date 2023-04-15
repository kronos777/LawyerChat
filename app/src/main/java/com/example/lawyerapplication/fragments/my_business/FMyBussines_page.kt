package com.example.lawyerapplication.fragments.my_business

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.lawyerapplication.R
import com.example.lawyerapplication.databinding.FragmentMyBussinesPageBinding
import com.example.lawyerapplication.utils.MPreference
import com.github.florent37.expansionpanel.ExpansionLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)

        //Log.d("TAG", "Current data id: ${item}")

                val docRef = getDocumentRef(context).document(item)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                            val category = getCategory(document.data!!.get("category") as String)
                            binding.titleSituationH1.text = category
                        } else {
                            Log.d("TAG", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }

        /**/
        val expansionLayout: ExpansionLayout = binding.expansionLayout
        expansionLayout.addListener { expansionLayout, expanded ->
            Log.d("accordion", "expansionLayout expanded" + expanded.toString())
            Log.d("accordion", "expansionLayout " + expansionLayout.toString())
        }

    }


    private fun getCategory(str: Any): String {
        return when(str) {
            "medical" -> "Медицинские услуги"

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
            "android.resource://" + R::class.java.getPackage().getName() + "/" + resourceId
        ).toString()
    }

    private fun parseParams() {
        val args = requireArguments()
        item = args.getString(BUSINESS_ITEM_ID).toString()
        // Toast.makeText(getActivity(),"all choice file" + situation9File, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val BUSINESS_ITEM_ID = ""
    }

}