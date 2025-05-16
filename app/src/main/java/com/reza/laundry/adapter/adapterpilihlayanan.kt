package com.reza.laundry.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.core.Context
import com.reza.laundry.R
import com.reza.laundry.modeldata.modellayanan
import com.reza.laundry.transaksi.DataTransaksiActivity

class adapterpilihlayanan (private val listLayanan: ArrayList<modellayanan>) :
    RecyclerView.Adapter<adapterpilihlayanan.ViewHolder>() {

    private val TAG = "AdapterPilihLayanan"
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihpelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder for position: $position")
        val nomor = position + 1
        val item = listLayanan[position]

        try {
            holder.tvID.text = "[$nomor]"
            holder.tvNama.text = item.namapilihlayanan ?: "Nama tidak tersedia"
            holder.tvHarga.text = "No HP : ${item.harga ?: "Tidak tersedia"}"

            Log.d(TAG, "Binding data: ${item.namaLayanan} at position $position")

            holder.cvCARD.setOnClickListener {
                try {
                    val intent = Intent(appContext, DataTransaksiActivity::class.java)
                    intent.putExtra("idPelanggan", item.idLayanan)
                    intent.putExtra("nama", item.namaLayanan)
                    intent.putExtra("harga", item.harga)
                    (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                    (appContext as Activity).finish()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in click listener: ${e.message}")
                    Toast.makeText(appContext, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error binding view holder: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        val count = listLayanan.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PILIHLAYANAN_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_PILIHLAYANAN_nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCARD_PILIHLAYANAN_nohp)
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIHLAYANAN)
    }
}