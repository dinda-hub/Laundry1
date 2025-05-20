package com.reza.laundry.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.reza.laundry.R
import com.reza.laundry.modeldata.modeltransaksitambahan
import com.reza.laundry.pilihan.PilihLayananActivity
import com.reza.laundry.pilihan.PilihPelangganActivity
import com.reza.laundry.pilihan.PilihTambahanActivity

class DataTransaksiActivity : AppCompatActivity() {
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var btnPilihPelanggan: Button
    private lateinit var btnPilihLayanan: Button
    private lateinit var btnProsesTransaksi: Button
    private lateinit var btnTambahan: Button

    private val dataList = mutableListOf<modeltransaksitambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan = ""
    private var idCabang = ""
    private var namaPelanggan = ""
    private var noHP = ""
    private var idLayanan = ""
    private var namaLayanan = ""
    private var hargaLayanan = ""
    private var idPegawai = ""

    private lateinit var sharedPref: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transaksi)

        FirebaseApp.initializeApp(this)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", "") ?: ""
        idPegawai = sharedPref.getString("idPegawai", "") ?: ""

        initViews()

        rvLayananTambahan.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
        }
        rvLayananTambahan.setHasFixedSize(true)

        btnPilihPelanggan.setOnClickListener {
            val intent = Intent(this, PilihPelangganActivity::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btnPilihLayanan.setOnClickListener {
            val intent = Intent(this, PilihLayananActivity::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        btnTambahan.setOnClickListener {
            val intent = Intent(this, PilihTambahanActivity::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) 
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
        tvNoHp = findViewById(R.id.tvpilihpelangganNoHP)
        tvNamaLayanan = findViewById(R.id.tvNamaLayanan)
        tvHargaLayanan = findViewById(R.id.tvpilihlayananHarga)
        rvLayananTambahan = findViewById(R.id.rvLayananTambahan)
        btnPilihPelanggan = findViewById(R.id.btnPilihPelanggan)
        btnPilihLayanan = findViewById(R.id.btnPilihLayanan)
        btnTambahan = findViewById(R.id.btnTambahan)
        btnProsesTransaksi = findViewById(R.id.btnProsesTransaksi)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                pilihPelanggan -> {
                    idPelanggan = data.getStringExtra("idPelanggan").orEmpty()
                    namaPelanggan = data.getStringExtra("nama").orEmpty()
                    noHP = data.getStringExtra("noHP").orEmpty()

                    tvNamaPelanggan.text = "Nama Pelanggan : $namaPelanggan"
                    tvNoHp.text = "No HP : $noHP"
                }

                pilihLayanan -> {
                    idLayanan = data.getStringExtra("idLayanan").orEmpty()
                    namaLayanan = data.getStringExtra("nama").orEmpty()
                    hargaLayanan = data.getStringExtra("harga").orEmpty()

                    tvNamaLayanan.text = "Nama Layanan : $namaLayanan"
                    tvHargaLayanan.text = "Harga : $hargaLayanan"
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            val msg = when (requestCode) {
                pilihPelanggan -> "Batal Memilih Pelanggan"
                pilihLayanan -> "Batal Memilih Layanan"
                pilihLayananTambahan -> "Batal Memilih Layanan Tambahan"
                else -> "Aksi Dibatalkan"
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}