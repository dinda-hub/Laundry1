package com.dinda.laundry.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.dinda.laundry.cabang.TambahanCabangActivity
import com.dinda.laundry.modeldata.modelcabang

class adapterdatacabang (
    private val cabangList: ArrayList<modelcabang>,
    private val onViewClick: (modelcabang) -> Unit,
    private val onItemClick: (modelcabang) -> Unit
) : RecyclerView.Adapter<adapterdatacabang.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdCabang: TextView = itemView.findViewById(R.id.tvIDCABANG)
        val tvNamaCabang: TextView = itemView.findViewById(R.id.tvNamaLokasiCabang)
        val tvAlamatCabang: TextView = itemView.findViewById(R.id.tvAlamatCabang)
        val tvTeleponCabang: TextView = itemView.findViewById(R.id.tvTeleponCabang)
        val btnView: Button = itemView.findViewById(R.id.btnLihat)
        val btnContact: Button = itemView.findViewById(R.id.btnContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.datacabang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cabang = cabangList[position]

        holder.tvIdCabang.text = cabang.idCabang ?: ""
        holder.tvNamaCabang.text = cabang.namaCabang ?: ""
        holder.tvAlamatCabang.text = cabang.alamatCabang ?: ""
        holder.tvTeleponCabang.text = cabang.nohpCabang ?: ""

        // Click listener untuk CardView (edit dengan placeholder data)
        holder.itemView.setOnClickListener {
            onItemClick(cabang)
        }

        // Click listener untuk tombol Lihat (dialog detail)
        holder.btnView.setOnClickListener {
            onViewClick(cabang)
        }

        // Click listener untuk tombol Contact (hubungi via WhatsApp/telepon)
        holder.btnContact.setOnClickListener {
            val context = holder.itemView.context
            val phoneNumber = cabang.nohpCabang?.replace(Regex("[^0-9]"), "") // Remove non-numeric characters

            if (!phoneNumber.isNullOrEmpty()) {
                // Format phone number for WhatsApp (remove leading 0, add 62 for Indonesia)
                val formattedNumber = if (phoneNumber.startsWith("0")) {
                    "62${phoneNumber.substring(1)}"
                } else if (!phoneNumber.startsWith("62")) {
                    "62$phoneNumber"
                } else {
                    phoneNumber
                }

                val message = "Hello, I would like to ask for information about laundry services at the branch. ${cabang.namaCabang}."
                val whatsappIntent = Intent(Intent.ACTION_VIEW)
                whatsappIntent.data = Uri.parse("https://wa.me/$formattedNumber?text=${Uri.encode(message)}")

                try {
                    context.startActivity(whatsappIntent)
                } catch (e: Exception) {
                    // Fallback to regular phone call if WhatsApp is not available
                    val phoneIntent = Intent(Intent.ACTION_DIAL)
                    phoneIntent.data = Uri.parse("tel:${cabang.nohpCabang}")
                    context.startActivity(phoneIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int = cabangList.size
}