package com.dinda.laundry.cabang

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dinda.laundry.R
import com.dinda.laundry.modeldata.modelcabang
import com.google.firebase.database.FirebaseDatabase
import com.reza.laundry.modeldata.modellayanan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TambahanCabangActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")

    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHP: EditText
    private lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambahan_cabang)

        etNama = findViewById(R.id.etnamatambahcabang)
        etAlamat = findViewById(R.id.etcabangtambah_alamat)
        etNoHP = findViewById(R.id.etcabangtambah_nohp)
        btSimpan = findViewById(R.id.btsimpantambah_cabang)

        btSimpan.setOnClickListener {
            validasi()
        }
    }

    private fun validasi() {
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val nohp = etNoHP.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = "Nama Cabang tidak boleh kosong"
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = "Alamat tidak boleh kosong"
            etAlamat.requestFocus()
            return
        }
        if (nohp.isEmpty()) {
            etNoHP.error = "No HP tidak boleh kosong"
            etNoHP.requestFocus()
            return
        }

        // Debug log sebelum menyimpan
        Log.d("SaveDebug", "Nama: $nama, Alamat: $alamat, No HP: $nohp")

        simpan(nama, alamat, nohp)
    }

    private fun simpan(nama: String, alamat: String, nohp: String) {
        val cabangBaru = myRef.push()
        val cabangID = cabangBaru.key ?: return

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val tanggalTerdaftar = sdf.format(Date())

        val data = modelcabang(
            idCabang = cabangID,
            namaCabang = nama,
            alamatCabang = alamat,
            nohpCabang = nohp, // Pastikan field name sama dengan model
            tanggalTerdaftar = tanggalTerdaftar
        )

        // Debug log data yang akan disimpan
        Log.d("SaveDebug", "Data yang disimpan: $data")

        cabangBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e("SaveError", "Error: ${exception.message}")
                Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}