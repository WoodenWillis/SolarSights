// SolarSights/app/src/main/java/com/example/solarsights/SolarRenderer.kt
package com.example.solarsights

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.solarsights.ui.theme.JsonUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SolarRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var planetProgramId: Int = 0
    private var ringProgramId: Int = 0
    private var aspect: Float = 1.0f
    private var startTime = System.nanoTime()
    private var parallaxOffset: Float = 0f

    private val vboIds = IntArray(2) // 0: Quad geometry, 1: Instance data
    private var instanceCount = 0
    private val STRIDE_BYTES = JsonUtils.FLOATS_PER_PLANET * 4 // now 64

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glClearColor(0.0f, 0.0f, 0.05f, 1.0f)

        try {
            planetProgramId =
                ShaderUtils.createProgram(context, "instanced_planet.vert", "instanced_planet.frag")
            ringProgramId = ShaderUtils.createProgram(context, "orbit_ring.vert", "orbit_ring.frag")
        } catch (e: Exception) {
            Log.e("SolarRenderer", "Shader Error: ${e.message}")
        }

        // 1. Setup VBO for Quad Geometry
        val quadCoords = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
        val quadBuffer =
            ByteBuffer.allocateDirect(quadCoords.size * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(quadCoords)
        quadBuffer.position(0)

        GLES32.glGenBuffers(2, vboIds, 0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0])
        GLES32.glBufferData(
            GLES32.GL_ARRAY_BUFFER,
            quadCoords.size * 4,
            quadBuffer,
            GLES32.GL_STATIC_DRAW
        )

        // 2. Setup VBO for Planet Data
        val planetData = JsonUtils.loadPlanetsToFloatArray(context)
        instanceCount = planetData.size / JsonUtils.FLOATS_PER_PLANET
        if (instanceCount > 0) {
            val planetBuffer =
                ByteBuffer.allocateDirect(planetData.size * 4).order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(planetData)
            planetBuffer.position(0)
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[1])
            GLES32.glBufferData(
                GLES32.GL_ARRAY_BUFFER,
                planetData.size * 4,
                planetBuffer,
                GLES32.GL_STATIC_DRAW
            )
        }
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        aspect = width.toFloat() / height.toFloat()
    }

    fun setParallax(offset: Float) {
        parallaxOffset = (offset - 0.5f) * 0.4f
    }

    fun onDestroy() {
        GLES32.glDeleteBuffers(2, vboIds, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)
        if (planetProgramId == 0 || instanceCount == 0) return

        val time = (System.nanoTime() - startTime) / 1_000_000_000f

        // PASS 1: RINGS
        GLES32.glUseProgram(ringProgramId)
        GLES32.glUniform1f(GLES32.glGetUniformLocation(ringProgramId, "u_Aspect"), aspect)
        GLES32.glUniform1f(GLES32.glGetUniformLocation(ringProgramId, "u_Offset"), parallaxOffset)
        GLES32.glUniform2f(GLES32.glGetUniformLocation(ringProgramId, "u_Center"), 0.0f, 0.0f)

        renderInstances(false)

        // PASS 2: PLANETS
        GLES32.glUseProgram(planetProgramId)
        GLES32.glUniform1f(GLES32.glGetUniformLocation(planetProgramId, "u_Time"), time)
        GLES32.glUniform1f(GLES32.glGetUniformLocation(planetProgramId, "u_Aspect"), aspect)
        GLES32.glUniform1f(GLES32.glGetUniformLocation(planetProgramId, "u_Offset"), parallaxOffset)
        GLES32.glUniform2f(GLES32.glGetUniformLocation(planetProgramId, "u_Center"), 0.0f, 0.0f)

        renderInstances(true)
    }

    private fun renderInstances(useColors: Boolean) {
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)

        // Bind Quad geometry (location 0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[0])
        GLES32.glEnableVertexAttribArray(0)
        GLES32.glVertexAttribPointer(0, 2, GLES32.GL_FLOAT, false, 0, 0)
        GLES32.glVertexAttribDivisor(0, 0) // Not instanced

        // Bind Planet instance data
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vboIds[1])

        // Location 1: Orbit params (radius, speed, size, phase)
        GLES32.glEnableVertexAttribArray(1)
        GLES32.glVertexAttribPointer(1, 4, GLES32.GL_FLOAT, false, STRIDE_BYTES, 0)
        GLES32.glVertexAttribDivisor(1, 1)

        // Location 2: Color
        if (useColors) {
            GLES32.glEnableVertexAttribArray(2)
            GLES32.glVertexAttribPointer(2, 4, GLES32.GL_FLOAT, false, STRIDE_BYTES, 16)
            GLES32.glVertexAttribDivisor(2, 1)
        }

        // ACTUALLY DRAW THE INSTANCES!
        GLES32.glDrawArraysInstanced(GLES32.GL_TRIANGLE_STRIP, 0, 4, instanceCount)

        // Cleanup
        GLES32.glDisableVertexAttribArray(0)
        GLES32.glDisableVertexAttribArray(1)
        if (useColors) {
            GLES32.glDisableVertexAttribArray(2)
        }

        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)
        GLES32.glDisable(GLES32.GL_BLEND)
    }
}