package com.example.gameoflifewallpaper

import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.content.Intent
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder

class ConwayWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return ConwayEngine()
    }

    inner class ConwayEngine : Engine(), Runnable {
        private val nativeEngine = NativeEngine()
        private val paint = Paint().apply {
            //color = Color.GREEN // Color of the alive cells
            //style = Paint.Style.FILL
            style = Paint.Style.FILL
        }

        private var isRunning = false
        private var renderThread: Thread? = null

        // Game board dimensions (initialization)
        private var dynamicGridWidth = 48
        private var dynamicGridHeight = 128
        private val chunkSize = 8

        // Variables for the actual mobile screen size
        private var screenWidth = 0
        private var screenHeight = 0

        // Color and pattern (automaton) variables
        private var aliveColor: Int = Color.GREEN
        private var deadColor: Int = Color.BLACK
        private var patternIndex: Int = 0

        // Gesture detector to detect a double tap
        private lateinit var gestureDetector : GestureDetector

        private var isEngineInitialized = false

        override fun onTouchEvent(event: MotionEvent?) {
            super.onTouchEvent(event)
            // If there is a valid touch, pass it to the detector
            if (event != null) {
                gestureDetector.onTouchEvent(event)
            }
        }

        private var lastPatternIndex: Int = -1
        private fun loadSettings() {
            // Read preferences saved by the user
            val prefs = baseContext.getSharedPreferences("ConwayPrefs", android.content.Context.MODE_PRIVATE)

            dynamicGridWidth = prefs.getInt("GRID_WIDTH", 48)
            val newPatternIndex = prefs.getInt("INITIAL_PATTERN", 0)
            val paletteIndex = prefs.getInt("COLOR_PALETTE", 0)

            if (newPatternIndex != lastPatternIndex) {
                lastPatternIndex = newPatternIndex
                patternIndex = newPatternIndex
                isEngineInitialized = false // Allows recalculateGridAndInit to call nativeEngine.initGame
            }

            // Fetch translated colors from our abstraction layer
            val colors = ConwayRenderer.getColorsFromPalette(paletteIndex)
            aliveColor = colors.aliveColor
            deadColor = colors.deadColor

            // Update the paint tool
            paint.color = aliveColor
        }

        // Variable to track the last tap time in milliseconds
        private var lastTapTime: Long = 0

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            // Enable touch events when the engine is born
            setTouchEventsEnabled(true)

            // Initialize the detector with the stable context
            gestureDetector = GestureDetector(this@ConwayWallpaperService, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    // Mandatory to return true so the detector keeps processing the gesture
                    return true
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    Log.d("ConwayWallpaper", "Double tap detected, opening editor...")
                    // EMERGENCY STOP!
                    // Stop the Wallpaper thread BEFORE opening the Editor
                    // to avoid collisions in C++
                    stopRendering()

                    // Launch the editor activity
                    val intent = Intent(this@ConwayWallpaperService, EditorActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)

                    return true
                }
            })
        }

        override fun onCommand(
            action: String?,
            x: Int,
            y: Int,
            z: Int,
            extras: Bundle?,
            resultRequested: Boolean
        ): Bundle? {
            // Check if the system command is a simple tap
            if (action == android.app.WallpaperManager.COMMAND_TAP) {
                val currentTime = System.currentTimeMillis()

                // If the difference between current and previous tap is less than 300ms, it's a double tap
                if (currentTime - lastTapTime < 300) {
                    openEditor()
                    // Reset the counter to avoid an accidental "triple tap"
                    lastTapTime = 0
                } else {
                    lastTapTime = currentTime
                }
            }
            return super.onCommand(action, x, y, z, extras, resultRequested)
        }

        private fun openEditor() {
            // Launch the editor activity.
            // Since we are in a Service and not an Activity, we need the FLAG_ACTIVITY_NEW_TASK flag
            val intent = android.content.Intent(applicationContext, EditorActivity::class.java)
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            // Initialize the C++ engine with the desired dimensions
            //nativeEngine.initGame(dynamicGridWidth, dynamicGridHeight, chunkSize, patternIndex)
            loadSettings()
        }

        private fun recalculateGridAndInit() {
            // Avoid division by zero if the screen hasn't been created yet
            if (screenWidth == 0 || screenHeight == 0) return

            // Calculate the theoretical cell size
            val targetCellSize = screenWidth.toFloat() / dynamicGridWidth

            // Calculate how many cells fit vertically
            val rawGridHeight = (screenHeight.toFloat() / targetCellSize).toInt()

            // Adjust to the nearest chunkSize multiple
            val remainder = rawGridHeight % chunkSize
            val newHeight = if (remainder >= chunkSize / 2) {
                rawGridHeight + (chunkSize - remainder)
            } else {
                (rawGridHeight - remainder).coerceAtLeast(chunkSize)
            }

            // Only initialize if it's the first time or if dimensions have changed
            if (!isEngineInitialized || dynamicGridHeight != newHeight) {
                dynamicGridHeight = newHeight
                nativeEngine.initGame(dynamicGridWidth, dynamicGridHeight, chunkSize, patternIndex)
                isEngineInitialized = true
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            // Save the physical screen size
            screenWidth = width
            screenHeight = height

            // Calculate and initialize
            recalculateGridAndInit()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                loadSettings()
                recalculateGridAndInit()
                startRendering()
            } else {
                stopRendering()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
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
                // Wait for the secondary thread to completely finish its current cycle
                renderThread?.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            renderThread = null
        }

        // Render loop executed on the secondary thread
        override fun run() {
            while (isRunning) {

                drawFrame()
                if (!isPreview) {
                    nativeEngine.updateGame() // Calculates the next frame in C++
                }

                try {
                    Thread.sleep(200) // Controls the time between iterations
                } catch (_: InterruptedException) {
                    break
                }
            }
        }

        private fun drawFrame() {
            val holder = surfaceHolder
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
}