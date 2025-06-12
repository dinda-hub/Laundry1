package com.dinda.laundry.pegawai

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.dinda.laundry.R
import com.reza.laundry.adapter.adapterdatapegawai
import com.reza.laundry.modeldata.modelpegawai
import com.dinda.laundry.pegawai.TambahanPegawaiActivity
import com.dinda.laundry.pelanggan.TambahanPelangganActivity
import com.reza.laundry.adapter.adapterdatapelanggan
import com.reza.laundry.modeldata.modelpelanggan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataPegawaiActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var rvDataPegawai: RecyclerView
    private lateinit var fabTambahPegawai: FloatingActionButton
    private lateinit var pegawaiList: ArrayList<modelpegawai>
    private lateinit var adapter: adapterdatapegawai
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        init()

        fabTambahPegawai.setOnClickListener {
            val intent = Intent(this, TambahanPegawaiActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.data_pegawai)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        try {
            rvDataPegawai = findViewById(R.id.rvDATA_PEGAWAI)
            fabTambahPegawai = findViewById(R.id.fabDATA_PEGAWAI_Tambah)

            pegawaiList = ArrayList()

            // Perbaikan untuk onDeleteClick
            adapter = adapterdatapegawai(
                pegawaiList,
                onItemClick = { pegawai ->
                    // Untuk edit pegawai
                    val intent = Intent(this@DataPegawaiActivity, TambahanPegawaiActivity::class.java).apply {
                        putExtra("idPegawai", pegawai.idPegawai)
                        putExtra("namaPegawai", pegawai.namaPegawai)
                        putExtra("alamatPegawai", pegawai.alamatPegawai)
                        putExtra("noHPPegawai", pegawai.noHPPegawai)
                        putExtra("cabangPegawai", pegawai.cabangPegawai)
                        putExtra("tanggalTerdaftar", pegawai.tanggalTerdaftar)
                    }
                    startActivity(intent)
                },
                onDeleteClick = { pegawai ->
                    // Untuk delete pegawai dengan konfirmasi
                    showDeleteConfirmation(pegawai)
                }
            )

            rvDataPegawai.layoutManager = LinearLayoutManager(this)
            rvDataPegawai.adapter = adapter

        } catch (e: Exception) {
            Log.e("DataPegawai", "Error in init: ${e.message}")
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

            val query = myRef.orderByChild("idPegawai").limitToLast(100)
            valueEventListener = query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pegawaiList.clear()
                    for (dataSnapshot in snapshot.children) {
                        try {
                            val pegawai = dataSnapshot.getValue(modelpegawai::class.java)
                            pegawai?.let {
                                // Update tanggal terdaftar dengan waktu saat ini jika kosong
                                if (it.tanggalTerdaftar.isNullOrEmpty()) {
                                    it.tanggalTerdaftar = getCurrentDateTime()
                                }
                                pegawaiList.add(it)
                            }
                        } catch (e: Exception) {
                            Log.e("DataPegawai", "Error parsing data: ${e.message}")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DataPegawaiActivity, "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DataPegawai", "Firebase Error: ${error.toException()}")
                }
            })
        } catch (e: Exception) {
            Log.e("DataPegawai", "Error in getDATA: ${e.message}")
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk menampilkan konfirmasi delete
    private fun showDeleteConfirmation(pegawai: modelpegawai) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to delete employee data? ${pegawai.namaPegawai ?: "ini"}?")

            builder.setPositiveButton("Ya") { _, _ ->
                deletePegawai(pegawai)
            }

            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            if (!isFinishing && !isDestroyed) {
                dialog.show()
            }
        } catch (e: Exception) {
            Log.e("DataPegawai", "Error showing delete confirmation: ${e.message}")
        }
    }

    // Fungsi untuk menghapus pegawai
    private fun deletePegawai(pegawai: modelpegawai) {
        val idPegawai = pegawai.idPegawai
        if (!idPegawai.isNullOrEmpty()) {
            val pegawaiRef = myRef.child(idPegawai)
            pegawaiRef.removeValue()
                .addOnSuccessListener {
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, "Employee data has been successfully deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    if (!isFinishing && !isDestroyed) {
                        Toast.makeText(this, "Failed to delete data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("DataPegawai", "Delete Error: ${exception}")
                }
        } else {
            Toast.makeText(this, "Invalid Employee ID", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk memperbarui tanggal terdaftar di Firebase
    private fun updateTanggalTerdaftarInFirebase(pegawai: modelpegawai) {
        val idPegawai = pegawai.idPegawai
        if (!idPegawai.isNullOrEmpty()) {
            val currentDateTime = getCurrentDateTime()
            val pegawaiRef = myRef.child(idPegawai)
            pegawaiRef.child("dateupdated").setValue(currentDateTime)
                .addOnSuccessListener {
                    Log.d("DataPegawai", "Registered date updated successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("DataPegawai", "Failed to update registered date: ${exception.message}")
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