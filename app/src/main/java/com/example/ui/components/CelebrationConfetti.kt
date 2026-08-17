package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
  val startX: Float,
  val speed: Float,
  val rotationSpeed: Float,
  val size: Float,
  val color: Color,
  val shapeType: Int // 0: rect, 1: circle, 2: star
)

/**
 * Efecto festivo de confeti brillante cayendo suavemente
 */
@Composable
fun CelebrationConfetti(
  modifier: Modifier = Modifier
) {
  val confettiColors = listOf(
    Color(0xFFFF1744),
    Color(0xFFFF9100),
    Color(0xFFFFEA00),
    Color(0xFF00E676),
    Color(0xFF00E5FF),
    Color(0xFFD500F9),
    Color(0xFFFF4081)
  )

  val particles = remember {
    List(45) {
      ConfettiParticle(
        startX = Random.nextFloat(),
        speed = 0.6f + Random.nextFloat() * 0.8f,
        rotationSpeed = (Random.nextFloat() - 0.5f) * 4f,
        size = 8f + Random.nextFloat() * 10f,
        color = confettiColors[Random.nextInt(confettiColors.size)],
        shapeType = Random.nextInt(3)
      )
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "confetti_anim")
  val progress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(4500, easing = LinearEasing)
    ),
    label = "fall_progress"
  )

  Canvas(modifier = modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    particles.forEach { p ->
      val currentY = ((progress * p.speed + p.startX) % 1f) * (h + 50f) - 20f
      val currentX = (p.startX * w) + (kotlin.math.sin(progress * 8f + p.startX * 10f) * 25f)
      val rotation = (progress * 360f * p.rotationSpeed) % 360f

      rotate(rotation, pivot = Offset(currentX, currentY)) {
        when (p.shapeType) {
          0 -> {
            drawRect(
              color = p.color,
              topLeft = Offset(currentX - p.size / 2, currentY - p.size / 2),
              size = Size(p.size, p.size * 0.6f)
            )
          }
          1 -> {
            drawCircle(
              color = p.color,
              radius = p.size / 2,
              center = Offset(currentX, currentY)
            )
          }
          else -> {
            drawRect(
              color = p.color,
              topLeft = Offset(currentX - p.size * 0.4f, currentY - p.size * 0.4f),
              size = Size(p.size * 0.8f, p.size * 0.8f)
            )
          }
        }
      }
    }
  }
}
