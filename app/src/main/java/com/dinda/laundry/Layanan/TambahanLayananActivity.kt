package com.dinda.laundry.Layanan

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dinda.laundry.R
import com.google.firebase.database.FirebaseDatabase
import com.reza.laundry.modeldata.modellayanan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TambahanLayananActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")

    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambahan_layanan)

        etNama = findViewById(R.id.etnamatambahlayanan)
        etHarga = findViewById(R.id.ettambahhargalayanan)
        etCabang = findViewById(R.id.etcabang_tambahanlayanan)
        btSimpan = findViewById(R.id.btsimpan_tambahanlayanan)

        btSimpan.setOnClickListener {
            validasi()
        }
    }

    private fun validasi() {
        val nama = etNama.text.toString().trim()
        val harga = etHarga.text.toString().trim()
        val cabang = etCabang.text.toString().trim()

        Log.d("ValidationDebug", "Nama: '$nama'")
        Log.d("ValidationDebug", "Harga: '$harga'")
        Log.d("ValidationDebug", "Cabang: '$cabang'")

        if (nama.isEmpty()) {
            etNama.error = "Nama layanan tidak boleh kosong"
            etNama.requestFocus()
            return
        }
        if (harga.isEmpty()) {
            etHarga.error = "Harga layanan tidak boleh kosong"
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = "Cabang layanan tidak boleh kosong"
            etCabang.requestFocus()
            return
        }

        simpan(nama, harga, cabang)
    }

    private fun simpan(nama: String, harga: String, cabang: String) {
        val layananBaru = myRef.push()
        val layananId = layananBaru.key ?: return

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val tanggalTerdaftar = sdf.format(Date())

        val data = modellayanan(
            idLayanan = layananId,
            namaLayanan = nama,
            hargaLayanan = harga,
            cabangLayanan = cabang, // PASTIKAN field name sama dengan model
            tanggalTerdaftar = tanggalTerdaftar
        )

        Log.d("SaveDebug", "Data yang akan disimpan:")
        Log.d("SaveDebug", "ID: ${data.idLayanan}")
        Log.d("SaveDebug", "Nama: ${data.namaLayanan}")
        Log.d("SaveDebug", "Harga: ${data.hargaLayanan}")
        Log.d("SaveDebug", "Cabang: '${data.cabangLayanan}'")
        Log.d("SaveDebug", "Tanggal: ${data.tanggalTerdaftar}")

        layananBaru.setValue(data)
            .addOnSuccessListener {
                Log.d("SaveDebug", "Data berhasil disimpan ke Firebase")
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e("SaveError", "Gagal menyimpan data: ${exception.message}")
                exception.printStackTrace()
                Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}