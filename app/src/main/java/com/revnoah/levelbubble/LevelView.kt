package com.revnoah.levelbubble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.view.View
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class LevelMode {
    Flat,
    Vertical
}

data class LevelReading(
    val x: Float,
    val y: Float,
    val z: Float,
    val mode: LevelMode
)

class LevelView(context: Context) : View(context) {
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(10, 12, 13)
    }
    private val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(31, 32, 31)
    }
    private val caseEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(6, 7, 7)
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val bezelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(11, 12, 12)
    }
    private val bezelEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(47, 49, 48)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val dataPanelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(13, 16, 14)
    }
    private val dataPanelEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(68, 82, 42)
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }
    private val dataGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(82, 165, 230, 31)
        style = Paint.Style.STROKE
        strokeWidth = 1.6f
    }
    private val meterTrackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(28, 38, 18)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 5f
    }
    private val meterValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(201, 245, 26)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 5f
    }
    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(70, 255, 255, 210)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 6f
    }
    private val tubeFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(178, 225, 4)
    }
    private val liquidPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(172, 220, 0)
    }
    private val liquidHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(105, 235, 255, 78)
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val glassSheenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(95, 255, 255, 220)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4f
    }
    private val markPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(18, 24, 16)
        style = Paint.Style.STROKE
        strokeWidth = 3.2f
    }
    private val mutedMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(165, 23, 31, 18)
        style = Paint.Style.STROKE
        strokeWidth = 2.4f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(232, 240, 244)
        textAlign = Paint.Align.CENTER
        textSize = 42f
    }
    private val smallTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 232, 240, 244)
        textAlign = Paint.Align.CENTER
        textSize = 22f
    }
    private val dataLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(178, 212, 238, 142)
        textAlign = Paint.Align.LEFT
        textSize = 18f
    }
    private val dataValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(218, 250, 54)
        textAlign = Paint.Align.RIGHT
        textSize = 24f
    }
    private val dataPrimaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(224, 255, 54)
        textAlign = Paint.Align.CENTER
        textSize = 48f
    }
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(95, 245, 255, 175)
    }
    private val bubbleEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(165, 38, 73, 14)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val bubbleHighlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 255, 255, 225)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val rect = RectF()

    private var reading = LevelReading(0f, 0f, 1f, LevelMode.Flat)
    private var unavailable = false

    fun updateLevel(nextReading: LevelReading) {
        unavailable = false
        reading = nextReading
        invalidate()
    }

    fun showUnavailable() {
        unavailable = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        if (unavailable) {
            drawUnavailable(canvas)
            return
        }

        when (reading.mode) {
            LevelMode.Flat -> drawFlatLevel(canvas)
            LevelMode.Vertical -> drawVerticalLevel(canvas)
        }
    }

    private fun drawFlatLevel(canvas: Canvas) {
        val layout = drawInstrumentCase(canvas)
        val circle = layout.circle
        val cx = circle.centerX()
        val cy = circle.centerY()
        val radius = circle.width() / 2f
        val bubbleRadius = radius * 0.15f
        val travel = radius - bubbleRadius - 18f
        val bubbleX = cx - reading.x.coerceIn(-1f, 1f) * travel
        val bubbleY = cy + reading.y.coerceIn(-1f, 1f) * travel
        val tilt = hypot(reading.x, reading.y).coerceIn(0f, 1f)
        val degrees = Math.toDegrees(atan2(tilt.toDouble(), abs(reading.z).toDouble())).toFloat()

        drawCircularVial(canvas, circle)
        drawConcentricMarks(canvas, cx, cy, radius)
        drawRoundBubble(canvas, bubbleX, bubbleY, bubbleRadius)
        drawCenterCrosshair(canvas, cx, cy, radius * 0.22f, markPaint)
        drawVerticalTube(canvas, layout.verticalTube, reading.z, reading.x)
        drawHorizontalTube(canvas, layout.horizontalTube, reading.x)
        drawPrecisionPanel(
            canvas = canvas,
            bounds = layout.sideDataPanel,
            title = "SURFACE",
            primary = "${formatDegrees(degrees)}°",
            rows = listOf(
                "X" to signedDegrees(reading.x, abs(reading.z)),
                "Y" to signedDegrees(reading.y, abs(reading.z)),
                "CENTER" to "${((1f - tilt).coerceIn(0f, 1f) * 100f).roundToInt()}%"
            ),
            meterValue = tilt
        )
    }

    private fun drawVerticalLevel(canvas: Canvas) {
        val layout = drawInstrumentCase(canvas)
        val tube = layout.verticalTube
        val cx = tube.centerX()
        val tubeRadius = tube.width() * 0.37f
        val verticalReference = if (reading.y >= 0f) 1f else -1f
        val plumbError = ((abs(reading.y) - 1f) * verticalReference).coerceIn(-1f, 1f)
        val lateralError = reading.x.coerceIn(-1f, 1f)
        val bubbleTravel = tube.height() / 2f - tubeRadius * 1.65f
        val bubbleY = (tube.centerY() - plumbError * bubbleTravel * 4.8f)
            .coerceIn(tube.top + tubeRadius * 1.3f, tube.bottom - tubeRadius * 1.3f)
        val bubbleX = cx - lateralError * tube.width() * 0.18f
        val offPlumb = hypot(reading.x, abs(reading.z)).coerceIn(0f, 1f)
        val degrees = Math.toDegrees(atan2(offPlumb.toDouble(), abs(reading.y).toDouble())).toFloat()

        drawCircularVial(canvas, layout.circle)
        drawConcentricMarks(canvas, layout.circle.centerX(), layout.circle.centerY(), layout.circle.width() / 2f)
        drawRoundBubble(
            canvas,
            layout.circle.centerX() - reading.x.coerceIn(-1f, 1f) * layout.circle.width() * 0.28f,
            layout.circle.centerY() + reading.y.coerceIn(-1f, 1f) * layout.circle.width() * 0.28f,
            layout.circle.width() * 0.075f
        )
        drawCenterCrosshair(canvas, layout.circle.centerX(), layout.circle.centerY(), layout.circle.width() * 0.11f, markPaint)
        drawHorizontalTube(canvas, layout.horizontalTube, reading.x)
        drawVerticalTube(canvas, tube, reading.z, reading.x, bubbleX, bubbleY)
        drawPrecisionPanel(
            canvas = canvas,
            bounds = layout.bottomDataPanel,
            title = "PLUMB",
            primary = "${formatDegrees(degrees)}°",
            rows = listOf(
                "SIDE" to signedDegrees(reading.x, abs(reading.y)),
                "FACE" to signedDegrees(reading.z, abs(reading.y)),
                "CENTER" to "${((1f - offPlumb).coerceIn(0f, 1f) * 100f).roundToInt()}%"
            ),
            meterValue = offPlumb
        )
    }

    private fun drawInstrumentCase(canvas: Canvas): InstrumentLayout {
        val side = min(width * 0.94f, height * 0.72f)
        val left = (width - side) / 2f
        val top = max(18f, (height - side) / 2f - height * 0.1f)
        rect.set(left, top, left + side, top + side)
        canvas.drawRoundRect(rect, side * 0.065f, side * 0.065f, panelPaint)
        canvas.drawRoundRect(rect, side * 0.065f, side * 0.065f, caseEdgePaint)

        val margin = side * 0.06f
        val circleSize = side * 0.66f
        val circle = RectF(
            left + margin,
            top + margin,
            left + margin + circleSize,
            top + margin + circleSize
        )
        val vertical = RectF(
            circle.right + side * 0.08f,
            circle.top,
            left + side - margin,
            circle.bottom
        )
        val horizontal = RectF(
            left + margin,
            circle.bottom + side * 0.08f,
            vertical.left,
            top + side - margin
        )
        val sideData = RectF(
            vertical.left,
            horizontal.top,
            left + side - margin,
            horizontal.bottom
        )
        val bottomTop = top + side + max(18f, height * 0.018f)
        val bottom = RectF(
            left + margin,
            bottomTop,
            left + side - margin,
            min(height - 18f, bottomTop + max(130f, height * 0.13f))
        )
        return InstrumentLayout(circle, vertical, horizontal, sideData, bottom)
    }

    private fun drawCircularVial(canvas: Canvas, circle: RectF) {
        val cx = circle.centerX()
        val cy = circle.centerY()
        val radius = circle.width() / 2f

        canvas.drawCircle(cx, cy, radius + 16f, bezelPaint)
        canvas.drawCircle(cx, cy, radius + 16f, bezelEdgePaint)
        liquidPaint.shader = RadialGradient(
            cx + radius * 0.18f,
            cy + radius * 0.32f,
            radius * 1.12f,
            intArrayOf(
                Color.rgb(216, 249, 12),
                Color.rgb(145, 207, 0),
                Color.rgb(63, 126, 0)
            ),
            floatArrayOf(0f, 0.58f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, liquidPaint)
        liquidPaint.shader = null
        canvas.drawCircle(cx, cy, radius, glassPaint)
        canvas.drawArc(
            cx - radius * 0.75f,
            cy - radius * 0.78f,
            cx + radius * 0.75f,
            cy + radius * 0.78f,
            210f,
            85f,
            false,
            liquidHighlightPaint
        )
    }

    private fun drawVerticalTube(
        canvas: Canvas,
        bounds: RectF,
        verticalTilt: Float,
        lateralTilt: Float,
        bubbleXOverride: Float? = null,
        bubbleYOverride: Float? = null
    ) {
        drawTubeBezel(canvas, bounds)
        val inset = bounds.width() * 0.2f
        val vial = RectF(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset)
        drawTubeLiquid(canvas, vial, vertical = true)
        val radius = vial.width() * 0.48f
        val bubbleX = bubbleXOverride ?: (vial.centerX() - lateralTilt.coerceIn(-1f, 1f) * vial.width() * 0.18f)
        val bubbleY = bubbleYOverride ?: (vial.centerY() + verticalTilt.coerceIn(-1f, 1f) * vial.height() * 0.34f)
        drawOvalBubble(canvas, bubbleX, bubbleY, radius * 0.9f, radius * 1.25f)
        drawTubeMarks(canvas, vial, vertical = true)
    }

    private fun drawHorizontalTube(canvas: Canvas, bounds: RectF, tilt: Float) {
        drawTubeBezel(canvas, bounds)
        val inset = bounds.height() * 0.2f
        val vial = RectF(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset)
        drawTubeLiquid(canvas, vial, vertical = false)
        val radius = vial.height() * 0.48f
        val bubbleX = vial.centerX() - tilt.coerceIn(-1f, 1f) * vial.width() * 0.34f
        drawOvalBubble(canvas, bubbleX, vial.centerY(), radius * 1.25f, radius * 0.9f)
        drawTubeMarks(canvas, vial, vertical = false)
    }

    private fun drawTubeBezel(canvas: Canvas, bounds: RectF) {
        canvas.drawRoundRect(bounds, 7f, 7f, bezelPaint)
        canvas.drawRoundRect(bounds, 7f, 7f, bezelEdgePaint)
    }

    private fun drawTubeLiquid(canvas: Canvas, vial: RectF, vertical: Boolean) {
        tubeFillPaint.shader = LinearGradient(
            vial.left,
            vial.top,
            if (vertical) vial.right else vial.left,
            if (vertical) vial.top else vial.bottom,
            intArrayOf(
                Color.rgb(54, 120, 0),
                Color.rgb(180, 232, 0),
                Color.rgb(219, 247, 18),
                Color.rgb(73, 137, 0)
            ),
            floatArrayOf(0f, 0.35f, 0.62f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(vial, 4f, 4f, tubeFillPaint)
        tubeFillPaint.shader = null
        canvas.drawRoundRect(vial, 4f, 4f, glassPaint)
        if (vertical) {
            canvas.drawLine(vial.left + vial.width() * 0.32f, vial.top + 10f, vial.left + vial.width() * 0.32f, vial.bottom - 10f, glassSheenPaint)
        } else {
            canvas.drawLine(vial.left + 10f, vial.top + vial.height() * 0.3f, vial.right - 10f, vial.top + vial.height() * 0.3f, glassSheenPaint)
        }
    }

    private fun drawRoundBubble(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        bubblePaint.shader = RadialGradient(
            cx - radius * 0.32f,
            cy - radius * 0.35f,
            radius * 1.1f,
            intArrayOf(
                Color.argb(185, 244, 255, 170),
                Color.argb(72, 230, 255, 120),
                Color.argb(38, 31, 85, 0)
            ),
            floatArrayOf(0f, 0.62f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, bubblePaint)
        bubblePaint.shader = null
        canvas.drawCircle(cx, cy, radius, bubbleEdgePaint)
        canvas.drawCircle(cx - radius * 0.28f, cy - radius * 0.32f, radius * 0.16f, bubbleHighlightPaint)
    }

    private fun drawOvalBubble(canvas: Canvas, cx: Float, cy: Float, rx: Float, ry: Float) {
        rect.set(cx - rx, cy - ry, cx + rx, cy + ry)
        bubblePaint.shader = RadialGradient(
            cx - rx * 0.25f,
            cy - ry * 0.3f,
            max(rx, ry) * 1.15f,
            intArrayOf(
                Color.argb(180, 244, 255, 170),
                Color.argb(70, 230, 255, 120),
                Color.argb(35, 31, 85, 0)
            ),
            floatArrayOf(0f, 0.62f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawOval(rect, bubblePaint)
        bubblePaint.shader = null
        canvas.drawOval(rect, bubbleEdgePaint)
    }

    private fun drawConcentricMarks(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.drawCircle(cx, cy, radius * 0.34f, mutedMarkPaint)
        canvas.drawCircle(cx, cy, radius * 0.62f, mutedMarkPaint)
        canvas.drawLine(cx - radius * 0.88f, cy, cx - radius * 0.48f, cy, markPaint)
        canvas.drawLine(cx + radius * 0.48f, cy, cx + radius * 0.88f, cy, markPaint)
        canvas.drawLine(cx, cy - radius * 0.88f, cx, cy - radius * 0.48f, markPaint)
        canvas.drawLine(cx, cy + radius * 0.48f, cx, cy + radius * 0.88f, markPaint)
    }

    private fun drawCenterCrosshair(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        canvas.drawLine(cx - size, cy, cx + size, cy, paint)
        canvas.drawLine(cx, cy - size, cx, cy + size, paint)
    }

    private fun drawTubeMarks(canvas: Canvas, vial: RectF, vertical: Boolean) {
        val offsets = listOf(-0.9f, 0f, 0.9f)
        for (offset in offsets) {
            if (vertical) {
                val y = vial.centerY() + offset * vial.height() * 0.24f
                canvas.drawLine(vial.left - 4f, y, vial.right + 4f, y, markPaint)
            } else {
                val x = vial.centerX() + offset * vial.width() * 0.24f
                canvas.drawLine(x, vial.top - 4f, x, vial.bottom + 4f, markPaint)
            }
        }
    }

    private fun drawPrecisionPanel(
        canvas: Canvas,
        bounds: RectF,
        title: String,
        primary: String,
        rows: List<Pair<String, String>>,
        meterValue: Float
    ) {
        if (bounds.width() < 90f || bounds.height() < 80f) return

        canvas.drawRoundRect(bounds, 7f, 7f, dataPanelPaint)
        canvas.drawRoundRect(bounds, 7f, 7f, dataPanelEdgePaint)

        val pad = min(bounds.width(), bounds.height()) * 0.12f
        val left = bounds.left + pad
        val right = bounds.right - pad
        val top = bounds.top + pad
        val titleSize = (bounds.height() * 0.12f).coerceIn(13f, 19f)
        val primarySize = (bounds.height() * 0.28f).coerceIn(26f, 46f)
        val rowSize = (bounds.height() * 0.13f).coerceIn(13f, 21f)

        dataLabelPaint.textSize = titleSize
        dataLabelPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(title, left, top + titleSize, dataLabelPaint)

        dataPrimaryPaint.textSize = primarySize
        canvas.drawText(primary, bounds.centerX(), bounds.top + bounds.height() * 0.43f, dataPrimaryPaint)

        val meterY = bounds.top + bounds.height() * 0.55f
        val meterStart = left
        val meterEnd = right
        canvas.drawLine(meterStart, meterY, meterEnd, meterY, meterTrackPaint)
        val center = (meterStart + meterEnd) / 2f
        val half = (meterEnd - meterStart) / 2f
        val clamped = meterValue.coerceIn(0f, 1f)
        canvas.drawLine(center, meterY, center + half * clamped, meterY, meterValuePaint)
        canvas.drawLine(center, meterY - 9f, center, meterY + 9f, dataGridPaint)

        dataLabelPaint.textSize = rowSize
        dataValuePaint.textSize = rowSize * 1.08f
        val rowStart = bounds.top + bounds.height() * 0.72f
        val rowGap = ((bounds.bottom - pad - rowStart) / max(1, rows.size - 1)).coerceAtLeast(rowSize * 1.15f)
        rows.forEachIndexed { index, row ->
            val y = rowStart + index * rowGap
            if (y <= bounds.bottom - pad * 0.45f) {
                canvas.drawLine(left, y - rowSize * 0.9f, right, y - rowSize * 0.9f, dataGridPaint)
                canvas.drawText(row.first, left, y, dataLabelPaint)
                canvas.drawText(row.second, right, y, dataValuePaint)
            }
        }
    }

    private fun formatDegrees(degrees: Float): String {
        return String.format("%.2f", degrees)
    }

    private fun signedDegrees(numerator: Float, denominator: Float): String {
        val degrees = Math.toDegrees(
            atan2(numerator.toDouble(), denominator.coerceAtLeast(0.001f).toDouble())
        ).toFloat()
        return String.format("%+.2f°", degrees)
    }

    private fun drawUnavailable(canvas: Canvas) {
        textPaint.textSize = min(width, height) * 0.065f
        smallTextPaint.textSize = min(width, height) * 0.038f
        canvas.drawText("Sensor unavailable", width / 2f, height / 2f - 10f, textPaint)
        canvas.drawText("This device does not report motion data.", width / 2f, height / 2f + 42f, smallTextPaint)
    }

    private data class InstrumentLayout(
        val circle: RectF,
        val verticalTube: RectF,
        val horizontalTube: RectF,
        val sideDataPanel: RectF,
        val bottomDataPanel: RectF
    )
}
