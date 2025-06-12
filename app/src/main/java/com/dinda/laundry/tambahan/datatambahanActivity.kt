package com.dinda.laundry.tambahan

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
import com.dinda.laundry.R
import com.dinda.laundry.adapter.adapterdatatambahan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reza.laundry.modeldata.modeltransaksitambahan

class datatambahanActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DataTambahan"
    }

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("tambahan")

    private lateinit var rvDataTambahan: RecyclerView
    private lateinit var fabTambahTambahan: FloatingActionButton
    private lateinit var tambahanList: ArrayList<modeltransaksitambahan>
    private lateinit var adapter: adapterdatatambahan
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datatambahan)

        tambahanList = ArrayList()
        init()
        setupAdapter()
        getDATA()
        setupClickListeners()
        setupWindowInsets()
    }

    private fun init() {
        rvDataTambahan = findViewById(R.id.rvDATA_TAMBAHAN)
        fabTambahTambahan = findViewById(R.id.fabDATA_TAMBAH_Tambah)
        rvDataTambahan.layoutManager = LinearLayoutManager(this)
        rvDataTambahan.setHasFixedSize(true)
    }

    private fun setupAdapter() {
        adapter = adapterdatatambahan(tambahanList)
        rvDataTambahan.adapter = adapter
    }

    private fun setupClickListeners() {
        fabTambahTambahan.setOnClickListener {
            val intent = Intent(this, tambahtambahanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getDATA() {
        removeExistingListener()

        // Urutkan berdasarkan timestamp terbaru
        val query = myRef.orderByChild("timestamp").limitToLast(100)

        valueEventListener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    tambahanList.clear()

                    for (dataSnapshot in snapshot.children) {
                        val tambahan = dataSnapshot.getValue(modeltransaksitambahan::class.java)
                        tambahan?.let {
                            tambahanList.add(it)
                            Log.d(TAG, "Added tambahan: ${it.namaLayanan} - ${it.tanggalTerdaftar}")
                        }
                    }

                    // Balik urutan supaya data terbaru muncul di atas
                    tambahanList.reverse()
                    adapter.notifyDataSetChanged()

                    if (tambahanList.isEmpty()) {
                        Toast.makeText(this@datatambahanActivity, "Additional data is empty", Toast.LENGTH_SHORT).show()
                    }

                    Log.d(TAG, "Loaded ${tambahanList.size} tambahan items")

                } catch (e: Exception) {
                    Log.e(TAG, "Error processing data: ${e.message}", e)
                    Toast.makeText(this@datatambahanActivity, "Error processing data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@datatambahanActivity, "Failed load data: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Firebase Error: ${error.toException()}")
            }
        })
    }

    private fun removeExistingListener() {
        valueEventListener?.let {
            myRef.removeEventListener(it)
            Log.d(TAG, "Removed existing event listener")
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Data_Tambahan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity resumed, reloading data")
        getDATA()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeExistingListener()
        Log.d(TAG, "Activity destroyed, listeners removed")
    }
}