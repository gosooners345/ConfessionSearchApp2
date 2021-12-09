package com.confessionsearch.release1.helpers

import android.view.View
import androidx.viewpager2.widget.ViewPager2


class RotateUpPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, pos: Float) {
        val width = page.width.toFloat()
        val height = page.height.toFloat()
        val rotation = ROTATION * pos * -1.25f
        page.pivotX = width * 0.5f
        page.pivotY = height
        page.rotation = rotation
    }

    companion object {
        private const val ROTATION = -15f
    }
}