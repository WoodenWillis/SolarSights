package com.example.solarsights

import android.content.Context
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class SolarWallpaperService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine {
        return SolarEngine()
    }

    inner class SolarEngine : WallpaperService.Engine() {
        private var glSurfaceView: GLSurfaceView? = null
        private var renderer: SolarRenderer? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)

            // Initialize GLSurfaceView
            glSurfaceView = object : GLSurfaceView(this@SolarWallpaperService) {
                override fun getHolder(): SurfaceHolder = surfaceHolder
            }

            glSurfaceView?.apply {
                setEGLContextClientVersion(3)
                setPreserveEGLContextOnPause(true)

                renderer = SolarRenderer(this@SolarWallpaperService)
                setRenderer(renderer)

                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                glSurfaceView?.onResume()
            } else {
                glSurfaceView?.onPause()
            }
        }

        // Inside SolarWallpaperService.kt -> SolarEngine class
        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            // Instead of setParallax, we usually pass the xOffset to the renderer
            // renderer.setOffset(xOffset)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            // Clean up rendering loop here
        }
    }
}