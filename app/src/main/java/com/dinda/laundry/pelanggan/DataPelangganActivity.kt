package com.dinda.laundry.pelanggan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.dinda.laundry.R
import com.reza.laundry.adapter.adapterdatapelanggan
import com.reza.laundry.modeldata.modelpelanggan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataPelangganActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")

    private lateinit var rvDataPelanggan: RecyclerView
    private lateinit var fabTambahPelanggan: FloatingActionButton
    private lateinit var pelangganList: ArrayList<modelpelanggan>
    private lateinit var adapter: adapterdatapelanggan
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pelanggan)

        init()

        fabTambahPelanggan.setOnClickListener {
            val intent = Intent(this, TambahanPelangganActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Data_Pelanggan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        try {
            rvDataPelanggan = findViewById(R.id.rvDATA_PELANGGAN)
            fabTambahPelanggan = findViewById(R.id.fabDATA_PENGGUNA_Tambah)

            pelangganList = ArrayList()
            adapter = adapterdatapelanggan(
                pelangganList,
                onItemClick = { pelanggan ->
                    // Untuk edit pelanggan
                    val intent = Intent(this@DataPelangganActivity, TambahanPelangganActivity::class.java).apply {
                        putExtra("idPelanggan", pelanggan.idPelanggan)
                        putExtra("namaPelanggan", pelanggan.namaPelanggan)
                        putExtra("alamatPelanggan", pelanggan.alamatPelanggan)
                        putExtra("noHPPelanggan", pelanggan.noHPPelanggan)
                        putExtra("cabangPelanggan", pelanggan.idCabangPelanggan)
                        putExtra("tanggalTerdaftar", pelanggan.tanggalTerdaftar)
                    }
                    startActivity(intent)
                },
                onDeleteClick = { pelanggan ->
                    // Untuk delete pelanggan dengan konfirmasi
                    showDeleteConfirmation(pelanggan)
                }
            )

            rvDataPelanggan.layoutManager = LinearLayoutManager(this)
            rvDataPelanggan.adapter = adapter

        } catch (e: Exception) {
            Log.e("DataPelanggan", "Error in init: ${e.message}")
            Toast.makeText(this, "Error initializing components", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mendapatkan tanggal dan jam saat ini
    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Fungsi alternatif untuk format yang berbeda
    private fun getCurrentDateTimeFormatted(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        return dateFormat.format(calendar.time)
    }

    private fun getDATA() {
        try {
            // Remove existing listener to prevent memory leaks
            valueEventListener?.let { myRef.removeEventListener(it) }

            val query = myRef.orderByChild("idPelanggan").limitToLast(100)
            valueEventListener = query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pelangganList.clear()
                    for (dataSnapshot in snapshot.children) {
                        try {
                            val pelanggan = dataSnapshot.getValue(modelpelanggan::class.java)
                            pelanggan?.let {
                                // Update tanggal terdaftar dengan waktu saat ini jika kosong
                                if (it.tanggalTerdaftar.isNullOrEmpty()) {
                                    it.tanggalTerdaftar = getCurrentDateTime()
                                }
                                pelangganList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e("DataPelanggan", "Error parsing data: ${e.message}")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DataPelangganActivity, "Gagal ambil data: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DataPelanggan", "Firebase Error: ${error.toException()}")
                }
            })
        } catch (e: Exception) {
            Log.e("DataPelanggan", "Error in getDATA: ${e.message}")
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk menampilkan konfirmasi delete
    private fun showDeleteConfirmation(pelanggan: modelpelanggan) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Konfirmasi Hapus")
            builder.setMessage("Apakah Anda yakin ingin menghapus data pelanggan ${pelanggan.namaPelanggan ?: "ini"}?")

            builder.setPositiveButton("Ya") { _, _ ->
                deletePelanggan(pelanggan)
            }

            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            if (!isFinishing && !isDestroyed) {
                dialog.show()
            }
        } catch (e: Exception) {
            Log.e("DataPelanggan", "Error showing delete confirmation: ${e.message}")
        }
    }

    // Fungsi untuk menghapus pelanggan
    private fun deletePelanggan(pelanggan: modelpelanggan) {
        val idPelanggan = pelanggan.idPelanggan
        if (!idPelanggan.isNullOrEmpty()) {
            val pelangganRef = myRef.child(idPelanggan)
            pelangganRef.removeValue()
                .addOnSuccessListener {
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, "Data pelanggan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, "Gagal menghapus data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("DataPelanggan", "Delete Error: ${exception}")
                }
        } else {
            Toast.makeText(this, "ID Pelanggan tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk memperbarui tanggal terdaftar di Firebase
    private fun updateTanggalTerdaftarInFirebase(pelanggan: modelpelanggan) {
        val idPelanggan = pelanggan.idPelanggan
        if (!idPelanggan.isNullOrEmpty()) {
            val currentDateTime = getCurrentDateTime()
            val pelangganRef = myRef.child(idPelanggan)
            pelangganRef.child("tanggalTerdaftar").setValue(currentDateTime)
                .addOnSuccessListener {
                    Log.d("DataPelanggan", "Tanggal terdaftar berhasil diperbarui")
                }
                .addOnFailureListener { exception ->
                    Log.e("DataPelanggan", "Gagal memperbarui tanggal terdaftar: ${exception.message}")
                }
        }
    }

    override fun onResume() {
        super.onResume()
        getDATA() // Refresh data setiap balik ke activity ini
    }

    override fun onDestroy() {
        super.onDestroy()
        // Properly remove the listener to prevent memory leaks
        valueEventListener?.let {
            myRef.removeEventListener(it)
            valueEventListener = null
        }
    }
}
