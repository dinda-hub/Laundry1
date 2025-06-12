package com.dinda.laundry.pilihan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
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
import com.reza.laundry.adapter.adapterpilihpelanggan
import com.reza.laundry.modeldata.modelpelanggan

class PilihPelangganActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tvKosong: TextView
    private val listPelanggan = mutableListOf<modelpelanggan>()
    private val filteredList = mutableListOf<modelpelanggan>()
    private lateinit var adapter: adapterpilihpelanggan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_pelanggan)

        // Initialize views
        recyclerView = findViewById(R.id.rvPILIH_PELANGGAN)
        searchView = findViewById(R.id.searchViewPelanggan)
        tvKosong = findViewById(R.id.tvKosong)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup adapter with click listener
        adapter = adapterpilihpelanggan(filteredList) { selectedPelanggan ->
            val intent = Intent()
            intent.putExtra("idPelanggan", selectedPelanggan.idPelanggan)
            intent.putExtra("namaPelanggan", selectedPelanggan.namaPelanggan)
            intent.putExtra("noHPPelanggan", selectedPelanggan.noHPPelanggan)
            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("pelanggan")

        // Setup search functionality
        setupSearchView()

        // Load data from Firebase
        loadPelangganData()
    }

    private fun loadPelangganData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPelanggan.clear()
                var counter = 1

                for (data in snapshot.children) {
                    val pelanggan = data.getValue(modelpelanggan::class.java)
                    if (pelanggan != null) {
                        // Set ID berurutan mulai dari 1
                        pelanggan.idPelanggan = counter.toString()
                        listPelanggan.add(pelanggan)
                        counter++
                    }
                }

                // Update filtered list and adapter
                filteredList.clear()
                filteredList.addAll(listPelanggan)
                adapter.notifyDataSetChanged()

                // Update empty state
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihPelangganActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            filteredList.addAll(listPelanggan)
        } else {
            // Filter berdasarkan nama, alamat, atau nomor HP
            val searchQuery = query.lowercase()
            for (pelanggan in listPelanggan) {
                if (pelanggan.namaPelanggan?.lowercase()?.contains(searchQuery) == true ||
                    pelanggan.alamatPelanggan?.lowercase()?.contains(searchQuery) == true ||
                    pelanggan.noHPPelanggan?.contains(searchQuery) == true) {
                    filteredList.add(pelanggan)
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
            tvKosong.text = if (listPelanggan.isEmpty()) {
                "No Customer data"
            } else {
                "No search results"
            }
        } else {
            tvKosong.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}