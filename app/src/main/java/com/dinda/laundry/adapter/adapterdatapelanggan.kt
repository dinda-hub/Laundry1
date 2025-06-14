package com.reza.laundry.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.dinda.laundry.R
import com.google.android.material.button.MaterialButton
import com.reza.laundry.modeldata.modelpelanggan

class adapterdatapelanggan(
    private val pelangganList: ArrayList<modelpelanggan>,
    private val onItemClick: (modelpelanggan) -> Unit,
    private val onDeleteClick: ((modelpelanggan) -> Unit)? = null
) : RecyclerView.Adapter<adapterdatapelanggan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDataIDPelanggan: TextView = itemView.findViewById(R.id.tvDataIDPelanggan)
        val tvDataNamaPelanggan: TextView = itemView.findViewById(R.id.tvDataNamaPelanggan)
        val tvDataAlamatPelanggan: TextView = itemView.findViewById(R.id.tvDataAlamatPelanggan)
        val tvDataNoHpPelanggan: TextView = itemView.findViewById(R.id.tvDataNoHpPelanggan)
        val tvDataCabangPelanggan: TextView = itemView.findViewById(R.id.tvDataCabangPelanggan)
        val tvDataTerdaftarPelanggan: TextView = itemView.findViewById(R.id.tvDataTerdaftarPelanggan)
        val btDataHubungiPelanggan: Button = itemView.findViewById(R.id.btDataHubungiPelanggan)
        val btnDataLihatPelanggan: Button = itemView.findViewById(R.id.btnDataLihatPelanggan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.datapelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = pelangganList[position]

        holder.tvDataIDPelanggan.text = pelanggan.idPelanggan ?: "N/A"
        holder.tvDataNamaPelanggan.text = pelanggan.namaPelanggan ?: "Name not available"
        holder.tvDataAlamatPelanggan.text = pelanggan.alamatPelanggan ?: "Address not available"
        holder.tvDataNoHpPelanggan.text = pelanggan.noHPPelanggan ?: "Mobile number not available"
        holder.tvDataCabangPelanggan.text = pelanggan.idCabangPelanggan ?: "Branch not available"
        holder.tvDataTerdaftarPelanggan.text = pelanggan.tanggalTerdaftar ?: "Date not available"

        // Tombol Hubungi - membuka WhatsApp atau dialer
        holder.btDataHubungiPelanggan.setOnClickListener {
            val phoneNumber = pelanggan.noHPPelanggan
            if (!phoneNumber.isNullOrEmpty()) {
                try {
                    // Coba buka WhatsApp terlebih dahulu
                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/${phoneNumber.removePrefix("0")}")
                    }
                    holder.itemView.context.startActivity(whatsappIntent)
                } catch (e: Exception) {
                    // Jika WhatsApp tidak tersedia, buka dialer
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    holder.itemView.context.startActivity(dialIntent)
                }
            } else {
                Toast.makeText(holder.itemView.context, "Mobile number not available", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Lihat - menampilkan dialog detail
        holder.btnDataLihatPelanggan.setOnClickListener {
            showDetailDialog(holder.itemView.context, pelanggan)
        }

        // Click pada item untuk edit
        holder.itemView.setOnClickListener {
            onItemClick(pelanggan)
        }
    }

    override fun getItemCount(): Int = pelangganList.size

    private fun showDetailDialog(context: Context, pelanggan: modelpelanggan) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialogmod_pelanggan)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Bind data ke dialog
        val tvIdPelanggan = dialog.findViewById<TextView>(R.id.idPelanggan)
        val tvNama = dialog.findViewById<TextView>(R.id.namaPelanggan)
        val tvAlamat = dialog.findViewById<TextView>(R.id.alamatPelanggan)
        val tvNoHP = dialog.findViewById<TextView>(R.id.nohpPelanggan)
        val tvCabang = dialog.findViewById<TextView>(R.id.cabangPelanggan)
        val tvTerdaftar = dialog.findViewById<TextView>(R.id.terdaftarPelanggan)
        val btnEdit = dialog.findViewById<MaterialButton>(R.id.btn_suntingPelanggan)
        val btnDelete = dialog.findViewById<MaterialButton>(R.id.btn_hapusPelanggan)

        // Set data
        tvIdPelanggan.text = pelanggan.idPelanggan ?: "N/A"
        tvNama.text = pelanggan.namaPelanggan ?: "Name not available"
        tvAlamat.text = pelanggan.alamatPelanggan ?: "Address not available"
        tvNoHP.text = pelanggan.noHPPelanggan ?: "Mobile number not available"
        tvCabang.text = pelanggan.idCabangPelanggan ?: "Branch not available"
        tvTerdaftar.text = pelanggan.tanggalTerdaftar ?: "Date not available"

        // Tombol Edit
        btnEdit.setOnClickListener {
            dialog.dismiss()
            onItemClick(pelanggan) // Memanggil fungsi edit yang sudah ada
        }

        // Tombol Delete
        btnDelete.setOnClickListener {
            dialog.dismiss()
            // Panggil callback delete jika tersedia
            onDeleteClick?.invoke(pelanggan)
        }

        dialog.show()
    }
}