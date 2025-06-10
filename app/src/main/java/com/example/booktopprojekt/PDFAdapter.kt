package com.example.booktopprojekt

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PDFAdapter(private val pdfList: List<PDFModel>) :
    RecyclerView.Adapter<PDFAdapter.PDFViewHolder>() {

    inner class PDFViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.titleTv)
        val descriptionTv: TextView = itemView.findViewById(R.id.descriptionTv)
        val thumbnailIv: ImageView = itemView.findViewById(R.id.thumbnailIv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_card, parent, false)
        return PDFViewHolder(view)
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val pdf = pdfList[position]
        holder.titleTv.text = pdf.title
        holder.descriptionTv.text = pdf.description
        holder.thumbnailIv.setImageResource(pdf.thumbnailResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdf.uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pdfList.size
}
