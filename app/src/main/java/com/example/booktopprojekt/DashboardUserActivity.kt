package com.example.booktopprojekt

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booktopprojekt.databinding.ActivityDashboardAdminBinding
import com.example.booktopprojekt.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager


class DashboardUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardUserBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private val pdfList = mutableListOf<PDFModel>()

    private lateinit var adapter: PDFAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        adapter = PDFAdapter(pdfList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addPdfFab.setOnClickListener {
            pdfPickerLauncher.launch("application/pdf")
        }


        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val title = "Nowy PDF"
            val description = "Plik został dodany"
            val thumbnail = R.drawable.ic_pdf
            pdfList.add(PDFModel(title, description, thumbnail, uri))
            adapter.notifyItemInserted(pdfList.size - 1)
        }
    }


    private fun checkUser()
    {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null)
        {
            //not logged, user can stay in dashboard
            binding.subTitleTv.text = "Nie Zalogowany"
        }
        else
        {
            //logged in, show user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.subTitleTv.text = email
        }
    }
}