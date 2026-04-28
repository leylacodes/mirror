package com.mirror.app.ui.avatar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.ui.theme.SpoonDull
import com.mirror.app.ui.theme.SpoonSilver
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun DrawScope.drawPileOfSpoons(state: AvatarState, shimmer: Float) {
    val w = size.width
    val h = size.height

    val spoonCount = maxOf(1, (state.healthScore * 10).roundToInt())
    val spoonColor = lerp(SpoonDull, SpoonSilver, state.healthScore)
        .let { if (state.isHibernating) it.copy(alpha = 0.5f) else it }

    val pileCenter = Offset(w / 2f, h * 0.6f)
    val spoonLength = h * 0.35f
    val bowlRadius = spoonLength * 0.15f

    for (i in 0 until spoonCount) {
        val angle = if (spoonCount == 1) -Math.PI.toFloat() / 2f
        else (-Math.PI / 2 + (i - spoonCount / 2.0) * 0.22).toFloat()

        val handleEndX = pileCenter.x + cos(angle) * spoonLength
        val handleEndY = pileCenter.y + sin(angle) * spoonLength
        val bowlCx = pileCenter.x + cos(angle) * (spoonLength * 0.1f)
        val bowlCy = pileCenter.y + sin(angle) * (spoonLength * 0.1f)

        // Handle — tapering rect approximated as a path
        val perpAngle = angle + Math.PI.toFloat() / 2f
        val halfW1 = 5f
        val halfW2 = 2f
        val handlePath = Path().apply {
            moveTo(bowlCx + cos(perpAngle) * halfW1, bowlCy + sin(perpAngle) * halfW1)
            lineTo(bowlCx - cos(perpAngle) * halfW1, bowlCy - sin(perpAngle) * halfW1)
            lineTo(handleEndX - cos(perpAngle) * halfW2, handleEndY - sin(perpAngle) * halfW2)
            lineTo(handleEndX + cos(perpAngle) * halfW2, handleEndY + sin(perpAngle) * halfW2)
            close()
        }
        drawPath(handlePath, spoonColor)

        // Bowl
        drawOval(
            color = spoonColor,
            topLeft = Offset(bowlCx - bowlRadius * 1.1f, bowlCy - bowlRadius),
            size = Size(bowlRadius * 2.2f, bowlRadius * 2f)
        )

        // Shimmer highlight on top spoon
        if (i == spoonCount - 1 && state.healthScore > 0.5f) {
            drawCircle(
                color = Color.White.copy(alpha = shimmer * 0.5f * state.healthScore),
                radius = bowlRadius * 0.4f,
                center = Offset(bowlCx - bowlRadius * 0.3f, bowlCy - bowlRadius * 0.3f)
            )
        }
    }

    // Face in the topmost bowl
    val topAngle = if (spoonCount == 1) -Math.PI.toFloat() / 2f
    else (-Math.PI / 2 + ((spoonCount - 1) - spoonCount / 2.0) * 0.22).toFloat()
    val topBowlX = pileCenter.x + cos(topAngle) * (spoonLength * 0.1f)
    val topBowlY = pileCenter.y + sin(topAngle) * (spoonLength * 0.1f)
    drawFace(Offset(topBowlX, topBowlY), bowlRadius * 0.7f, state.moodScore)

    if (state.isHibernating) drawHibernationOverlay()
}
