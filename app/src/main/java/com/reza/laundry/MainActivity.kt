package com.reza.laundry

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cek apakah perangkat mendukung fitur WindowInsets
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Menyesuaikan padding untuk area sistem (status bar, navigasi)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            val tvGreeting = findViewById<TextView>(R.id.greeting_text)
            tvGreeting.text = getGreetingMessage()

            val tvDate = findViewById<TextView>(R.id.date_text)
            tvDate.text = getCurrentDate()
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDate.format(formatter)
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
}