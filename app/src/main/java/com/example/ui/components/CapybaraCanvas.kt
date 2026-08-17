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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.example.model.CapybaraBackground
import com.example.model.CapybaraColor
import com.example.model.CapybaraGlasses
import com.example.model.CapybaraHat
import com.example.model.CapybaraShirt
import com.example.model.CapybaraShoes
import com.example.model.CapybaraState
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lienzo animado en estilo dibujo animado que dibuja al capibara y su entorno 100% en código.
 */
@Composable
fun CapybaraCanvas(
  state: CapybaraState,
  modifier: Modifier = Modifier,
  onPet: () -> Unit = {}
) {
  // Animación continua de flotación / respiración sutil y divertida
  val infiniteTransition = rememberInfiniteTransition(label = "capy_anim")
  val breathOffset by infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
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

    // 2. Dibujar al Capibara en la posición central
    val capyCenterX = width * 0.50f
    val capyCenterY = height * 0.58f + breathOffset
    val capyScale = (width.coerceAtMost(height) / 360f).coerceIn(0.65f, 1.45f)

    drawCapybaraCharacter(
      state = state,
      centerX = capyCenterX,
      centerY = capyCenterY,
      scaleFactor = capyScale
    )
  }
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
      radius = rainbowRadius - (index * 7f),
      center = rainbowCenter,
      style = Stroke(width = 8f)
    )
  }

  // Sol suave en esquina
  drawCircle(
    color = Color(0xFFFFE066),
    radius = w * 0.12f,
    center = Offset(w * 0.85f, h * 0.14f)
  )
  drawCircle(
    color = Color(0xFFFFF9C4).copy(alpha = 0.6f),
    radius = w * 0.16f,
    center = Offset(w * 0.85f, h * 0.14f)
  )

  // Nubes esponjosas
  drawCloud(Offset(w * 0.18f, h * 0.16f), w * 0.14f)
  drawCloud(Offset(w * 0.62f, h * 0.22f), w * 0.11f)

  // Colina trasera
  val hill1Path = Path().apply {
    moveTo(0f, h * 0.65f)
    cubicTo(w * 0.3f, h * 0.55f, w * 0.7f, h * 0.68f, w, h * 0.58f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(hill1Path, Color(0xFF81C784))

  // Colina frontal
  val hill2Path = Path().apply {
    moveTo(0f, h * 0.72f)
    cubicTo(w * 0.4f, h * 0.63f, w * 0.8f, h * 0.75f, w, h * 0.68f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(hill2Path, Color(0xFF5DB85B))

  // Margaritas en el prado
  drawDaisy(Offset(w * 0.12f, h * 0.82f), 14f)
  drawDaisy(Offset(w * 0.24f, h * 0.90f), 18f)
  drawDaisy(Offset(w * 0.82f, h * 0.84f), 16f)
  drawDaisy(Offset(w * 0.92f, h * 0.76f), 13f)
  drawDaisy(Offset(w * 0.75f, h * 0.93f), 15f)

  // Mariposa animada
  val butterflyX = w * 0.22f + sin(anim * 0.05f) * 20f
  val butterflyY = h * 0.42f + cos(anim * 0.05f) * 12f
  drawButterfly(Offset(butterflyX, butterflyY), Color(0xFFFF4081))
}

private fun DrawScope.drawBeachBackground(w: Float, h: Float, anim: Float) {
  // Cielo soleado de playa
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF29B6F6), Color(0xFF81D4FA), Color(0xFFFFE082)),
      startY = 0f,
      endY = h * 0.65f
    ),
    size = Size(w, h)
  )

  // Sol radiante brillante
  val sunCenter = Offset(w * 0.20f, h * 0.16f)
  drawCircle(Color(0xFFFFD54F), radius = w * 0.13f, center = sunCenter)
  drawCircle(Color(0xFFFFEE58).copy(alpha = 0.5f), radius = w * 0.18f, center = sunCenter)
  // Rayos del sol
  for (i in 0 until 8) {
    val angle = (i * 45f + anim * 0.2f) * (Math.PI / 180f)
    val r1 = w * 0.15f
    val r2 = w * 0.22f
    drawLine(
      color = Color(0xFFFFCA28),
      start = Offset(sunCenter.x + cos(angle).toFloat() * r1, sunCenter.y + sin(angle).toFloat() * r1),
      end = Offset(sunCenter.x + cos(angle).toFloat() * r2, sunCenter.y + sin(angle).toFloat() * r2),
      strokeWidth = 5f,
      cap = StrokeCap.Round
    )
  }

  // Mar turquesa
  val seaPath = Path().apply {
    moveTo(0f, h * 0.56f)
    cubicTo(w * 0.25f, h * 0.54f + sin(anim * 0.05f) * 5f, w * 0.75f, h * 0.58f, w, h * 0.55f)
    lineTo(w, h * 0.72f)
    lineTo(0f, h * 0.72f)
    close()
  }
  drawPath(seaPath, Color(0xFF00ACC1))

  // Olas con espuma blanca
  val wavePath = Path().apply {
    moveTo(0f, h * 0.62f)
    cubicTo(w * 0.3f, h * 0.60f, w * 0.7f, h * 0.64f, w, h * 0.61f)
    lineTo(w, h * 0.68f)
    cubicTo(w * 0.6f, h * 0.69f, w * 0.3f, h * 0.65f, 0f, h * 0.67f)
    close()
  }
  drawPath(wavePath, Color(0xFF80DEEA))
  drawPath(wavePath, Color.White.copy(alpha = 0.7f), style = Stroke(width = 4f))

  // Arena dorada de la orilla
  val sandPath = Path().apply {
    moveTo(0f, h * 0.66f)
    cubicTo(w * 0.35f, h * 0.63f, w * 0.7f, h * 0.69f, w, h * 0.65f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(sandPath, Color(0xFFFFD54F))

  // Palmera en el lateral derecho
  val trunkPath = Path().apply {
    moveTo(w * 0.90f, h * 0.78f)
    quadraticBezierTo(w * 0.92f, h * 0.45f, w * 0.82f, h * 0.24f)
    lineTo(w * 0.86f, h * 0.24f)
    quadraticBezierTo(w * 0.96f, h * 0.45f, w * 0.96f, h * 0.78f)
    close()
  }
  drawPath(trunkPath, Color(0xFF8D6E63))

  // Hojas de palmera
  val palmCenter = Offset(w * 0.84f, h * 0.24f)
  val leafColors = listOf(Color(0xFF43A047), Color(0xFF2E7D32), Color(0xFF66BB6A))
  val angles = listOf(-160f, -120f, -70f, -20f, 30f)
  angles.forEachIndexed { i, a ->
    rotate(a, pivot = palmCenter) {
      drawOval(
        color = leafColors[i % leafColors.size],
        topLeft = Offset(palmCenter.x - 70f, palmCenter.y - 18f),
        size = Size(85f, 26f)
      )
    }
  }
  // Cocos
  drawCircle(Color(0xFF5D4037), radius = 10f, center = Offset(palmCenter.x - 5f, palmCenter.y + 8f))
  drawCircle(Color(0xFF4E342E), radius = 9f, center = Offset(palmCenter.x + 8f, palmCenter.y + 6f))

  // Estrella de mar en la arena
  drawStarfish(Offset(w * 0.15f, h * 0.86f), Color(0xFFFF7043))
  drawSeashell(Offset(w * 0.88f, h * 0.88f), Color(0xFFFFAB91))
}

private fun DrawScope.drawForestBackground(w: Float, h: Float, anim: Float) {
  // Cielo de bosque misterioso y luminoso
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFF80CBC4), Color(0xFFA5D6A7), Color(0xFFE8F5E9)),
      startY = 0f,
      endY = h * 0.65f
    ),
    size = Size(w, h)
  )

  // Árboles en la lejanía (siluetas verde azulado)
  val distantTreesPath = Path().apply {
    moveTo(0f, h * 0.62f)
    for (i in 0..10) {
      val x = (i / 10f) * w
      val treeH = if (i % 2 == 0) h * 0.44f else h * 0.48f
      lineTo(x, treeH)
      lineTo(x + (w / 20f), h * 0.62f)
    }
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(distantTreesPath, Color(0xFF4DB6AC).copy(alpha = 0.5f))

  // Suelo del bosque
  val groundPath = Path().apply {
    moveTo(0f, h * 0.68f)
    cubicTo(w * 0.35f, h * 0.64f, w * 0.7f, h * 0.70f, w, h * 0.66f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(groundPath, Color(0xFF388E3C))

  // Gran árbol a la izquierda
  drawRect(Color(0xFF5D4037), topLeft = Offset(w * 0.05f, h * 0.35f), size = Size(w * 0.08f, h * 0.40f))
  drawCircle(Color(0xFF2E7D32), radius = w * 0.18f, center = Offset(w * 0.09f, h * 0.32f))
  drawCircle(Color(0xFF43A047), radius = w * 0.14f, center = Offset(w * 0.15f, h * 0.28f))

  // Gran árbol a la derecha
  drawRect(Color(0xFF6D4C41), topLeft = Offset(w * 0.88f, h * 0.40f), size = Size(w * 0.07f, h * 0.35f))
  drawCircle(Color(0xFF1B5E20), radius = w * 0.16f, center = Offset(w * 0.91f, h * 0.36f))

  // Setas rojas con lunares blancos
  drawMushroom(Offset(w * 0.18f, h * 0.85f), scale = 1.3f)
  drawMushroom(Offset(w * 0.26f, h * 0.89f), scale = 0.9f)
  drawMushroom(Offset(w * 0.84f, h * 0.87f), scale = 1.1f)

  // Luces mágicas / luciérnagas flotantes
  for (i in 0 until 6) {
    val fX = (w * (0.2f + i * 0.12f) + sin(anim * 0.04f + i) * 15f)
    val fY = (h * (0.35f + (i % 3) * 0.12f) + cos(anim * 0.04f + i) * 12f)
    drawCircle(Color(0xFFFFF59D).copy(alpha = 0.7f), radius = 5f, center = Offset(fX, fY))
    drawCircle(Color(0xFFFFF9C4).copy(alpha = 0.3f), radius = 10f, center = Offset(fX, fY))
  }
}

private fun DrawScope.drawSunsetBackground(w: Float, h: Float, anim: Float) {
  // Cielo con degradado cálido atardecer
  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(
        Color(0xFF4A148C), // Morado noche
        Color(0xFF880E4F), // Magenta
        Color(0xFFE64A19), // Naranja ardiente
        Color(0xFFFFB300)  // Dorado
      ),
      startY = 0f,
      endY = h * 0.75f
    ),
    size = Size(w, h)
  )

  // Sol poniente o luna suave
  val sunsetSunCenter = Offset(w * 0.5f, h * 0.48f)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFFF59D), Color(0xFFFF9800).copy(alpha = 0.7f), Color.Transparent),
      center = sunsetSunCenter,
      radius = w * 0.32f
    ),
    radius = w * 0.32f,
    center = sunsetSunCenter
  )
  drawCircle(Color(0xFFFFEB3B), radius = w * 0.12f, center = sunsetSunCenter)

  // Estrellitas titilantes en la parte superior
  val starOffsets = listOf(
    Offset(w * 0.12f, h * 0.08f),
    Offset(w * 0.35f, h * 0.05f),
    Offset(w * 0.78f, h * 0.07f),
    Offset(w * 0.88f, h * 0.14f),
    Offset(w * 0.22f, h * 0.18f),
    Offset(w * 0.65f, h * 0.12f)
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

// -------------------------------------------------------------
// DIBUJO DEL CAPIBARA
// -------------------------------------------------------------
private fun DrawScope.drawCapybaraCharacter(
  state: CapybaraState,
  centerX: Float,
  centerY: Float,
  scaleFactor: Float
) {
  scale(scaleFactor, pivot = Offset(centerX, centerY)) {
    val c = state.color
    val outlineColor = Color(0xFF2C1810)
    val strokeW = 4.5f

    // 1. Sombra en el suelo
    drawOval(
      color = Color(0x33000000),
      topLeft = Offset(centerX - 105f, centerY + 80f),
      size = Size(210f, 32f)
    )

    // 2. Capa de superhéroe (si la lleva, va detrás del cuerpo)
    if (state.shirt == CapybaraShirt.SUPERHERO) {
      drawCapeBack(centerX, centerY)
    }

    // 3. Patas traseras (dibujadas detrás)
    drawBackLegs(c, outlineColor, strokeW, centerX, centerY)

    // Si tiene zapatos traseros
    if (state.shoes != CapybaraShoes.NONE) {
      drawBackShoes(state.shoes, centerX, centerY)
    }

    // 4. Cuerpo principal del Capibara (forma de barril redondeado adorable)
    val bodyBounds = Rect(centerX - 95f, centerY - 45f, centerX + 85f, centerY + 85f)
    val bodyPath = Path().apply {
      addRoundRect(
        RoundRect(
          bodyBounds,
          cornerRadius = CornerRadius(65f, 65f)
        )
      )
    }

    // Relleno degradado del cuerpo
    drawPath(
      path = bodyPath,
      brush = Brush.verticalGradient(
        colors = listOf(c.highlightColor, c.baseColor, c.shadowColor),
        startY = bodyBounds.top,
        endY = bodyBounds.bottom
      )
    )
    // Contorno del cuerpo
    drawPath(bodyPath, outlineColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

    // Pancita suave (más clara)
    val bellyPath = Path().apply {
      addOval(Rect(centerX - 55f, centerY + 10f, centerX + 55f, centerY + 75f))
    }
    drawPath(bellyPath, c.bellyColor.copy(alpha = 0.75f))

    // 5. Ropa en el torso
    if (state.shirt != CapybaraShirt.NONE) {
      drawShirtOnBody(state.shirt, centerX, centerY, strokeW, outlineColor)
    }

    // 6. Patas delanteras
    drawFrontLegs(c, outlineColor, strokeW, centerX, centerY)

    // Zapatos delanteros
    if (state.shoes != CapybaraShoes.NONE) {
      drawFrontShoes(state.shoes, centerX, centerY)
    }

    // 7. Cabeza y hocico del Capibara
    drawCapybaraHeadAndFace(state, c, outlineColor, strokeW, centerX, centerY)

    // 8. Gafas (sobre los ojos)
    if (state.glasses != CapybaraGlasses.NONE) {
      drawGlasses(state.glasses, centerX, centerY)
    }

    // 9. Gorro / Accesorio superior
    if (state.hat != CapybaraHat.NONE) {
      drawHat(state.hat, centerX, centerY)
    }

    // 10. Efecto de felicidad (si está feliz: corazoncitos o estrellitas flotantes)
    if (state.isHappy || state.happinessCount > 1) {
      drawHappySparks(centerX, centerY)
    }
  }
}

// Patas traseras
private fun DrawScope.drawBackLegs(
  c: CapybaraColor,
  outlineColor: Color,
  strokeW: Float,
  cx: Float,
  cy: Float
) {
  // Pata trasera izquierda
  drawRoundRect(
    color = c.shadowColor,
    topLeft = Offset(cx - 82f, cy + 55f),
    size = Size(32f, 40f),
    cornerRadius = CornerRadius(16f, 16f)
  )
  drawRoundRect(
    color = outlineColor,
    topLeft = Offset(cx - 82f, cy + 55f),
    size = Size(32f, 40f),
    cornerRadius = CornerRadius(16f, 16f),
    style = Stroke(width = strokeW)
  )

  // Pata trasera derecha
  drawRoundRect(
    color = c.shadowColor,
    topLeft = Offset(cx + 50f, cy + 55f),
    size = Size(32f, 40f),
    cornerRadius = CornerRadius(16f, 16f)
  )
  drawRoundRect(
    color = outlineColor,
    topLeft = Offset(cx + 50f, cy + 55f),
    size = Size(32f, 40f),
    cornerRadius = CornerRadius(16f, 16f),
    style = Stroke(width = strokeW)
  )
}

// Patas delanteras
private fun DrawScope.drawFrontLegs(
  c: CapybaraColor,
  outlineColor: Color,
  strokeW: Float,
  cx: Float,
  cy: Float
) {
  // Pata delantera izquierda
  drawRoundRect(
    brush = Brush.verticalGradient(listOf(c.baseColor, c.shadowColor)),
    topLeft = Offset(cx - 50f, cy + 60f),
    size = Size(30f, 38f),
    cornerRadius = CornerRadius(15f, 15f)
  )
  drawRoundRect(
    color = outlineColor,
    topLeft = Offset(cx - 50f, cy + 60f),
    size = Size(30f, 38f),
    cornerRadius = CornerRadius(15f, 15f),
    style = Stroke(width = strokeW)
  )
  // Uñitas tiernas
  for (i in 0..2) {
    drawLine(
      color = outlineColor,
      start = Offset(cx - 45f + i * 8f, cy + 90f),
      end = Offset(cx - 45f + i * 8f, cy + 96f),
      strokeWidth = 3f,
      cap = StrokeCap.Round
    )
  }

  // Pata delantera derecha
  drawRoundRect(
    brush = Brush.verticalGradient(listOf(c.baseColor, c.shadowColor)),
    topLeft = Offset(cx + 20f, cy + 60f),
    size = Size(30f, 38f),
    cornerRadius = CornerRadius(15f, 15f)
  )
  drawRoundRect(
    color = outlineColor,
    topLeft = Offset(cx + 20f, cy + 60f),
    size = Size(30f, 38f),
    cornerRadius = CornerRadius(15f, 15f),
    style = Stroke(width = strokeW)
  )
  for (i in 0..2) {
    drawLine(
      color = outlineColor,
      start = Offset(cx + 25f + i * 8f, cy + 90f),
      end = Offset(cx + 25f + i * 8f, cy + 96f),
      strokeWidth = 3f,
      cap = StrokeCap.Round
    )
  }
}

// Cabeza, orejitas, ojos, nariz y sonrisa
private fun DrawScope.drawCapybaraHeadAndFace(
  state: CapybaraState,
  c: CapybaraColor,
  outlineColor: Color,
  strokeW: Float,
  cx: Float,
  cy: Float
) {
  val headY = cy - 75f
  val headX = cx - 15f

  // 1. Orejas
  // Oreja izquierda
  drawOval(
    color = c.shadowColor,
    topLeft = Offset(headX - 52f, headY - 24f),
    size = Size(26f, 32f)
  )
  drawOval(
    color = Color(0xFFFFB6C1),
    topLeft = Offset(headX - 48f, headY - 18f),
    size = Size(18f, 22f)
  )
  drawOval(
    color = outlineColor,
    topLeft = Offset(headX - 52f, headY - 24f),
    size = Size(26f, 32f),
    style = Stroke(width = strokeW)
  )

  // Oreja derecha
  drawOval(
    color = c.shadowColor,
    topLeft = Offset(headX + 26f, headY - 24f),
    size = Size(26f, 32f)
  )
  drawOval(
    color = Color(0xFFFFB6C1),
    topLeft = Offset(headX + 30f, headY - 18f),
    size = Size(18f, 22f)
  )
  drawOval(
    color = outlineColor,
    topLeft = Offset(headX + 26f, headY - 24f),
    size = Size(26f, 32f),
    style = Stroke(width = strokeW)
  )

  // 2. Cabeza del Capibara (forma de caja redondeada característica)
  val headBounds = Rect(headX - 55f, headY - 15f, headX + 55f, headY + 70f)
  val headPath = Path().apply {
    addRoundRect(
      RoundRect(
        headBounds,
        topLeft = CornerRadius(45f, 45f),
        topRight = CornerRadius(45f, 45f),
        bottomLeft = CornerRadius(38f, 38f),
        bottomRight = CornerRadius(38f, 38f)
      )
    )
  }
  drawPath(
    headPath,
    brush = Brush.verticalGradient(
      colors = listOf(c.highlightColor, c.baseColor),
      startY = headBounds.top,
      endY = headBounds.bottom
    )
  )
  drawPath(headPath, outlineColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

  // 3. Hocico cuadrilátero redondeado en la parte inferior
  val snoutBounds = Rect(headX - 38f, headY + 22f, headX + 38f, headY + 68f)
  val snoutPath = Path().apply {
    addRoundRect(RoundRect(snoutBounds, CornerRadius(24f, 24f)))
  }
  drawPath(snoutPath, c.shadowColor.copy(alpha = 0.45f))

  // 4. Mofletes sonrojados tiernos
  drawOval(
    color = Color(0xFFFF6F91).copy(alpha = 0.55f),
    topLeft = Offset(headX - 50f, headY + 28f),
    size = Size(22f, 15f)
  )
  drawOval(
    color = Color(0xFFFF6F91).copy(alpha = 0.55f),
    topLeft = Offset(headX + 28f, headY + 28f),
    size = Size(22f, 15f)
  )

  // 5. Ojos
  val eyeY = headY + 12f
  val leftEyeX = headX - 26f
  val rightEyeX = headX + 26f

  if (state.isHappy) {
    // Ojos cerrados de felicidad ^ ^
    drawHappyEye(Offset(leftEyeX, eyeY), outlineColor)
    drawHappyEye(Offset(rightEyeX, eyeY), outlineColor)
  } else {
    // Ojos grandes cartoon brillantes
    drawCartoonEye(Offset(leftEyeX, eyeY), outlineColor)
    drawCartoonEye(Offset(rightEyeX, eyeY), outlineColor)
  }

  // 6. Nariz y fosas nasales
  val noseY = headY + 36f
  drawOval(
    color = Color(0xFF26120B),
    topLeft = Offset(headX - 16f, noseY),
    size = Size(32f, 16f)
  )
  // Fosas nasales
  drawCircle(Color(0xFF140805), radius = 3.5f, center = Offset(headX - 7f, noseY + 8f))
  drawCircle(Color(0xFF140805), radius = 3.5f, center = Offset(headX + 7f, noseY + 8f))
  // Brillo en la nariz
  drawCircle(Color.White.copy(alpha = 0.7f), radius = 2.5f, center = Offset(headX - 5f, noseY + 4f))

  // 7. Boquita tierna de capibara (forma de w suave '3')
  val mouthY = headY + 54f
  val mouthPath = Path().apply {
    // Línea central desde la nariz
    moveTo(headX, noseY + 16f)
    lineTo(headX, mouthY)
    // Curva izquierda
    moveTo(headX - 14f, mouthY - 2f)
    quadraticBezierTo(headX - 7f, mouthY + 8f, headX, mouthY)
    // Curva derecha
    quadraticBezierTo(headX + 7f, mouthY + 8f, headX + 14f, mouthY - 2f)
  }
  drawPath(mouthPath, outlineColor, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
}

private fun DrawScope.drawCartoonEye(center: Offset, outlineColor: Color) {
  // Blanco del ojo
  drawOval(
    color = Color.White,
    topLeft = Offset(center.x - 12f, center.y - 12f),
    size = Size(24f, 24f)
  )
  drawOval(
    color = outlineColor,
    topLeft = Offset(center.x - 12f, center.y - 12f),
    size = Size(24f, 24f),
    style = Stroke(width = 3f)
  )
  // Iris/Pupila oscura grande
  drawCircle(
    color = Color(0xFF1F1209),
    radius = 8.5f,
    center = Offset(center.x, center.y)
  )
  // Brillo principal grande
  drawCircle(
    color = Color.White,
    radius = 3.8f,
    center = Offset(center.x - 2.5f, center.y - 3f)
  )
  // Brillo secundario pequeño
  drawCircle(
    color = Color.White,
    radius = 1.8f,
    center = Offset(center.x + 3.5f, center.y + 3f)
  )
  // Pestañas tiernas
  drawLine(
    color = outlineColor,
    start = Offset(center.x - 10f, center.y - 10f),
    end = Offset(center.x - 15f, center.y - 15f),
    strokeWidth = 2.5f,
    cap = StrokeCap.Round
  )
}

private fun DrawScope.drawHappyEye(center: Offset, outlineColor: Color) {
  val path = Path().apply {
    moveTo(center.x - 12f, center.y + 2f)
    quadraticBezierTo(center.x, center.y - 12f, center.x + 12f, center.y + 2f)
  }
  drawPath(path, outlineColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
  // Pestañita
  drawLine(
    outlineColor,
    Offset(center.x + 10f, center.y - 2f),
    Offset(center.x + 16f, center.y - 8f),
    strokeWidth = 3f,
    cap = StrokeCap.Round
  )
}

// -------------------------------------------------------------
// VESTIMENTA (ROPA / CAMISETAS)
// -------------------------------------------------------------
private fun DrawScope.drawShirtOnBody(
  shirt: CapybaraShirt,
  cx: Float,
  cy: Float,
  strokeW: Float,
  outlineColor: Color
) {
  val shirtBounds = Rect(cx - 75f, cy - 20f, cx + 75f, cy + 60f)
  val shirtPath = Path().apply {
    addRoundRect(RoundRect(shirtBounds, CornerRadius(30f, 30f)))
  }

  when (shirt) {
    CapybaraShirt.NONE -> {}

    CapybaraShirt.STRIPED -> {
      // Fondo blanco
      drawPath(shirtPath, Color.White)
      // Rayas azules
      clipPath(shirtPath) {
        for (i in 0..5) {
          val y = cy - 20f + (i * 16f)
          drawRect(
            color = Color(0xFF1E88E5),
            topLeft = Offset(cx - 80f, y),
            size = Size(160f, 8f)
          )
        }
      }
      // Cuello rojo
      drawOval(
        color = Color(0xFFE53935),
        topLeft = Offset(cx - 30f, cy - 26f),
        size = Size(60f, 16f)
      )
      drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW))
    }

    CapybaraShirt.HEARTS -> {
      // Vestido rosa brillante
      drawPath(shirtPath, Color(0xFFFF4081))
      // Corazoncitos estampados
      val heartOffsets = listOf(
        Offset(cx - 45f, cy),
        Offset(cx, cy - 5f),
        Offset(cx + 45f, cy),
        Offset(cx - 25f, cy + 30f),
        Offset(cx + 25f, cy + 30f)
      )
      heartOffsets.forEach { drawMiniHeart(it, Color.White, 7f) }
      // Volante inferior
      drawOval(
        color = Color(0xFFFF80AB),
        topLeft = Offset(cx - 78f, cy + 50f),
        size = Size(156f, 20f)
      )
      drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW))
    }

    CapybaraShirt.RAINBOW_SWEATER -> {
      // Suéter a rayas arcoíris
      val rainbowBands = listOf(
        Color(0xFFFF5252),
        Color(0xFFFF9800),
        Color(0xFFFFEB3B),
        Color(0xFF4CAF50),
        Color(0xFF29B6F6),
        Color(0xFFAB47BC)
      )
      clipPath(shirtPath) {
        rainbowBands.forEachIndexed { i, color ->
          drawRect(
            color = color,
            topLeft = Offset(cx - 80f, cy - 20f + (i * 14f)),
            size = Size(160f, 14f)
          )
        }
      }
      drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW))
    }

    CapybaraShirt.SUPERHERO -> {
      // Pechera azul con estrella dorada
      drawPath(shirtPath, Color(0xFF1565C0))
      drawStar(Offset(cx, cy + 15f), 18f, Color(0xFFFFD700))
      drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW))
    }

    CapybaraShirt.HAWAIIAN -> {
      // Camisa turquesa tropical
      drawPath(shirtPath, Color(0xFF00B4D8))
      // Flores hawaianas
      val flowerPoints = listOf(
        Offset(cx - 40f, cy),
        Offset(cx + 35f, cy - 5f),
        Offset(cx - 20f, cy + 35f),
        Offset(cx + 30f, cy + 35f)
      )
      flowerPoints.forEach { drawMiniFlower(it, Color(0xFFFF5400), Color(0xFFFFDD00)) }
      drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW))
    }

    CapybaraShirt.DINOSAUR -> {
      // Disfraz verde con crestas amarillas
      drawPath(shirtPath, Color(0xFF43A047))
      // Púas de dinosaurio en la espalda
      for (i in 0..3) {
        val spikePath = Path().apply {
          val spikeX = cx + 55f + (i * 6f)
          val spikeY = cy - 10f + (i * 18f)
          moveTo(spikeX, spikeY)
          lineTo(spikeX + 16f, spikeY + 8f)
          lineTo(spikeX, spikeY + 16f)
          close()
        }
        drawPath(spikePath, Color(0xFFFFEB3B))
        drawPath(spikePath, outlineColor, style = Stroke(width = 3f))
      }
      drawPath(shirtPath, outlineColor, style = Stroke(width = strokeW))
    }
  }
}

// Capa de superhéroe trasera
private fun DrawScope.drawCapeBack(cx: Float, cy: Float) {
  val capePath = Path().apply {
    moveTo(cx - 50f, cy - 35f)
    cubicTo(cx - 120f, cy + 40f, cx - 100f, cy + 100f, cx - 60f, cy + 95f)
    cubicTo(cx, cy + 90f, cx + 70f, cy + 98f, cx + 110f, cy + 85f)
    cubicTo(cx + 95f, cy + 30f, cx + 50f, cy - 35f, cx + 45f, cy - 35f)
    close()
  }
  drawPath(capePath, Color(0xFFD32F2F))
  drawPath(capePath, Color(0xFF2C1810), style = Stroke(width = 4f, join = StrokeJoin.Round))
}

// -------------------------------------------------------------
// GAFAS
// -------------------------------------------------------------
private fun DrawScope.drawGlasses(glasses: CapybaraGlasses, cx: Float, cy: Float) {
  val headX = cx - 15f
  val eyeY = cy - 75f + 12f
  val leftEyeX = headX - 26f
  val rightEyeX = headX + 26f

  when (glasses) {
    CapybaraGlasses.NONE -> {}

    CapybaraGlasses.SUNGLASSES -> {
      // Montura negra y lentes oscuras
      val leftLens = Path().apply {
        addRoundRect(RoundRect(Rect(leftEyeX - 18f, eyeY - 14f, leftEyeX + 18f, eyeY + 16f), CornerRadius(10f, 10f)))
      }
      val rightLens = Path().apply {
        addRoundRect(RoundRect(Rect(rightEyeX - 18f, eyeY - 14f, rightEyeX + 18f, eyeY + 16f), CornerRadius(10f, 10f)))
      }
      // Puente central
      drawLine(Color(0xFF212121), Offset(leftEyeX + 16f, eyeY - 2f), Offset(rightEyeX - 16f, eyeY - 2f), strokeWidth = 5f)
      // Lentes
      drawPath(leftLens, Color(0xFF212121))
      drawPath(rightLens, Color(0xFF212121))
      // Reflejos blancos en diagonal
      drawLine(Color.White.copy(alpha = 0.6f), Offset(leftEyeX - 12f, eyeY - 8f), Offset(leftEyeX - 4f, eyeY + 10f), strokeWidth = 3f)
      drawLine(Color.White.copy(alpha = 0.6f), Offset(rightEyeX - 12f, eyeY - 8f), Offset(rightEyeX - 4f, eyeY + 10f), strokeWidth = 3f)
      // Borde
      drawPath(leftLens, Color.Black, style = Stroke(width = 3f))
      drawPath(rightLens, Color.Black, style = Stroke(width = 3f))
    }

    CapybaraGlasses.HEART_GLASSES -> {
      // Gafas de corazón rosa
      drawHeartGlassesLens(Offset(leftEyeX, eyeY))
      drawHeartGlassesLens(Offset(rightEyeX, eyeY))
      // Puente
      drawLine(Color(0xFFFF1493), Offset(leftEyeX + 14f, eyeY), Offset(rightEyeX - 14f, eyeY), strokeWidth = 4f)
    }

    CapybaraGlasses.GOLD_ROUND -> {
      // Gafas redondas doradas
      drawCircle(Color(0xFFFFD700).copy(alpha = 0.35f), radius = 17f, center = Offset(leftEyeX, eyeY))
      drawCircle(Color(0xFFFFD700), radius = 17f, center = Offset(leftEyeX, eyeY), style = Stroke(width = 4f))

      drawCircle(Color(0xFFFFD700).copy(alpha = 0.35f), radius = 17f, center = Offset(rightEyeX, eyeY))
      drawCircle(Color(0xFFFFD700), radius = 17f, center = Offset(rightEyeX, eyeY), style = Stroke(width = 4f))

      drawLine(Color(0xFFFFD700), Offset(leftEyeX + 16f, eyeY), Offset(rightEyeX - 16f, eyeY), strokeWidth = 4f)
      // Patillas
      drawLine(Color(0xFFFFD700), Offset(leftEyeX - 17f, eyeY), Offset(leftEyeX - 30f, eyeY - 4f), strokeWidth = 3.5f)
      drawLine(Color(0xFFFFD700), Offset(rightEyeX + 17f, eyeY), Offset(rightEyeX + 30f, eyeY - 4f), strokeWidth = 3.5f)
    }

    CapybaraGlasses.STAR_GLASSES -> {
      // Gafas de estrella amarilla
      drawStar(Offset(leftEyeX, eyeY), 22f, Color(0xFFFFEB3B).copy(alpha = 0.85f))
      drawStar(Offset(leftEyeX, eyeY), 22f, Color(0xFFF57F17), strokeWidth = 3.5f)

      drawStar(Offset(rightEyeX, eyeY), 22f, Color(0xFFFFEB3B).copy(alpha = 0.85f))
      drawStar(Offset(rightEyeX, eyeY), 22f, Color(0xFFF57F17), strokeWidth = 3.5f)

      drawLine(Color(0xFFF57F17), Offset(leftEyeX + 14f, eyeY), Offset(rightEyeX - 14f, eyeY), strokeWidth = 4f)
    }

    CapybaraGlasses.SNORKEL -> {
      // Máscara de buceo
      val maskRect = Rect(leftEyeX - 22f, eyeY - 16f, rightEyeX + 22f, eyeY + 18f)
      drawRoundRect(
        color = Color(0xFF00E5FF).copy(alpha = 0.45f),
        topLeft = Offset(maskRect.left, maskRect.top),
        size = Size(maskRect.width, maskRect.height),
        cornerRadius = CornerRadius(16f, 16f)
      )
      drawRoundRect(
        color = Color(0xFFFF3D00),
        topLeft = Offset(maskRect.left, maskRect.top),
        size = Size(maskRect.width, maskRect.height),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 4.5f)
      )
      // Tubo de snorkel amarillo a la derecha
      val tubePath = Path().apply {
        moveTo(rightEyeX + 22f, eyeY + 8f)
        cubicTo(rightEyeX + 38f, eyeY + 10f, rightEyeX + 42f, eyeY - 40f, rightEyeX + 35f, eyeY - 55f)
      }
      drawPath(tubePath, Color(0xFFFFEA00), style = Stroke(width = 7f, cap = StrokeCap.Round))
      drawPath(tubePath, Color(0xFF2C1810), style = Stroke(width = 1.5f))
    }
  }
}

private fun DrawScope.drawHeartGlassesLens(center: Offset) {
  val path = Path().apply {
    val s = 1.3f
    moveTo(center.x, center.y + 12f * s)
    cubicTo(center.x - 16f * s, center.y + 2f * s, center.x - 18f * s, center.y - 14f * s, center.x, center.y - 4f * s)
    cubicTo(center.x + 18f * s, center.y - 14f * s, center.x + 16f * s, center.y + 2f * s, center.x, center.y + 12f * s)
    close()
  }
  drawPath(path, Color(0xFFFF4081).copy(alpha = 0.45f))
  drawPath(path, Color(0xFFFF1493), style = Stroke(width = 4f))
}

// -------------------------------------------------------------
// GORROS Y ACCESORIOS SUPERIORES
// -------------------------------------------------------------
private fun DrawScope.drawHat(hat: CapybaraHat, cx: Float, cy: Float) {
  val headX = cx - 15f
  val topHeadY = cy - 75f - 15f
  val outline = Color(0xFF2C1810)

  when (hat) {
    CapybaraHat.NONE -> {}

    CapybaraHat.ORANGE -> {
      // Mandarina kawaii en la cabeza
      val orangeCenter = Offset(headX, topHeadY - 14f)
      // Sombra
      drawCircle(Color(0x33000000), radius = 18f, center = Offset(orangeCenter.x, orangeCenter.y + 5f))
      // Naranja
      drawCircle(Color(0xFFFF9100), radius = 20f, center = orangeCenter)
      drawCircle(Color(0xFFFFAB40), radius = 16f, center = Offset(orangeCenter.x - 3f, orangeCenter.y - 3f))
      drawCircle(outline, radius = 20f, center = orangeCenter, style = Stroke(width = 3.5f))
      // Tallo marrón
      drawLine(Color(0xFF5D4037), orangeCenter, Offset(orangeCenter.x, orangeCenter.y - 28f), strokeWidth = 4f, cap = StrokeCap.Round)
      // Hojita verde
      val leaf = Path().apply {
        moveTo(orangeCenter.x, orangeCenter.y - 25f)
        quadraticBezierTo(orangeCenter.x + 18f, orangeCenter.y - 32f, orangeCenter.x + 22f, orangeCenter.y - 22f)
        quadraticBezierTo(orangeCenter.x + 10f, orangeCenter.y - 18f, orangeCenter.x, orangeCenter.y - 25f)
        close()
      }
      drawPath(leaf, Color(0xFF4CAF50))
      drawPath(leaf, outline, style = Stroke(width = 2.5f))
    }

    CapybaraHat.FLOWER -> {
      // Flor hawaiana tropical rosada
      val flowerPos = Offset(headX + 28f, topHeadY)
      for (i in 0 until 5) {
        val angle = (i * 72f) * (Math.PI / 180f)
        val pX = flowerPos.x + cos(angle).toFloat() * 16f
        val pY = flowerPos.y + sin(angle).toFloat() * 16f
        drawCircle(Color(0xFFFF4081), radius = 12f, center = Offset(pX, pY))
        drawCircle(outline, radius = 12f, center = Offset(pX, pY), style = Stroke(width = 2.5f))
      }
      // Centro amarillo
      drawCircle(Color(0xFFFFEB3B), radius = 9f, center = flowerPos)
      drawCircle(outline, radius = 9f, center = flowerPos, style = Stroke(width = 2.5f))
    }

    CapybaraHat.CROWN -> {
      // Corona dorada de princesa real
      val crownPath = Path().apply {
        moveTo(headX - 35f, topHeadY + 5f)
        lineTo(headX - 42f, topHeadY - 35f)
        lineTo(headX - 16f, topHeadY - 15f)
        lineTo(headX, topHeadY - 45f)
        lineTo(headX + 16f, topHeadY - 15f)
        lineTo(headX + 42f, topHeadY - 35f)
        lineTo(headX + 35f, topHeadY + 5f)
        close()
      }
      drawPath(crownPath, Color(0xFFFFD700))
      drawPath(crownPath, outline, style = Stroke(width = 4f, join = StrokeJoin.Round))
      // Joyas en las puntas (rubí, diamante, zafiro)
      drawCircle(Color(0xFFE91E63), radius = 5f, center = Offset(headX - 42f, topHeadY - 35f))
      drawCircle(Color(0xFF00E5FF), radius = 6f, center = Offset(headX, topHeadY - 45f))
      drawCircle(Color(0xFF9C27B0), radius = 5f, center = Offset(headX + 42f, topHeadY - 35f))
    }

    CapybaraHat.PARTY_HAT -> {
      // Gorro de fiesta cónico
      val conePath = Path().apply {
        moveTo(headX - 28f, topHeadY + 5f)
        lineTo(headX, topHeadY - 55f)
        lineTo(headX + 28f, topHeadY + 5f)
        close()
      }
      drawPath(conePath, Color(0xFF00E676))
      // Rayas coloridas en el gorro
      clipPath(conePath) {
        drawLine(Color(0xFFFF1744), Offset(headX - 40f, topHeadY - 10f), Offset(headX + 40f, topHeadY - 30f), strokeWidth = 8f)
        drawLine(Color(0xFFFFD600), Offset(headX - 40f, topHeadY - 30f), Offset(headX + 40f, topHeadY - 50f), strokeWidth = 8f)
      }
      drawPath(conePath, outline, style = Stroke(width = 4f, join = StrokeJoin.Round))
      // Pompón superior
      drawCircle(Color(0xFFFF1744), radius = 10f, center = Offset(headX, topHeadY - 55f))
    }

    CapybaraHat.CAP -> {
      // Gorra deportiva roja
      val capDome = Path().apply {
        moveTo(headX - 40f, topHeadY + 2f)
        cubicTo(headX - 35f, topHeadY - 30f, headX + 35f, topHeadY - 30f, headX + 40f, topHeadY + 2f)
        close()
      }
      drawPath(capDome, Color(0xFFE53935))
      drawPath(capDome, outline, style = Stroke(width = 4f))
      // Visera
      val visor = Path().apply {
        moveTo(headX - 20f, topHeadY + 2f)
        quadraticBezierTo(headX + 45f, topHeadY - 5f, headX + 60f, topHeadY + 12f)
        quadraticBezierTo(headX + 30f, topHeadY + 10f, headX - 20f, topHeadY + 2f)
        close()
      }
      drawPath(visor, Color(0xFFC62828))
      drawPath(visor, outline, style = Stroke(width = 3.5f))
      // Botón superior
      drawCircle(Color(0xFFFFD54F), radius = 5f, center = Offset(headX, topHeadY - 25f))
    }

    CapybaraHat.WIZARD -> {
      // Gorro de mago púrpura
      val brim = Path().apply {
        addOval(Rect(headX - 52f, topHeadY - 6f, headX + 52f, topHeadY + 12f))
      }
      val cone = Path().apply {
        moveTo(headX - 36f, topHeadY)
        cubicTo(headX - 20f, topHeadY - 45f, headX - 10f, topHeadY - 65f, headX + 25f, topHeadY - 75f)
        cubicTo(headX + 10f, topHeadY - 55f, headX + 25f, topHeadY - 35f, headX + 36f, topHeadY)
        close()
      }
      drawPath(cone, Color(0xFF6200EA))
      drawPath(brim, Color(0xFF4A148C))
      drawPath(cone, outline, style = Stroke(width = 4f))
      drawPath(brim, outline, style = Stroke(width = 4f))
      // Luna dorada
      drawCircle(Color(0xFFFFD600), radius = 9f, center = Offset(headX + 2f, topHeadY - 32f))
      drawCircle(Color(0xFF6200EA), radius = 7f, center = Offset(headX + 6f, topHeadY - 34f))
    }

    CapybaraHat.BERET -> {
      // Boina francesa de artista
      val beretPath = Path().apply {
        addOval(Rect(headX - 48f, topHeadY - 22f, headX + 48f, topHeadY + 4f))
      }
      drawPath(beretPath, Color(0xFFD81B60))
      drawPath(beretPath, outline, style = Stroke(width = 4f))
      // Rabillo de la boina
      drawLine(Color(0xFF880E4F), Offset(headX, topHeadY - 22f), Offset(headX, topHeadY - 28f), strokeWidth = 4f, cap = StrokeCap.Round)
    }
  }
}

// -------------------------------------------------------------
// ZAPATOS / BOTITAS
// -------------------------------------------------------------
private fun DrawScope.drawBackShoes(shoes: CapybaraShoes, cx: Float, cy: Float) {
  val shoeColor = getShoeColor(shoes)
  val outline = Color(0xFF2C1810)

  // Bota trasera izquierda
  drawShoeShape(Offset(cx - 84f, cy + 72f), shoeColor, outline, shoes)
  // Bota trasera derecha
  drawShoeShape(Offset(cx + 48f, cy + 72f), shoeColor, outline, shoes)
}

private fun DrawScope.drawFrontShoes(shoes: CapybaraShoes, cx: Float, cy: Float) {
  val shoeColor = getShoeColor(shoes)
  val outline = Color(0xFF2C1810)

  // Bota delantera izquierda
  drawShoeShape(Offset(cx - 52f, cy + 76f), shoeColor, outline, shoes)
  // Bota delantera derecha
  drawShoeShape(Offset(cx + 18f, cy + 76f), shoeColor, outline, shoes)
}

private fun getShoeColor(shoes: CapybaraShoes): Color {
  return when (shoes) {
    CapybaraShoes.RAIN_BOOTS -> Color(0xFFFFEB3B)
    CapybaraShoes.SNEAKERS -> Color(0xFF00B0FF)
    CapybaraShoes.RED_BOOTS -> Color(0xFFE53935)
    CapybaraShoes.GOLD_SHOES -> Color(0xFFFFD700)
    CapybaraShoes.ROLLER_SKATES -> Color(0xFFFF4081)
    CapybaraShoes.NONE -> Color.Transparent
  }
}

private fun DrawScope.drawShoeShape(pos: Offset, color: Color, outline: Color, shoes: CapybaraShoes) {
  val shoeBounds = Rect(pos.x, pos.y, pos.x + 34f, pos.y + 24f)
  val path = Path().apply {
    addRoundRect(RoundRect(shoeBounds, topLeft = CornerRadius(8f, 8f), topRight = CornerRadius(14f, 14f), bottomLeft = CornerRadius(12f, 12f), bottomRight = CornerRadius(12f, 12f)))
  }
  drawPath(path, color)
  drawPath(path, outline, style = Stroke(width = 3.5f))

  if (shoes == CapybaraShoes.SNEAKERS) {
    // Cordones blancos y suela
    drawRect(Color.White, topLeft = Offset(pos.x, pos.y + 19f), size = Size(34f, 5f))
    drawLine(Color.White, Offset(pos.x + 6f, pos.y + 6f), Offset(pos.x + 18f, pos.y + 6f), strokeWidth = 2.5f)
  } else if (shoes == CapybaraShoes.ROLLER_SKATES) {
    // Ruedas de patín
    drawCircle(Color(0xFF00E5FF), radius = 5f, center = Offset(pos.x + 6f, pos.y + 28f))
    drawCircle(Color(0xFFFFEA00), radius = 5f, center = Offset(pos.x + 28f, pos.y + 28f))
  }
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
  // Alas
  drawOval(color, topLeft = Offset(pos.x - 12f, pos.y - 12f), size = Size(10f, 14f))
  drawOval(color, topLeft = Offset(pos.x + 2f, pos.y - 12f), size = Size(10f, 14f))
  drawOval(color.copy(alpha = 0.8f), topLeft = Offset(pos.x - 8f, pos.y), size = Size(8f, 10f))
  drawOval(color.copy(alpha = 0.8f), topLeft = Offset(pos.x, pos.y), size = Size(8f, 10f))
  // Cuerpo
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
    // Tallo
    drawRoundRect(
      Color(0xFFFFF9C4),
      topLeft = Offset(pos.x - 5f, pos.y),
      size = Size(10f, 18f),
      cornerRadius = CornerRadius(4f, 4f)
    )
    // Sombrero rojo
    val cap = Path().apply {
      moveTo(pos.x - 16f, pos.y + 4f)
      quadraticBezierTo(pos.x, pos.y - 20f, pos.x + 16f, pos.y + 4f)
      close()
    }
    drawPath(cap, Color(0xFFE53935))
    // Lunares blancos
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

private fun DrawScope.drawHappySparks(cx: Float, cy: Float) {
  drawMiniHeart(Offset(cx - 75f, cy - 85f), Color(0xFFFF4081), 12f)
  drawMiniHeart(Offset(cx + 65f, cy - 95f), Color(0xFFFF4081), 10f)
  drawStar(Offset(cx - 85f, cy - 35f), 10f, Color(0xFFFFD600))
  drawStar(Offset(cx + 80f, cy - 45f), 12f, Color(0xFFFFD600))
}
