package com.dinda.laundry.pilihan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.dinda.laundry.R
import com.google.firebase.database.DatabaseReference
import com.reza.laundry.adapter.adapterpilihlayanan
import com.reza.laundry.adapter.adapterpilihpelanggan
import com.reza.laundry.modeldata.modellayanan
import com.reza.laundry.modeldata.modelpelanggan

class PilihLayananActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var tvKosong: TextView
    private val listLayanan = mutableListOf<modellayanan>()
    private val filteredList = mutableListOf<modellayanan>()
    private lateinit var adapter: adapterpilihlayanan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_layanan)

        // Initialize views
        recyclerView = findViewById(R.id.rvPILIH_LAYANAN)
        searchView = findViewById(R.id.searchViewLayanan)
        tvKosong = findViewById(R.id.tvKosongLayanan)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup adapter with click listener
        adapter = adapterpilihlayanan(filteredList) { selectedLayanan ->
            val intent = Intent()
            intent.putExtra("idTambahan", selectedLayanan.idLayanan)
            intent.putExtra("namaLayanan", selectedLayanan.namaLayanan)
            intent.putExtra("hargaLayanan", selectedLayanan.hargaLayanan)
            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("layanan")

        // Setup search functionality
        setupSearchView()

        // Load data from Firebase
        loadLayananaData()
    }

    private fun loadLayananaData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listLayanan.clear()
                var counter = 1

                for (data in snapshot.children) {
                    val layanan = data.getValue(modellayanan::class.java)
                    if (layanan != null) {
                        // Set ID berurutan mulai dari 1
                        layanan.idLayanan = counter.toString()
                        listLayanan.add(layanan)
                        counter++
                    }
                }

                // Update filtered list and adapter
                filteredList.clear()
                filteredList.addAll(listLayanan)
                adapter.notifyDataSetChanged()

                // Update empty state
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihLayananActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText ?: "")
                return true
            }
        })
    }

    private fun filterList(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            // Jika search kosong, tampilkan semua data
            filteredList.addAll(listLayanan)
        } else {
            // Filter berdasarkan nama, alamat, atau nomor HP
            val searchQuery = query.lowercase()
            for (layanan in listLayanan) {
                if (layanan.namaLayanan?.lowercase()?.contains(searchQuery) == true ||
                    layanan.hargaLayanan?.contains(searchQuery) == true) {
                    filteredList.add(layanan)
                }
            }
        }

        adapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (filteredList.isEmpty()) {
            tvKosong.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            // Update pesan berdasarkan kondisi
            tvKosong.text = if (listLayanan.isEmpty()) {
                "Tidak ada data layanan"
            } else {
                "Tidak ada hasil pencarian"
            }
        } else {
            tvKosong.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}