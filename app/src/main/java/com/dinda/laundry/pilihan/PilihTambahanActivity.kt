package com.dinda.laundry.pilihan

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
import com.dinda.laundry.R
import com.dinda.laundry.adapter.adapterpilihtambahan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reza.laundry.adapter.adapterpilihlayanan
import com.reza.laundry.adapter.adapterpilihpelanggan
import com.reza.laundry.modeldata.modellayanan
import com.reza.laundry.modeldata.modelpelanggan
import com.reza.laundry.modeldata.modeltransaksitambahan

class PilihTambahanActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var tvKosong: TextView
    private val listTambahan = mutableListOf<modeltransaksitambahan>()
    private val filteredList = mutableListOf<modeltransaksitambahan>()
    private lateinit var adapter: adapterpilihtambahan

    companion object {
        private const val TAG = "PilihTambahanActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_tambahan)

        try {
            // Initialize views dengan error handling
            initializeViews()
            setupRecyclerView()
            setupSearchView()
            initializeFirebase()
            loadTambahanData()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvPILIH_TAMBAHAN)
            ?: throw IllegalStateException("RecyclerView rvPILIH_TAMBAHAN not found")

        searchView = findViewById(R.id.searchViewTambahan)
            ?: throw IllegalStateException("SearchView searchViewTambahan not found")

        tvKosong = findViewById(R.id.tvKosongTambahan)
            ?: throw IllegalStateException("TextView tvKosongTambahan not found")
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup adapter with click listener
        adapter = adapterpilihtambahan(filteredList) { selectedTambahan ->
            Log.d(TAG, "Selected tambahan: ${selectedTambahan.namaLayanan}, ${selectedTambahan.hargaLayanan}")

            val intent = Intent()
            // Gunakan key yang konsisten
            intent.putExtra("idTambahan", selectedTambahan.idLayanan ?: selectedTambahan.id)
            intent.putExtra("namaTambahan", selectedTambahan.namaLayanan ?: "")
            intent.putExtra("hargaTambahan", selectedTambahan.hargaLayanan ?: "")

            Log.d(TAG, "Sending data: id=${selectedTambahan.idLayanan ?: selectedTambahan.id}, nama=${selectedTambahan.namaLayanan}, harga=${selectedTambahan.hargaLayanan}")

            setResult(RESULT_OK, intent)
            finish()
        }
        recyclerView.adapter = adapter
    }

    private fun initializeFirebase() {
        try {
            dbRef = FirebaseDatabase.getInstance().getReference("tambahan")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
            throw e
        }
    }

    private fun loadTambahanData() {
        try {
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        listTambahan.clear()
                        Log.d(TAG, "Loading data from Firebase. Snapshot exists: ${snapshot.exists()}")

                        for (data in snapshot.children) {
                            try {
                                val tambahan = data.getValue(modeltransaksitambahan::class.java)
                                Log.d(TAG, "Raw data from Firebase: ${data.key} -> ${data.value}")

                                if (tambahan != null) {
                                    // Create new object with proper IDs
                                    val finalTambahan = modeltransaksitambahan(
                                        id = if (tambahan.id.isNotEmpty()) tambahan.id else (data.key ?: ""),
                                        idLayanan = if (tambahan.idLayanan.isNullOrEmpty()) (data.key ?: "") else tambahan.idLayanan,
                                        namaLayanan = tambahan.namaLayanan,
                                        hargaLayanan = tambahan.hargaLayanan,
                                        tanggalTerdaftar = tambahan.tanggalTerdaftar
                                    )

                                    // Validate data before adding
                                    if (!finalTambahan.namaLayanan.isNullOrEmpty() &&
                                        !finalTambahan.hargaLayanan.isNullOrEmpty()) {

                                        listTambahan.add(finalTambahan)
                                        Log.d(TAG, "Added tambahan: ${finalTambahan.namaLayanan} - ${finalTambahan.hargaLayanan}")
                                    } else {
                                        Log.w(TAG, "Skipped invalid tambahan: nama=${finalTambahan.namaLayanan}, harga=${finalTambahan.hargaLayanan}")
                                    }
                                } else {
                                    Log.w(TAG, "Null tambahan from Firebase for key: ${data.key}")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing individual tambahan data for key: ${data.key}", e)
                            }
                        }

                        // Update UI on main thread
                        runOnUiThread {
                            updateFilteredList("")
                            Log.d(TAG, "Total tambahan loaded: ${listTambahan.size}")
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing Firebase data", e)
                        runOnUiThread {
                            Toast.makeText(this@PilihTambahanActivity, "Error memproses data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase error: ${error.message}")
                    runOnUiThread {
                        Toast.makeText(this@PilihTambahanActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
                        updateEmptyState()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Firebase listener", e)
            Toast.makeText(this, "Error connecting to database: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateFilteredList(newText ?: "")
                return true
            }
        })
    }

    private fun updateFilteredList(query: String) {
        try {
            filteredList.clear()

            if (query.isEmpty()) {
                // Jika search kosong, tampilkan semua data
                filteredList.addAll(listTambahan)
            } else {
                // Filter berdasarkan nama layanan atau harga
                val searchQuery = query.lowercase()
                for (tambahan in listTambahan) {
                    if (tambahan.namaLayanan?.lowercase()?.contains(searchQuery) == true ||
                        tambahan.hargaLayanan?.contains(searchQuery) == true) {
                        filteredList.add(tambahan)
                    }
                }
            }

            adapter.notifyDataSetChanged()
            updateEmptyState()

        } catch (e: Exception) {
            Log.e(TAG, "Error filtering list", e)
        }
    }

    private fun updateEmptyState() {
        try {
            if (filteredList.isEmpty()) {
                tvKosong.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE

                // Update pesan berdasarkan kondisi
                tvKosong.text = if (listTambahan.isEmpty()) {
                    "Tidak ada data tambahan"
                } else {
                    "Tidak ada hasil pencarian"
                }
            } else {
                tvKosong.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating empty state", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Clean up Firebase listener if needed
            // Biasanya ValueEventListener akan otomatis cleaned up
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
}