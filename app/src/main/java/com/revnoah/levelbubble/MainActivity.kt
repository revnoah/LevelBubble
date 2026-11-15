package com.revnoah.levelbubble

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : Activity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var levelView: LevelView
    private var activeSensor: Sensor? = null

    private val gravity = FloatArray(3)
    private var hasReading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        activeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        levelView = LevelView(this)
        setContentView(levelView)

        if (activeSensor == null) {
            levelView.showUnavailable()
        }
    }

    override fun onResume() {
        super.onResume()
        activeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.values.size < 3) return

        if (event.sensor.type == Sensor.TYPE_GRAVITY || !hasReading) {
            gravity[0] = event.values[0]
            gravity[1] = event.values[1]
            gravity[2] = event.values[2]
        } else {
            val alpha = 0.82f
            gravity[0] = alpha * gravity[0] + (1f - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1f - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1f - alpha) * event.values[2]
        }

        hasReading = true
        val magnitude = sqrt(
            gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]
        ).coerceAtLeast(0.001f)

        val normalizedX = gravity[0] / magnitude
        val normalizedY = gravity[1] / magnitude
        val normalizedZ = gravity[2] / magnitude
        val isFlat = abs(normalizedZ) >= 0.82f

        levelView.updateLevel(
            LevelReading(
                x = normalizedX,
                y = normalizedY,
                z = normalizedZ,
                mode = if (isFlat) LevelMode.Flat else LevelMode.Vertical
            )
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
