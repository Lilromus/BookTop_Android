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
import com.example.booktopprojekt.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.intellij.lang.annotations.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progres dialogue
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialogue
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Prosze zaczekać")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle click, not have acc, goto register screen

        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //handle click, begin login
        binding.loginBtn.setOnClickListener {
            validateData()
        }

    }
    private var email = ""
    private var password = ""


    private fun validateData()
    {
        //Input Data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Nieprawidłowy email", Toast.LENGTH_SHORT).show();
        }
        else if(email.isEmpty())
        {
            Toast.makeText(this, "Prosze wpisać swój email", Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty())
        {
            Toast.makeText(this, "Wpisz swoje hasło", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loginUser()
        }
    }

    private fun loginUser()
    {
        progressDialog.setMessage("Logowanie się...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Nieudana próba logowania, bo ${e.message}", Toast.LENGTH_SHORT).show();
            }
    }

    private fun checkUser()
    {

        progressDialog.setMessage("Checking User")

        val firebaseUser = firebaseAuth.currentUser

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user type if its user or admin
                    val userType = snapshot.child("userType").value
                    if(userType == "user")
                    {
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    }
                    else if(userType == "admin")
                    {
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })



    }
}