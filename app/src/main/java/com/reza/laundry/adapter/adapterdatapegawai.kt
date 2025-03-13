package com.reza.laundry.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.reza.laundry.R
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.core.Context
import com.reza.laundry.modeldata.modelpegawai
import com.reza.laundry.pegawai.TambahanPegawaiActivity

class adapterdatapegawai (private val listPegawai: ArrayList<modelpegawai>) :
    RecyclerView.Adapter<adapterdatapegawai.ViewHolder>() {
        lateinit var appContext: Context

    override  fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = listPegawai[position]

        holder.tvCARD_PEGAWAI_ID.text = pelanggan.idPegawai
        holder.tvCARD_PEGAWAI_NAMA.text = pelanggan.namaPegawai
        holder.tvCARD_PEGAWAI_ALAMAT.text = pelanggan.alamatPegawai
        holder.tvCARD_PEGAWAI_NOHP.text = pelanggan.noHPPegawai
        holder.tvCARD_PEGAWAI_CABANG.text = pelanggan.idCabangPegawai
        holder.cardPegawai.setOnClickListener {

        }
        holder.lihat.setOnClickListener {

        }
        holder.hubungi.setOnClickListener{

        }
        holder.cardPegawai.setOnClickListener {
            val intent = Intent(appContext, TambahanPegawaiActivity::class.java)
            intent.putExtra("Judul", "Edit Pegawai")
            intent.putExtra("idPegawai", pelanggan.idPegawai)
            intent.putExtra("namaPegawai", pelanggan.namaPegawai)
            intent.putExtra("noHPPegawai", pelanggan.noHPPegawai)
            intent.putExtra("alamatPegawai", pelanggan.alamatPegawai)
            intent.putExtra("idCabang", pelanggan.idCabangPegawai)
            startActivity(intent)
        }
    }



    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardPegawai = itemView.findViewById<View>(R.id.cvPegawai)
        val tvCARD_PEGAWAI_ID = itemView.findViewById<TextView>(R.id.tv_id_pegawai)
        val tvCARD_PEGAWAI_NAMA = itemView.findViewById<TextView>(R.id.tv_nama_pegawai)
        val tvCARD_PEGAWAI_ALAMAT = itemView.findViewById<TextView>(R.id.tv_alamat_pegawai)
        val tvCARD_PEGAWAI_NOHP = itemView.findViewById<TextView>(R.id.tv_no_hp_pegawai)
        val tvCARD_PEGAWAI_CABANG = itemView.findViewById<TextView>(R.id.tv_cabang_pegawai)
        val lihat = itemView.findViewById<Button>(R.id.btn_lihat_pegawai)
        val hubungi = itemView.findViewById<Button>(R.id.btn_hubungi_pegawai)
    }
}