package com.dinda.laundry.cabang

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.Layanan.TambahanLayananActivity
import com.dinda.laundry.R
import com.dinda.laundry.adapter.adapterdatacabang
import com.dinda.laundry.adapter.adapterdatalayanan
import com.dinda.laundry.modeldata.modelcabang
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.reza.laundry.modeldata.modellayanan

class DataCabangActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DataCabang"
        private const val MAX_ITEMS = 100

        // Intent extra keys
        const val EXTRA_ID_CABANG = "idCabang"
        const val EXTRA_NAMA_LOKASI = "namaCabang"
        const val EXTRA_ALAMAT = "alamatCabang"
        const val EXTRA_TELEPON = "teleponCabang"
    }

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")

    private lateinit var rvDataCabang: RecyclerView
    private lateinit var fabTambahCabang: FloatingActionButton
    private lateinit var cabangList: ArrayList<modelcabang>
    private lateinit var adapter: adapterdatacabang
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        try {
            initializeViews()
            setupRecyclerView()
            setupClickListeners()
            setupWindowInsets()
            loadData()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeViews() {
        rvDataCabang = findViewById(R.id.rvDATA_CABANG)
        fabTambahCabang = findViewById(R.id.fabDATA_CABANG_Tambah)
    }

    private fun setupRecyclerView() {
        cabangList = ArrayList()
        adapter = adapterdatacabang(
            cabangList,
            onViewClick = { cabang ->
                showDetailDialog(cabang)
            },
            onItemClick = { cabang ->
                navigateToTambahCabang(cabang)
            }
        )
        rvDataCabang.layoutManager = LinearLayoutManager(this)
        rvDataCabang.setHasFixedSize(true)
        rvDataCabang.adapter = adapter
    }

    private fun setupClickListeners() {
        fabTambahCabang.setOnClickListener {
            navigateToTambahCabang()
        }
    }

    private fun navigateToTambahCabang(cabang: modelcabang? = null) {
        try {
            // Ganti dengan activity yang benar sesuai dengan yang Anda buat
            val intent = Intent(this, TambahanCabangActivity::class.java)
            cabang?.let {
                intent.putExtra(EXTRA_ID_CABANG, it.idCabang)
                intent.putExtra(EXTRA_NAMA_LOKASI, it.namaCabang)
                intent.putExtra(EXTRA_ALAMAT, it.alamatCabang)
                intent.putExtra(EXTRA_TELEPON, it.nohpCabang)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to TambahanCabangActivity: ${e.message}", e)
            Toast.makeText(this, "Additional ActivityBranchActivity has not been created or is not listed in the manifest", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDeleteConfirmationDialog(cabang: modelcabang) {
        try {
            AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete the branch? \"${cabang.namaCabang}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    hapusDataCabang(cabang)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing delete dialog: ${e.message}", e)
        }
    }

    private fun showDetailDialog(cabang: modelcabang) {
        try {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogmod_cabang, null)

            // Set data to dialog views
            dialogView.findViewById<TextView>(R.id.dialog_id_cabang).text = cabang.idCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.dialog_nama_cabang).text = cabang.namaCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.dialog_alamat_cabang).text = cabang.alamatCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.dialog_telepon_cabang).text = cabang.nohpCabang ?: "-"

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Setup button listeners
            dialogView.findViewById<MaterialButton>(R.id.btn_edit_cabang).setOnClickListener {
                dialog.dismiss()
                navigateToTambahCabang(cabang)
            }

            dialogView.findViewById<MaterialButton>(R.id.btn_delete_cabang).setOnClickListener {
                dialog.dismiss()
                showDeleteConfirmationDialog(cabang)
            }

            dialog.show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing detail dialog: ${e.message}", e)
            Toast.makeText(this, "Error displaying dialog: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hapusDataCabang(cabang: modelcabang) {
        if (cabang.idCabang.isNullOrEmpty()) {
            Toast.makeText(this, "Branch ID not valid", Toast.LENGTH_SHORT).show()
            return
        }

        myRef.child(cabang.idCabang!!).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Branch data successfully deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete data: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error deleting data: ${e.message}", e)
            }
    }

    private fun loadData() {
        try {
            removeExistingListener()

            val query = myRef.orderByChild("idCabang").limitToLast(MAX_ITEMS)

            valueEventListener = query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        cabangList.clear()
                        for (dataSnapshot in snapshot.children) {
                            val cabang = dataSnapshot.getValue(modelcabang::class.java)
                            if (cabang != null) {
                                cabangList.add(cabang)
                                Log.d(TAG, "Added branch: ${cabang.namaCabang}")
                            }
                        }
                        cabangList.reverse() // Supaya data terbaru muncul di atas
                        adapter.notifyDataSetChanged()

                        if (cabangList.isEmpty()) {
                            Toast.makeText(this@DataCabangActivity, "Empty branch data", Toast.LENGTH_SHORT).show()
                        }

                        Log.d(TAG, "Loaded ${cabangList.size} cabang items")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing data: ${e.message}", e)
                        Toast.makeText(this@DataCabangActivity, "Error processing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DataCabangActivity, "Failed to access data: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Firebase Error: ${error.toException()}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading data: ${e.message}", e)
            Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeExistingListener() {
        valueEventListener?.let {
            myRef.removeEventListener(it)
            Log.d(TAG, "Removed existing event listener")
        }
    }

    private fun setupWindowInsets() {
        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting window insets: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Activity resumed, reloading data")
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeExistingListener()
        Log.d(TAG, "Activity destroyed, listeners removed")
    }
}
