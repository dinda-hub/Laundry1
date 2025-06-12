package com.dinda.laundry.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modeltransaksitambahan
import java.text.NumberFormat
import java.util.Locale

class adapterpilihtambahan (
    private val list: MutableList<modeltransaksitambahan>,
    private val onItemClick: (modeltransaksitambahan) -> Unit
) : RecyclerView.Adapter<adapterpilihtambahan.ViewHolder>() {

    companion object {
        private const val TAG = "AdapterPilihTambahan"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNomorUrut: TextView = itemView.findViewById(R.id.tvNomorUrut)
        private val tvNama: TextView = itemView.findViewById(R.id.namapilihlayanantambah)
        private val tvHarga: TextView = itemView.findViewById(R.id.hargapilihanlayanantambah)

        fun bind(tambahan: modeltransaksitambahan, position: Int) {
            // Set nomor urut (position + 1)
            tvNomorUrut.text = (position + 1).toString()

            // Log data yang diterima untuk debugging
            Log.d(TAG, "Binding position $position - ID: ${tambahan.id}, Name: ${tambahan.namaLayanan}, Harga: ${tambahan.hargaLayanan}")

            // Set nama layanan dengan validasi
            tvNama.text = if (tambahan.namaLayanan.isNullOrBlank()) {
                Log.w(TAG, "Empty service name for position $position")
                "Layanan Tidak Diketahui"
            } else {
                tambahan.namaLayanan
            }

            // Format harga dengan currency Indonesia
            tvHarga.text = formatHarga(tambahan.hargaLayanan, position)

            // Set click listener dengan null safety
            itemView.setOnClickListener {
                Log.d(TAG, "Item clicked at position $position: ${tambahan.namaLayanan}")
                onItemClick(tambahan)
            }
        }

        private fun formatHarga(harga: String?, position: Int): String {
            return when {
                harga.isNullOrBlank() -> {
                    Log.w(TAG, "Empty price for position $position")
                    "Rp 0"
                }
                else -> {
                    try {
                        // Bersihkan string dari karakter non-digit
                        val cleanHarga = harga.replace(Regex("[^0-9.]"), "")

                        // Convert ke number
                        val hargaNumber = when {
                            cleanHarga.contains(".") -> cleanHarga.toDouble().toLong()
                            cleanHarga.isNotEmpty() -> cleanHarga.toLong()
                            else -> 0L
                        }

                        // Format dengan currency Indonesia
                        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
                        "Rp ${formatter.format(hargaNumber)}"

                    } catch (e: NumberFormatException) {
                        Log.w(TAG, "Price format error for position $position: ${e.message}")
                        "Rp $harga"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rincian_tambahan_invoice, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < list.size) {
            holder.bind(list[position], position)
        } else {
            Log.e(TAG, "Invalid position: $position, list size: ${list.size}")
        }
    }

    // Method untuk mendapatkan item berdasarkan posisi
    fun getItem(position: Int): modeltransaksitambahan? {
        return if (position in 0 until list.size) {
            list[position]
        } else {
            Log.w(TAG, "Invalid position for getItem: $position")
            null
        }
    }

    // Method untuk update data
    fun updateData(newList: List<modeltransaksitambahan>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
        Log.d(TAG, "Data updated, new size: ${list.size}")
    }
}