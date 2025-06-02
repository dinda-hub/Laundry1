package com.dinda.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modeltransaksitambahan

class adapterpilihtambahan (
    private val list: List<modeltransaksitambahan>,
    private val onItemClick: (modeltransaksitambahan) -> Unit
) : RecyclerView.Adapter<adapterpilihtambahan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.namapilihlayanantambah)
        val tvHarga: TextView = itemView.findViewById(R.id.hargapilihanlayanantambah)

        fun bind(tambahan: modeltransaksitambahan) {
            tvNama.text = tambahan.namaLayanan
            tvHarga.text = "Harga: ${tambahan.hargaLayanan}"

            itemView.setOnClickListener {
                onItemClick(tambahan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_layanantambahan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}