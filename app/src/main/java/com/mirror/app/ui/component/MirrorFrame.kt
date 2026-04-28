package com.mirror.app.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mirror.app.ui.theme.GoldAccent
import com.mirror.app.ui.theme.PrimaryVariant

@Composable
fun MirrorFrame(
    size: Dp = 300.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val rx = this.size.width / 2f - 8.dp.toPx()
            val ry = this.size.height / 2f - 8.dp.toPx()

            // Outer glow ring
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(GoldAccent.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = rx + 16.dp.toPx()
                ),
                topLeft = Offset(cx - rx - 16.dp.toPx(), cy - ry - 16.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(
                    (rx + 16.dp.toPx()) * 2,
                    (ry + 16.dp.toPx()) * 2
                )
            )

            // Brass frame
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(GoldAccent, PrimaryVariant, GoldAccent, PrimaryVariant),
                    start = Offset(0f, 0f),
                    end = Offset(this.size.width, this.size.height)
                ),
                topLeft = Offset(cx - rx, cy - ry),
                size = androidx.compose.ui.geometry.Size(rx * 2, ry * 2),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Inner accent ring
            drawOval(
                color = GoldAccent.copy(alpha = 0.4f),
                topLeft = Offset(cx - rx + 6.dp.toPx(), cy - ry + 6.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(
                    (rx - 6.dp.toPx()) * 2,
                    (ry - 6.dp.toPx()) * 2
                ),
                style = Stroke(width = 1.dp.toPx())
            )

            // Ornament dots at cardinal points
            val dotRadius = 4.dp.toPx()
            listOf(
                Offset(cx, cy - ry - 4.dp.toPx()),
                Offset(cx, cy + ry + 4.dp.toPx()),
                Offset(cx - rx - 4.dp.toPx(), cy),
                Offset(cx + rx + 4.dp.toPx(), cy)
            ).forEach { pos ->
                drawCircle(color = GoldAccent, radius = dotRadius, center = pos)
            }
        }

        Box(
            modifier = Modifier
                .size(size - 24.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
