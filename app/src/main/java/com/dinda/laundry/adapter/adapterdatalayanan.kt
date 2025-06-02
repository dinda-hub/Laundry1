package com.dinda.laundry.adapter

import android.content.Intent
import android.util.Log
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

        // DEBUG: Log semua data yang diterima
        Log.d("AdapterDebug", "=== Item ke-$position ===")
        Log.d("AdapterDebug", "ID Layanan: ${currentLayanan.idLayanan}")
        Log.d("AdapterDebug", "Nama Layanan: ${currentLayanan.namaLayanan}")
        Log.d("AdapterDebug", "Harga: ${currentLayanan.hargaLayanan}")
        Log.d("AdapterDebug", "Cabang: '${currentLayanan.cabangLayanan}'")
        Log.d("AdapterDebug", "Cabang null? ${currentLayanan.cabangLayanan == null}")
        Log.d("AdapterDebug", "Cabang empty? ${currentLayanan.cabangLayanan?.isEmpty()}")

        // Set data to views dengan pengecekan null yang lebih detail
        holder.tvIdLayanan.text = currentLayanan.idLayanan ?: "ID tidak tersedia"
        holder.tvNamaLayanan.text = currentLayanan.namaLayanan ?: "Nama tidak tersedia"
        holder.tvHargaLayanan.text = "Rp ${currentLayanan.hargaLayanan ?: "0"}"

        // Pengecekan cabang yang lebih detail
        val cabangText = when {
            currentLayanan.cabangLayanan.isNullOrEmpty() -> "Cabang tidak diketahui"
            else -> "Cabang: ${currentLayanan.cabangLayanan}"
        }
        holder.tvCabangLayanan.text = cabangText

        // DEBUG: Log text yang di-set ke TextView
        Log.d("AdapterDebug", "Text yang di-set ke TextView Cabang: '$cabangText'")

        // Set click listeners
        holder.btnHubungi.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Menghubungi layanan ${currentLayanan.namaLayanan}", Toast.LENGTH_SHORT).show()
        }

        holder.btnLihat.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TambahanLayananActivity::class.java).apply {
                putExtra("idLayanan", currentLayanan.idLayanan)
                putExtra("JudulLayanan", "Edit Layanan")
                putExtra("namaLayanan", currentLayanan.namaLayanan)
                putExtra("harga", currentLayanan.hargaLayanan)
                putExtra("idCabangLayanan", currentLayanan.cabangLayanan)
            }
            context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TambahanLayananActivity::class.java).apply {
                putExtra("idLayanan", currentLayanan.idLayanan)
                putExtra("JudulLayanan", "Detail Layanan")
                putExtra("namaLayanan", currentLayanan.namaLayanan)
                putExtra("harga", currentLayanan.hargaLayanan)
                putExtra("idCabangLayanan", currentLayanan.cabangLayanan)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return layananList.size
    }
}