package com.reza

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.reza.laundry.R
import com.reza.laundry.pegawai.DataPegawaiActivity
import com.reza.laundry.pelanggan.DataPelangganActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    lateinit var tvDate: TextView
    lateinit var tvGreeting : TextView
    lateinit var cvPelanggan : CardView
    lateinit var cvPegawai : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        init()
        pencet()
        tvDate.text = getCurrentDate()
        tvGreeting.text = getGreetingMessage()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
    }

    fun init(){
        tvDate=findViewById(R.id.date_text)
        tvGreeting=findViewById(R.id.greeting_text)
        cvPelanggan=findViewById(R.id.cvPelanggan)
        cvPegawai=findViewById(R.id.cvPegawai)
    }

    fun pencet(){
        cvPelanggan.setOnClickListener{
            val intent = Intent(this@MainActivity, DataPelangganActivity::class.java)
            startActivity(intent)
        }
        cvPegawai.setOnClickListener {
            val intent = Intent(this@MainActivity, DataPegawaiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.hour in 5..10 -> "Selamat Pagi"
            currentTime.hour in 11..14 -> "Selamat Siang"
            currentTime.hour in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDate.format(formatter)
    }
    
    fun main(){
        val tvGreeting = findViewById<TextView>(R.id.greeting_text)
        tvGreeting.text = getGreetingMessage()
        
        val tvDate = findViewById<TextView>(R.id.date_text)
        tvDate.text = getCurrentDate()
        
    }
}