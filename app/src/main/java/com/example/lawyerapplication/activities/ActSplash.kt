package com.example.lawyerapplication.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.CollectionReference
import com.example.lawyerapplication.R
import com.example.lawyerapplication.utils.MPreference
import com.example.lawyerapplication.utils.UserUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActSplash : AppCompatActivity() {

    @Inject
    lateinit var preference: MPreference

    @Inject
    lateinit var userCollection: CollectionReference

    //private lateinit var navController: NavController

    private lateinit var sharedViewModel: SharedViewModel

    //biometric auth
    private var isHaveBiometric: Boolean = true
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_splash)

        //navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        UserUtils.updatePushToken(this, userCollection,false)
        sharedViewModel.onFromSplash()
        sharedViewModel.openMainAct.observe(this, {
            if (preference.isLoggedIn() && preference.getUserProfile() != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                /*checkBiometricInDevice()
                if (isHaveBiometric) { // Тут проверка
                    initializeButtonForBiometric() // тут инициализируем кнопку входа по биометрии
                } else {
                    Toast.makeText(this@ActSplash, "у вас нет биометрической аутентификации", Toast.LENGTH_SHORT).show()
                }*/
            } else {
                startActivity(Intent(this, WelcomeAboutActivity::class.java))
                //startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }


    private fun checkBiometricInDevice() {
        val biometricManager = BiometricManager.from(this)
        //val buttonOpenBiometric = findViewById<Button>(R.id.buttonOpenCamera)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
              //  buttonOpenBiometric.visibility = View.VISIBLE
                isHaveBiometric = true
            }

            else -> {
               // buttonOpenBiometric.visibility = View.GONE
                isHaveBiometric = false
            }
        }
    }

    private fun initializeButtonForBiometric() {
       // val buttonBiometric = findViewById<Button>(R.id.buttonOpenBiometric)

        biometricPrompt = BiometricPrompt(this@ActSplash, ContextCompat.getMainExecutor(this), object:
            BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val cryptoObject = result.cryptoObject
                goMain()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(this@ActSplash, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@ActSplash, "Error", Toast.LENGTH_SHORT).show()
            }
        }
        )

       // buttonBiometric.setOnClickListener {
            biometricPrompt.authenticate(createBiometricPromptInfo())
       // }
    }

    private fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authorization")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun goMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}