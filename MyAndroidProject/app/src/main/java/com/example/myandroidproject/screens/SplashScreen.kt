package com.example.myandroidproject.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBackground = Color(0xFFEAF4FF)
private val TitleBlue = Color(0xFF1565C0)
private val WaterBlue = Color(0xFF29B6F6)

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val titleAlpha = remember { Animatable(0f) }
    val messageAlpha = remember { Animatable(0f) }
    val waterLevel = remember { Animatable(0f) }
    val bubbleProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )

        delay(300)

        messageAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )

        delay(200)

        waterLevel.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500)
        )

        bubbleProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SplashBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Habit Planner & Tracker",
            style = MaterialTheme.typography.headlineMedium,
            color = TitleBlue,
            modifier = Modifier.alpha(titleAlpha.value)
        )

        Spacer(modifier = Modifier.height(16.dp))

        WaterText(
            text = "Stay hydrated",
            waterLevel = waterLevel.value,
            bubbleProgress = bubbleProgress.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .alpha(messageAlpha.value)
        )
    }
}

@Composable
private fun WaterText(
    text: String,
    waterLevel: Float,
    bubbleProgress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val paint = android.graphics.Paint(
            android.graphics.Paint.ANTI_ALIAS_FLAG
        ).apply {
            textAlign = android.graphics.Paint.Align.LEFT
            textSize = 42.sp.toPx()
            typeface = android.graphics.Typeface.create(
                "sans-serif-medium",
                android.graphics.Typeface.BOLD
            )
        }

        val textWidth = paint.measureText(text)
        val centerX = (size.width - textWidth) / 2f
        val baseline = size.height / 2f -
                (paint.ascent() + paint.descent()) / 2f

        val textPath = android.graphics.Path()

        paint.getTextPath(
            text,
            0,
            text.length,
            centerX,
            baseline,
            textPath
        )

        // Draw the complete phrase in dark blue.
        paint.color = android.graphics.Color.rgb(21, 101, 192)

        drawContext.canvas.nativeCanvas.drawPath(
            textPath,
            paint
        )

        // Calculate the rising water level.
        val waterTop = size.height * (1f - waterLevel)

        val canvas = drawContext.canvas.nativeCanvas
        val saveCount = canvas.save()

        // Restrict the water to the actual letter shapes.
        canvas.clipPath(textPath)

        val waterPaint = android.graphics.Paint(
            android.graphics.Paint.ANTI_ALIAS_FLAG
        ).apply {
            color = android.graphics.Color.rgb(41, 182, 246)
        }

        canvas.drawRect(
            0f,
            waterTop,
            size.width,
            size.height,
            waterPaint
        )

        // Add bubbles inside the water-filled letters.
        if (waterLevel > 0.05f) {
            val bubblePaint = android.graphics.Paint(
                android.graphics.Paint.ANTI_ALIAS_FLAG
            ).apply {
                color = android.graphics.Color.WHITE
                alpha = 210
            }

            val bubblePositions = listOf(
                0.15f to 0.25f,
                0.30f to 0.60f,
                0.45f to 0.35f,
                0.60f to 0.70f,
                0.75f to 0.45f,
                0.88f to 0.30f
            )

            bubblePositions.forEachIndexed { index, position ->
                val phase = bubbleProgress * 2f + index * 0.7f

                val x = centerX + textWidth * position.first
                val y = waterTop +
                        (size.height - waterTop) *
                        ((position.second - phase * 0.15f + 1f) % 1f)

                if (y >= waterTop && y <= size.height) {
                    canvas.drawCircle(
                        x,
                        y,
                        3.dp.toPx(),
                        bubblePaint
                    )
                }
            }
        }

        canvas.restoreToCount(saveCount)
    }
}