package com.dinda.laundry.transaksi

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinda.laundry.R
import com.dinda.laundry.adapter.adapterdatatambahan
import com.dinda.laundry.modeldata.modelinvoice
import com.reza.laundry.modeldata.modeltransaksitambahan
import java.io.IOException
import java.io.OutputStream
import java.io.Serializable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InvoiceActivity : AppCompatActivity() {

    private lateinit var tvTanggal: TextView
    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHP: TextView
    private var tvStatus: TextView? = null
    private lateinit var tvLayananUtama: TextView
    private lateinit var tvHargaLayanan: TextView
    private var rvTambahan: RecyclerView? = null
    private lateinit var tvSubtotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnCetak: Button
    private lateinit var btnKirimWhatsapp: Button

    private val listTambahan = ArrayList<modeltransaksitambahan>()
    private var adapter: InvoiceAdditionalServicesAdapter? = null

    // Property untuk menyimpan nomor HP pelanggan
    private var noHPPelanggan: String = ""

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val printerMAC = "DC:0D:51:A7:FF:7A"
    private val printerUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 100
        private const val TAG = "InvoiceTransaksi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        // Debug semua extra yang diterima
        debugIntentExtras()

        initializeViews()
        initializeBluetooth()
        requestBluetoothPermissions()
        setupRecyclerView()
        loadDataFromIntent()
        setupButtons()

        // Setup action bar untuk kembali ke DataTransaksi
        setupActionBar()

        // Setup modern back press handling
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        // Modern way untuk handle back press (Available since AndroidX Activity 1.0.0)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToDataTransaksi()
            }
        })
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Invoice Transaksi"
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateBackToDataTransaksi()
        return true
    }

    private fun navigateBackToDataTransaksi() {
        try {
            // Cari activity DataTransaksi di stack dan kembali ke sana
            val intent = Intent()
            intent.setClassName(this, "com.dinda.laundry.MainActivity")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating back to Home", e)
            // Fallback: gunakan finish() biasa
            finish()
        }
    }

    private fun debugIntentExtras() {
        val extras = intent.extras
        if (extras != null) {
            Log.d(TAG, "=== DEBUG INTENT EXTRAS ===")
            for (key in extras.keySet()) {
                val value = extras.get(key)
                Log.d(TAG, "Key: $key = Value: $value")
            }
            Log.d(TAG, "=== END DEBUG EXTRAS ===")
        } else {
            Log.d(TAG, "No extras found in intent")
        }
    }

    private fun initializeViews() {
        try {
            tvTanggal = findViewById(R.id.tv_tanggal_invoice)
            tvIdTransaksi = findViewById(R.id.tv_id_transaksi)
            tvNamaPelanggan = findViewById(R.id.tv_nama_pelanggan_invoice)
            tvNoHP = findViewById(R.id.tv_no_hp_invoice)
            tvLayananUtama = findViewById(R.id.tv_nama_layanan_invoice)
            tvHargaLayanan = findViewById(R.id.tv_harga_layanan_invoice)
            tvSubtotalTambahan = findViewById(R.id.tv_tambahan_invoice)
            btnCetak = findViewById(R.id.btn_cetak_invoice)
            btnKirimWhatsapp = findViewById(R.id.btn_kirim_wa)

            // Coba cari RecyclerView, jika tidak ada biarkan null
            rvTambahan = findViewById(R.id.rv_tambahan_konfirmasi)

            // Cari tvTotalBayar dengan ID yang ada di layout saat ini
            tvTotalBayar = findViewById(R.id.tv_total_bayar)
                ?: findViewById(R.id.tv_total_bayar)
                        ?: throw IllegalStateException("TextView for total payment not found")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            showToast("Error initializing views: ${e.message}")
            finish()
        }
    }

    private fun initializeBluetooth() {
        bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        } else {
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            val permissionsNeeded = permissions.filter {
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (permissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_BLUETOOTH_PERMISSIONS)
            }
        }
    }

    // ADAPTER KHUSUS UNTUK INVOICE TANPA BUTTON
    inner class InvoiceAdditionalServicesAdapter(
        private val additionalServices: List<modeltransaksitambahan>
    ) : RecyclerView.Adapter<InvoiceAdditionalServicesAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNumber: TextView = itemView.findViewById(R.id.tv_number)
            val tvServiceName: TextView = itemView.findViewById(R.id.tv_service_name)
            val tvServicePrice: TextView = itemView.findViewById(R.id.tv_service_price)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Gunakan layout sederhana tanpa button
            val view = createSimpleItemView(parent)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val service = additionalServices[position]
            holder.tvNumber.text = (position + 1).toString()
            holder.tvServiceName.text = service.namaLayanan ?: "Unknown"

            // Format harga dengan currency
            val price = service.hargaLayanan?.replace("[^\\d]".toRegex(), "")?.toIntOrNull() ?: 0
            holder.tvServicePrice.text = formatCurrency(price)
        }

        override fun getItemCount(): Int = additionalServices.size

        // Buat layout sederhana programmatically jika layout XML tidak tersedia
        private fun createSimpleItemView(parent: ViewGroup): View {
            return try {
                // Coba gunakan layout XML jika ada
                LayoutInflater.from(parent.context).inflate(R.layout.item_additional_service, parent, false)
            } catch (e: Exception) {
                // Jika layout XML tidak ada, buat secara programmatically
                createProgrammaticLayout(parent.context)
            }
        }

        private fun createProgrammaticLayout(context: Context): View {
            val linearLayout = android.widget.LinearLayout(context).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                setPadding(32, 24, 32, 24)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            // TextView untuk nomor
            val tvNumber = TextView(context).apply {
                id = R.id.tv_number
                layoutParams = android.widget.LinearLayout.LayoutParams(80, ViewGroup.LayoutParams.WRAP_CONTENT)
                textSize = 14f
                gravity = android.view.Gravity.CENTER
                setTextColor(android.graphics.Color.BLACK)
            }

            // TextView untuk nama layanan
            val tvServiceName = TextView(context).apply {
                id = R.id.tv_service_name
                layoutParams = android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(24, 0, 24, 0)
                }
                textSize = 14f
                setTextColor(android.graphics.Color.BLACK)
            }

            // TextView untuk harga
            val tvServicePrice = TextView(context).apply {
                id = R.id.tv_service_price
                layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(android.graphics.Color.BLACK)
            }

            linearLayout.addView(tvNumber)
            linearLayout.addView(tvServiceName)
            linearLayout.addView(tvServicePrice)

            return linearLayout
        }
    }

    private fun setupRecyclerView() {
        rvTambahan?.let { recyclerView ->
            // Gunakan adapter khusus untuk invoice tanpa button
            adapter = InvoiceAdditionalServicesAdapter(listTambahan)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            // Opsional: tambahkan divider
            val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            recyclerView.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun loadDataFromIntent() {
        try {
            // Coba ambil data sebagai object invoice dulu (untuk backward compatibility)
            val invoice = intent.getSerializableExtra("invoice") as? modelinvoice

            if (invoice != null) {
                Log.d(TAG, "Loading data from invoice object")
                loadFromInvoiceObject(invoice)
            } else {
                Log.d(TAG, "Loading data from individual extras")
                loadFromIndividualData()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error loading data from intent", e)
            showToast("Error loading data: ${e.message}")
            finish()
        }
    }

    private fun loadFromInvoiceObject(invoice: modelinvoice) {
        val tanggalTerdaftarStr = formatTimestamp(invoice.tanggalTerdaftar)

        tvTanggal.text = tanggalTerdaftarStr
        tvIdTransaksi.text = invoice.idTransaksi ?: "-"
        tvNamaPelanggan.text = invoice.namaPelanggan ?: "-"
        tvStatus?.text = invoice.status ?: "-"
        tvLayananUtama.text = invoice.namaLayanan ?: "-"

        // Ambil nomor HP dengan berbagai kemungkinan key
        noHPPelanggan = invoice.noHPPelanggan
            ?: intent.getStringExtra("noHP")
                    ?: intent.getStringExtra("noHPPelanggan")
                    ?: intent.getStringExtra("phoneNumber")
                    ?: intent.getStringExtra("phone")
                    ?: ""

        Log.d(TAG, "NoHP from invoice object: '$noHPPelanggan'")
        tvNoHP.text = "Phone Number: ${if (noHPPelanggan.isNotEmpty()) noHPPelanggan else "Not Available"}"

        @Suppress("UNCHECKED_CAST")
        val tambahan = intent.getSerializableExtra("layanan") as? ArrayList<modeltransaksitambahan> ?: arrayListOf()

        listTambahan.clear()
        listTambahan.addAll(tambahan)
        adapter?.notifyDataSetChanged()

        val subtotalTambahan = tambahan.sumOf { layanan ->
            layanan.hargaLayanan?.toIntOrNull() ?: 0
        }

        val totalBayar = invoice.totalBayar ?: 0
        val hargaLayananUtama = totalBayar - subtotalTambahan

        tvHargaLayanan.text = formatCurrency(hargaLayananUtama)
        tvSubtotalTambahan.text = formatCurrency(subtotalTambahan)
        tvTotalBayar.text = formatCurrency(totalBayar)
    }

    private fun loadFromIndividualData() {
        // Ambil data individual dari KonfirmasiTransaksi dengan berbagai kemungkinan key
        val nama = intent.getStringExtra("namaPelanggan")
            ?: intent.getStringExtra("nama")
            ?: intent.getStringExtra("customerName")
            ?: "-"

        // Coba berbagai kemungkinan key untuk nomor HP
        noHPPelanggan = intent.getStringExtra("noHP")
            ?: intent.getStringExtra("noHPPelanggan")
                    ?: intent.getStringExtra("phoneNumber")
                    ?: intent.getStringExtra("phone")
                    ?: intent.getStringExtra("nomorHP")
                    ?: intent.getStringExtra("hp")
                    ?: ""

        Log.d(TAG, "NoHP from individual data: '$noHPPelanggan'")

        val layanan = intent.getStringExtra("namaLayanan")
            ?: intent.getStringExtra("layanan")
            ?: "-"

        val harga = intent.getStringExtra("hargaLayanan")
            ?: intent.getStringExtra("harga")
            ?: "0"

        val totalHarga = intent.getIntExtra("totalHarga", 0)
        val metodePembayaran = intent.getStringExtra("metodePembayaran")
            ?: intent.getStringExtra("metode")
            ?: "-"

        val tambahan = intent.getSerializableExtra("listTambahan") as? ArrayList<modeltransaksitambahan>
            ?: intent.getSerializableExtra("tambahan") as? ArrayList<modeltransaksitambahan>
            ?: arrayListOf()

        // Generate ID transaksi dan tanggal saat ini
        val currentTime = System.currentTimeMillis()
        val idTransaksi = "TRX${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date(currentTime))}"
        val tanggalStr = formatTimestamp(currentTime)

        // Set data ke view
        tvTanggal.text = tanggalStr
        tvIdTransaksi.text = idTransaksi
        tvNamaPelanggan.text = nama

        // Tampilkan nomor HP dengan status yang jelas
        val displayHP = when {
            noHPPelanggan.isNotEmpty() && noHPPelanggan.isNotBlank() -> noHPPelanggan
            else -> "Not Available"
        }
        tvNoHP.text = "$displayHP"

        tvStatus?.text = "Waiting for Payment ($metodePembayaran)"
        tvLayananUtama.text = layanan

        // Update RecyclerView untuk tambahan
        listTambahan.clear()
        listTambahan.addAll(tambahan)
        adapter?.notifyDataSetChanged()

        // Hitung subtotal tambahan
        val subtotalTambahan = tambahan.sumOf { item ->
            val cleanHarga = item.hargaLayanan?.replace("[^\\d]".toRegex(), "") ?: "0"
            cleanHarga.toIntOrNull() ?: 0
        }

        // Hitung harga layanan utama
        val cleanHargaUtama = harga.replace("[^\\d]".toRegex(), "")
        val hargaLayananUtama = cleanHargaUtama.toIntOrNull() ?: 0

        // Set ke TextView
        tvHargaLayanan.text = formatCurrency(hargaLayananUtama)
        tvSubtotalTambahan.text = formatCurrency(subtotalTambahan)
        tvTotalBayar.text = formatCurrency(totalHarga)
    }

    private fun formatTimestamp(timestamp: Long?): String {
        return try {
            if (timestamp == null || timestamp == 0L) return "-"
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting timestamp", e)
            "-"
        }
    }

    private fun formatCurrency(value: Int): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            formatter.format(value)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting currency", e)
            "Rp $value"
        }
    }

    private fun setupButtons() {
        btnCetak.setOnClickListener {
            if (!hasBluetoothConnectPermission()) {
                showToast("Bluetooth permission is required for printing")
                return@setOnClickListener
            }

            val message = buildPrintMessage()
            printToBluetooth(message)
        }

        btnKirimWhatsapp.setOnClickListener {
            Log.d(TAG, "WhatsApp button clicked. noHPPelanggan = '$noHPPelanggan'")

            // Validasi nomor HP sebelum mengirim
            if (noHPPelanggan.isEmpty() || noHPPelanggan.isBlank() || noHPPelanggan == "Not Available") {
                showToast("Customer mobile number is not available or invalid")
                Log.w(TAG, "WhatsApp failed: No valid phone number. Value: '$noHPPelanggan'")
                return@setOnClickListener
            }

            val message = buildWhatsappMessage()
            sendWhatsappMessage(message)
        }
    }

    private fun buildPrintMessage(): String {
        return buildString {
            // Header
            append("================================\n")
            append("          LAUNDRY DINDA         \n")
            append("        Alamat Laundry         \n")
            append("================================\n")
            append("\n")

            // Informasi Transaksi
            append("ID Transaksi : ${tvIdTransaksi.text}\n")
            append("Tanggal      : ${tvTanggal.text}\n")
            append("Pelanggan    : ${tvNamaPelanggan.text}\n")

            // Tampilkan nomor HP di struk jika tersedia
            if (noHPPelanggan.isNotEmpty() && noHPPelanggan != "Tidak tersedia") {
                append("No. HP       : $noHPPelanggan\n")
            }

            tvStatus?.let {
                append("Status       : ${it.text}\n")
            }

            append("--------------------------------\n")

            // Layanan Utama
            val namaUtama = tvLayananUtama.text.toString()
            val hargaUtama = tvHargaLayanan.text.toString()

            append("LAYANAN UTAMA:\n")
            append(formatItemLine(namaUtama, hargaUtama))

            // Layanan Tambahan
            if (listTambahan.isNotEmpty()) {
                append("\nLAYANAN TAMBAHAN:\n")
                listTambahan.forEachIndexed { index, item ->
                    val namaItem = "${index + 1}. ${item.namaLayanan ?: ""}"
                    val hargaItem = "Rp ${item.hargaLayanan ?: "0"}"
                    append(formatItemLine(namaItem, hargaItem))
                }
                append("--------------------------------\n")
                append(formatItemLine("Subtotal Tambahan", tvSubtotalTambahan.text.toString()))
            }

            append("================================\n")
            append(formatTotalLine("TOTAL BAYAR", tvTotalBayar.text.toString()))
            append("================================\n")
            append("\n")
            append("     Terima kasih telah         \n")
            append("    memilih LAUNDRY DINDA       \n")
            append("\n")
            append("--------------------------------\n")
            append("        Simpan struk ini        \n")
            append("      sebagai bukti transaksi   \n")
            append("\n\n\n")
        }
    }

    private fun formatItemLine(nama: String, harga: String): String {
        val maxWidth = 32 // Total lebar struk
        val cleanHarga = harga.replace("Rp", "").trim()
        val formattedHarga = "Rp $cleanHarga"

        // Jika nama terlalu panjang, potong dan beri ...
        val maxNamaWidth = maxWidth - formattedHarga.length - 1
        val namaFormatted = if (nama.length > maxNamaWidth) {
            nama.take(maxNamaWidth - 3) + "..."
        } else {
            nama
        }

        // Hitung spasi yang dibutuhkan
        val spacesCount = maxWidth - namaFormatted.length - formattedHarga.length
        val spaces = " ".repeat(maxOf(1, spacesCount))

        return "$namaFormatted$spaces$formattedHarga\n"
    }

    // Fungsi helper untuk format total dengan emphasis
    private fun formatTotalLine(label: String, amount: String): String {
        val maxWidth = 32
        val cleanAmount = amount.replace("Rp", "").trim()
        val formattedAmount = "Rp $cleanAmount"
        val totalText = "$label: $formattedAmount"

        // Center align untuk total
        val padding = (maxWidth - totalText.length) / 2
        val leftPadding = " ".repeat(maxOf(0, padding))

        return "$leftPadding$totalText\n"
    }

    private fun buildWhatsappMessage(): String {
        return buildString {
            append("*Hai ${tvNamaPelanggan.text}* 👋\n\n")
            append("*Berikut rincian laundry Anda:*\n")
            append("• ID Transaksi: ${tvIdTransaksi.text}\n")
            append("• Tanggal: ${tvTanggal.text}\n")
            tvStatus?.let { append("• Status: ${it.text}\n") }
            append("\n")
            append("*Layanan Utama:*\n")
            append("• ${tvLayananUtama.text} - ${tvHargaLayanan.text}\n\n")

            if (listTambahan.isNotEmpty()) {
                append("*Layanan Tambahan:*\n")
                listTambahan.forEachIndexed { index, item ->
                    append("${index + 1}. ${item.namaLayanan ?: ""} - Rp ${item.hargaLayanan ?: "0"}\n")
                }
                append("\n")
            }

            append("*Total Bayar:* ${tvTotalBayar.text}\n\n")
            append("Terima kasih telah menggunakan Laundry Dinda 🤍")
        }
    }

    private fun sendWhatsappMessage(message: String) {
        try {
            // Format nomor HP
            val formattedNumber = formatPhoneNumber(noHPPelanggan)

            Log.d(TAG, "Original number: '$noHPPelanggan', Formatted: '$formattedNumber'")

            if (formattedNumber.isEmpty()) {
                showToast("Mobile number is invalid or cannot be formatted")
                return
            }

            // PERBAIKAN: Gunakan wa.me tanpa + di depan
            val whatsappUrl = "https://wa.me/$formattedNumber?text=${Uri.encode(message)}"
            Log.d(TAG, "WhatsApp URL: $whatsappUrl")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))

            // Langsung start tanpa cek resolveActivity dulu
            startActivity(intent)
            showToast("Membuka WhatsApp...")

        } catch (e: Exception) {
            Log.e(TAG, "Error sending WhatsApp message", e)
            // Jika gagal, coba dengan cara lain
            try {
                val formattedNumber = formatPhoneNumber(noHPPelanggan)
                val fallbackUrl = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                startActivity(fallbackIntent)
                showToast("Open WhatsApp...")
            } catch (e2: Exception) {
                showToast("Can't open WhatsApp")
            }
        }
    }

    // Fungsi untuk memformat nomor HP dengan logging
    private fun formatPhoneNumber(phoneNumber: String): String {
        Log.d(TAG, "formatPhoneNumber called with: '$phoneNumber'")

        if (phoneNumber.isEmpty() || phoneNumber.isBlank() || phoneNumber == "Not Available") {
            Log.w(TAG, "Phone number is empty, blank, or 'Tidak tersedia'")
            return ""
        }

        // Hapus semua karakter non-digit
        val cleanNumber = phoneNumber.replace("[^\\d]".toRegex(), "")
        Log.d(TAG, "Clean number: '$cleanNumber'")

        if (cleanNumber.length < 8) {
            Log.w(TAG, "Phone number too short: $cleanNumber")
            return ""
        }

        val result = when {
            // Jika sudah dimulai dengan 62, gunakan langsung
            cleanNumber.startsWith("62") -> cleanNumber
            // Jika dimulai dengan 08, ganti dengan 628
            cleanNumber.startsWith("08") -> "62${cleanNumber.substring(1)}"
            // Jika dimulai dengan 8, tambahkan 62
            cleanNumber.startsWith("8") -> "62$cleanNumber"
            // Jika dimulai dengan 0, ganti dengan 62
            cleanNumber.startsWith("0") -> "62${cleanNumber.substring(1)}"
            // Lainnya, tambahkan 62
            else -> "62$cleanNumber"
        }

        Log.d(TAG, "Final formatted number: '$result'")
        return result
    }

    private fun closeBluetoothConnection() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing bluetooth connection", e)
        } finally {
            outputStream = null
            bluetoothSocket = null
        }
    }

    private fun isBluetoothConnected(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket? {
        if (!hasBluetoothConnectPermission()) {
            showToastOnMainThread("Bluetooth permission is required")
            return null
        }

        return try {
            device.createRfcommSocketToServiceRecord(printerUUID)
        } catch (e: SecurityException) {
            showToastOnMainThread("Bluetooth permission is required")
            null
        } catch (e: Exception) {
            try {
                val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                m.invoke(device, 1) as BluetoothSocket
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to create bluetooth socket", e2)
                null
            }
        }
    }

    private fun printToBluetooth(text: String) {
        Thread {
            if (!hasBluetoothConnectPermission()) {
                showToastOnMainThread("Bluetooth permission is required for printing")
                return@Thread
            }

            var socket: BluetoothSocket? = null
            var stream: OutputStream? = null

            try {
                if (bluetoothAdapter == null) {
                    showToastOnMainThread("BBluetooth is not available")
                    return@Thread
                }

                if (!bluetoothAdapter!!.isEnabled) {
                    showToastOnMainThread("Bluetooth is not active")
                    return@Thread
                }

                val device: BluetoothDevice? = try {
                    bluetoothAdapter?.getRemoteDevice(printerMAC)
                } catch (e: SecurityException) {
                    showToastOnMainThread("Bluetooth permission is required to access the device")
                    return@Thread
                } catch (e: Exception) {
                    showToastOnMainThread("Printer not found: ${e.message}")
                    return@Thread
                }

                if (device == null) {
                    showToastOnMainThread("Printer not found")
                    return@Thread
                }

                socket = createBluetoothSocket(device)
                if (socket == null) {
                    showToastOnMainThread("Failed to create Bluetooth socket")
                    return@Thread
                }

                try {
                    bluetoothAdapter?.cancelDiscovery()
                } catch (e: SecurityException) {
                    showToastOnMainThread("Bluetooth permission is required")
                    return@Thread
                }

                showToastOnMainThread("Connecting to a printer...")
                socket.connect()

                stream = socket.outputStream
                stream.write(text.toByteArray())
                stream.flush()

                showToastOnMainThread("Print successful")

            } catch (e: SecurityException) {
                showToastOnMainThread("Bluetooth permission is required")
                Log.e(TAG, "Bluetooth security exception", e)
            } catch (e: Exception) {
                showToastOnMainThread("Print failed: ${e.message}")
                Log.e(TAG, "Bluetooth printing error", e)
            } finally {
                try {
                    stream?.close()
                    socket?.close()
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing bluetooth resources", e)
                }
            }
        }.start()
    }

    private fun showToastOnMainThread(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@InvoiceActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    showToast("IBluetooth permission granted")
                } else {
                    showToast("Bluetooth permission is required for printing")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeBluetoothConnection()
    }

    // Implementasi showToast yang belum ada
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}