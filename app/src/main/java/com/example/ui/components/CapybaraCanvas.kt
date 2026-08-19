package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.CapybaraBackground
import com.example.model.CapybaraColor
import com.example.model.CapybaraDrink
import com.example.model.CapybaraGlasses
import com.example.model.CapybaraHat
import com.example.model.CapybaraShirt
import com.example.model.CapybaraShoes
import com.example.model.CapybaraState
import com.example.model.CapybaraVehicle
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lienzo principal del capibara con soporte de sprites PNG para el cuerpo (según CapybaraColor)
 * y accesorios vectoriales dibujados encima adaptados al espacio normalizado 512x512.
 */
@Composable
fun CapybaraCanvas(
  state: CapybaraState,
  modifier: Modifier = Modifier,
  onPet: () -> Unit = {}
) {
  // Animación continua de respiración sutil y relajada
  val infiniteTransition = rememberInfiniteTransition(label = "capy_anim")
  val breathOffset by infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath"
  )

  val waveAnim by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave"
  )

  // Carga del sprite PNG correspondiente al color del Capibara
  val spriteRes = when (state.color) {
    CapybaraColor.CLASSIC -> R.drawable.capybara_classic
    CapybaraColor.CHOCOLATE -> R.drawable.capybara_chocolate
    CapybaraColor.ROSE_PASTEL -> R.drawable.capybara_rose
    CapybaraColor.MINT -> R.drawable.capybara_mint
    CapybaraColor.LAVENDER -> R.drawable.capybara_lavender
    CapybaraColor.SUNNY -> R.drawable.capybara_sunny
    CapybaraColor.SKY_BLUE -> R.drawable.capybara_sky
    CapybaraColor.CARAMEL -> R.drawable.capybara_caramel
    CapybaraColor.FLAMINGO_PINK -> R.drawable.capybara_flamingo
    CapybaraColor.JUNGLE_GREEN -> R.drawable.capybara_jungle
  }
  val capyPainter = painterResource(id = spriteRes)

  val interactionSource = remember { MutableInteractionSource() }

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onPet
      )
  ) {
    val width = size.width
    val height = size.height
    if (width <= 0f || height <= 0f) return@Canvas

    // 1. Dibujar el fondo seleccionado
    drawBackgroundScene(state.background, width, height, waveAnim)

    // 2. Escalar para ocupar ~70% del alto del lienzo y centrar
    val spriteSize = height * 0.70f
    val centerX = width * 0.50f
    val centerY = height * 0.53f + breathOffset
    val spriteLeft = centerX - spriteSize / 2f
    val spriteTop = centerY - spriteSize / 2f

    // 3. Dibujar sombra suave en el suelo bajo el sprite
    drawOval(
      color = Color(0x35000000),
      topLeft = Offset(centerX - spriteSize * 0.38f, centerY + spriteSize * 0.40f),
      size = Size(spriteSize * 0.76f, spriteSize * 0.12f)
    )

    // 4. Dibujar al Capibara con sus accesorios en el espacio normalizado 512x512
    translate(left = spriteLeft, top = spriteTop) {
      scale(scale = spriteSize / 512f, pivot = Offset.Zero) {
        drawCapybaraWithSprite(
          painter = capyPainter,
          state = state
        )
      }
    }
  }
}

// -------------------------------------------------------------
// DIBUJO DEL PERSONAJE CON SPRITE PNG Y ACCESORIOS (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawCapybaraWithSprite(
  painter: Painter,
  state: CapybaraState
) {
  val outlineColor = Color(0xFF24140E)
  val strokeW = 5f

  // 1. Vehículo de fondo (detrás del capibara)
  if (state.vehicle != CapybaraVehicle.NONE) {
    drawVehicle512(state.vehicle, outlineColor, strokeW)
  }

  // 2. Capa de superhéroe trasera
  if (state.shirt == CapybaraShirt.SUPERHERO) {
    drawCapeBack512(outlineColor, strokeW)
  }

  // 3. SPRITE PNG DEL CAPIBARA (512x512)
  with(painter) {
    draw(Size(512f, 512f))
  }

  // 4. Ropa / Vestimenta vectorial adaptada
  if (state.shirt != CapybaraShirt.NONE) {
    drawShirt512(state.shirt, outlineColor, strokeW)
  }

  // 5. Zapatos en las patitas delanteras
  if (state.shoes != CapybaraShoes.NONE) {
    drawShoes512(state.shoes, outlineColor, strokeW)
  }

  // 6. Gafas sobre los ojos
  if (state.glasses != CapybaraGlasses.NONE) {
    drawGlasses512(state.glasses, outlineColor, strokeW)
  }

  // 7. Gorro / Accesorio sobre la cabeza
  if (state.hat != CapybaraHat.NONE) {
    drawHat512(state.hat, outlineColor, strokeW)
  }

  // 8. Bebida refrescante al lado
  if (state.drink != CapybaraDrink.NONE) {
    drawDrink512(state.drink, outlineColor, strokeW)
  }

  // 9. Reacciones y efectos de alegría
  if (state.isHappy || state.happinessCount > 1) {
    drawHappySparks512()
  }
}

// -------------------------------------------------------------
// VESTIMENTA (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawShirt512(
  shirt: CapybaraShirt,
  outlineColor: Color,
  strokeW: Float
) {
  val shirtPath = Path().apply {
    moveTo(170f, 275f)
    cubicTo(205f, 288f, 307f, 288f, 342f, 275f)
    cubicTo(382f, 320f, 390f, 400f, 375f, 450f)
    cubicTo(320f, 472f, 192f, 472f, 137f, 450f)
    cubicTo(122f, 400f, 130f, 320f, 170f, 275f)
    close()
  }

  clipPath(shirtPath) {
    when (shirt) {
      CapybaraShirt.NONE -> {}

      CapybaraShirt.STRIPED -> {
        drawRect(Color.White, topLeft = Offset(110f, 260f), size = Size(292f, 220f))
        for (i in 0..8) {
          val y = 270f + (i * 22f)
          drawRect(Color(0xFF1E88E5), topLeft = Offset(110f, y), size = Size(292f, 11f))
        }
        // Cuello rojo
        drawOval(Color(0xFFE53935), topLeft = Offset(216f, 268f), size = Size(80f, 22f))
      }

      CapybaraShirt.HEARTS -> {
        drawRect(Color(0xFFFF4081), topLeft = Offset(110f, 260f), size = Size(292f, 220f))
        val hearts = listOf(
          Offset(180f, 320f),
          Offset(256f, 310f),
          Offset(332f, 320f),
          Offset(210f, 370f),
          Offset(302f, 370f),
          Offset(180f, 420f),
          Offset(256f, 425f),
          Offset(332f, 420f)
        )
        hearts.forEach { drawMiniHeart(it, Color.White, 9f) }
        drawRect(Color(0xFFFF80AB), topLeft = Offset(110f, 435f), size = Size(292f, 35f))
      }

      CapybaraShirt.RAINBOW_SWEATER -> {
        val rainbowColors = listOf(
          Color(0xFFFF5252),
          Color(0xFFFF9800),
          Color(0xFFFFEB3B),
          Color(0xFF4CAF50),
          Color(0xFF29B6F6),
          Color(0xFFAB47BC),
          Color(0xFFFF4081)
        )
        rainbowColors.forEachIndexed { index, color ->
          drawRect(color, topLeft = Offset(110f, 265f + index * 26f), size = Size(292f, 26f))
        }
      }

      CapybaraShirt.SUPERHERO -> {
        drawRect(Color(0xFF1565C0), topLeft = Offset(110f, 260f), size = Size(292f, 220f))
        drawRect(Color(0xFFFFD700), topLeft = Offset(110f, 395f), size = Size(292f, 16f))
        drawStar(Offset(256f, 340f), 24f, Color(0xFFFFD700))
      }

      CapybaraShirt.HAWAIIAN -> {
        drawRect(Color(0xFF00B4D8), topLeft = Offset(110f, 260f), size = Size(292f, 220f))
        val flowerPoints = listOf(
          Offset(180f, 320f),
          Offset(260f, 310f),
          Offset(335f, 330f),
          Offset(200f, 385f),
          Offset(310f, 390f),
          Offset(256f, 430f)
        )
        flowerPoints.forEach { drawMiniFlower(it, Color(0xFFFF5400), Color(0xFFFFDD00)) }
      }

      CapybaraShirt.DINOSAUR -> {
        drawRect(Color(0xFF43A047), topLeft = Offset(110f, 260f), size = Size(292f, 220f))
        drawOval(Color(0xFF81C784), topLeft = Offset(206f, 320f), size = Size(100f, 120f))
      }
    }
  }

  // Contorno de la ropa
  drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

  // Púas de dinosaurio en los costados
  if (shirt == CapybaraShirt.DINOSAUR) {
    for (i in 0..3) {
      val spikePath = Path().apply {
        val sY = 285f + (i * 38f)
        moveTo(145f, sY)
        lineTo(115f, sY + 12f)
        lineTo(140f, sY + 28f)
        close()
      }
      drawPath(spikePath, Color(0xFFFFEB3B))
      drawPath(spikePath, outlineColor, style = Stroke(width = 3.5f))
    }
  }
}

// Capa de superhéroe trasera
private fun DrawScope.drawCapeBack512(outline: Color, strokeW: Float) {
  val capePath = Path().apply {
    moveTo(175f, 270f)
    cubicTo(80f, 320f, 60f, 440f, 90f, 475f)
    cubicTo(140f, 485f, 372f, 485f, 422f, 475f)
    cubicTo(452f, 440f, 432f, 320f, 337f, 270f)
    close()
  }
  drawPath(capePath, Color(0xFFD32F2F))
  drawPath(capePath, outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))
}

// -------------------------------------------------------------
// GAFAS (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawGlasses512(glasses: CapybaraGlasses, outline: Color, strokeW: Float) {
  val leftEye = Offset(195f, 185f)
  val rightEye = Offset(317f, 185f)

  when (glasses) {
    CapybaraGlasses.NONE -> {}

    CapybaraGlasses.SUNGLASSES -> {
      val leftLens = Path().apply {
        addRoundRect(RoundRect(Rect(leftEye.x - 30f, leftEye.y - 20f, leftEye.x + 30f, leftEye.y + 22f), CornerRadius(12f, 12f)))
      }
      val rightLens = Path().apply {
        addRoundRect(RoundRect(Rect(rightEye.x - 30f, rightEye.y - 20f, rightEye.x + 30f, rightEye.y + 22f), CornerRadius(12f, 12f)))
      }
      drawPath(leftLens, Color(0xFF212121))
      drawPath(rightLens, Color(0xFF212121))
      // Reflejos
      drawLine(Color.White.copy(alpha = 0.65f), Offset(leftEye.x - 15f, leftEye.y - 12f), Offset(leftEye.x, leftEye.y + 12f), strokeWidth = 3.5f)
      drawLine(Color.White.copy(alpha = 0.65f), Offset(rightEye.x - 15f, rightEye.y - 12f), Offset(rightEye.x, rightEye.y + 12f), strokeWidth = 3.5f)
      drawPath(leftLens, Color.Black, style = Stroke(width = 4f))
      drawPath(rightLens, Color.Black, style = Stroke(width = 4f))
      // Puente central
      drawLine(Color.Black, Offset(leftEye.x + 30f, leftEye.y), Offset(rightEye.x - 30f, rightEye.y), strokeWidth = 5f, cap = StrokeCap.Round)
      // Patillas hacia los lados
      drawLine(Color.Black, Offset(leftEye.x - 30f, leftEye.y - 5f), Offset(130f, leftEye.y - 15f), strokeWidth = 4f)
      drawLine(Color.Black, Offset(rightEye.x + 30f, rightEye.y - 5f), Offset(382f, rightEye.y - 15f), strokeWidth = 4f)
    }

    CapybaraGlasses.HEART_GLASSES -> {
      drawHeartGlassesLens(leftEye, scale = 1.8f)
      drawHeartGlassesLens(rightEye, scale = 1.8f)
      drawLine(Color(0xFFFF1493), Offset(leftEye.x + 24f, leftEye.y), Offset(rightEye.x - 24f, rightEye.y), strokeWidth = 5f, cap = StrokeCap.Round)
      drawLine(Color(0xFFFF1493), Offset(leftEye.x - 25f, leftEye.y - 5f), Offset(130f, leftEye.y - 15f), strokeWidth = 4f)
      drawLine(Color(0xFFFF1493), Offset(rightEye.x + 25f, rightEye.y - 5f), Offset(382f, rightEye.y - 15f), strokeWidth = 4f)
    }

    CapybaraGlasses.GOLD_ROUND -> {
      drawCircle(Color(0xFFFFD700).copy(alpha = 0.35f), radius = 28f, center = leftEye)
      drawCircle(Color(0xFFFFD700).copy(alpha = 0.35f), radius = 28f, center = rightEye)
      drawCircle(Color(0xFFFFD700), radius = 28f, center = leftEye, style = Stroke(width = 4.5f))
      drawCircle(Color(0xFFFFD700), radius = 28f, center = rightEye, style = Stroke(width = 4.5f))
      drawLine(Color(0xFFFFD700), Offset(leftEye.x + 28f, leftEye.y), Offset(rightEye.x - 28f, rightEye.y), strokeWidth = 4.5f)
      drawLine(Color(0xFFFFD700), Offset(leftEye.x - 28f, leftEye.y - 5f), Offset(130f, leftEye.y - 15f), strokeWidth = 4f)
      drawLine(Color(0xFFFFD700), Offset(rightEye.x + 28f, rightEye.y - 5f), Offset(382f, rightEye.y - 15f), strokeWidth = 4f)
    }

    CapybaraGlasses.STAR_GLASSES -> {
      drawStar(leftEye, 32f, Color(0xFFFFEB3B).copy(alpha = 0.85f))
      drawStar(rightEye, 32f, Color(0xFFFFEB3B).copy(alpha = 0.85f))
      drawStar(leftEye, 32f, Color(0xFFF57F17), strokeWidth = 4f)
      drawStar(rightEye, 32f, Color(0xFFF57F17), strokeWidth = 4f)
      drawLine(Color(0xFFF57F17), Offset(leftEye.x + 26f, leftEye.y), Offset(rightEye.x - 26f, rightEye.y), strokeWidth = 5f)
      drawLine(Color(0xFFF57F17), Offset(leftEye.x - 28f, leftEye.y - 5f), Offset(130f, leftEye.y - 15f), strokeWidth = 4f)
      drawLine(Color(0xFFF57F17), Offset(rightEye.x + 28f, rightEye.y - 5f), Offset(382f, rightEye.y - 15f), strokeWidth = 4f)
    }

    CapybaraGlasses.SNORKEL -> {
      val maskRect = Rect(leftEye.x - 35f, leftEye.y - 24f, rightEye.x + 35f, rightEye.y + 26f)
      drawRoundRect(
        color = Color(0xFF00E5FF).copy(alpha = 0.50f),
        topLeft = Offset(maskRect.left, maskRect.top),
        size = Size(maskRect.width, maskRect.height),
        cornerRadius = CornerRadius(18f, 18f)
      )
      drawRoundRect(
        color = Color(0xFFFF3D00),
        topLeft = Offset(maskRect.left, maskRect.top),
        size = Size(maskRect.width, maskRect.height),
        cornerRadius = CornerRadius(18f, 18f),
        style = Stroke(width = 5f)
      )
      // Tubo de snorkel con boquilla
      val tubePath = Path().apply {
        moveTo(rightEye.x + 35f, rightEye.y + 5f)
        cubicTo(rightEye.x + 65f, rightEye.y + 5f, rightEye.x + 70f, rightEye.y - 85f, rightEye.x + 55f, rightEye.y - 105f)
      }
      drawPath(tubePath, Color(0xFFFFEA00), style = Stroke(width = 9f, cap = StrokeCap.Round))
      drawPath(tubePath, Color(0xFF2C1810), style = Stroke(width = 2.5f))
    }

    CapybaraGlasses.BUTTERFLY_GLASSES -> {
      drawButterflyWingLens(leftEye, isLeft = true)
      drawButterflyWingLens(rightEye, isLeft = false)
      drawLine(Color(0xFF7B1FA2), Offset(leftEye.x + 20f, leftEye.y), Offset(rightEye.x - 20f, rightEye.y), strokeWidth = 4.5f)
      drawLine(Color(0xFF7B1FA2), Offset(leftEye.x - 26f, leftEye.y - 5f), Offset(130f, leftEye.y - 15f), strokeWidth = 3.5f)
      drawLine(Color(0xFF7B1FA2), Offset(rightEye.x + 26f, rightEye.y - 5f), Offset(382f, rightEye.y - 15f), strokeWidth = 3.5f)
    }

    CapybaraGlasses.MOON_GLASSES -> {
      drawMoonGlassesLens(leftEye, isLeft = true)
      drawMoonGlassesLens(rightEye, isLeft = false)
      drawLine(Color(0xFFFFD700), Offset(leftEye.x + 22f, leftEye.y), Offset(rightEye.x - 22f, rightEye.y), strokeWidth = 4.5f)
      drawLine(Color(0xFFFFD700), Offset(leftEye.x - 26f, leftEye.y - 5f), Offset(130f, leftEye.y - 15f), strokeWidth = 3.5f)
      drawLine(Color(0xFFFFD700), Offset(rightEye.x + 26f, rightEye.y - 5f), Offset(382f, rightEye.y - 15f), strokeWidth = 3.5f)
    }
  }
}

private fun DrawScope.drawHeartGlassesLens(center: Offset, scale: Float = 1.0f) {
  val path = Path().apply {
    val s = scale
    moveTo(center.x, center.y + 14f * s)
    cubicTo(center.x - 20f * s, center.y + 2f * s, center.x - 22f * s, center.y - 18f * s, center.x, center.y - 4f * s)
    cubicTo(center.x + 22f * s, center.y - 18f * s, center.x + 20f * s, center.y + 2f * s, center.x, center.y + 14f * s)
    close()
  }
  drawPath(path, Color(0xFFFF4081).copy(alpha = 0.55f))
  drawPath(path, Color(0xFFFF1493), style = Stroke(width = 4.5f))
}

private fun DrawScope.drawButterflyWingLens(center: Offset, isLeft: Boolean) {
  val mult = if (isLeft) -1f else 1f
  val wingPath = Path().apply {
    moveTo(center.x, center.y)
    cubicTo(center.x + 40f * mult, center.y - 35f, center.x + 50f * mult, center.y + 10f, center.x + 30f * mult, center.y + 30f)
    cubicTo(center.x + 10f * mult, center.y + 25f, center.x - 10f * mult, center.y + 15f, center.x, center.y)
    close()
  }
  drawPath(wingPath, Color(0xFFE040FB).copy(alpha = 0.55f))
  drawPath(wingPath, Color(0xFF7B1FA2), style = Stroke(width = 4f))
  drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(center.x + 20f * mult, center.y - 10f))
}

private fun DrawScope.drawMoonGlassesLens(center: Offset, isLeft: Boolean) {
  val mult = if (isLeft) 1f else -1f
  val moonPath = Path().apply {
    moveTo(center.x + 24f * mult, center.y - 24f)
    cubicTo(center.x - 24f * mult, center.y - 24f, center.x - 24f * mult, center.y + 24f, center.x + 24f * mult, center.y + 24f)
    cubicTo(center.x + 2f * mult, center.y + 14f, center.x + 2f * mult, center.y - 14f, center.x + 24f * mult, center.y - 24f)
    close()
  }
  drawPath(moonPath, Color(0xFF80D8FF).copy(alpha = 0.55f))
  drawPath(moonPath, Color(0xFFFFD700), style = Stroke(width = 4f))
  drawStar(Offset(center.x + 28f * mult, center.y - 12f), 8f, Color(0xFFFFD700))
}

// -------------------------------------------------------------
// GORROS Y ACCESORIOS SUPERIORES (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawHat512(hat: CapybaraHat, outline: Color, strokeW: Float) {
  val headTop = Offset(256f, 108f)

  when (hat) {
    CapybaraHat.NONE -> {}

    CapybaraHat.ORANGE -> {
      // Mandarina kawaii en la cabeza
      val orangeCenter = Offset(headTop.x, headTop.y - 24f)
      drawCircle(Color(0x33000000), radius = 28f, center = Offset(orangeCenter.x, orangeCenter.y + 8f))
      drawCircle(Color(0xFFFF9100), radius = 30f, center = orangeCenter)
      drawCircle(Color(0xFFFFAB40), radius = 22f, center = Offset(orangeCenter.x - 4f, orangeCenter.y - 4f))
      drawCircle(outline, radius = 30f, center = orangeCenter, style = Stroke(width = 4.5f))
      // Tallo y hoja
      drawLine(Color(0xFF5D4037), orangeCenter, Offset(orangeCenter.x, orangeCenter.y - 42f), strokeWidth = 5f, cap = StrokeCap.Round)
      val leaf = Path().apply {
        moveTo(orangeCenter.x, orangeCenter.y - 38f)
        quadraticBezierTo(orangeCenter.x + 24f, orangeCenter.y - 48f, orangeCenter.x + 30f, orangeCenter.y - 32f)
        quadraticBezierTo(orangeCenter.x + 15f, orangeCenter.y - 26f, orangeCenter.x, orangeCenter.y - 38f)
        close()
      }
      drawPath(leaf, Color(0xFF4CAF50))
      drawPath(leaf, outline, style = Stroke(width = 3.5f))
    }

    CapybaraHat.FLOWER -> {
      // Flor hawaiana tropical rosada
      val flowerPos = Offset(headTop.x - 55f, headTop.y + 5f)
      for (i in 0 until 5) {
        val angle = (i * 72f) * (Math.PI / 180f)
        val pX = flowerPos.x + cos(angle).toFloat() * 22f
        val pY = flowerPos.y + sin(angle).toFloat() * 22f
        drawCircle(Color(0xFFFF4081), radius = 16f, center = Offset(pX, pY))
        drawCircle(outline, radius = 16f, center = Offset(pX, pY), style = Stroke(width = 3.5f))
      }
      drawCircle(Color(0xFFFFEB3B), radius = 12f, center = flowerPos)
      drawCircle(outline, radius = 12f, center = flowerPos, style = Stroke(width = 3.5f))
    }

    CapybaraHat.CROWN -> {
      // Corona dorada con gemas
      val crownPath = Path().apply {
        moveTo(headTop.x - 48f, headTop.y + 8f)
        lineTo(headTop.x - 56f, headTop.y - 45f)
        lineTo(headTop.x - 20f, headTop.y - 20f)
        lineTo(headTop.x, headTop.y - 60f)
        lineTo(headTop.x + 20f, headTop.y - 20f)
        lineTo(headTop.x + 56f, headTop.y - 45f)
        lineTo(headTop.x + 48f, headTop.y + 8f)
        close()
      }
      drawPath(crownPath, Color(0xFFFFD700))
      drawPath(crownPath, outline, style = Stroke(width = 5f, join = StrokeJoin.Round))
      drawCircle(Color(0xFFE91E63), radius = 7f, center = Offset(headTop.x - 56f, headTop.y - 45f))
      drawCircle(Color(0xFF00E5FF), radius = 8f, center = Offset(headTop.x, headTop.y - 60f))
      drawCircle(Color(0xFF9C27B0), radius = 7f, center = Offset(headTop.x + 56f, headTop.y - 45f))
    }

    CapybaraHat.PARTY_HAT -> {
      // Gorro de fiesta cónico
      val conePath = Path().apply {
        moveTo(headTop.x - 38f, headTop.y + 8f)
        lineTo(headTop.x, headTop.y - 75f)
        lineTo(headTop.x + 38f, headTop.y + 8f)
        close()
      }
      drawPath(conePath, Color(0xFF00E676))
      clipPath(conePath) {
        drawLine(Color(0xFFFF1744), Offset(headTop.x - 55f, headTop.y - 12f), Offset(headTop.x + 55f, headTop.y - 38f), strokeWidth = 11f)
        drawLine(Color(0xFFFFD600), Offset(headTop.x - 55f, headTop.y - 38f), Offset(headTop.x + 55f, headTop.y - 64f), strokeWidth = 11f)
      }
      drawPath(conePath, outline, style = Stroke(width = 5f, join = StrokeJoin.Round))
      drawCircle(Color(0xFFFF1744), radius = 13f, center = Offset(headTop.x, headTop.y - 75f))
    }

    CapybaraHat.CAP -> {
      // Gorra deportiva roja
      val capDome = Path().apply {
        moveTo(headTop.x - 50f, headTop.y + 6f)
        cubicTo(headTop.x - 44f, headTop.y - 38f, headTop.x + 44f, headTop.y - 38f, headTop.x + 50f, headTop.y + 6f)
        close()
      }
      drawPath(capDome, Color(0xFFE53935))
      drawPath(capDome, outline, style = Stroke(width = 5f))
      val visor = Path().apply {
        moveTo(headTop.x - 45f, headTop.y + 6f)
        quadraticBezierTo(headTop.x, headTop.y + 24f, headTop.x + 45f, headTop.y + 6f)
        quadraticBezierTo(headTop.x, headTop.y + 12f, headTop.x - 45f, headTop.y + 6f)
        close()
      }
      drawPath(visor, Color(0xFFC62828))
      drawPath(visor, outline, style = Stroke(width = 4.5f))
      drawCircle(Color(0xFFFFD54F), radius = 6f, center = Offset(headTop.x, headTop.y - 30f))
    }

    CapybaraHat.WIZARD -> {
      // Gorro de mago púrpura
      val brim = Path().apply {
        addOval(Rect(headTop.x - 65f, headTop.y - 8f, headTop.x + 65f, headTop.y + 16f))
      }
      val cone = Path().apply {
        moveTo(headTop.x - 46f, headTop.y)
        cubicTo(headTop.x - 28f, headTop.y - 60f, headTop.x - 15f, headTop.y - 90f, headTop.x + 30f, headTop.y - 105f)
        cubicTo(headTop.x + 12f, headTop.y - 78f, headTop.x + 30f, headTop.y - 46f, headTop.x + 46f, headTop.y)
        close()
      }
      drawPath(cone, Color(0xFF6200EA))
      drawPath(brim, Color(0xFF4A148C))
      drawPath(cone, outline, style = Stroke(width = 5f))
      drawPath(brim, outline, style = Stroke(width = 5f))
      drawCircle(Color(0xFFFFD600), radius = 12f, center = Offset(headTop.x + 3f, headTop.y - 44f))
      drawCircle(Color(0xFF6200EA), radius = 9f, center = Offset(headTop.x + 9f, headTop.y - 47f))
    }

    CapybaraHat.BERET -> {
      // Boina de artista
      val beretPath = Path().apply {
        addOval(Rect(headTop.x - 58f, headTop.y - 30f, headTop.x + 58f, headTop.y + 6f))
      }
      drawPath(beretPath, Color(0xFFD81B60))
      drawPath(beretPath, outline, style = Stroke(width = 5f))
      drawLine(Color(0xFF880E4F), Offset(headTop.x, headTop.y - 30f), Offset(headTop.x, headTop.y - 40f), strokeWidth = 5f, cap = StrokeCap.Round)
    }

    CapybaraHat.COWBOY -> {
      // Sombrero vaquero de cuero marrón
      val cowboyBrim = Path().apply {
        moveTo(headTop.x - 75f, headTop.y - 6f)
        cubicTo(headTop.x - 30f, headTop.y + 12f, headTop.x + 30f, headTop.y + 12f, headTop.x + 75f, headTop.y - 6f)
        cubicTo(headTop.x + 60f, headTop.y + 18f, headTop.x - 60f, headTop.y + 18f, headTop.x - 75f, headTop.y - 6f)
        close()
      }
      val cowboyCrown = Path().apply {
        moveTo(headTop.x - 36f, headTop.y + 3f)
        cubicTo(headTop.x - 34f, headTop.y - 45f, headTop.x - 18f, headTop.y - 64f, headTop.x, headTop.y - 54f)
        cubicTo(headTop.x + 18f, headTop.y - 64f, headTop.x + 34f, headTop.y - 45f, headTop.x + 36f, headTop.y + 3f)
        close()
      }
      drawPath(cowboyCrown, Color(0xFF795548))
      drawPath(cowboyCrown, outline, style = Stroke(width = 5f))
      drawPath(cowboyBrim, Color(0xFF8D6E63))
      drawPath(cowboyBrim, outline, style = Stroke(width = 5f))
      drawRect(Color(0xFF3E2723), topLeft = Offset(headTop.x - 34f, headTop.y - 12f), size = Size(68f, 11f))
      drawStar(Offset(headTop.x, headTop.y - 6f), 8f, Color(0xFFFFD700))
    }

    CapybaraHat.STAR_TIARA -> {
      // Tiara de estrellas dorada
      val tiaraBand = Path().apply {
        moveTo(headTop.x - 46f, headTop.y + 6f)
        cubicTo(headTop.x - 15f, headTop.y - 3f, headTop.x + 15f, headTop.y - 3f, headTop.x + 46f, headTop.y + 6f)
      }
      drawPath(tiaraBand, Color(0xFFFFD700), style = Stroke(width = 7f, cap = StrokeCap.Round))
      drawPath(tiaraBand, outline, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
      drawStar(Offset(headTop.x - 28f, headTop.y - 18f), 12f, Color(0xFFFFEB3B))
      drawStar(Offset(headTop.x, headTop.y - 30f), 18f, Color(0xFFFFD700))
      drawStar(Offset(headTop.x + 28f, headTop.y - 18f), 12f, Color(0xFFFFEB3B))
      drawCircle(Color(0xFF00E5FF), radius = 4.5f, center = Offset(headTop.x, headTop.y - 30f))
    }
  }
}

// -------------------------------------------------------------
// ZAPATOS (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawShoes512(shoes: CapybaraShoes, outline: Color, strokeW: Float) {
  val shoeColor = getShoeColor(shoes)
  val leftPos = Offset(175f, 442f)
  val rightPos = Offset(285f, 442f)

  drawSingleShoe512(leftPos, shoeColor, outline, shoes, strokeW, width = 52f)
  drawSingleShoe512(rightPos, shoeColor, outline, shoes, strokeW, width = 52f)
}

private fun DrawScope.drawSingleShoe512(
  pos: Offset,
  color: Color,
  outline: Color,
  shoes: CapybaraShoes,
  strokeW: Float,
  width: Float
) {
  val shoeBounds = Rect(pos.x, pos.y, pos.x + width, pos.y + 26f)
  val path = Path().apply {
    addRoundRect(
      RoundRect(
        shoeBounds,
        topLeft = CornerRadius(10f, 10f),
        topRight = CornerRadius(16f, 16f),
        bottomLeft = CornerRadius(12f, 12f),
        bottomRight = CornerRadius(12f, 12f)
      )
    )
  }
  drawPath(path, color)
  drawPath(path, outline, style = Stroke(width = strokeW))

  if (shoes == CapybaraShoes.SNEAKERS) {
    drawRect(Color.White, topLeft = Offset(pos.x, pos.y + 18f), size = Size(width, 8f))
    drawPath(path, outline, style = Stroke(width = strokeW))
  } else if (shoes == CapybaraShoes.ROLLER_SKATES) {
    drawCircle(Color(0xFF00E5FF), radius = 6f, center = Offset(pos.x + 8f, pos.y + 30f))
    drawCircle(Color(0xFFFFEA00), radius = 6f, center = Offset(pos.x + width - 8f, pos.y + 30f))
  } else if (shoes == CapybaraShoes.BEACH_SANDALS) {
    drawLine(Color(0xFFFF4081), Offset(pos.x + 4f, pos.y + 4f), Offset(pos.x + width * 0.5f, pos.y + 22f), strokeWidth = 5f, cap = StrokeCap.Round)
    drawLine(Color(0xFFFF4081), Offset(pos.x + width - 4f, pos.y + 4f), Offset(pos.x + width * 0.5f, pos.y + 22f), strokeWidth = 5f, cap = StrokeCap.Round)
  } else if (shoes == CapybaraShoes.SPACE_BOOTS) {
    drawRect(Color(0xFF90A4AE), topLeft = Offset(pos.x, pos.y + 16f), size = Size(width, 7f))
    drawCircle(Color(0xFF00E5FF), radius = 4f, center = Offset(pos.x + width * 0.5f, pos.y + 8f))
  }
}

private fun getShoeColor(shoes: CapybaraShoes): Color {
  return when (shoes) {
    CapybaraShoes.RAIN_BOOTS -> Color(0xFFFFEB3B)
    CapybaraShoes.SNEAKERS -> Color(0xFF00B0FF)
    CapybaraShoes.RED_BOOTS -> Color(0xFFE53935)
    CapybaraShoes.GOLD_SHOES -> Color(0xFFFFD700)
    CapybaraShoes.ROLLER_SKATES -> Color(0xFFFF4081)
    CapybaraShoes.BEACH_SANDALS -> Color(0xFF00E5FF)
    CapybaraShoes.SPACE_BOOTS -> Color(0xFFECEFF1)
    CapybaraShoes.NONE -> Color.Transparent
  }
}

// -------------------------------------------------------------
// BEBIDAS (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawDrink512(drink: CapybaraDrink, outline: Color, strokeW: Float) {
  val cx = 420f
  val cy = 410f

  when (drink) {
    CapybaraDrink.NONE -> {}

    CapybaraDrink.ORANGE_JUICE -> {
      drawOval(Color(0x28000000), topLeft = Offset(cx - 20f, cy + 32f), size = Size(40f, 15f))
      val glassPath = Path().apply {
        moveTo(cx - 16f, cy)
        lineTo(cx + 16f, cy)
        lineTo(cx + 12f, cy + 38f)
        lineTo(cx - 12f, cy + 38f)
        close()
      }
      drawPath(glassPath, Color(0xFFFF9800))
      drawPath(glassPath, outline, style = Stroke(width = strokeW * 0.8f))
      drawLine(Color(0xFFE53935), Offset(cx - 3f, cy + 24f), Offset(cx - 12f, cy - 22f), strokeWidth = 5f, cap = StrokeCap.Round)
      drawLine(Color.White, Offset(cx - 8f, cy - 10f), Offset(cx - 12f, cy - 22f), strokeWidth = 5f, cap = StrokeCap.Round)
      drawCircle(Color(0xFFFFB74D), radius = 11f, center = Offset(cx + 16f, cy))
      drawCircle(Color(0xFFFF9800), radius = 11f, center = Offset(cx + 16f, cy), style = Stroke(width = 2.5f))
    }

    CapybaraDrink.STRAWBERRY_SMOOTHIE -> {
      drawOval(Color(0x28000000), topLeft = Offset(cx - 20f, cy + 32f), size = Size(40f, 15f))
      val glassPath = Path().apply {
        moveTo(cx - 17f, cy)
        lineTo(cx + 17f, cy)
        lineTo(cx + 12f, cy + 38f)
        lineTo(cx - 12f, cy + 38f)
        close()
      }
      drawPath(glassPath, Color(0xFFFF80AB))
      drawPath(glassPath, outline, style = Stroke(width = strokeW * 0.8f))
      drawCircle(Color.White, radius = 14f, center = Offset(cx, cy - 4f))
      drawCircle(Color(0xFFFF1744), radius = 6f, center = Offset(cx + 4f, cy - 13f))
      drawLine(Color(0xFFFF4081), Offset(cx - 4f, cy + 16f), Offset(cx - 14f, cy - 22f), strokeWidth = 5f, cap = StrokeCap.Round)
    }

    CapybaraDrink.CHOCOLATE_MILK -> {
      drawOval(Color(0x28000000), topLeft = Offset(cx - 20f, cy + 32f), size = Size(40f, 15f))
      val glassPath = Path().apply {
        moveTo(cx - 16f, cy)
        lineTo(cx + 16f, cy)
        lineTo(cx + 12f, cy + 38f)
        lineTo(cx - 12f, cy + 38f)
        close()
      }
      drawPath(glassPath, Color(0xFF6D4C41))
      drawPath(glassPath, outline, style = Stroke(width = strokeW * 0.8f))
      drawCircle(Color(0xFFFFF8E1), radius = 12f, center = Offset(cx, cy - 4f))
      drawLine(Color(0xFF3E2723), Offset(cx - 6f, cy - 4f), Offset(cx + 6f, cy + 6f), strokeWidth = 4f)
      drawLine(Color(0xFFD7CCC8), Offset(cx - 3f, cy + 18f), Offset(cx - 12f, cy - 22f), strokeWidth = 6f, cap = StrokeCap.Round)
    }

    CapybaraDrink.LEMONADE -> {
      drawOval(Color(0x28000000), topLeft = Offset(cx - 20f, cy + 32f), size = Size(40f, 15f))
      val glassPath = Path().apply {
        moveTo(cx - 16f, cy)
        lineTo(cx + 16f, cy)
        lineTo(cx + 12f, cy + 38f)
        lineTo(cx - 12f, cy + 38f)
        close()
      }
      drawPath(glassPath, Color(0xFFFFEB3B).copy(alpha = 0.90f))
      drawPath(glassPath, outline, style = Stroke(width = strokeW * 0.8f))
      drawRect(Color.White.copy(alpha = 0.8f), topLeft = Offset(cx - 8f, cy + 6f), size = Size(8f, 8f))
      drawRect(Color.White.copy(alpha = 0.8f), topLeft = Offset(cx + 3f, cy + 12f), size = Size(8f, 8f))
      drawCircle(Color(0xFF4CAF50), radius = 6f, center = Offset(cx + 11f, cy - 3f))
      drawLine(Color(0xFF00E676), Offset(cx - 3f, cy + 22f), Offset(cx - 12f, cy - 22f), strokeWidth = 5f, cap = StrokeCap.Round)
    }

    CapybaraDrink.TROPICAL_COCO -> {
      drawOval(Color(0x28000000), topLeft = Offset(cx - 24f, cy + 28f), size = Size(48f, 16f))
      val cocoHalf = Path().apply {
        moveTo(cx - 22f, cy + 3f)
        cubicTo(cx - 25f, cy + 38f, cx + 25f, cy + 38f, cx + 22f, cy + 3f)
        close()
      }
      drawPath(cocoHalf, Color(0xFF5D4037))
      drawPath(cocoHalf, outline, style = Stroke(width = strokeW * 0.8f))
      drawOval(Color(0xFFFFF9E6), topLeft = Offset(cx - 20f, cy), size = Size(40f, 12f))
      drawOval(outline, topLeft = Offset(cx - 20f, cy), size = Size(40f, 12f), style = Stroke(width = 2.5f))
      val umbrella = Path().apply {
        moveTo(cx + 3f, cy - 22f)
        lineTo(cx + 28f, cy - 12f)
        lineTo(cx + 12f, cy - 3f)
        close()
      }
      drawPath(umbrella, Color(0xFFFF4081))
      drawLine(Color(0xFFFFEB3B), Offset(cx + 3f, cy - 22f), Offset(cx + 15f, cy + 3f), strokeWidth = 3f)
      val strawPath = Path().apply {
        moveTo(cx - 6f, cy + 10f)
        lineTo(cx - 12f, cy - 15f)
        lineTo(cx - 25f, cy - 22f)
      }
      drawPath(strawPath, Color(0xFF00E5FF), style = Stroke(width = 5f, cap = StrokeCap.Round))
      drawCircle(Color(0xFFFF5252), radius = 6f, center = Offset(cx - 15f, cy + 3f))
    }
  }
}

// -------------------------------------------------------------
// VEHÍCULOS (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawVehicle512(vehicle: CapybaraVehicle, outline: Color, strokeW: Float) {
  val cx = 256f
  val cy = 440f

  when (vehicle) {
    CapybaraVehicle.NONE -> {}

    CapybaraVehicle.BICYCLE -> {
      val wheelRadius = 22f
      val wheel1 = Offset(cx - 95f, cy + 15f)
      val wheel2 = Offset(cx + 95f, cy + 15f)
      drawCircle(Color(0xFF424242), radius = wheelRadius, center = wheel1, style = Stroke(width = 6f))
      drawCircle(Color(0xFF424242), radius = wheelRadius, center = wheel2, style = Stroke(width = 6f))
      drawCircle(Color(0xFFE0E0E0), radius = wheelRadius - 4f, center = wheel1)
      drawCircle(Color(0xFFE0E0E0), radius = wheelRadius - 4f, center = wheel2)
      val frameColor = Color(0xFF26A69A)
      val bottomBracket = Offset(cx, cy + 15f)
      val seatPos = Offset(cx - 40f, cy - 25f)
      val handlePos = Offset(cx + 55f, cy - 35f)
      drawLine(frameColor, wheel1, bottomBracket, strokeWidth = 5f)
      drawLine(frameColor, bottomBracket, seatPos, strokeWidth = 5f)
      drawLine(frameColor, bottomBracket, handlePos, strokeWidth = 5f)
      drawLine(frameColor, seatPos, handlePos, strokeWidth = 5f)
      drawLine(frameColor, handlePos, wheel2, strokeWidth = 5f)
      drawOval(Color(0xFF5D4037), topLeft = Offset(seatPos.x - 14f, seatPos.y - 6f), size = Size(28f, 10f))
      drawLine(Color(0xFF757575), Offset(handlePos.x - 8f, handlePos.y - 10f), Offset(handlePos.x + 10f, handlePos.y - 10f), strokeWidth = 5f, cap = StrokeCap.Round)
    }

    CapybaraVehicle.SCOOTER -> {
      val w1 = Offset(cx - 85f, cy + 22f)
      val w2 = Offset(cx + 85f, cy + 22f)
      drawCircle(Color(0xFF00E5FF), radius = 12f, center = w1)
      drawCircle(outline, radius = 12f, center = w1, style = Stroke(width = 3.5f))
      drawCircle(Color(0xFF00E5FF), radius = 12f, center = w2)
      drawCircle(outline, radius = 12f, center = w2, style = Stroke(width = 3.5f))
      val deck = Path().apply {
        addRoundRect(RoundRect(Rect(cx - 90f, cy + 12f, cx + 80f, cy + 22f), CornerRadius(5f, 5f)))
      }
      drawPath(deck, Color(0xFFFFD600))
      drawPath(deck, outline, style = Stroke(width = 3.5f))
      drawLine(Color(0xFFFF6D00), Offset(cx + 70f, cy + 14f), Offset(cx + 55f, cy - 45f), strokeWidth = 6f, cap = StrokeCap.Round)
      drawLine(Color(0xFFFFD600), Offset(cx + 38f, cy - 45f), Offset(cx + 72f, cy - 45f), strokeWidth = 6f, cap = StrokeCap.Round)
    }

    CapybaraVehicle.MOTORCYCLE -> {
      val mw1 = Offset(cx - 90f, cy + 18f)
      val mw2 = Offset(cx + 90f, cy + 18f)
      drawCircle(Color(0xFF37474F), radius = 18f, center = mw1)
      drawCircle(Color.White, radius = 9f, center = mw1)
      drawCircle(Color(0xFF37474F), radius = 18f, center = mw2)
      drawCircle(Color.White, radius = 9f, center = mw2)
      val bodyPath = Path().apply {
        moveTo(cx - 100f, cy + 12f)
        cubicTo(cx - 105f, cy - 25f, cx - 25f, cy - 25f, cx - 5f, cy + 2f)
        lineTo(cx + 35f, cy + 8f)
        cubicTo(cx + 50f, cy - 20f, cx + 85f, cy - 25f, cx + 85f, cy + 12f)
        close()
      }
      drawPath(bodyPath, Color(0xFF4DD0E1))
      drawPath(bodyPath, outline, style = Stroke(width = 4.5f))
      drawRoundRect(Color(0xFF4E342E), topLeft = Offset(cx - 90f, cy - 25f), size = Size(75f, 14f), cornerRadius = CornerRadius(6f, 6f))
      drawCircle(Color(0xFFFFF59D), radius = 8f, center = Offset(cx + 90f, cy - 20f))
      drawCircle(outline, radius = 8f, center = Offset(cx + 90f, cy - 20f), style = Stroke(width = 3f))
    }

    CapybaraVehicle.CAR -> {
      val cw1 = Offset(cx - 95f, cy + 20f)
      val cw2 = Offset(cx + 95f, cy + 20f)
      drawCircle(Color(0xFF212121), radius = 16f, center = cw1)
      drawCircle(Color(0xFFECEFF1), radius = 7f, center = cw1)
      drawCircle(Color(0xFF212121), radius = 16f, center = cw2)
      drawCircle(Color(0xFFECEFF1), radius = 7f, center = cw2)
      val carBody = Path().apply {
        moveTo(cx - 125f, cy + 16f)
        cubicTo(cx - 130f, cy - 15f, cx - 75f, cy - 20f, cx - 45f, cy - 20f)
        lineTo(cx + 30f, cy - 20f)
        cubicTo(cx + 70f, cy - 20f, cx + 120f, cy - 10f, cx + 125f, cy + 16f)
        close()
      }
      drawPath(carBody, Color(0xFFE53935))
      drawPath(carBody, outline, style = Stroke(width = 4.5f))
      val windshield = Path().apply {
        moveTo(cx + 10f, cy - 20f)
        lineTo(cx + 50f, cy - 20f)
        lineTo(cx + 35f, cy - 55f)
        lineTo(cx + 5f, cy - 55f)
        close()
      }
      drawPath(windshield, Color(0xFF80D8FF).copy(alpha = 0.65f))
      drawPath(windshield, outline, style = Stroke(width = 3f))
      drawCircle(Color(0xFFFFEE58), radius = 7f, center = Offset(cx + 120f, cy + 2f))
    }
  }
}

// -------------------------------------------------------------
// REACCIONES DE FELICIDAD (512x512)
// -------------------------------------------------------------
private fun DrawScope.drawHappySparks512() {
  drawMiniHeart(Offset(130f, 110f), Color(0xFFFF4081), 18f)
  drawMiniHeart(Offset(385f, 100f), Color(0xFFFF4081), 15f)
  drawStar(Offset(95f, 220f), 16f, Color(0xFFFFD600))
  drawStar(Offset(415f, 230f), 18f, Color(0xFFFFD600))
}

// -------------------------------------------------------------
// DIBUJO DE FONDOS
// -------------------------------------------------------------
private fun DrawScope.drawBackgroundScene(
  bg: CapybaraBackground,
  w: Float,
  h: Float,
  animTime: Float
) {
  when (bg) {
    CapybaraBackground.BEACH -> drawBeachBackground(w, h, animTime)
    CapybaraBackground.FOREST -> drawForestBackground(w, h, animTime)
    CapybaraBackground.MEADOW -> drawMeadowBackground(w, h, animTime)
    CapybaraBackground.SUNSET -> drawSunsetBackground(w, h, animTime)
    CapybaraBackground.RAINFOREST -> drawRainforestBackground(w, h, animTime)
    CapybaraBackground.SPACE -> drawSpaceBackground(w, h, animTime)
  }
}

private fun DrawScope.drawMeadowBackground(w: Float, h: Float, anim: Float) {
  // Cielo azul celeste
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF56B4FD), Color(0xFFBCE5FF), Color(0xFFE9F7FF)),
      startY = 0f,
      endY = h * 0.7f
    ),
    size = Size(w, h)
  )

  // Arcoíris alegre
  val rainbowRadius = w * 0.65f
  val rainbowCenter = Offset(w * 0.35f, h * 0.48f)
  val rainbowColors = listOf(
    Color(0xFFFF5252).copy(alpha = 0.55f),
    Color(0xFFFF7A00).copy(alpha = 0.55f),
    Color(0xFFFFD600).copy(alpha = 0.55f),
    Color(0xFF4CAF50).copy(alpha = 0.55f),
    Color(0xFF29B6F6).copy(alpha = 0.55f),
    Color(0xFFAB47BC).copy(alpha = 0.55f)
  )
  rainbowColors.forEachIndexed { index, color ->
    drawCircle(
      color = color,
      radius = rainbowRadius - (index * 12f),
      center = rainbowCenter,
      style = Stroke(width = 12f)
    )
  }

  // Nubes flotantes esponjosas
  val cloud1X = (w * 0.15f + sin(anim * 0.02f) * 15f)
  val cloud2X = (w * 0.75f + cos(anim * 0.02f) * 20f)
  drawCloud(Offset(cloud1X, h * 0.16f), size = 65f)
  drawCloud(Offset(cloud2X, h * 0.22f), size = 80f)

  // Colinas verdes onduladas
  val backHill = Path().apply {
    moveTo(0f, h * 0.60f)
    cubicTo(w * 0.25f, h * 0.54f, w * 0.7f, h * 0.65f, w, h * 0.57f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(backHill, Color(0xFF81C784))

  val frontHill = Path().apply {
    moveTo(0f, h * 0.68f)
    cubicTo(w * 0.35f, h * 0.63f, w * 0.65f, h * 0.72f, w, h * 0.66f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(frontHill, Color(0xFF4CAF50))

  // Margaritas y mariposas en la pradera
  val daisyOffsets = listOf(
    Offset(w * 0.12f, h * 0.82f),
    Offset(w * 0.22f, h * 0.88f),
    Offset(w * 0.78f, h * 0.85f),
    Offset(w * 0.88f, h * 0.80f),
    Offset(w * 0.32f, h * 0.92f),
    Offset(w * 0.70f, h * 0.90f)
  )
  daisyOffsets.forEach { drawDaisy(it, radius = 10f) }

  val butterflyPos = Offset(w * 0.25f + sin(anim * 0.05f) * 20f, h * 0.45f + cos(anim * 0.05f) * 15f)
  drawButterfly(butterflyPos, Color(0xFFFF4081))
  val butterflyPos2 = Offset(w * 0.82f + cos(anim * 0.04f) * 25f, h * 0.50f + sin(anim * 0.04f) * 20f)
  drawButterfly(butterflyPos2, Color(0xFFFFB300))
}

private fun DrawScope.drawBeachBackground(w: Float, h: Float, anim: Float) {
  // Cielo soleado de playa
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF29B6F6), Color(0xFF81D4FA), Color(0xFFE1F5FE)),
      startY = 0f,
      endY = h * 0.6f
    ),
    size = Size(w, h)
  )

  // Sol brillante radiante
  val sunCenter = Offset(w * 0.82f, h * 0.16f)
  drawCircle(Color(0xFFFFD54F).copy(alpha = 0.35f), radius = 55f, center = sunCenter)
  drawCircle(Color(0xFFFFEE58), radius = 38f, center = sunCenter)

  for (i in 0 until 8) {
    val angle = (i * 45f + anim * 0.5f) * (Math.PI / 180f)
    val startX = sunCenter.x + cos(angle).toFloat() * 44f
    val startY = sunCenter.y + sin(angle).toFloat() * 44f
    val endX = sunCenter.x + cos(angle).toFloat() * 62f
    val endY = sunCenter.y + sin(angle).toFloat() * 62f
    drawLine(Color(0xFFFFD54F), Offset(startX, startY), Offset(endX, endY), strokeWidth = 3f, cap = StrokeCap.Round)
  }

  drawCloud(Offset(w * 0.25f, h * 0.18f), size = 60f)

  // Mar tropical azul y turquesa
  val seaPath = Path().apply {
    moveTo(0f, h * 0.54f)
    for (i in 0..10) {
      val x = (i / 10f) * w
      val waveY = h * 0.54f + sin(anim * 0.05f + i) * 6f
      lineTo(x, waveY)
    }
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(seaPath, Color(0xFF00ACC1))

  // Arena dorada de playa
  val sandPath = Path().apply {
    moveTo(0f, h * 0.66f)
    cubicTo(w * 0.35f, h * 0.62f, w * 0.65f, h * 0.70f, w, h * 0.65f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(sandPath, Color(0xFFFFD54F))

  // Palmera
  val trunkBase = Offset(w * 0.88f, h * 0.68f)
  val palmTrunk = Path().apply {
    moveTo(trunkBase.x - 12f, trunkBase.y)
    cubicTo(trunkBase.x - 22f, h * 0.50f, trunkBase.x - 8f, h * 0.35f, trunkBase.x - 30f, h * 0.22f)
    lineTo(trunkBase.x - 14f, h * 0.22f)
    cubicTo(trunkBase.x + 6f, h * 0.35f, trunkBase.x - 6f, h * 0.50f, trunkBase.x + 12f, trunkBase.y)
    close()
  }
  drawPath(palmTrunk, Color(0xFF8D6E63))

  val palmTop = Offset(trunkBase.x - 22f, h * 0.22f)
  for (i in 0 until 5) {
    val angle = (-40f + i * 45f) * (Math.PI / 180f)
    val leafEndX = palmTop.x + cos(angle).toFloat() * 65f
    val leafEndY = palmTop.y + sin(angle).toFloat() * 45f
    val leafPath = Path().apply {
      moveTo(palmTop.x, palmTop.y)
      quadraticBezierTo(palmTop.x + cos(angle).toFloat() * 35f, palmTop.y - 18f, leafEndX, leafEndY)
    }
    drawPath(leafPath, Color(0xFF43A047), style = Stroke(width = 6f, cap = StrokeCap.Round))
  }

  drawStarfish(Offset(w * 0.15f, h * 0.88f), Color(0xFFFF5252))
  drawSeashell(Offset(w * 0.80f, h * 0.90f), Color(0xFFFF80AB))
}

private fun DrawScope.drawForestBackground(w: Float, h: Float, anim: Float) {
  // Cielo de bosque verde menta suave
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF80DEEA), Color(0xFFB2DFDB), Color(0xFFE8F5E9)),
      startY = 0f,
      endY = h * 0.65f
    ),
    size = Size(w, h)
  )

  // Capa lejana de pinos
  val treeColorFar = Color(0xFF388E3C).copy(alpha = 0.6f)
  for (i in 0..7) {
    val treeX = (i / 6.5f) * w
    val treeH = h * 0.30f
    val treeY = h * 0.40f
    val path = Path().apply {
      moveTo(treeX, treeY)
      lineTo(treeX + 35f, treeY + treeH)
      lineTo(treeX - 35f, treeY + treeH)
      close()
    }
    drawPath(path, treeColorFar)
  }

  // Capa cercana de pinos
  val treeColorNear = Color(0xFF2E7D32)
  for (i in 0..5) {
    val treeX = (i / 4.5f) * w - 20f
    val treeH = h * 0.35f
    val treeY = h * 0.36f
    val path = Path().apply {
      moveTo(treeX, treeY)
      lineTo(treeX + 45f, treeY + treeH)
      lineTo(treeX - 45f, treeY + treeH)
      close()
    }
    drawPath(path, treeColorNear)
  }

  // Suelo musgoso
  val groundPath = Path().apply {
    moveTo(0f, h * 0.66f)
    cubicTo(w * 0.3f, h * 0.62f, w * 0.7f, h * 0.68f, w, h * 0.64f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(groundPath, Color(0xFF33691E))

  // Setas mágicas
  drawMushroom(Offset(w * 0.12f, h * 0.85f), scale = 1.3f)
  drawMushroom(Offset(w * 0.84f, h * 0.86f), scale = 1.1f)
  drawMushroom(Offset(w * 0.89f, h * 0.89f), scale = 0.8f)

  // Luciernagas brillantes
  for (i in 0 until 6) {
    val fx = (w * (0.15f + (i * 0.14f))) + sin(anim * 0.04f + i) * 12f
    val fy = (h * (0.45f + (i % 3) * 0.10f)) + cos(anim * 0.04f + i) * 10f
    drawCircle(Color(0xFFFFFF00).copy(alpha = 0.35f), radius = 9f, center = Offset(fx, fy))
    drawCircle(Color(0xFFFFF59D), radius = 4f, center = Offset(fx, fy))
  }
}

private fun DrawScope.drawSunsetBackground(w: Float, h: Float, anim: Float) {
  // Cielo al atardecer (degradado naranja, rosa y violeta)
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF3F1D38), Color(0xFF6B2B50), Color(0xFFD34848), Color(0xFFFF8B3D), Color(0xFFFFD166)),
      startY = 0f,
      endY = h * 0.70f
    ),
    size = Size(w, h)
  )

  // Sol gigante del atardecer
  val sunCenter = Offset(w * 0.50f, h * 0.48f)
  drawCircle(Color(0xFFFFE082).copy(alpha = 0.4f), radius = 70f, center = sunCenter)
  drawCircle(Color(0xFFFFD54F), radius = 48f, center = sunCenter)

  // Estrellas titilantes tempranas
  val starOffsets = listOf(
    Offset(w * 0.15f, h * 0.10f),
    Offset(w * 0.35f, h * 0.06f),
    Offset(w * 0.75f, h * 0.08f),
    Offset(w * 0.88f, h * 0.14f),
    Offset(w * 0.20f, h * 0.22f),
    Offset(w * 0.80f, h * 0.25f)
  )
  starOffsets.forEachIndexed { index, offset ->
    val pulse = (sin(anim * 0.08f + index) + 1f) * 0.5f
    drawStar(offset, size = 6f + pulse * 4f, color = Color(0xFFFFF9C4))
  }

  // Siluetas de montañas violetas
  val mountain1 = Path().apply {
    moveTo(0f, h * 0.62f)
    lineTo(w * 0.28f, h * 0.45f)
    lineTo(w * 0.55f, h * 0.64f)
    lineTo(w * 0.85f, h * 0.42f)
    lineTo(w, h * 0.56f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(mountain1, Color(0xFF311B92).copy(alpha = 0.85f))

  // Suelo en sombra cálida
  val groundSunset = Path().apply {
    moveTo(0f, h * 0.70f)
    cubicTo(w * 0.3f, h * 0.66f, w * 0.7f, h * 0.72f, w, h * 0.68f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(groundSunset, Color(0xFF21092F))
}

private fun DrawScope.drawRainforestBackground(w: Float, h: Float, anim: Float) {
  // Cielo y canopia verde esmeralda
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF004D40), Color(0xFF00796B), Color(0xFF2E7D32), Color(0xFF81C784)),
      startY = 0f,
      endY = h * 0.75f
    ),
    size = Size(w, h)
  )

  // Rayos de sol tropicales
  for (i in 0..4) {
    val rayX = w * (0.15f + i * 0.20f)
    val rayPath = Path().apply {
      moveTo(rayX, 0f)
      lineTo(rayX + 30f, 0f)
      lineTo(rayX - 30f, h * 0.7f)
      lineTo(rayX - 70f, h * 0.7f)
      close()
    }
    drawPath(rayPath, Color(0xFFFFEB3B).copy(alpha = 0.12f))
  }

  val leafColorDark = Color(0xFF1B5E20)
  val leafColorMid = Color(0xFF2E7D32)
  val leafColorLight = Color(0xFF43A047)

  for (x in 0..6) {
    val px = x * (w / 5.5f)
    drawCircle(leafColorDark.copy(alpha = 0.85f), radius = w * 0.16f, center = Offset(px, h * 0.38f))
  }

  for (i in 0..4) {
    val lx = w * (0.05f + i * 0.25f)
    val sway = sin(anim * 0.03f + i) * 6f
    val leafPath = Path().apply {
      moveTo(lx, 0f)
      quadraticBezierTo(lx + 40f + sway, h * 0.18f, lx + 20f, h * 0.32f)
      quadraticBezierTo(lx - 20f, h * 0.20f, lx, 0f)
      close()
    }
    drawPath(leafPath, if (i % 2 == 0) leafColorMid else leafColorLight)
    drawPath(leafPath, Color(0xFF0A3311), style = Stroke(width = 1.5f))
  }

  val flowerPositions = listOf(
    Offset(w * 0.12f, h * 0.46f),
    Offset(w * 0.88f, h * 0.42f),
    Offset(w * 0.75f, h * 0.58f),
    Offset(w * 0.20f, h * 0.62f)
  )
  flowerPositions.forEachIndexed { idx, pos ->
    val fColor = if (idx % 2 == 0) Color(0xFFFF1744) else Color(0xFFFF9100)
    for (petal in 0..4) {
      val angle = (petal * 72f) * (Math.PI / 180f)
      drawCircle(fColor, radius = 9f, center = Offset(pos.x + cos(angle).toFloat() * 11f, pos.y + sin(angle).toFloat() * 11f))
    }
    drawCircle(Color(0xFFFFEA00), radius = 6f, center = pos)
  }

  val bFlyX = w * 0.32f + sin(anim * 0.04f) * 20f
  val bFlyY = h * 0.35f + cos(anim * 0.05f) * 12f
  drawButterfly(Offset(bFlyX, bFlyY), Color(0xFF00E5FF))

  val groundJungle = Path().apply {
    moveTo(0f, h * 0.66f)
    cubicTo(w * 0.35f, h * 0.63f, w * 0.65f, h * 0.69f, w, h * 0.65f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(groundJungle, Color(0xFF1B3815))
}

private fun DrawScope.drawSpaceBackground(w: Float, h: Float, anim: Float) {
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF050518), Color(0xFF1A0A3A), Color(0xFF28104E), Color(0xFF0B132B)),
      startY = 0f,
      endY = h
    ),
    size = Size(w, h)
  )

  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFE040FB).copy(alpha = 0.28f), Color.Transparent),
      center = Offset(w * 0.25f, h * 0.28f),
      radius = w * 0.35f
    ),
    radius = w * 0.35f,
    center = Offset(w * 0.25f, h * 0.28f)
  )
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.25f), Color.Transparent),
      center = Offset(w * 0.78f, h * 0.42f),
      radius = w * 0.30f
    ),
    radius = w * 0.30f,
    center = Offset(w * 0.78f, h * 0.42f)
  )

  val saturnPos = Offset(w * 0.82f, h * 0.20f)
  drawCircle(Color(0xFFFFB74D), radius = 24f, center = saturnPos)
  drawCircle(Color(0xFFFF9800), radius = 20f, center = Offset(saturnPos.x - 3f, saturnPos.y - 3f))
  val ring = Path().apply {
    addOval(Rect(saturnPos.x - 44f, saturnPos.y - 12f, saturnPos.x + 44f, saturnPos.y + 12f))
  }
  rotate(degrees = -25f, pivot = saturnPos) {
    drawPath(ring, Color(0xFFFFE082).copy(alpha = 0.75f), style = Stroke(width = 6f))
    drawPath(ring, Color(0xFFFFD54F), style = Stroke(width = 2f))
  }

  val moonPos = Offset(w * 0.18f, h * 0.16f)
  drawCircle(Color(0xFF80D8FF), radius = 16f, center = moonPos)
  drawCircle(Color(0xFF40C4FF).copy(alpha = 0.6f), radius = 4f, center = Offset(moonPos.x - 5f, moonPos.y - 3f))

  val spaceStars = listOf(
    Offset(w * 0.08f, h * 0.08f),
    Offset(w * 0.38f, h * 0.06f),
    Offset(w * 0.50f, h * 0.15f),
    Offset(w * 0.62f, h * 0.09f),
    Offset(w * 0.92f, h * 0.07f),
    Offset(w * 0.12f, h * 0.32f),
    Offset(w * 0.42f, h * 0.28f),
    Offset(w * 0.68f, h * 0.32f),
    Offset(w * 0.30f, h * 0.45f),
    Offset(w * 0.88f, h * 0.52f)
  )
  spaceStars.forEachIndexed { index, starPos ->
    val blink = (sin(anim * 0.07f + index * 1.3f) + 1f) * 0.5f
    val starColor = when (index % 4) {
      0 -> Color(0xFFFFF59D)
      1 -> Color(0xFF80D8FF)
      2 -> Color(0xFFFF80AB)
      else -> Color.White
    }
    drawStar(starPos, size = 4f + blink * 5f, color = starColor)
  }

  val groundMoon = Path().apply {
    moveTo(0f, h * 0.68f)
    cubicTo(w * 0.35f, h * 0.65f, w * 0.7f, h * 0.70f, w, h * 0.67f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(groundMoon, Color(0xFF37474F))

  drawOval(Color(0xFF263238), topLeft = Offset(w * 0.14f, h * 0.75f), size = Size(36f, 12f))
  drawOval(Color(0xFF263238), topLeft = Offset(w * 0.72f, h * 0.78f), size = Size(50f, 15f))
  drawOval(Color(0xFF263238), topLeft = Offset(w * 0.45f, h * 0.86f), size = Size(40f, 11f))
}

// -------------------------------------------------------------
// ELEMENTOS DECORATIVOS SECUNDARIOS
// -------------------------------------------------------------
private fun DrawScope.drawCloud(center: Offset, size: Float) {
  val cloudColor = Color.White.copy(alpha = 0.90f)
  drawCircle(cloudColor, radius = size * 0.45f, center = center)
  drawCircle(cloudColor, radius = size * 0.35f, center = Offset(center.x - size * 0.4f, center.y + size * 0.1f))
  drawCircle(cloudColor, radius = size * 0.38f, center = Offset(center.x + size * 0.4f, center.y + size * 0.08f))
  drawOval(cloudColor, topLeft = Offset(center.x - size * 0.6f, center.y), size = Size(size * 1.2f, size * 0.4f))
}

private fun DrawScope.drawDaisy(pos: Offset, radius: Float) {
  val petalColor = Color.White
  for (i in 0 until 6) {
    val angle = (i * 60f) * (Math.PI / 180f)
    val pX = pos.x + cos(angle).toFloat() * (radius * 0.6f)
    val pY = pos.y + sin(angle).toFloat() * (radius * 0.6f)
    drawCircle(petalColor, radius = radius * 0.45f, center = Offset(pX, pY))
  }
  drawCircle(Color(0xFFFFD600), radius = radius * 0.4f, center = pos)
}

private fun DrawScope.drawButterfly(pos: Offset, color: Color) {
  drawOval(color, topLeft = Offset(pos.x - 12f, pos.y - 12f), size = Size(10f, 14f))
  drawOval(color, topLeft = Offset(pos.x + 2f, pos.y - 12f), size = Size(10f, 14f))
  drawOval(color.copy(alpha = 0.8f), topLeft = Offset(pos.x - 8f, pos.y), size = Size(8f, 10f))
  drawOval(color.copy(alpha = 0.8f), topLeft = Offset(pos.x, pos.y), size = Size(8f, 10f))
  drawRoundRect(Color(0xFF3E2723), topLeft = Offset(pos.x - 2f, pos.y - 8f), size = Size(4f, 16f), cornerRadius = CornerRadius(2f, 2f))
}

private fun DrawScope.drawStarfish(pos: Offset, color: Color) {
  drawStar(pos, 16f, color)
  drawCircle(Color.White.copy(alpha = 0.6f), radius = 2f, center = pos)
}

private fun DrawScope.drawSeashell(pos: Offset, color: Color) {
  val shell = Path().apply {
    moveTo(pos.x, pos.y + 8f)
    lineTo(pos.x - 10f, pos.y - 8f)
    quadraticBezierTo(pos.x, pos.y - 14f, pos.x + 10f, pos.y - 8f)
    close()
  }
  drawPath(shell, color)
  drawPath(shell, Color(0xFF8D6E63), style = Stroke(width = 2f))
}

private fun DrawScope.drawMushroom(pos: Offset, scale: Float) {
  scale(scale, pivot = pos) {
    drawRoundRect(
      Color(0xFFFFF9C4),
      topLeft = Offset(pos.x - 5f, pos.y),
      size = Size(10f, 18f),
      cornerRadius = CornerRadius(4f, 4f)
    )
    val cap = Path().apply {
      moveTo(pos.x - 16f, pos.y + 4f)
      quadraticBezierTo(pos.x, pos.y - 20f, pos.x + 16f, pos.y + 4f)
      close()
    }
    drawPath(cap, Color(0xFFE53935))
    drawCircle(Color.White, radius = 3f, center = Offset(pos.x, pos.y - 6f))
    drawCircle(Color.White, radius = 2.2f, center = Offset(pos.x - 8f, pos.y - 2f))
    drawCircle(Color.White, radius = 2.2f, center = Offset(pos.x + 8f, pos.y - 2f))
  }
}

private fun DrawScope.drawStar(
  center: Offset,
  size: Float,
  color: Color,
  strokeWidth: Float? = null
) {
  val path = Path().apply {
    val points = 5
    val outerR = size
    val innerR = size * 0.45f
    for (i in 0 until points * 2) {
      val r = if (i % 2 == 0) outerR else innerR
      val angle = (i * Math.PI / points) - (Math.PI / 2)
      val x = center.x + (r * cos(angle)).toFloat()
      val y = center.y + (r * sin(angle)).toFloat()
      if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
  }
  if (strokeWidth != null) {
    drawPath(path, color, style = Stroke(width = strokeWidth))
  } else {
    drawPath(path, color)
  }
}

private fun DrawScope.drawMiniHeart(center: Offset, color: Color, size: Float) {
  val path = Path().apply {
    moveTo(center.x, center.y + size)
    cubicTo(center.x - size * 1.3f, center.y, center.x - size * 1.5f, center.y - size, center.x, center.y - size * 0.4f)
    cubicTo(center.x + size * 1.5f, center.y - size, center.x + size * 1.3f, center.y, center.x, center.y + size)
    close()
  }
  drawPath(path, color)
}

private fun DrawScope.drawMiniFlower(center: Offset, petalColor: Color, centerColor: Color) {
  for (i in 0 until 5) {
    val angle = (i * 72f) * (Math.PI / 180f)
    val pX = center.x + cos(angle).toFloat() * 7f
    val pY = center.y + sin(angle).toFloat() * 7f
    drawCircle(petalColor, radius = 4f, center = Offset(pX, pY))
  }
  drawCircle(centerColor, radius = 3.5f, center = center)
}
