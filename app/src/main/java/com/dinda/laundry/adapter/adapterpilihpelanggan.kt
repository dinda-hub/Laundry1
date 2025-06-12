package com.reza.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modelpelanggan
import com.dinda.laundry.transaksi.DataTransaksiActivity

class adapterpilihpelanggan(
    private val listPelanggan: MutableList<modelpelanggan>,
    private val onItemClick: (modelpelanggan) -> Unit
) : RecyclerView.Adapter<adapterpilihpelanggan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvPilihPelanggan: CardView = itemView.findViewById(R.id.cvPilihPelanggan)
        val tvIDPilihPelanggan: TextView = itemView.findViewById(R.id.tvIDPilihPelanggan)
        val tvNamaPilihPelanggan: TextView = itemView.findViewById(R.id.tvNamaPilihPelanggan)
        val tvAlamatPilihPelanggan: TextView = itemView.findViewById(R.id.tvAlamatPilihPelanggan)
        val tvNoHPPilihPelanggan: TextView = itemView.findViewById(R.id.tvNoHPPilihPelanggan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihpelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]

        // Set data ke views
        holder.tvIDPilihPelanggan.text = "[${pelanggan.idPelanggan}]"
        holder.tvNamaPilihPelanggan.text = pelanggan.namaPelanggan ?: holder.itemView.context.getString(R.string.nama_tidak_tersedia)
        holder.tvAlamatPilihPelanggan.text = holder.itemView.context.getString(R.string.alamat_format, pelanggan.alamatPelanggan ?: holder.itemView.context.getString(R.string.tidak_tersedia))
        holder.tvNoHPPilihPelanggan.text = holder.itemView.context.getString(R.string.no_hp_format, pelanggan.noHPPelanggan ?: holder.itemView.context.getString(R.string.tidak_tersedia))

        // Set click listener
        holder.cvPilihPelanggan.setOnClickListener {
            onItemClick(pelanggan)
        }
    }

    override fun getItemCount(): Int = listPelanggan.size
}