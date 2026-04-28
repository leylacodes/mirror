package com.mirror.app.ui.avatar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.ui.theme.WaterClean
import com.mirror.app.ui.theme.WaterMurky
import kotlin.math.sin

fun DrawScope.drawWaterBucket(state: AvatarState, wavePhase: Float) {
    val w = size.width
    val h = size.height

    val bucketTopWidth = w * 0.65f
    val bucketBottomWidth = w * 0.45f
    val bucketTop = h * 0.25f
    val bucketBottom = h * 0.82f
    val bucketHeight = bucketBottom - bucketTop

    val topLeft = (w - bucketTopWidth) / 2f
    val bottomLeft = (w - bucketBottomWidth) / 2f

    val bucketPath = Path().apply {
        moveTo(topLeft, bucketTop)
        lineTo(topLeft + bucketTopWidth, bucketTop)
        lineTo(bottomLeft + bucketBottomWidth, bucketBottom)
        lineTo(bottomLeft, bucketBottom)
        close()
    }

    // Bucket outline
    drawPath(bucketPath, Color(0xFF78909C))
    drawPath(bucketPath, Color(0xFF546E7A), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

    // Water level = blend of recentAvg and healthScore
    val waterLevel = ((state.recentAvg + state.healthScore) / 2f).coerceIn(0f, 1f)
    val waterColor = lerp(WaterMurky, WaterClean, state.healthScore)
        .let { if (state.isHibernating) it.copy(alpha = 0.5f) else it }

    val waterY = bucketBottom - bucketHeight * waterLevel

    // Animated wave surface
    val waterFillPath = Path().apply {
        // Trapezoid clip at water level with wave
        val leftEdgeAtWater = bottomLeft + (topLeft - bottomLeft) * ((bucketBottom - waterY) / bucketHeight)
        val rightEdgeAtWater = bottomLeft + bucketBottomWidth + (topLeft + bucketTopWidth - bottomLeft - bucketBottomWidth) * ((bucketBottom - waterY) / bucketHeight)

        moveTo(leftEdgeAtWater, waterY)
        val steps = 20
        for (i in 0..steps) {
            val x = leftEdgeAtWater + (rightEdgeAtWater - leftEdgeAtWater) * i / steps
            val wave = sin(wavePhase + i * 0.5f) * 4f
            lineTo(x, waterY + wave)
        }
        lineTo(bottomLeft + bucketBottomWidth, bucketBottom)
        lineTo(bottomLeft, bucketBottom)
        close()
    }

    clipPath(bucketPath) {
        drawPath(waterFillPath, waterColor)
    }

    // Handle / rim
    drawLine(Color(0xFF78909C), Offset(topLeft, bucketTop), Offset(topLeft + bucketTopWidth, bucketTop), 4f)

    // Face above water line
    val faceCenter = Offset(w / 2f, waterY - h * 0.08f)
    val faceRadius = bucketTopWidth * 0.18f
    drawFace(faceCenter, faceRadius, state.moodScore)

    if (state.isHibernating) drawHibernationOverlay()
}
