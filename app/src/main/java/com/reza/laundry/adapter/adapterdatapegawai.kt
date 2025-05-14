package com.reza.laundry.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reza.laundry.R
import com.reza.laundry.modeldata.modelpegawai
import com.reza.laundry.pegawai.TambahanPegawaiActivity

class adapterdatapegawai(private val listPegawai: ArrayList<modelpegawai>) :
    RecyclerView.Adapter<adapterdatapegawai.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pegawai = listPegawai[position]

        holder.tvCARD_PEGAWAI_ID.text = pegawai.idPegawai
        holder.tvCARD_PEGAWAI_NAMA.text = pegawai.namaPegawai
        holder.tvCARD_PEGAWAI_ALAMAT.text = pegawai.alamatPegawai
        holder.tvCARD_PEGAWAI_NOHP.text = pegawai.noHPPegawai
        holder.tvCARD_PEGAWAI_CABANG.text = pegawai.idCabangPegawai

        holder.cardPegawai.setOnClickListener {
            val context: Context = holder.itemView.context
            val intent = Intent(context, TambahanPegawaiActivity::class.java).apply {
                putExtra("Judul", "Edit Pegawai")
                putExtra("idPegawai", pegawai.idPegawai)
                putExtra("namaPegawai", pegawai.namaPegawai)
                putExtra("noHPPegawai", pegawai.noHPPegawai)
                putExtra("alamatPegawai", pegawai.alamatPegawai)
                putExtra("idCabang", pegawai.idCabangPegawai)
            }

            Log.d("AdapterDataPegawai", "Mengirim Judul: ${intent.getStringExtra("Judul")}")

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardPegawai: View = itemView.findViewById(R.id.cvPegawai)
        val tvCARD_PEGAWAI_ID: TextView = itemView.findViewById(R.id.tv_id_pegawai)
        val tvCARD_PEGAWAI_NAMA: TextView = itemView.findViewById(R.id.tv_nama_pegawai)
        val tvCARD_PEGAWAI_ALAMAT: TextView = itemView.findViewById(R.id.tv_alamat_pegawai)
        val tvCARD_PEGAWAI_NOHP: TextView = itemView.findViewById(R.id.tv_no_hp_pegawai)
        val tvCARD_PEGAWAI_CABANG: TextView = itemView.findViewById(R.id.tv_cabang_pegawai)
    }
}
