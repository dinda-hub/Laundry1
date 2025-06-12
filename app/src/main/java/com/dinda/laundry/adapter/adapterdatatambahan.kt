package com.dinda.laundry.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.google.firebase.database.FirebaseDatabase
import com.reza.laundry.modeldata.modeltransaksitambahan
import java.util.Locale

class adapterdatatambahan (
    private val list: ArrayList<modeltransaksitambahan>
) : RecyclerView.Adapter<adapterdatatambahan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idLayanan: TextView = itemView.findViewById(R.id.tvDataIDTambahan)
        val nama: TextView = itemView.findViewById(R.id.tvDataNamaTambahan)
        val harga: TextView = itemView.findViewById(R.id.tvDataHargaTambahan)
        val tanggalDibuat: TextView = itemView.findViewById(R.id.tv_TerdaftarTambahan)
        val btnHapus: Button = itemView.findViewById(R.id.btnHapusTambahan) // Tambahan untuk tombol hapus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.datatambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.idLayanan.text = "${item.idLayanan ?: "-"}"
        holder.nama.text = item.namaLayanan ?: "Name not available"

        // Format harga dengan pemisah ribuan
        val hargaFormatted = try {
            val harga = item.hargaLayanan?.toDoubleOrNull() ?: 0.0
            String.format(Locale("id", "ID"), "Rp %,.0f", harga)
        } catch (e: Exception) {
            "Rp ${item.hargaLayanan ?: "0"}"
        }
        holder.harga.text = hargaFormatted

        // Format tanggal dan jam
        if (!item.tanggalTerdaftar.isNullOrEmpty()) {
            holder.tanggalDibuat.text = "${item.tanggalTerdaftar}"
        } else {
            holder.tanggalDibuat.text = "-"
        }

        // Tambahan: Setup click listener untuk tombol hapus
        holder.btnHapus.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, item, position)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: ArrayList<modeltransaksitambahan>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    // Tambahan: Fungsi untuk menampilkan dialog konfirmasi hapus
    private fun showDeleteConfirmationDialog(context: android.content.Context, item: modeltransaksitambahan, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete the service? \"${item.namaLayanan}\"?")
            .setPositiveButton("Delete") { _, _ ->
                deleteItem(context, item, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Tambahan: Fungsi untuk menghapus data dari Firebase
    private fun deleteItem(context: android.content.Context, item: modeltransaksitambahan, position: Int) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("tambahan")

        // Hapus berdasarkan ID layanan
        item.idLayanan?.let { id ->
            myRef.orderByChild("idLayanan").equalTo(id)
                .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                        for (dataSnapshot in snapshot.children) {
                            dataSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    // Hapus dari list lokal dan update adapter
                                    list.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, list.size)

                                    Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Data failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } ?: run {
            Toast.makeText(context, "Service ID not found", Toast.LENGTH_SHORT).show()
        }
    }
}