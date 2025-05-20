package com.reza.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reza.laundry.R
import com.reza.laundry.modeldata.modellayanan

class adapterpilihlayanan (private val listLayanan: ArrayList<modellayanan>) :
    RecyclerView.Adapter<adapterpilihlayanan.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihlayanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLayanan[position]
        holder.tvCARD_LAYANAN_ID.text = item.idLayanan
        holder.tvCARD_LAYANAN_NAMA.text = item.namaLayanan
        holder.tvCARD_LAYANAN_HARGA.text = item.harga
        holder.cvCARD_LAYANAN.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return listLayanan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD_LAYANAN= itemView.findViewById<View>(R.id.cvcardlayanan)
        val tvCARD_LAYANAN_ID = itemView.findViewById<TextView>(R.id.tvcardpilihanlayanan)
        val tvCARD_LAYANAN_NAMA = itemView.findViewById<TextView>(R.id.pilihlayanan)
        val tvCARD_LAYANAN_HARGA = itemView.findViewById<TextView>(R.id.hargapilihanlayanan)
    }
}
