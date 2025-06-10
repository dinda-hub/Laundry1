package com.dinda.laundry.pelanggan

import android.os.Bundle
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dinda.laundry.R
import com.google.firebase.database.FirebaseDatabase
import com.reza.laundry.modeldata.modelpelanggan

class TambahanPelangganActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")

    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHP: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambahan_pelanggan)
        init()
        btSimpan.setOnClickListener{
            cekValidasi()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tambah_pelanggan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        tvJudul = findViewById(R.id.tvtambahpelanggan)
        etNama = findViewById(R.id.etnamalengkap_tambahanpelanggan)
        etAlamat = findViewById(R.id.etalamat_tambahanpelanggan)
        etNoHP = findViewById(R.id.ethp_tambahanpelanggan)
        etCabang = findViewById(R.id.etcabang_tambahanpelanggan)
        btSimpan = findViewById(R.id.btsimpan_tambahanpelanggan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama)
            Toast.makeText(this, this.getString(R.string.validasi_nama),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat)
            Toast.makeText(this, this.getString(R.string.validasi_alamat),Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (noHp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_no_hp)
            Toast.makeText(this, this.getString(R.string.validasi_alamat),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang)
            Toast.makeText(this, this.getString(R.string.validasi_alamat),Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        simpan()
    }

    fun simpan() {
        val pelangganBaru = myRef.push()
        val pelangganid = pelangganBaru.key
        val data = modelpelanggan(
            pelangganid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString()
        )
        pelangganBaru.setValue(data).addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.sukses_menambah),
                    Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.gagal_menambah),
                    Toast.LENGTH_SHORT
                ).show()
            }
            }
}
