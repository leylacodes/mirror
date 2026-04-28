package com.mirror.app.ui.avatar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import com.mirror.app.domain.model.AvatarState
import com.mirror.app.ui.theme.TreeGreen
import com.mirror.app.ui.theme.TreeYellow
import kotlin.math.roundToInt

fun DrawScope.drawTree(state: AvatarState, shimmer: Float) {
    val w = size.width
    val h = size.height

    val trunkWidth = w * 0.18f
    val trunkHeight = h * 0.4f
    val trunkLeft = (w - trunkWidth) / 2f
    val trunkTop = h * 0.55f
    val trunkColor = Color(0xFF5D4037)

    drawRect(
        color = trunkColor,
        topLeft = Offset(trunkLeft, trunkTop),
        size = Size(trunkWidth, trunkHeight)
    )

    val leafColor = lerp(TreeYellow, TreeGreen, state.healthScore)
    val leafCount = (3 + (state.healthScore * 17)).roundToInt()
    val canopyCenter = Offset(w / 2f, h * 0.38f)
    val canopyRadius = w * 0.25f + state.healthScore * w * 0.1f

    val leafRadius = canopyRadius * 0.38f
    val offsets = listOf(
        Offset(0f, -canopyRadius * 0.75f),
        Offset(-canopyRadius * 0.65f, -canopyRadius * 0.3f),
        Offset(canopyRadius * 0.65f, -canopyRadius * 0.3f),
        Offset(-canopyRadius * 0.4f, canopyRadius * 0.2f),
        Offset(canopyRadius * 0.4f, canopyRadius * 0.2f),
        Offset(0f, 0f)
    )

    val activeClusters = maxOf(1, (offsets.size * state.healthScore).roundToInt())
    offsets.take(activeClusters).forEach { offset ->
        drawCircle(
            color = leafColor.copy(alpha = if (state.isHibernating) 0.5f else 1f),
            radius = leafRadius,
            center = canopyCenter + offset
        )
    }

    // Extra leaf dots for higher health
    if (leafCount > 6) {
        val extras = leafCount - 6
        for (i in 0 until extras) {
            val angle = (i.toFloat() / extras) * 2 * Math.PI.toFloat()
            val r = canopyRadius * 0.55f
            drawCircle(
                color = leafColor.copy(alpha = 0.7f),
                radius = leafRadius * 0.5f,
                center = Offset(
                    canopyCenter.x + r * kotlin.math.cos(angle),
                    canopyCenter.y + r * kotlin.math.sin(angle)
                )
            )
        }
    }

    // Shimmer highlights on leaves at high health
    if (state.healthScore > 0.7f && !state.isHibernating) {
        drawCircle(
            color = Color.White.copy(alpha = shimmer * 0.3f * state.healthScore),
            radius = leafRadius * 0.4f,
            center = canopyCenter + offsets[0]
        )
    }

    val faceCenter = Offset(w / 2f, trunkTop + trunkHeight * 0.4f)
    val faceRadius = trunkWidth * 0.6f
    drawFace(faceCenter, faceRadius, state.moodScore)

    if (state.isHibernating) drawHibernationOverlay()
}
