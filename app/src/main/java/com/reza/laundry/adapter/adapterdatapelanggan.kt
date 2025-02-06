package com.reza.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reza.laundry.R
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.reza.laundry.modeldata.modelpelanggan

class adapterdatapelanggan(
    private val listPelanggan: ArrayList<modelpelanggan>
) : RecyclerView.Adapter<adapterdatapelanggan.ViewHolder>()
{
    override  fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_PELANGGAN = itemView.findViewById<View>(R.id.cvPelanggan)
        val tvCARD_PELANGGAN_ID = itemView.findViewById<View>(R.id.tvtambahpelanggan)
        val tvCARD_PELANGGAN_NAMA = itemView.findViewById<View>(R.id.tv_nama_pelanggan)
        val tvCARD_PELANGGAN_ALAMAT = itemView.findViewById<View>(R.id.tvalamat_tambahanpelanggan)
        val tvCARD_PELANGGAN_NOHP = itemView.findViewById<View>(R.id.tv_no_hp_pelanggan)

    }
}