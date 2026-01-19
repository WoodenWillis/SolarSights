// SolarSights/app/src/main/java/com/example/solarsights/ui/theme/JsonUtils.kt
package com.example.solarsights.ui.theme

import android.content.Context
import android.graphics.Color
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject

object JsonUtils {
    const val FLOATS_PER_PLANET = 16
    // Adjust this: Higher = Planets closer to center. Lower = Planets farther out.
    private const val SCALING_FACTOR = 300.0f
    private fun elem(planet: JSONObject, key: String, field: String, fallback: Double): Double {
        val obj = planet.optJSONObject(key) ?: return fallback
        return obj.optDouble(field, fallback)
    }

    private fun elemValue(planet: JSONObject, key: String, fallback: Double = Double.NaN): Double =
        elem(planet, key, "value", fallback)

    private fun elemVariation(planet: JSONObject, key: String, fallback: Double = Double.NaN): Double =
        elem(planet, key, "variation", fallback)

    fun loadPlanetsToFloatArray(context: Context): FloatArray {
        return try {
            val jsonString = context.assets.open("planets.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val result = FloatArray(jsonArray.length() * FLOATS_PER_PLANET)

            for (i in 0 until jsonArray.length()) {
                val planet = jsonArray.getJSONObject(i)
                val offset = i * FLOATS_PER_PLANET

// 1. Calculate finalSize
// Use the "mean_radius" from JSON, or "radius", or a fallback.
                val physicalRadius = planet.optDouble("mean_radius",
                    planet.optDouble("radius", 5000.0))
// Apply some scaling so planets aren't microscopic or filling the screen
                val finalSize = (physicalRadius / 20000.0).toFloat().coerceIn(0.05f, 0.5f)

// 2. Radius Math (Existing)
                val dist = if (planet.has("perihelion")) {
                    (planet.optDouble("perihelion") + planet.optDouble("aphelion")) / 2.0
                } else {
                    planet.optDouble("distance", 100.0)
                }
                val glRadius = (dist / SCALING_FACTOR).toFloat()


                // Speed Math
                // Speed + Phase from mean_anomaly (falls back to orbital_period/random)
                val meanAnomalyDeg = elemValue(planet, "mean_anomaly", Double.NaN)
                val meanMotionDegPerDay = elemVariation(planet, "mean_anomaly", Double.NaN)

// Keep your existing "feel": old code roughly implies ~6.366 days/sec
                val daysPerSecond = 40.0 / (2.0 * Math.PI)

                val glSpeed = if (!meanMotionDegPerDay.isNaN()) {
                    // convert deg/day -> rad/day -> rad/sec (in our accelerated time scale)
                    (Math.toRadians(meanMotionDegPerDay) * daysPerSecond).toFloat()
                } else {
                    val period = planet.optDouble("orbital_period", 365.0)
                    if (period != 0.0) (40.0f / period).toFloat() else 0f
                }

                val startPhase = if (!meanAnomalyDeg.isNaN()) {
                    Math.toRadians(meanAnomalyDeg).toFloat()
                } else {
                    (Math.random() * Math.PI * 2.0).toFloat()
                }


                // Color Parsing
                val colorHex = planet.optString("color_dark", planet.optString("color", "#FFFFFF"))
                val colorInt = try { Color.parseColor(colorHex) } catch (e: Exception) { Color.GRAY }

                // Assignments using the now-defined offset
                result[offset + 0] = glRadius
                result[offset + 1] = glSpeed
                result[offset + 2] = finalSize
                result[offset + 3] = startPhase
                result[offset + 4] = Color.red(colorInt) / 255f
                result[offset + 5] = Color.green(colorInt) / 255f
                result[offset + 6] = Color.blue(colorInt) / 255f
                result[offset + 7] = 1.0f
            }
            Log.d("SolarSights", "Loaded ${jsonArray.length()} planets successfully.")
            result
        } catch (e: Exception) {
            Log.e("SolarSights", "Failed to load planets: ${e.message}")
            floatArrayOf()
        }
    }
}