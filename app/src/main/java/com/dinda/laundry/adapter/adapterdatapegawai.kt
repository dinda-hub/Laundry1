package com.reza.laundry.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.dinda.laundry.pegawai.TambahanPegawaiActivity
import com.google.android.material.button.MaterialButton
import com.reza.laundry.modeldata.modelpegawai

class adapterdatapegawai(
    private val listPegawai: ArrayList<modelpegawai>,
    private val onItemClick: (modelpegawai) -> Unit,
    private val onDeleteClick: (modelpegawai) -> Unit  // Parameter baru untuk delete
) : RecyclerView.Adapter<adapterdatapegawai.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPegawai[position]

        holder.tvDataIDPegawai.text = item.idPegawai ?: ""
        holder.tvNama.text = item.namaPegawai ?: ""
        holder.tvAlamat.text = item.alamatPegawai ?: ""
        holder.tvNoHP.text = item.noHPPegawai ?: ""
        holder.tvTerdaftar.text = "Terdaftar: ${item.tanggalTerdaftar ?: "-"}"
        holder.tvCabang.text = "Cabang ${item.cabangPegawai ?: "Tidak Ada Cabang"}"

        // Klik card view (itemView)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Tombol hubungi - WhatsApp
        holder.btHubungi.setOnClickListener {
            val context = holder.itemView.context
            val phoneNumber = item.noHPPegawai?.replace(Regex("[^0-9]"), "") // Remove non-numeric characters

            if (!phoneNumber.isNullOrEmpty()) {
                // Format phone number for WhatsApp (remove leading 0, add 62 for Indonesia)
                val formattedNumber = if (phoneNumber.startsWith("0")) {
                    "62${phoneNumber.substring(1)}"
                } else if (!phoneNumber.startsWith("62")) {
                    "62$phoneNumber"
                } else {
                    phoneNumber
                }

                val message = "Halo ${item.namaPegawai}, saya ingin menghubungi Anda mengenai layanan laundry."
                val whatsappIntent = Intent(Intent.ACTION_VIEW)
                whatsappIntent.data = Uri.parse("https://wa.me/$formattedNumber?text=${Uri.encode(message)}")

                try {
                    context.startActivity(whatsappIntent)
                } catch (e: Exception) {
                    // Fallback to regular phone call if WhatsApp is not available
                    val phoneIntent = Intent(Intent.ACTION_DIAL)
                    phoneIntent.data = Uri.parse("tel:${item.noHPPegawai}")
                    context.startActivity(phoneIntent)
                }
            }
        }

        // Tombol lihat - Show dialog
        holder.btLihat.setOnClickListener {
            showEmployeeDetailDialog(holder.itemView.context, item)
        }
    }

    private fun showEmployeeDetailDialog(context: android.content.Context, pegawai: modelpegawai) {
        val dialog = Dialog(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialogmod_pegawai, null)
        dialog.setContentView(dialogView)

        // Set dialog properties
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Fill dialog with employee data
        dialogView.findViewById<TextView>(R.id.IDPegawai).text = pegawai.idPegawai ?: ""
        dialogView.findViewById<TextView>(R.id.NamaPegawai).text = pegawai.namaPegawai ?: ""
        dialogView.findViewById<TextView>(R.id.AlamatPegawai).text = pegawai.alamatPegawai ?: ""
        dialogView.findViewById<TextView>(R.id.NoHpPegawai).text = pegawai.noHPPegawai ?: ""
        dialogView.findViewById<TextView>(R.id.CabangPegawai).text = pegawai.cabangPegawai ?: ""

        // Set tanggal terdaftar instead of status
        val tanggalTerdaftarTextView = dialogView.findViewById<TextView>(R.id.TerdaftarPegawai)
        tanggalTerdaftarTextView.text = pegawai.tanggalTerdaftar ?: "-"

        // Handle dialog buttons
        val btnEdit = dialogView.findViewById<MaterialButton>(R.id.btn_edit_pegawai)
        val btnDelete = dialogView.findViewById<MaterialButton>(R.id.btn_delete_pegawai)

        btnEdit.setOnClickListener {
            // Handle edit action - gunakan onItemClick untuk edit
            onItemClick(pegawai)
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            // Gunakan onDeleteClick callback yang baru
            onDeleteClick(pegawai)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int = listPegawai.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDataIDPegawai: TextView = itemView.findViewById(R.id.tvDataIDPegawai)
        val tvNama: TextView = itemView.findViewById(R.id.tvDataNamaPegawai)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvDataAlamatPegawai)
        val tvNoHP: TextView = itemView.findViewById(R.id.tvDataNoHpPegawai)
        val tvCabang: TextView = itemView.findViewById(R.id.tvDataCabangPegawai)
        val tvTerdaftar: TextView = itemView.findViewById(R.id.tvDataTerdaftarPegawai)
        val btHubungi: Button = itemView.findViewById(R.id.btDataHubungiPegawai)
        val btLihat: Button = itemView.findViewById(R.id.btnDataLihatPegawai)
    }
}
