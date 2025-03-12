package com.reza.laundry.pegawai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.reza.laundry.R
import com.reza.laundry.modeldata.modelpegawai

class TambahanPegawaiActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHP: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambahan_pegawai)

        init()

        btSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    fun init() {
        tvJudul = findViewById(R.id.tvJudulPegawai)
        etNama = findViewById(R.id.etnamalengkap_tambahanpegawai)
        etAlamat = findViewById(R.id.etalamat_tambahanpegawai)
        etNoHP = findViewById(R.id.ethp_tambahanpegawai)
        etCabang = findViewById(R.id.etcabang_tambahanpegawai)
        btSimpan = findViewById(R.id.btsimpan_tambahanpegawai)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pegawai),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (noHp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_no_hp_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        simpan()
    }

    fun simpan() {
        val pegawaiBaru = myRef.push()
        val pegawaiid = pegawaiBaru.key
        val data = modelpegawai(
            pegawaiid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString()
        )
        pegawaiBaru.setValue(data).addOnSuccessListener {
            Toast.makeText(
                this,
                this.getString(R.string.tambah_pegawai_sukses),
                Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_pegawai_gagal),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
