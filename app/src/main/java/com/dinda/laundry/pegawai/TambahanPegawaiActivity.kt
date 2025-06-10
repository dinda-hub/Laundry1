package com.dinda.laundry.pegawai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.dinda.laundry.R
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

    var idPegawai: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambahan_pegawai)

        init()
        getData()

        btSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    fun getData(){
        idPegawai = intent.getStringExtra("idPegawai").toString()
        val judul = intent.getStringExtra("JudulPegawai")
        val nama = intent.getStringExtra("namaPegawai")
        val alamat = intent.getStringExtra("alamatPegawai")
        val noHP = intent.getStringExtra("noHPPegawai")
        val cabang = intent.getStringExtra("idCabangPegawai")

        // Set judul terlebih dahulu
        tvJudul.text = judul ?: this.getString(R.string.tambahpegawai)

        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNoHP.setText(noHP)
        etCabang.setText(cabang)

        // Perbaiki logika kondisi
        if (judul != null && judul.equals(this.getString(R.string.edit_pegawai))) {
            mati()
            btSimpan.text = "Sunting"
        } else {
            hidup()
            etNama.requestFocus()
            btSimpan.text = "Simpan"
        }
    }
    fun mati(){
        etNama.isEnabled=false
        etAlamat.isEnabled=false
        etNoHP.isEnabled=false
        etCabang.isEnabled=false
    }

    fun hidup(){
        etNama.isEnabled=true
        etAlamat.isEnabled=true
        etNoHP.isEnabled=true
        etCabang.isEnabled=true
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
            Toast.makeText(this, this.getString(R.string.validasi_no_hp),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang)
            Toast.makeText(this, this.getString(R.string.validasi_cabang),Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        if (btSimpan.text.equals("Simpan")){
            simpan()
        }else if (btSimpan.text.equals("Sunting")){
            hidup()
            etNama.requestFocus()
            btSimpan.text="Perbarui"
        }else if (btSimpan.text.equals("Perbarui")){
            update()
        }
    }

    fun update(){
        val pegawaiRef = database.getReference("pegawai").child(idPegawai)
        val data = modelpegawai(
            idPegawai,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString(),
            )
        val UpdateData = mutableMapOf<String, Any>()
        UpdateData["namaPegawai"] = data.namaPegawai.toString()
        UpdateData["alamatPegawai"] = data.alamatPegawai.toString()
        UpdateData["noHPPegawai"] = data.noHPPegawai.toString()
        UpdateData["idCabang"] = data.cabangPegawai.toString()
        pegawaiRef.updateChildren(UpdateData).addOnSuccessListener {
            Toast.makeText(this@TambahanPegawaiActivity,"Data Pegawai Diperbarui", Toast.LENGTH_SHORT)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@TambahanPegawaiActivity, "Data Pegawai Gagal Diperbarui", Toast.LENGTH_SHORT)
        }
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
