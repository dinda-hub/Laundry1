package com.reza.laundry.pegawai

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.reza.laundry.R
import com.reza.laundry.adapter.adapterdatapegawai
import com.reza.laundry.modeldata.modelpegawai
import com.reza.laundry.pegawai.TambahanPegawaiActivity

class DataPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var rvDATA_PEGAWAI: RecyclerView
    lateinit var fabDATA_PEGAWAI_Tambah: FloatingActionButton
    lateinit var pegawaiList: ArrayList<modelpegawai>

    //    @SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_PEGAWAI.layoutManager=layoutManager
        rvDATA_PEGAWAI.setHasFixedSize(true)

        pegawaiList = arrayListOf<modelpegawai>()
        getData()
        tekan()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.data_pegawai)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init(){
        rvDATA_PEGAWAI = findViewById(R.id.rvDATA_PEGAWAI)
        fabDATA_PEGAWAI_Tambah = findViewById(R.id.fabDATA_PEGAWAI_Tambah)
    }

    fun getData() {
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pegawaiList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(modelpegawai::class.java)
                        pegawaiList.add(pegawai!!)

                    }
                    val adapter = adapterdatapegawai(pegawaiList)
                    rvDATA_PEGAWAI.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPegawaiActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    fun tekan(){
        fabDATA_PEGAWAI_Tambah.setOnClickListener {
            val intent = Intent(this, TambahanPegawaiActivity::class.java)
            startActivity(intent)
        }
    }
}