package com.dinda.laundry

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dinda.laundry.modeldata.modelusers
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtNama: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var ivTogglePassword: ImageView
    private var isPasswordVisible = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        edtNama = findViewById(R.id.etusernameregister)
        edtPhone = findViewById(R.id.etnohpregister)
        edtPassword = findViewById(R.id.etkatasandiregister)
        btnRegister = findViewById(R.id.btdaftarregister)
        ivTogglePassword = findViewById(R.id.ivTogglePasswordRegister)

        setupPasswordToggle()

        btnRegister.setOnClickListener {
            val nama = edtNama.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (nama.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all columns!", Toast.LENGTH_SHORT).show()
            } else {
                val user = modelusers(password = password, username = nama)

                val db = FirebaseDatabase.getInstance().getReference("users")
                db.child(phone).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.Registerberhasil), Toast.LENGTH_SHORT).show()
                        finish() // kembali ke login
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setupPasswordToggle() {
        ivTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
                isPasswordVisible = false
            } else {
                // Show password
                edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                isPasswordVisible = true
            }
            // Move cursor to end of text
            edtPassword.setSelection(edtPassword.text.length)
        }
    }
}