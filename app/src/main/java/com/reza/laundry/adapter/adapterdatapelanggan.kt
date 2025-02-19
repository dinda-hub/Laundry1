package com.reza.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.reza.laundry.R
import android.widget.TextView
import com.reza.laundry.modeldata.modelpelanggan

class adapterdatapelanggan(private val listPelanggan: ArrayList<modelpelanggan>) :
    RecyclerView.Adapter<adapterdatapelanggan.ViewHolder>() {

    override  fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.datapelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPelanggan[position]

        holder.tvCARD_PELANGGAN_ID.text = pelanggan.idPelanggan
        holder.tvCARD_PELANGGAN_NAMA.text = pelanggan.namaPelanggan
        holder.tvCARD_PELANGGAN_ALAMAT.text = pelanggan.alamatPelanggan
        holder.tvCARD_PELANGGAN_NOHP.text = pelanggan.noHPPelanggan
        holder.tvCARD_PELANGGAN_CABANG.text = pelanggan.idCabang
        holder.cardPelanggan.setOnClickListener {

        }
        holder.lihat.setOnClickListener {

        }
        holder.hubungi.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardPelanggan = itemView.findViewById<View>(R.id.datapelanggan)
        val tvCARD_PELANGGAN_ID = itemView.findViewById<TextView>(R.id.tv_id_pelanggan)
        val tvCARD_PELANGGAN_NAMA = itemView.findViewById<TextView>(R.id.tv_nama_pelanggan)
        val tvCARD_PELANGGAN_ALAMAT = itemView.findViewById<TextView>(R.id.tv_alamat)
        val tvCARD_PELANGGAN_NOHP = itemView.findViewById<TextView>(R.id.tv_no_hp_pelanggan)
        val tvCARD_PELANGGAN_CABANG = itemView.findViewById<TextView>(R.id.tv_cabang_pelanggan)
        val lihat = itemView.findViewById<Button>(R.id.btn_lihat1)
        val hubungi = itemView.findViewById<Button>(R.id.btn_hubungi1)
    }
}