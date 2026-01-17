package com.example.solarsights

// Matches the "value" and "variation" structure in planets.json
data class OrbitalElement(
    val value: Double,
    val variation: Double
)

data class Moon(
    val name: String,
    val diameter: Double,
    val rotation_period: Double,
    val perihelion: Double,
    val aphelion: Double,
    val eccentricity: OrbitalElement,
    val mean_anomaly: OrbitalElement,
    val orbital_period: Double,
    val color_dark: String,
    val color_light: String
)

data class Planet(
    val name: String,
    val diameter: Double,
    val perihelion: Double,
    val aphelion: Double,
    val eccentricity: OrbitalElement,
    val mean_anomaly: OrbitalElement,
    val color_dark: String,
    val color_light: String,
    val moons: List<Moon> = emptyList()
)