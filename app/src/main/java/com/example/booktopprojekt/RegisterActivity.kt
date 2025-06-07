package com.example.booktopprojekt

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booktopprojekt.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.internal.common.FirebaseInstallationId
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import org.intellij.lang.annotations.Pattern

class RegisterActivity : AppCompatActivity() {

    //view binding

    private lateinit var binding: ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progres dialogue
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialogue
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Prosze zaczekać")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click
        binding.registerBtn.setOnClickListener {
            validateData()
        }
    }


    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData()
    {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        if(name.isEmpty())
        {
            Toast.makeText(this, "Wpisz swoje imię...", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Niepoprawny email...", Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(this, "Wpisz swoje hasło...", Toast.LENGTH_SHORT).show();
        }

        else if(cPassword.isEmpty()){
            Toast.makeText(this, "Potwierdź swoje hasło...", Toast.LENGTH_SHORT).show();
        }
        else if(password != cPassword)
        {
            Toast.makeText(this, "Hasła nie pasują..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            createUserAccount()
        }
    }

    private fun createUserAccount()
    {
        progressDialog.setMessage("Tworzenia konto...")
        progressDialog.show()

        //create user firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Błąd podczas tworzenia twojego konta przez ${e.message}", Toast.LENGTH_SHORT).show();
            }
    }

    private fun updateUserInfo()
    {
        progressDialog.setMessage("Zapisanie danych użytkowniku..")

        val timestamp = System.currentTimeMillis()

        val uid: String? = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = uid!!
        hashMap["email"] = email
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid).setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Konto zostało stworzone...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Błąd podczas zapisywanie dane o użytkowniku przez ${e.message}", Toast.LENGTH_SHORT).show();
            }
    }


}