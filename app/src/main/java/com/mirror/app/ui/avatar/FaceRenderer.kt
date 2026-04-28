package com.mirror.app.ui.avatar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawFace(center: Offset, radius: Float, moodScore: Int) {
    val eyeRadius = radius * 0.1f
    val eyeOffsetX = radius * 0.3f
    val eyeOffsetY = radius * 0.15f
    val eyeY = center.y - eyeOffsetY
    val leftEye = Offset(center.x - eyeOffsetX, eyeY)
    val rightEye = Offset(center.x + eyeOffsetX, eyeY)
    val mouthY = center.y + radius * 0.25f
    val mouthHalfWidth = radius * 0.35f
    val strokeWidth = radius * 0.08f

    val faceColor = Color.White

    when (moodScore) {
        1 -> {
            // X eyes
            val x = eyeRadius * 0.8f
            listOf(leftEye, rightEye).forEach { eye ->
                drawLine(faceColor, Offset(eye.x - x, eye.y - x), Offset(eye.x + x, eye.y + x), strokeWidth, StrokeCap.Round)
                drawLine(faceColor, Offset(eye.x + x, eye.y - x), Offset(eye.x - x, eye.y + x), strokeWidth, StrokeCap.Round)
            }
            // Strong frown
            val path = Path().apply {
                moveTo(center.x - mouthHalfWidth, mouthY)
                quadraticTo(center.x, mouthY + radius * 0.28f, center.x + mouthHalfWidth, mouthY)
            }
            drawPath(path, faceColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        }
        2 -> {
            // Drooping eyes
            drawOval(faceColor, Offset(leftEye.x - eyeRadius, leftEye.y - eyeRadius * 0.5f), Size(eyeRadius * 2, eyeRadius))
            drawOval(faceColor, Offset(rightEye.x - eyeRadius, rightEye.y - eyeRadius * 0.5f), Size(eyeRadius * 2, eyeRadius))
            // Slight frown
            val path = Path().apply {
                moveTo(center.x - mouthHalfWidth * 0.7f, mouthY)
                quadraticTo(center.x, mouthY + radius * 0.12f, center.x + mouthHalfWidth * 0.7f, mouthY)
            }
            drawPath(path, faceColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        }
        3 -> {
            // Round eyes + flat mouth
            drawCircle(faceColor, eyeRadius, leftEye)
            drawCircle(faceColor, eyeRadius, rightEye)
            drawLine(faceColor, Offset(center.x - mouthHalfWidth * 0.6f, mouthY), Offset(center.x + mouthHalfWidth * 0.6f, mouthY), strokeWidth, StrokeCap.Round)
        }
        4 -> {
            // Open eyes + smile
            drawCircle(faceColor, eyeRadius * 1.2f, leftEye)
            drawCircle(faceColor, eyeRadius * 1.2f, rightEye)
            val path = Path().apply {
                moveTo(center.x - mouthHalfWidth, mouthY)
                quadraticTo(center.x, mouthY - radius * 0.18f, center.x + mouthHalfWidth, mouthY)
            }
            drawPath(path, faceColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
        }
        5 -> {
            // Wide eyes + big smile + sparkles
            drawCircle(faceColor, eyeRadius * 1.4f, leftEye)
            drawCircle(faceColor, eyeRadius * 1.4f, rightEye)
            val path = Path().apply {
                moveTo(center.x - mouthHalfWidth, mouthY)
                quadraticTo(center.x, mouthY - radius * 0.3f, center.x + mouthHalfWidth, mouthY)
            }
            drawPath(path, faceColor, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            // Sparkle dots
            val sparkleColor = Color(0xFFFFD700)
            drawCircle(sparkleColor, eyeRadius * 0.4f, Offset(center.x + radius * 0.6f, center.y - radius * 0.4f))
            drawCircle(sparkleColor, eyeRadius * 0.3f, Offset(center.x - radius * 0.65f, center.y - radius * 0.5f))
        }
        else -> {
            // Default: neutral
            drawCircle(faceColor, eyeRadius, leftEye)
            drawCircle(faceColor, eyeRadius, rightEye)
            drawLine(faceColor, Offset(center.x - mouthHalfWidth * 0.6f, mouthY), Offset(center.x + mouthHalfWidth * 0.6f, mouthY), strokeWidth, StrokeCap.Round)
        }
    }
}

fun DrawScope.drawHibernationOverlay() {
    // Semi-transparent grey wash
    drawRect(Color(0xFF808080).copy(alpha = 0.35f))

    // Cobweb in top-left corner
    val webColor = Color(0xFFBBBBBB).copy(alpha = 0.5f)
    val webStroke = Stroke(1.5f)
    val origin = Offset(0f, 0f)
    val radii = listOf(40f, 80f, 120f)
    val angles = listOf(0f, 30f, 60f, 90f)

    angles.forEach { angle ->
        val rad = Math.toRadians(angle.toDouble())
        val far = 150f
        drawLine(webColor, origin, Offset((far * Math.cos(rad)).toFloat(), (far * Math.sin(rad)).toFloat()), 1.5f)
    }
    radii.forEach { r ->
        drawArc(webColor, startAngle = 0f, sweepAngle = 90f, useCenter = false, topLeft = Offset(-r, -r), size = Size(r * 2, r * 2), style = webStroke)
    }

    // Cobweb in bottom-right corner
    val bx = size.width
    val by = size.height
    angles.forEach { angle ->
        val rad = Math.toRadians((180f + angle).toDouble())
        val far = 150f
        drawLine(webColor, Offset(bx, by), Offset(bx + (far * Math.cos(rad)).toFloat(), by + (far * Math.sin(rad)).toFloat()), 1.5f)
    }
    radii.forEach { r ->
        drawArc(webColor, startAngle = 180f, sweepAngle = 90f, useCenter = false, topLeft = Offset(bx - r, by - r), size = Size(r * 2, r * 2), style = webStroke)
    }
}
