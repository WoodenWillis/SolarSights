package com.example.solarsights

import android.content.Context
import android.opengl.GLES32
import android.util.Log

object ShaderUtils {
    private const val TAG = "ShaderUtils"
    fun createProgram(context: Context, vertexPath: String, fragmentPath: String): Int {
        val vertexSource = loadShaderSource(context, vertexPath)
        val fragmentSource = loadShaderSource(context, fragmentPath)

        val vertexShader = compileShader(GLES32.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = compileShader(GLES32.GL_FRAGMENT_SHADER, fragmentSource)

        if (vertexShader == 0 || fragmentShader == 0) return 0

        val program = GLES32.glCreateProgram()
        if (program != 0) {
            GLES32.glAttachShader(program, vertexShader)
            GLES32.glAttachShader(program, fragmentShader)

            // Bind attribute locations BEFORE linking
            GLES32.glBindAttribLocation(program, 0, "a_Position")
            GLES32.glBindAttribLocation(program, 1, "a_OrbitParams")
            GLES32.glBindAttribLocation(program, 2, "a_Color")

            GLES32.glLinkProgram(program)

            val linkStatus = IntArray(1)
            GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error linking program: ${GLES32.glGetProgramInfoLog(program)}")
                GLES32.glDeleteProgram(program)
                return 0
            }
        }
        return program
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES32.glCreateShader(type)
        if (shader != 0) {
            GLES32.glShaderSource(shader, shaderCode)
            GLES32.glCompileShader(shader)

            val compileStatus = IntArray(1)
            GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                Log.e(TAG, "Error compiling shader: ${GLES32.glGetShaderInfoLog(shader)}")
                GLES32.glDeleteShader(shader)
                return 0
            }
        }
        return shader
    }

    private fun loadShaderSource(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
