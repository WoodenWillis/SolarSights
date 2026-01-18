// SolarSights/app/src/main/java/com/example/solarsights/ui/theme/JsonUtils.kt
package com.example.solarsights.ui.theme

import android.content.Context
import android.graphics.Color
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object JsonUtils {
    const val FLOATS_PER_PLANET = 8
    // Adjust this: Higher = Planets closer to center. Lower = Planets farther out.
    private const val SCALING_FACTOR = 300.0f

    fun loadPlanetsToFloatArray(context: Context): FloatArray {
        return try {
            val jsonString = context.assets.open("planets.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val result = FloatArray(jsonArray.length() * FLOATS_PER_PLANET)

            for (i in 0 until jsonArray.length()) {
                val planet = jsonArray.getJSONObject(i)
                // 1. Define offset at the start of the loop
                val offset = i * FLOATS_PER_PLANET

                // Radius Math
                val dist = if (planet.has("perihelion")) {
                    (planet.optDouble("perihelion") + planet.optDouble("aphelion")) / 2.0
                } else {
                    planet.optDouble("distance", 100.0)
                }
                val glRadius = (dist / SCALING_FACTOR).toFloat()

                // Speed Math
                val period = planet.optDouble("orbital_period", 365.0)
                val glSpeed = if (period != 0.0) (40.0f / period).toFloat() else 0f

                // Size Math - Extract diameter from JSON
                val diameter = planet.optDouble("diameter", 10000.0)
                val glSize = (kotlin.math.sqrt(diameter) / 2500.0).toFloat()
                val boosted = glSize * (if (i < 4) 1.2f else 1.0f)
                val finalSize = boosted.coerceIn(0.012f, 0.085f)

                val startPhase = (Math.random() * Math.PI * 2).toFloat()

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