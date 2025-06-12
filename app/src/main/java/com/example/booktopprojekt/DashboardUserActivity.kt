package com.example.booktopprojekt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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

        loadPdfList()//zaladowac

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
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                e.printStackTrace()
                Toast.makeText(this, "Brak uprawnień do pliku PDF. Nie można go zapisać.", Toast.LENGTH_SHORT).show()
            }

            val fileName = getFileNameFromUri(it)
            val metadataList = loadPdfMetadata()

            val matched = metadataList.find { meta ->
                fileName.contains(meta.fileName, ignoreCase = true)
            }

            val title = if (fileName.length > 30) fileName.take(30) + "..." else fileName
            val description = matched?.description ?: "Plik został dodany"
            val thumbnailResId = if (matched != null) {
                resources.getIdentifier(matched.coverResId, "drawable", packageName)
            } else {
                R.drawable.ic_pdf
            }

            pdfList.add(PDFModel(title, description, thumbnailResId, it.toString()))
            adapter.notifyItemInserted(pdfList.size - 1)
            savePdfList()
        }
    }


    private fun getFileNameFromUri(uri: Uri): String {
        var result = "Nowy PDF"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = it.getString(index)
                }
            }
        }
        return result
    }

    private fun loadPdfMetadata(): List<PdfMetadata> {
        val inputStream = resources.openRawResource(R.raw.pdf_data)
        val jsonText = inputStream.bufferedReader().use { it.readText() }

        val gson = com.google.gson.Gson()
        val type = object : com.google.gson.reflect.TypeToken<List<PdfMetadata>>() {}.type
        return gson.fromJson(jsonText, type)
    }

    private fun loadPdfList() {
        val prefs = getSharedPreferences("pdf_prefs", MODE_PRIVATE)
        val json = prefs.getString("pdf_list", null)
        val type = object : com.google.gson.reflect.TypeToken<MutableList<PDFModel>>() {}.type
        val savedList = com.google.gson.Gson().fromJson<MutableList<PDFModel>>(json, type) ?: mutableListOf()
        pdfList.clear()
        pdfList.addAll(savedList)
    }

    private fun savePdfList() {
        //zapisanie listy ksiazek
        val prefs = getSharedPreferences("pdf_prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = com.google.gson.Gson()
        val json = gson.toJson(pdfList)
        editor.putString("pdf_list", json)
    }



    private fun checkUser()
    {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null)
        {
            binding.subTitleTv.text = "Nie Zalogowany"
        }
        else
        {
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}