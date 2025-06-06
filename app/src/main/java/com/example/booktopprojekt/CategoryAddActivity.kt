package com.example.booktopprojekt

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booktopprojekt.databinding.ActivityCategoryAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity: AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityCategoryAddBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        // firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog( this)
        progressDialog.setTitle("Proszę czekać...")
        progressDialog.setCanceledOnTouchOutside (false)

        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var category = ""

    private fun validateData() {

        //validate data
        //get data
        category = binding.categoryEt.text.toString().trim()
        //validate data
        if (category.isEmpty()) {
            Toast.makeText( this, "Wprowadź kategorię...", Toast.LENGTH_SHORT).show()
        }
        else{
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        //show progress
        progressDialog.show()
        //get timestamp
        val timestamp = System.currentTimeMillis()
        //setup data to add in firebase db
        val hashMap = HashMap<String, Any>() //second param is Any; because the value could be of any type
        hashMap["id"] = "$timestamp" //put in string quotes because timestamp is in double, we need in string for id
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"
        //add to firebase db: Database Root > Categories > categoryId > category info
        val ref = FirebaseDatabase.getInstance().getReference ( "Kategorii")
        ref.child( "$timestamp")
        .setValue (hashMap)
            .addOnSuccessListener {

                //added successfully
                progressDialog.dismiss()
                Toast.makeText(  this, "Dodano pomyślnie...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->

                //failed to add
                progressDialog.dismiss()
                Toast.makeText( this, "Nie udało się dodać z powodu ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}