package com.reza.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.reza.laundry.R
import com.reza.laundry.modeldata.modelpelanggan
import com.reza.laundry.transaksi.DataTransaksiActivity

class adapterpilihpelanggan(
    private val pelangganList: ArrayList<modelpelanggan>
) : RecyclerView.Adapter<adapterpilihpelanggan.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihpelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = pelangganList[position]

        holder.tvID.text = "[$nomor]"
        holder.tvNama.text = item.namaPelanggan
        holder.tvAlamat.text = "Alamat : ${item.alamatPelanggan}"
        holder.tvNoHP.text = "No HP : ${item.noHPPelanggan}"

        holder.cvCARD.setOnClickListener {
            val intent = Intent(appContext, DataTransaksiActivity::class.java)
            intent.putExtra("idPelanggan", item.idPelanggan)
            intent.putExtra("nama", item.namaPelanggan)
            intent.putExtra("noHP", item.noHPPelanggan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return pelangganList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID = itemView.findViewById<TextView>(R.id.tvIDPilihPelanggan)
        val tvNama = itemView.findViewById<TextView>(R.id.tvNamaPilihPelanggan)
        val tvAlamat = itemView.findViewById<TextView>(R.id.tvAlamatPilihPelanggan)
        val tvNoHP = itemView.findViewById<TextView>(R.id.tvNoHPPilihPelanggan)
        val cvCARD = itemView.findViewById<View>(R.id.cvPilihPelanggan)
    }
}