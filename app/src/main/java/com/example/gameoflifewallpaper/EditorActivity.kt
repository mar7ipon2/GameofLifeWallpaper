package com.example.gameoflifewallpaper

import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint

class EditorActivity : AppCompatActivity(), Runnable, SurfaceHolder.Callback {

    private lateinit var surfaceView: SurfaceView
    private var renderThread: Thread? = null
    private var isRunning = false
    private var isPlaying = true // To control Play/Pause button

    // Grid colors
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private var deadColor: Int = Color.BLACK

    private val nativeEngine = NativeEngine()

    // The dimensions
    private var screenWidth = 0
    private var screenHeight = 0
    private var dynamicGridWidth = 48
    private var dynamicGridHeight = 0 // Get from C++
    private val chunkSize = 8

    private var patternIndex: Int = 0

    private fun loadSettings() {
        val prefs = baseContext.getSharedPreferences("ConwayPrefs", MODE_PRIVATE)

        dynamicGridWidth = prefs.getInt("GRID_WIDTH", 48)
        patternIndex = prefs.getInt("INITIAL_PATTERN", 0)
        val paletteIndex = prefs.getInt("COLOR_PALETTE", 0)

        var aliveColor = Color.GREEN
        // Fetch translated colors from our abstraction layer
        val colors = ConwayRenderer.getColorsFromPalette(paletteIndex)
        aliveColor = colors.aliveColor
        deadColor = colors.deadColor

        // Update the paint tool
        paint.color = aliveColor
    }

    @SuppressLint("ClickableViewAccessibility") // Shut down the access warning
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        surfaceView = findViewById(R.id.editorSurfaceView)
        surfaceView.holder.addCallback(this)

        // Load grid settings
        loadSettings()
        // Set up buttons from corresponding layout
        setupButtons()

        // DIRECT TOUCH ASSIGNMENT ON THE SURFACE
        surfaceView.setOnTouchListener { view, event ->
            // If the thread is not running, ignore the touch to prevent errors
            if (!isRunning) return@setOnTouchListener false

            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    // Calculate the actual cell size based exclusively on the SurfaceView
                    val cellSize = view.width.toFloat() / dynamicGridWidth

                    // Transform the local coordinates of the view to 1D array indices
                    val gridX = (event.x / cellSize).toInt()
                    val gridY = (event.y / cellSize).toInt()

                    // Protect the native C++ environment by strictly checking boundaries
                    if (gridX in 0 until dynamicGridWidth && gridY in 0 until dynamicGridHeight) {
                        nativeEngine.setCell(gridX, gridY)
                    }
                    true // Indicate that the event was processed successfully
                }
                MotionEvent.ACTION_UP -> {
                    // Best practice: invoke performClick on touch up for accessibility
                    view.performClick()
                    true
                }
                else -> false
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setupButtons() {
        val btnPlayPause = findViewById<Button>(R.id.btnPlayPause)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnApply = findViewById<Button>(R.id.btnApply)

        btnPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            btnPlayPause.text = if (isPlaying) "Pause" else "Play"
        }

        btnReset.setOnClickListener {
            // Visually pause the simulation to avoid strange flickering
            isPlaying = false
            btnPlayPause.text = "Play"
            // Reset the C++ vector with the current configuration
            nativeEngine.initGame(dynamicGridWidth, dynamicGridHeight, chunkSize, patternIndex)
        }

        // On exit, close the activity and go back
        btnApply.setOnClickListener {
            finish()
        }
    }

    // SurfaceView lifecycle control (similar to WallpaperService)
    override fun surfaceCreated(holder: SurfaceHolder) {
        // Read the dimensions initialized by the service directly from the C++ engine
        dynamicGridWidth = nativeEngine.getWidth()
        dynamicGridHeight = nativeEngine.getHeight()

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        // KEY SYNCHRONIZATION: Read the actual dimensions initialized by the Wallpaper
        dynamicGridWidth = nativeEngine.getWidth()
        dynamicGridHeight = nativeEngine.getHeight()

        // If for some reason C++ is not initialized, use fallback values
        if (dynamicGridWidth <= 0 || dynamicGridHeight <= 0) {
            val prefs = baseContext.getSharedPreferences("ConwayPrefs", MODE_PRIVATE)
            dynamicGridWidth = prefs.getInt("GRID_WIDTH", 48)
            // Re-initialize to avoid a crash
            nativeEngine.initGame(dynamicGridWidth, 128, 8, 0)
            dynamicGridWidth = nativeEngine.getWidth()
            dynamicGridHeight = nativeEngine.getHeight()
        }

        startRendering()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopRendering()
    }

    private fun startRendering() {
        if (!isRunning) {
            isRunning = true
            renderThread = Thread(this)
            renderThread?.start()
        }
    }

    private fun stopRendering() {
        isRunning = false
        renderThread?.interrupt()
        try {
            renderThread?.join(500) // Wait for the thread to actually finish
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        renderThread = null
    }

    // 3. The loop identical to your service, conditioned by the Play/Pause button
    override fun run() {
        while (isRunning) {
            // Draw Canvas (you can reuse the drawFrame function you already had)
            drawFrame()

            if (isPlaying) {
                nativeEngine.updateGame()
            }

            try {
                Thread.sleep(200)
            } catch (_: InterruptedException) {
                break
            }
        }
    }

    private fun drawFrame() {
        val holder = surfaceView.holder
        var canvas: Canvas? = null
        try {
            canvas = holder.lockCanvas()
            if (canvas != null) {
                val grid = nativeEngine.getGrid()
                if (grid != null) {
                    // Delegate rendering to the extracted abstraction object
                    ConwayRenderer.drawGrid(
                        canvas = canvas,
                        grid = grid,
                        gridWidth = dynamicGridWidth,
                        gridHeight = dynamicGridHeight,
                        deadColor = deadColor,
                        paint = paint
                    )
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

}