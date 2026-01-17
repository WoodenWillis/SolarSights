package com.example.solarsights

import android.opengl.Matrix
import kotlin.math.*

object SolarMath {
    fun getPositionMatrix(planet: Planet, timeSeconds: Double): FloatArray {
        // Calculate orbital angle based on mean anomaly and variation
        val days = timeSeconds / 86400.0 // Convert to simulation days
        val angle = Math.toRadians(planet.mean_anomaly.value + (planet.mean_anomaly.variation * days))

        // Simple circular orbit calculation for now (Recreating the minimalist Pixel style)
        val orbitRadius = (planet.perihelion + planet.aphelion) / 2.0f
        val x = (cos(angle) * orbitRadius).toFloat()
        val y = (sin(angle) * orbitRadius).toFloat()

        val matrix = FloatArray(16)
        Matrix.setIdentityM(matrix, 0)
        Matrix.translateM(matrix, 0, x, y, 0f)

        // Use planet diameter for scale
        val scale = (planet.diameter / 12742.0).toFloat() * 20f // Normalized to Earth size
        Matrix.scaleM(matrix, 0, scale, scale, 1.0f)

        return matrix
    }
}