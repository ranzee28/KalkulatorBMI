package com.example.kalkulatorbmi

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private data class BmiCategoryInfo(
        val kategori: String,
        val colorRes: Int,
        val bgRes: Int,
        val saran: String,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inisialisasi komponen UI
        val etBerat = findViewById<EditText>(R.id.etBerat)
        val etTinggi = findViewById<EditText>(R.id.etTinggi)
        val btnHitung = findViewById<Button>(R.id.btnHitung)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val tvHasil = findViewById<TextView>(R.id.tvHasil)
        val tvBmiAngka = findViewById<TextView>(R.id.tvBmiAngka)
        val tvKategoriBadge = findViewById<TextView>(R.id.tvKategoriBadge)
        val tvKeterangan = findViewById<TextView>(R.id.tvKeterangan)

        // 2. Event click tombol Hitung
        btnHitung.setOnClickListener {
            // Sembunyikan keyboard saat tombol ditekan
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)

            val strBerat = etBerat.text.toString().trim()
            val strTinggi = etTinggi.text.toString().trim()

            // Validasi input kosong (syarat tugas)
            if (strBerat.isEmpty() || strTinggi.isEmpty()) {
                Toast.makeText(this, "Harap isi berat dan tinggi badan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val berat = strBerat.toDoubleOrNull()
            val tinggiCm = strTinggi.toDoubleOrNull()

            if ((berat == null) || (tinggiCm == null) || (tinggiCm <= 0.0) || (berat <= 0.0)) {
                Toast.makeText(this, "Input tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hitung BMI: kg / (m^2)
            val tinggiMeter = tinggiCm / 100
            val bmi = berat / (tinggiMeter * tinggiMeter)

            // Kategori BMI, Warna Badge, dan Saran
            val (kategori, colorRes, bgRes, saran) = when {
                bmi < 18.5 -> BmiCategoryInfo(
                    kategori = getString(R.string.cat_underweight),
                    colorRes = R.color.bmi_underweight,
                    bgRes = R.color.bmi_underweight_bg,
                    saran = "Berat badan Anda di bawah batas ideal. Dianjurkan memperbanyak konsumsi makanan bergizi seimbang dan cukup protein.",
                )
                bmi in (18.5..24.9) -> BmiCategoryInfo(
                    kategori = getString(R.string.cat_normal),
                    colorRes = R.color.bmi_normal,
                    bgRes = R.color.bmi_normal_bg,
                    saran = "Luar biasa! Berat badan Anda berada dalam kategori ideal. Pertahankan pola makan teratur dan rutin berolahraga.",
                )
                bmi in (25.0..29.9) -> BmiCategoryInfo(
                    kategori = getString(R.string.cat_overweight),
                    colorRes = R.color.bmi_overweight,
                    bgRes = R.color.bmi_overweight_bg,
                    saran = "Berat badan Anda melebihi rentang ideal. Disarankan mengatur pola makan sehat dan rutin berolahraga kardio.",
                )
                else -> BmiCategoryInfo(
                    kategori = getString(R.string.cat_obese),
                    colorRes = R.color.bmi_obese,
                    bgRes = R.color.bmi_obese_bg,
                    saran = "Anda berada dalam kategori obesitas. Sebaiknya perhatikan asupan kalori harian dan konsultasi dengan ahli kesehatan.",
                )
            }

            // Tampilkan ke komponen UI modern
            tvBmiAngka.text = String.format(Locale.getDefault(), "%.1f", bmi)
            tvKategoriBadge.text = kategori
            tvKategoriBadge.setTextColor(ContextCompat.getColor(this, colorRes))
            tvKategoriBadge.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, bgRes))
            tvKeterangan.text = saran

            // Tampilkan hasil ke tvHasil original untuk kompatibilitas syarat tugas
            val hasilText = String.format(Locale.getDefault(), "BMI Anda: %.1f\nKategori: %s", bmi, kategori)
            tvHasil.text = hasilText
        }

        // 3. Event click tombol Reset
        btnReset.setOnClickListener {
            etBerat.text.clear()
            etTinggi.text.clear()
            etBerat.clearFocus()
            etTinggi.clearFocus()

            tvBmiAngka.text = getString(R.string.result_initial_score)
            tvKategoriBadge.text = getString(R.string.result_initial_badge)
            tvKategoriBadge.setTextColor(ContextCompat.getColor(this, R.color.primary))
            tvKategoriBadge.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_light))
            tvKeterangan.text = getString(R.string.result_initial_desc)
            tvHasil.text = getString(R.string.result_default_placeholder)

            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}