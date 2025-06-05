package com.dinda.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modeltransaksitambahan
import java.util.Locale

class adapterdatatambahan (
    private val list: ArrayList<modeltransaksitambahan>
) : RecyclerView.Adapter<adapterdatatambahan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idLayanan: TextView = itemView.findViewById(R.id.tvDataIDTambahan)
        val nama: TextView = itemView.findViewById(R.id.tvDataNamaTambahan)
        val harga: TextView = itemView.findViewById(R.id.tvDataHargaTambahan)
        val tanggalDibuat: TextView = itemView.findViewById(R.id.tv_TerdaftarTambahan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.datatambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.idLayanan.text = "ID: ${item.idLayanan ?: "-"}"
        holder.nama.text = item.namaLayanan ?: "Nama tidak tersedia"

        // Format harga dengan pemisah ribuan
        val hargaFormatted = try {
            val harga = item.hargaLayanan?.toDoubleOrNull() ?: 0.0
            String.format(Locale("id", "ID"), "Rp %,.0f", harga)
        } catch (e: Exception) {
            "Rp ${item.hargaLayanan ?: "0"}"
        }
        holder.harga.text = hargaFormatted

        // Format tanggal dan jam
        if (!item.tanggalTerdaftar.isNullOrEmpty()) {
            holder.tanggalDibuat.text = "Tanggal: ${item.tanggalTerdaftar}"
        } else {
            holder.tanggalDibuat.text = "Tanggal: -"
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: ArrayList<modeltransaksitambahan>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}