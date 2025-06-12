package com.dinda.laundry.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.Layanan.TambahanLayananActivity
import com.dinda.laundry.R
import com.reza.laundry.modeldata.modellayanan

class adapterdatalayanan (
    private val listLayanan: ArrayList<modellayanan>,
    private val onEditClick: ((modellayanan, Int) -> Unit)? = null,
    private val onDeleteClick: ((modellayanan, Int) -> Unit)? = null,
    private val onViewClick: ((modellayanan, Int) -> Unit)? = null
) : RecyclerView.Adapter<adapterdatalayanan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_layanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLayanan[position]

        // Binding data dengan null check
        holder.tvDataIDLayanan?.text = item.idLayanan ?: ""
        holder.tvNama?.text = item.namaLayanan ?: ""
        holder.tvHarga?.text = item.hargaLayanan ?: ""
        holder.tvTerdaftar?.text = "${item.tanggalTerdaftar ?: "-"}"
        holder.tvCabang?.text = "${item.cabangLayanan ?: "-"}"

        // Click listener untuk card view (edit/sunting)
        holder.itemView.setOnClickListener {
            onEditClick?.invoke(item, position)
        }

        // Click listener untuk tombol hapus
        holder.btnHapus?.setOnClickListener {
            showDeleteConfirmation(holder.itemView, item, position)
        }

        // Click listener untuk tombol lihat (dialog_mod_layanan)
        holder.btnLihat?.setOnClickListener {
            onViewClick?.invoke(item, position)
        }
    }

    private fun showDeleteConfirmation(view: View, item: modellayanan, position: Int) {
        AlertDialog.Builder(view.context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete the service? \"${item.namaLayanan}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                onDeleteClick?.invoke(item, position)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Fungsi untuk menghapus item dari list
    fun removeItem(position: Int) {
        if (position >= 0 && position < listLayanan.size) {
            listLayanan.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listLayanan.size)
        }
    }

    // Fungsi untuk update item setelah edit
    fun updateItem(position: Int, updatedItem: modellayanan) {
        if (position >= 0 && position < listLayanan.size) {
            listLayanan[position] = updatedItem
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return listLayanan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Gunakan nullable untuk menghindari crash jika ID tidak ditemukan
        val tvDataIDLayanan: TextView? = try {
            itemView.findViewById(R.id.tvDataIDLayanan)
        } catch (e: Exception) {
            Log.e("AdapterLayanan", "tvDataIDLayanan not found: ${e.message}")
            null
        }

        val tvNama: TextView? = try {
            itemView.findViewById(R.id.tvDataNamaLayanan)
        } catch (e: Exception) {
            Log.e("AdapterLayanan", "tvDataNamaLayanan not found: ${e.message}")
            null
        }

        val tvHarga: TextView? = try {
            itemView.findViewById(R.id.tvDataHargaLayanan)
        } catch (e: Exception) {
            Log.e("AdapterLayanan", "tvDataHargaLayanan not found: ${e.message}")
            null
        }

        val tvCabang: TextView? = try {
            itemView.findViewById(R.id.tvDataCabangLayanan)
        } catch (e: Exception) {
            Log.e("AdapterLayanan", "tvDataCabangLayanan not found: ${e.message}")
            null
        }

        val tvTerdaftar: TextView? = try {
            itemView.findViewById(R.id.tv_Terdaftar)
        } catch (e: Exception) {
            // Coba ID alternatif yang mungkin ada
            try {
                itemView.findViewById(R.id.tv_Terdaftar)
            } catch (e2: Exception) {
                Log.e("AdapterLayanan", "tv_Terdaftar/tvDataTerdaftarLayanan not found: ${e2.message}")
                null
            }
        }

        // Tombol hapus dan lihat dengan try-catch
        val btnHapus: Button? = try {
            itemView.findViewById(R.id.btnHapus)
        } catch (e: Exception) {
            // Coba ID alternatif
            try {
                itemView.findViewById(R.id.btnHapus)
            } catch (e2: Exception) {
                Log.e("AdapterLayanan", "btnHapus/btDataHapusLayanan not found: ${e2.message}")
                null
            }
        }

        val btnLihat: Button? = try {
            itemView.findViewById(R.id.btnLihat)
        } catch (e: Exception) {
            // Coba ID alternatif
            try {
                itemView.findViewById(R.id.btnLihat)
            } catch (e2: Exception) {
                Log.e("AdapterLayanan", "btnLihat/btnDataLihatLayanan not found: ${e2.message}")
                null
            }
        }

        init {
            // Log untuk debugging - lihat ID mana yang berhasil ditemukan
            Log.d("AdapterLayanan", """
                ViewHolder initialized:
                - tvDataIDLayanan: ${if (tvDataIDLayanan != null) "Found" else "Not Found"}
                - tvNama: ${if (tvNama != null) "Found" else "Not Found"}
                - tvHarga: ${if (tvHarga != null) "Found" else "Not Found"}
                - tvCabang: ${if (tvCabang != null) "Found" else "Not Found"}
                - tvTerdaftar: ${if (tvTerdaftar != null) "Found" else "Not Found"}
                - btnHapus: ${if (btnHapus != null) "Found" else "Not Found"}
                - btnLihat: ${if (btnLihat != null) "Found" else "Not Found"}
            """.trimIndent())
        }
    }
}