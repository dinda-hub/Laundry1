package com.dinda.laundry.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.Layanan.TambahanLayananActivity
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modellayanan

class adapterdatalayanan (private val layananList: ArrayList<modellayanan>) :
    RecyclerView.Adapter<adapterdatalayanan.LayananViewHolder>() {

    class LayananViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdLayanan: TextView = itemView.findViewById(R.id.tvidlayanan)
        val tvNamaLayanan: TextView = itemView.findViewById(R.id.tvnamadatalayanan)
        val tvHargaLayanan: TextView = itemView.findViewById(R.id.tvhargadatalayanan)
        val tvCabangLayanan: TextView = itemView.findViewById(R.id.tvcabangdatalayanan)
        val btnHubungi: Button = itemView.findViewById(R.id.btnhubungidatalayanan)
        val btnLihat: Button = itemView.findViewById(R.id.btnlihatdatalayanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayananViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_layanan, parent, false)
        return LayananViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LayananViewHolder, position: Int) {
        val currentLayanan = layananList[position]

        // Set data to views
        holder.tvIdLayanan.text = currentLayanan.idLayanan ?: "ID tidak tersedia"
        holder.tvNamaLayanan.text = currentLayanan.namaLayanan ?: "Nama tidak tersedia"
        holder.tvHargaLayanan.text = "Rp ${currentLayanan.hargaLayanan ?: "0"}"
        holder.tvCabangLayanan.text = "Cabang: ${currentLayanan.CabangLayanan ?: "Tidak diketahui"}"

        // Set click listeners
        holder.btnHubungi.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Menghubungi layanan ${currentLayanan.namaLayanan}", Toast.LENGTH_SHORT).show()
            // Implementasi hubungi di sini (misalnya buka WhatsApp, telepon, dll)
        }

        holder.btnLihat.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TambahanLayananActivity::class.java).apply {
                putExtra("idLayanan", currentLayanan.idLayanan)
                putExtra("JudulLayanan", "Edit Layanan")
                putExtra("namaLayanan", currentLayanan.namaLayanan)
                putExtra("harga", currentLayanan.hargaLayanan)
                putExtra("idCabangLayanan", currentLayanan.CabangLayanan)
            }
            context.startActivity(intent)
        }

        // Optional: Set click listener for the entire card
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TambahanLayananActivity::class.java).apply {
                putExtra("idLayanan", currentLayanan.idLayanan)
                putExtra("JudulLayanan", "Detail Layanan")
                putExtra("namaLayanan", currentLayanan.namaLayanan)
                putExtra("harga", currentLayanan.hargaLayanan)
                putExtra("idCabangLayanan", currentLayanan.CabangLayanan)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return layananList.size
    }
}