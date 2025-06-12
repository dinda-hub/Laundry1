package com.reza.laundry.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modellayanan

class adapterpilihlayanan (
    private val listLayanan: MutableList<modellayanan>,
    private val onItemClick: (modellayanan) -> Unit
) : RecyclerView.Adapter<adapterpilihlayanan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvPilihLayanan: CardView = itemView.findViewById(R.id.cvcardlayanan)
        val tvIDPilihLayanan: TextView = itemView.findViewById(R.id.tvcardpilihanlayanan)
        val tvNamaPilihLayanan: TextView = itemView.findViewById(R.id.namapilihlayanan)
        val tvHargaPilihLayanan: TextView = itemView.findViewById(R.id.hargapilihanlayanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihlayanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layanan = listLayanan[position]

        // Set data ke views
        holder.tvIDPilihLayanan.text = "[${layanan.idLayanan}]"
        holder.tvNamaPilihLayanan.text = layanan.namaLayanan ?: "Name not available"
        holder.tvHargaPilihLayanan.text = "Rp ${layanan.hargaLayanan ?: 0}"

        // Set click listener
        holder.cvPilihLayanan.setOnClickListener {
            onItemClick(layanan)
        }
    }

    override fun getItemCount(): Int = listLayanan.size
}