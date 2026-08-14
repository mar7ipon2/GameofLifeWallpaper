package com.example.gameoflifewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.toColorInt

object ConwayRenderer {

    // Helper data class to return both colors cleanly
    data class PaletteColors(val aliveColor: Int, val deadColor: Int)

    fun getColorsFromPalette(paletteIndex: Int): PaletteColors {
        return when (paletteIndex) {
            0 -> PaletteColors("#39FF14".toColorInt(), "#121212".toColorInt()) // Neon
            1 -> PaletteColors("#8A2BE2".toColorInt(), "#F5F5DC".toColorInt()) // Purple on Light
            2 -> PaletteColors("#00FFFF".toColorInt(), "#0A192F".toColorInt()) // Cyan
            3 -> PaletteColors("#FF7A6B".toColorInt(), "#211F2B".toColorInt()) // Coral
            4 -> PaletteColors("#FFC83D".toColorInt(), "#F3EEE3".toColorInt()) // Gold
            5 -> PaletteColors("#61D9A8".toColorInt(), "#063F2E".toColorInt()) // Mint
            6 -> PaletteColors("#69E36B".toColorInt(), "#3D2161".toColorInt()) // Vivid Green on Dark Lilac
            7 -> PaletteColors("#FFD600".toColorInt(), "#006D6D".toColorInt()) // Yellow on teal
            8 -> PaletteColors("#7E1E3F".toColorInt(), "#0D1B3D".toColorInt()) // Blaugrana
            else -> PaletteColors(Color.GREEN, Color.BLACK) // Default fallback
        }
    }

    fun drawGrid(
        canvas: Canvas,
        grid: ByteArray,
        gridWidth: Int,
        gridHeight: Int,
        deadColor: Int,
        paint: Paint
    ) {
        // Clear the background with the dead cell color
        canvas.drawColor(deadColor)

        // Calculate the cell size based exclusively on the canvas width
        val cellSize = canvas.width.toFloat() / gridWidth

        // Iterate through the 1D array mapped as a 2D grid
        for (y in 0 until gridHeight) {
            for (x in 0 until gridWidth) {
                val index = y * gridWidth + x

                // Draw a rectangle only if the cell is alive (1)
                if (grid[index].toInt() == 1) {
                    val left = x * cellSize
                    val top = y * cellSize
                    val right = left + cellSize
                    val bottom = top + cellSize

                    canvas.drawRect(left, top, right, bottom, paint)
                }
            }
        }
    }
}