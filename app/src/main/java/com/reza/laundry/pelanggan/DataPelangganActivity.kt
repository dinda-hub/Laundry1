package com.reza.laundry.pelanggan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.reza.laundry.R
import com.reza.laundry.adapter.adapterdatapelanggan
import com.reza.laundry.modeldata.modelpelanggan

class DataPelangganActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rvDATA_PELANGGAN: RecyclerView
    lateinit var fabDATA_PENGGUNA_Tambah: FloatingActionButton
    lateinit var pelangganList: ArrayList<modelpelanggan>

//    @SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pelanggan)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_PELANGGAN.layoutManager=layoutManager
        rvDATA_PELANGGAN.setHasFixedSize(true)

        pelangganList = arrayListOf<modelpelanggan>()
        getData()
        tekan()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Data_Pelanggan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init(){
        rvDATA_PELANGGAN = findViewById(R.id.rvDATA_PELANGGAN)
        fabDATA_PENGGUNA_Tambah = findViewById(R.id.fabDATA_PENGGUNA_Tambah)
    }

    fun getData() {
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pelangganList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(modelpelanggan::class.java)
                        pelangganList.add(pegawai!!)

                    }
                    val adapter = adapterdatapelanggan(pelangganList)
                    rvDATA_PELANGGAN.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPelangganActivity, error.message,Toast.LENGTH_SHORT).show()
            }
        })


    }

    fun tekan(){
        fabDATA_PENGGUNA_Tambah.setOnClickListener {
            val intent = Intent(this,TambahanPelangganActivity::class.java)
            startActivity(intent)
        }
    }
}
