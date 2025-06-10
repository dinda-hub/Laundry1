package com.dinda.laundry.Layanan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.dinda.laundry.adapter.adapterdatalayanan
import com.dinda.laundry.pegawai.TambahanPegawaiActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reza.laundry.adapter.adapterdatapegawai
import com.reza.laundry.modeldata.modellayanan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataLayananActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")

    private lateinit var rvDataLayanan: RecyclerView
    private lateinit var fabTambahLayanan: FloatingActionButton
    private lateinit var layananList: ArrayList<modellayanan>
    private lateinit var adapter: adapterdatalayanan

    private fun init() {
        rvDataLayanan = findViewById(R.id.rvDATA_LAYANAN)
        fabTambahLayanan = findViewById(R.id.fabDATA_LAYANAN_Tambah)

        rvDataLayanan.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAdapter() {
        adapter = adapterdatalayanan(
            layananList,
            onEditClick = { layanan, position ->
                // Handle edit/sunting - Intent ke activity edit
                val intent = Intent(this, TambahanLayananActivity::class.java) // atau EditLayanan::class.java
                intent.putExtra("EDIT_MODE", true)
                intent.putExtra("LAYANAN_ID", layanan.idLayanan)
                intent.putExtra("NAMA_LAYANAN", layanan.namaLayanan)
                intent.putExtra("HARGA_LAYANAN", layanan.hargaLayanan)
                intent.putExtra("CABANG_LAYANAN", layanan.cabangLayanan)
                intent.putExtra("POSITION", position)
                startActivityForResult(intent, 100) // Request code 100 untuk edit
            },
            onDeleteClick = { layanan, position ->
                // Handle delete dari Firebase
                deleteLayananFromFirebase(layanan, position)
            },
            onViewClick = { layanan, position ->
                // Handle view - Tampilkan dialog_mod_layanan
                showViewLayananDialog(layanan)
            }
        )
        rvDataLayanan.adapter = adapter
    }

    private fun deleteLayananFromFirebase(layanan: modellayanan, position: Int) {
        layanan.idLayanan?.let { id ->
            myRef.child(id).removeValue()
                .addOnSuccessListener {
                    if (position >= 0) {
                        adapter.removeItem(position)
                    } else {
                        // Refresh data jika dipanggil dari dialog
                        getDATA()
                    }
                    Toast.makeText(this, "Layanan berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal menghapus layanan: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showViewLayananDialog(layanan: modellayanan) {
        val dialog = AppCompatDialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogmod_layanan, null)

        // Bind data ke dialog
        val tvDialogIdLayanan = dialogView.findViewById<TextView>(R.id.IdLayanan)
        val tvDialogNamaLayanan = dialogView.findViewById<TextView>(R.id.NamaLayanan)
        val tvDialogHargaLayanan = dialogView.findViewById<TextView>(R.id.HargaLayanan)
        val tvDialogCabangLayanan = dialogView.findViewById<TextView>(R.id.CabangLayanan)
        val tvDialogTanggalTerdaftar = dialogView.findViewById<TextView>(R.id.TanggalTerdaftarLayanan)
        val btnEditLayanan = dialogView.findViewById<Button>(R.id.btneditlayanan)
        val btnDeleteLayanan = dialogView.findViewById<Button>(R.id.btndeletelayanan)

        tvDialogIdLayanan?.text = layanan.idLayanan ?: "-"
        tvDialogNamaLayanan?.text = layanan.namaLayanan ?: "-"
        tvDialogHargaLayanan?.text = layanan.hargaLayanan ?: "-"
        tvDialogCabangLayanan?.text = layanan.cabangLayanan ?: "-"
        tvDialogTanggalTerdaftar?.text = layanan.tanggalTerdaftar ?: "-"

        // Handle tombol Sunting
        btnEditLayanan?.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, TambahanLayananActivity::class.java)
            intent.putExtra("EDIT_MODE", true)
            intent.putExtra("LAYANAN_ID", layanan.idLayanan)
            intent.putExtra("NAMA_LAYANAN", layanan.namaLayanan)
            intent.putExtra("HARGA_LAYANAN", layanan.hargaLayanan)
            intent.putExtra("CABANG_LAYANAN", layanan.cabangLayanan)
            startActivityForResult(intent, 100)
        }

        // Handle tombol Hapus
        btnDeleteLayanan?.setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmationDialog(layanan)
        }

        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showDeleteConfirmationDialog(layanan: modellayanan) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus layanan \"${layanan.namaLayanan}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteLayananFromFirebase(layanan, -1)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun getDATA() {
        val query = myRef.orderByChild("idLayanan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    layananList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(modellayanan::class.java)
                        layanan?.let { layananList.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLayananActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)

        layananList = ArrayList()
        init()
        setupAdapter()
        getDATA()

        fabTambahLayanan.setOnClickListener {
            val intent = Intent(this, TambahanLayananActivity::class.java)
            startActivityForResult(intent, 200) // Request code 200 untuk tambah
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Data_LAYANAN)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                100 -> {
                    // Edit result - refresh data
                    getDATA()
                    Toast.makeText(this, "Data layanan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                200 -> {
                    // Add result - refresh data
                    getDATA()
                    Toast.makeText(this, "Layanan baru berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}