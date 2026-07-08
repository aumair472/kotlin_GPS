package com.geosnap.core.media

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * Draws stamp [lines] onto [bitmap] in place. Layout scales by output size (not screen dp) with safe
 * margins, a semi-opaque dark container, and a monospace face for alignment (DESIGN_SYSTEM camera
 * overlay rules + CAMERA_GPS_PIPELINE visible overlay rules).
 */
class PhotoStamper @Inject constructor() {

    fun stamp(bitmap: Bitmap, lines: List<String>) {
        if (lines.isEmpty()) return
        val canvas = Canvas(bitmap)
        val w = bitmap.width
        val h = bitmap.height
        val base = min(w, h)

        val margin = base * 0.03f
        val padding = base * 0.018f
        val textSize = max(base * 0.026f, 18f)
        val lineGap = textSize * 0.45f

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            this.textSize = textSize
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(0xCC, 0x11, 0x18, 0x27)
        }

        // Wrap to available width.
        val maxWidth = w - margin * 2 - padding * 2
        val wrapped = lines.flatMap { wrap(it, textPaint, maxWidth) }

        val lineHeight = textSize + lineGap
        val blockHeight = padding * 2 + wrapped.size * lineHeight - lineGap
        var blockWidth = 0f
        wrapped.forEach { blockWidth = max(blockWidth, textPaint.measureText(it)) }
        blockWidth += padding * 2

        val left = margin
        val top = h - margin - blockHeight
        val radius = base * 0.012f
        canvas.drawRoundRect(
            RectF(left, top, left + blockWidth, top + blockHeight),
            radius, radius, bgPaint,
        )

        var y = top + padding - textPaint.fontMetrics.ascent
        wrapped.forEach { line ->
            canvas.drawText(line, left + padding, y, textPaint)
            y += lineHeight
        }
    }

    private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
        if (paint.measureText(text) <= maxWidth) return listOf(text)
        val words = text.split(' ')
        val result = mutableListOf<String>()
        var current = StringBuilder()
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) > maxWidth && current.isNotEmpty()) {
                result += current.toString()
                current = StringBuilder(word)
            } else {
                current = StringBuilder(candidate)
            }
        }
        if (current.isNotEmpty()) result += current.toString()
        return result
    }
}
