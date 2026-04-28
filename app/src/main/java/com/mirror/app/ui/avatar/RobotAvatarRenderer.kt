package com.mirror.app.ui.avatar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.ui.theme.RobotRust
import com.mirror.app.ui.theme.RobotSilver

fun DrawScope.drawRobot(state: AvatarState, shimmer: Float) {
    val w = size.width
    val h = size.height
    val rustAlpha = (1f - state.healthScore).coerceIn(0f, 0.9f)
    val bodyColor = if (state.isHibernating) RobotSilver.copy(alpha = 0.5f) else RobotSilver

    // Body
    val bodyLeft = w * 0.2f
    val bodyTop = h * 0.42f
    val bodyWidth = w * 0.6f
    val bodyHeight = h * 0.38f
    drawRoundRect(bodyColor, Offset(bodyLeft, bodyTop), Size(bodyWidth, bodyHeight), CornerRadius(12f, 12f))

    // Head
    val headSize = w * 0.45f
    val headLeft = (w - headSize) / 2f
    val headTop = h * 0.1f
    drawRoundRect(bodyColor, Offset(headLeft, headTop), Size(headSize, headSize * 0.8f), CornerRadius(10f, 10f))

    // Neck
    drawRect(bodyColor, Offset(w * 0.42f, headTop + headSize * 0.78f), Size(w * 0.16f, h * 0.06f))

    // Arms
    drawRoundRect(bodyColor, Offset(w * 0.06f, bodyTop + bodyHeight * 0.1f), Size(w * 0.12f, bodyHeight * 0.7f), CornerRadius(8f, 8f))
    drawRoundRect(bodyColor, Offset(w * 0.82f, bodyTop + bodyHeight * 0.1f), Size(w * 0.12f, bodyHeight * 0.7f), CornerRadius(8f, 8f))

    // Legs
    drawRoundRect(bodyColor, Offset(w * 0.28f, bodyTop + bodyHeight - 4f), Size(w * 0.17f, h * 0.15f), CornerRadius(6f, 6f))
    drawRoundRect(bodyColor, Offset(w * 0.55f, bodyTop + bodyHeight - 4f), Size(w * 0.17f, h * 0.15f), CornerRadius(6f, 6f))

    // Rust patches
    if (rustAlpha > 0.1f) {
        val rustPath = Path().apply {
            moveTo(bodyLeft + bodyWidth * 0.2f, bodyTop + bodyHeight * 0.3f)
            lineTo(bodyLeft + bodyWidth * 0.4f, bodyTop + bodyHeight * 0.2f)
            lineTo(bodyLeft + bodyWidth * 0.45f, bodyTop + bodyHeight * 0.55f)
            lineTo(bodyLeft + bodyWidth * 0.15f, bodyTop + bodyHeight * 0.6f)
            close()
        }
        drawPath(rustPath, RobotRust.copy(alpha = rustAlpha * 0.7f))

        if (rustAlpha > 0.5f) {
            val rustPath2 = Path().apply {
                moveTo(headLeft + headSize * 0.6f, headTop + headSize * 0.4f)
                lineTo(headLeft + headSize * 0.85f, headTop + headSize * 0.3f)
                lineTo(headLeft + headSize * 0.9f, headTop + headSize * 0.7f)
                lineTo(headLeft + headSize * 0.55f, headTop + headSize * 0.75f)
                close()
            }
            drawPath(rustPath2, RobotRust.copy(alpha = (rustAlpha - 0.5f) * 1.4f))
        }
    }

    // Shine diagonal at high health
    if (state.healthScore > 0.8f && !state.isHibernating) {
        drawLine(
            Color.White.copy(alpha = shimmer * 0.4f),
            Offset(bodyLeft + bodyWidth * 0.1f, bodyTop + bodyHeight * 0.1f),
            Offset(bodyLeft + bodyWidth * 0.4f, bodyTop + bodyHeight * 0.4f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
    }

    // Antenna
    drawLine(bodyColor, Offset(w / 2f, headTop), Offset(w / 2f, headTop - h * 0.07f), 4f)
    drawCircle(bodyColor, 6f, Offset(w / 2f, headTop - h * 0.07f))

    // LED eyes (concentric circles with glow)
    val eyeY = headTop + headSize * 0.35f
    val eyeRadius = headSize * 0.1f
    listOf(headLeft + headSize * 0.28f, headLeft + headSize * 0.72f).forEach { ex ->
        val eyeColor = when {
            state.moodScore >= 4 -> Color(0xFF00E5FF)
            state.moodScore == 3 -> Color(0xFF69F0AE)
            else -> Color(0xFFFF5252)
        }
        drawCircle(eyeColor.copy(alpha = 0.3f), eyeRadius * 1.5f, Offset(ex, eyeY))
        drawCircle(eyeColor.copy(alpha = 0.6f), eyeRadius, Offset(ex, eyeY))
        drawCircle(Color.White, eyeRadius * 0.4f, Offset(ex, eyeY))
    }

    // Mouth
    val mouthCenter = Offset(w / 2f, headTop + headSize * 0.62f)
    drawFace(mouthCenter, headSize * 0.22f, state.moodScore)

    if (state.isHibernating) drawHibernationOverlay()
}
