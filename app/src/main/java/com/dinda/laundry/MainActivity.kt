package com.dinda.laundry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dinda.laundry.Layanan.DataLayananActivity
import com.dinda.laundry.R
import com.dinda.laundry.cabang.DataCabangActivity
import com.dinda.laundry.pegawai.DataPegawaiActivity
import com.dinda.laundry.pelanggan.DataPelangganActivity
import com.dinda.laundry.tambahan.datatambahanActivity
import com.dinda.laundry.transaksi.DataTransaksiActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var pelanggan1: LinearLayout
    lateinit var pegawai1: CardView
    lateinit var layanan: CardView
    lateinit var transaksi: LinearLayout
    lateinit var cabang : CardView
    lateinit var tambahan : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        init()
        tekan()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        pelanggan1 = findViewById(R.id.Pelanggan)
        pegawai1 = findViewById(R.id.Pegawai)
        layanan = findViewById(R.id.layanan)
        transaksi = findViewById(R.id.Transaksi)
        cabang = findViewById(R.id.cabang)
        tambahan = findViewById(R.id.Tambahan)

        // Debug: Pastikan semua view ditemukan
        Log.d("MainActivity", "Pelanggan: ${pelanggan1 != null}")
        Log.d("MainActivity", "Pegawai: ${pegawai1 != null}")
        Log.d("MainActivity", "Layanan: ${layanan != null}")
        Log.d("MainActivity", "Cabang: ${cabang != null}")
        Log.d("MainActivity", "Transaksi: ${transaksi != null}")
    }

    fun tekan() {
        pelanggan1.setOnClickListener {
            Log.d("MainActivity", "Pelanggan clicked")
            val intent = Intent(this, DataPelangganActivity::class.java)
            startActivity(intent)
        }

        pegawai1.setOnClickListener {
            Log.d("MainActivity", "Pegawai clicked")
            val intent = Intent(this, DataPegawaiActivity::class.java)
            startActivity(intent)
        }

        layanan.setOnClickListener {
            Log.d("MainActivity", "Layanan clicked")
            val intent = Intent(this, DataLayananActivity::class.java)
            startActivity(intent)
        }

        cabang.setOnClickListener {
            Log.d("MainActivity", "Cabang clicked")
            val intent = Intent(this, DataCabangActivity::class.java)
            startActivity(intent)
        }

        tambahan.setOnClickListener {
            Log.d("MainActivity", "Tambahan clicked")
            val intent = Intent(this, datatambahanActivity::class.java)
            startActivity(intent)
        }

        transaksi.setOnClickListener {
            Log.d("MainActivity", "Transaksi clicked")
            val intent = Intent(this, DataTransaksiActivity::class.java)
            startActivity(intent)
        }

        // Referensi TextView
        val helloTextView = findViewById<View>(R.id.greeting_text) as TextView
        val dateTextView = findViewById<View>(R.id.date_text) as TextView

        // Mengatur tanggal hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        dateTextView.text = currentDate

        // Mengatur pesan berdasarkan waktu
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour in 5..11 -> "Selamat Pagi, Dinda"
            hour in 12..14 -> "Selamat Siang, Dinda"
            hour in 15..17 -> "Selamat Sore, Dinda"
            else -> "Selamat Malam, Dinda"
        }
        helloTextView.text = greeting
    }
}