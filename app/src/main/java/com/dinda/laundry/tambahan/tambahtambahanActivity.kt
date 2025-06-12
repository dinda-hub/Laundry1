package com.dinda.laundry.tambahan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dinda.laundry.R
import com.google.firebase.database.FirebaseDatabase
import com.reza.laundry.modeldata.modeltransaksitambahan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class tambahtambahanActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("tambahan")

    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambahtambahan)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tambah_tambahan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etNama = findViewById(R.id.etnamatambahan)
        etHarga = findViewById(R.id.ettambahanharga)
        btnSimpan = findViewById(R.id.btsimpan_tambahtambahan)

        btnSimpan.setOnClickListener {
            validasi()
        }
    }

    private fun validasi() {
        val nama = etNama.text.toString().trim()
        val hargaStr = etHarga.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = "Additional service name cannot be empty"
            etNama.requestFocus()
            return
        }
        if (hargaStr.isEmpty()) {
            etHarga.error = "Additional service price cannot be empty"
            etHarga.requestFocus()
            return
        }

        val harga = hargaStr.toIntOrNull()
        if (harga == null) {
            etHarga.error = "Prices must be numbers"
            etHarga.requestFocus()
            return
        }

        simpan(nama, harga)
    }

    private fun simpan(nama: String, harga: Int) {
        val tambahanBaru = myRef.push()
        val tambahanId = tambahanBaru.key ?: return

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val tanggalDitambahkan = sdf.format(Date())

        // PERBAIKAN: Sesuaikan dengan constructor model Anda
        val data = modeltransaksitambahan(
            id = tambahanId,
            idLayanan = tambahanId,
            namaLayanan = nama,
            hargaLayanan = harga.toString(),
            tanggalTerdaftar = tanggalDitambahkan
        )

        tambahanBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully saved additional services", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed saved additional services", Toast.LENGTH_SHORT).show()
            }
    }
}