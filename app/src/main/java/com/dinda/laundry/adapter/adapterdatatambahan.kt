package com.dinda.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modeltransaksitambahan

class adapterdatatambahan (
    private val list: List<modeltransaksitambahan>
) : RecyclerView.Adapter<adapterdatatambahan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.namatambahan)
        val harga: TextView = itemView.findViewById(R.id.hargatambahan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.datatambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nama.text = item.namaLayanan
        holder.harga.text = "Rp. ${item.hargaLayanan}"
    }

    override fun getItemCount(): Int = list.size
}