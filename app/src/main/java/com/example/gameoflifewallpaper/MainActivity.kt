package com.example.gameoflifewallpaper

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerSize = findViewById<Spinner>(R.id.spinnerSize)
        val spinnerPalette = findViewById<Spinner>(R.id.spinnerPalette)
        val spinnerPattern = findViewById<Spinner>(R.id.spinnerPattern)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Close the menu and return to the preview
        }

        // Load previous preferences so Spinners show the last selection
        loadPreferences(spinnerSize, spinnerPalette, spinnerPattern)

        btnSave.setOnClickListener {
            // Get the selected positions (0, 1, or 2)
            val sizeIndex = spinnerSize.selectedItemPosition
            val paletteIndex = spinnerPalette.selectedItemPosition
            val patternIndex = spinnerPattern.selectedItemPosition

            // Translate indices to technical values
            val gridWidth = when (sizeIndex) {
                0 -> 8   // Size 1 (Large, fewer cells). Multiple of 8
                1 -> 16  // Size 2
                2 -> 24  // Size 3
                3 -> 48  // Size 4 (Small, more cells)
                else -> 48
            }

            // Save to device
            val prefs = getSharedPreferences("ConwayPrefs", Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putInt("GRID_WIDTH", gridWidth)
                putInt("COLOR_PALETTE", paletteIndex)
                putInt("INITIAL_PATTERN", patternIndex)
                apply() // apply() is asynchronous and more efficient than commit()
            }

            Toast.makeText(this, "Guardado. Vuelve a aplicar el fondo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPreferences(size: Spinner, palette: Spinner, pattern: Spinner) {
        val prefs = getSharedPreferences("ConwayPrefs", Context.MODE_PRIVATE)

        // Read saved width, default is 48
        val savedWidth = prefs.getInt("GRID_WIDTH", 48)

        // Reverse mapping of width to Spinner index
        val sizeIndex = when (savedWidth) {
            8 -> 0
            16 -> 1
            24 -> 2
            48 -> 3
            else -> 1
        }

        size.setSelection(sizeIndex)
        palette.setSelection(prefs.getInt("COLOR_PALETTE", 0))
        pattern.setSelection(prefs.getInt("INITIAL_PATTERN", 0))
    }
}