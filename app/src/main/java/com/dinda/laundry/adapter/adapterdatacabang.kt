package com.dinda.laundry.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.dinda.laundry.cabang.TambahanCabangActivity
import com.dinda.laundry.modeldata.modelcabang

class adapterdatacabang (private val cabangList: ArrayList<modelcabang>) :
    RecyclerView.Adapter<adapterdatacabang.CabangViewHolder>() {

    class CabangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdCabang: TextView = itemView.findViewById(R.id.tvidCabang)
        val tvNamaCabang: TextView = itemView.findViewById(R.id.tvnamadatacabang)
        val tvAlamatCabang: TextView = itemView.findViewById(R.id.tvalamatdatacabang)
        val tvNoHPCabang: TextView = itemView.findViewById(R.id.tvnohpdatacabang)
        val btnHubungi: Button = itemView.findViewById(R.id.btnhubungidatacabang)
        val btnLihat: Button = itemView.findViewById(R.id.btnlihatdatacabang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapterdatacabang.CabangViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.datacabang, parent, false)
        return adapterdatacabang.CabangViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: adapterdatacabang.CabangViewHolder, position: Int) {
        val currentCabang = cabangList[position]

        // Set data to views
        holder.tvIdCabang.text = currentCabang.idCabang ?: "ID tidak tersedia"
        holder.tvNamaCabang.text = currentCabang.namaCabang ?: "Nama tidak tersedia"
        holder.tvAlamatCabang.text = "Alamat: ${currentCabang.alamatCabang ?: "Alamat tidak diketahui"}"
        holder.tvNoHPCabang.text = "No HP: ${currentCabang.nohpCabang ?: "No HP tidak diketahui"}"

        // Set click listeners
        holder.btnHubungi.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Menghubungi Cabang ${currentCabang.namaCabang}", Toast.LENGTH_SHORT).show()
            // Implementasi hubungi di sini (misalnya buka WhatsApp, telepon, dll)
        }

        holder.btnLihat.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TambahanCabangActivity::class.java).apply {
                putExtra("idCabang", currentCabang.idCabang)
                putExtra("judulCabang", "Edit Cabang")
                putExtra("namaCabang", currentCabang.namaCabang)
                putExtra("alamatCabang", currentCabang.alamatCabang)
                putExtra("nohpCabang", currentCabang.nohpCabang)
            }
            context.startActivity(intent)
        }

        // Optional: Set click listener for the entire card
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TambahanCabangActivity::class.java).apply {
                putExtra("idCabang", currentCabang.idCabang)
                putExtra("judulCabang", "Edit Cabang")
                putExtra("namaCabang", currentCabang.namaCabang)
                putExtra("alamatCabang", currentCabang.alamatCabang)
                putExtra("nohpCabang", currentCabang.nohpCabang)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return cabangList.size
    }
}