package com.example.solarsights

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class SolarWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = SolarEngine()

    inner class SolarEngine : Engine() {

        private var glSurfaceView: GLSurfaceView? = null
        private var renderer: SolarRenderer? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            // Helps ensure we actually get an RGBA surface when supported
            surfaceHolder.setFormat(PixelFormat.RGBA_8888)

            // Create a GLSurfaceView that renders into the wallpaper's SurfaceHolder
            glSurfaceView = object : GLSurfaceView(this@SolarWallpaperService) {
                override fun getHolder(): SurfaceHolder = surfaceHolder
            }.apply {
                setEGLContextClientVersion(3)
                // Request RGBA8888 (alpha can matter for some devices/pickers)
                setEGLConfigChooser(8, 8, 8, 8, 0, 0)
                preserveEGLContextOnPause = true

                renderer = SolarRenderer(this@SolarWallpaperService)
                setRenderer(renderer)

                // Keep it running while visible (we’ll pause in onVisibilityChanged)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }

        // CRITICAL: forward wallpaper surface lifecycle to GLSurfaceView
        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            glSurfaceView?.surfaceCreated(holder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            glSurfaceView?.surfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            glSurfaceView?.surfaceDestroyed(holder)
            super.onSurfaceDestroyed(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                glSurfaceView?.onResume()
            } else {
                glSurfaceView?.onPause()
            }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            // Thread-safe: run on GL thread
            glSurfaceView?.queueEvent {
                renderer?.setParallax(xOffset)
            }
        }

        override fun onDestroy() {
            glSurfaceView?.onPause()
            glSurfaceView = null
            renderer = null
            super.onDestroy()
        }
    }
}
