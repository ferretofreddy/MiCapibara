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
 * Rediseñado con anatomía fiel al cartoon clásico: cuerpo barril sentado, hocico romo característico,
 * orejas diminutas, ojos tiernos y silueta completa adaptable para todas las prendas.
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
    initialValue = -3f,
    targetValue = 3f,
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

    // 2. Dibujar al Capibara en tamaño generoso y centrado
    val capyCenterX = width * 0.48f
    val capyCenterY = height * 0.52f + breathOffset
    val capyScale = (width.coerceAtMost(height) / 290f).coerceIn(0.9f, 2.0f)

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

  // Colinas verdes onduladas (fondo y primer plano)
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

  // Mariposas revoloteando
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

  // Rayos del sol
  for (i in 0 until 8) {
    val angle = (i * 45f + anim * 0.5f) * (Math.PI / 180f)
    val startX = sunCenter.x + cos(angle).toFloat() * 44f
    val startY = sunCenter.y + sin(angle).toFloat() * 44f
    val endX = sunCenter.x + cos(angle).toFloat() * 62f
    val endY = sunCenter.y + sin(angle).toFloat() * 62f
    drawLine(Color(0xFFFFD54F), Offset(startX, startY), Offset(endX, endY), strokeWidth = 3f, cap = StrokeCap.Round)
  }

  // Nube de playa
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

  // Olas blancas
  val waveFoam = Path().apply {
    moveTo(0f, h * 0.62f)
    cubicTo(w * 0.3f, h * 0.60f + sin(anim * 0.06f) * 4f, w * 0.7f, h * 0.64f, w, h * 0.61f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(waveFoam, Color(0xFFE0F7FA))

  // Arena dorada de la playa
  val sandPath = Path().apply {
    moveTo(0f, h * 0.66f)
    cubicTo(w * 0.4f, h * 0.62f, w * 0.75f, h * 0.68f, w, h * 0.65f)
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(sandPath, Color(0xFFFFE082))

  // Palmera en la esquina derecha
  val palmTrunk = Path().apply {
    moveTo(w * 0.96f, h * 0.75f)
    quadraticBezierTo(w * 0.90f, h * 0.45f, w * 0.85f, h * 0.28f)
    quadraticBezierTo(w * 0.94f, h * 0.45f, w * 0.99f, h * 0.75f)
    close()
  }
  drawPath(palmTrunk, Color(0xFF795548))

  // Hojas de palmera verdes
  val palmTop = Offset(w * 0.86f, h * 0.28f)
  val leafAngles = listOf(-160f, -120f, -70f, -20f, 30f)
  leafAngles.forEach { deg ->
    val rad = deg * (Math.PI / 180f)
    val endX = palmTop.x + cos(rad).toFloat() * 70f
    val endY = palmTop.y + sin(rad).toFloat() * 45f
    val leafPath = Path().apply {
      moveTo(palmTop.x, palmTop.y)
      quadraticBezierTo((palmTop.x + endX) / 2f, palmTop.y - 18f, endX, endY)
      quadraticBezierTo((palmTop.x + endX) / 2f, palmTop.y + 10f, palmTop.x, palmTop.y)
      close()
    }
    drawPath(leafPath, Color(0xFF43A047))
  }

  // Cocos
  drawCircle(Color(0xFF4E342E), radius = 7f, center = Offset(palmTop.x - 4f, palmTop.y + 6f))
  drawCircle(Color(0xFF3E2723), radius = 7f, center = Offset(palmTop.x + 8f, palmTop.y + 8f))

  // Conchas marinas y estrellas de mar en la arena
  drawStarfish(Offset(w * 0.15f, h * 0.88f), Color(0xFFFF7043))
  drawSeashell(Offset(w * 0.85f, h * 0.89f), Color(0xFFFFCCBC))
}

private fun DrawScope.drawForestBackground(w: Float, h: Float, anim: Float) {
  // Cielo boscoso suave
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
// DIBUJO DEL CAPIBARA (ESTILO CARTOON CLÁSICO SENTADO)
// -------------------------------------------------------------
private fun DrawScope.drawCapybaraCharacter(
  state: CapybaraState,
  centerX: Float,
  centerY: Float,
  scaleFactor: Float
) {
  scale(scaleFactor, pivot = Offset(centerX, centerY)) {
    val c = state.color
    val outlineColor = Color(0xFF24140E)
    val strokeW = 3.2f

    // 1. Sombra en el suelo
    drawOval(
      color = Color(0x35000000),
      topLeft = Offset(centerX - 110f, centerY + 86f),
      size = Size(220f, 32f)
    )

    // 2. Capa de superhéroe trasera (si está seleccionada)
    if (state.shirt == CapybaraShirt.SUPERHERO) {
      drawCapeBack(centerX, centerY, outlineColor, strokeW)
    }

    // 3. Oreja trasera (pequeña, al fondo)
    drawBackEar(centerX, centerY, c, outlineColor, strokeW)

    // 4. Silueta anatómica completa del Capibara (cabeza, hocico y cuerpo barril sentado juntos)
    val fullBodySilhouette = createCapybaraSilhouettePath(centerX, centerY)

    // Relleno degradado cálido del pelaje
    drawPath(
      path = fullBodySilhouette,
      brush = Brush.verticalGradient(
        colors = listOf(c.highlightColor, c.baseColor, c.shadowColor),
        startY = centerY - 110f,
        endY = centerY + 95f
      )
    )

    // Pancita y pecho con tono cálido suave
    val chestSoftPath = Path().apply {
      moveTo(centerX - 20f, centerY + 15f)
      cubicTo(centerX + 35f, centerY + 15f, centerX + 50f, centerY + 65f, centerX + 20f, centerY + 85f)
      cubicTo(centerX - 10f, centerY + 90f, centerX - 50f, centerY + 70f, centerX - 20f, centerY + 15f)
      close()
    }
    drawPath(chestSoftPath, c.bellyColor.copy(alpha = 0.50f))

    // 5. Pata delantera lejana (detrás de la pata frontal)
    drawFarFrontLeg(centerX, centerY, c, outlineColor, strokeW)
    if (state.shoes != CapybaraShoes.NONE) {
      drawFarFrontShoe(state.shoes, centerX, centerY, outlineColor, strokeW)
    }

    // 6. Ropa que envuelve toda la silueta completa del capibara
    if (state.shirt != CapybaraShirt.NONE) {
      drawFullSilhouetteClothing(
        shirt = state.shirt,
        bodyPath = fullBodySilhouette,
        cx = centerX,
        cy = centerY,
        strokeW = strokeW,
        outlineColor = outlineColor
      )
    }

    // 7. Pliegue de la pata trasera sentada (muslo/anca curva)
    drawHindLegFlank(centerX, centerY, c, outlineColor, strokeW)

    // 8. Pata delantera cercana (frontal)
    drawNearFrontLeg(centerX, centerY, c, outlineColor, strokeW)

    // Zapatos cercanos (pie delantero y pie trasero sentado)
    if (state.shoes != CapybaraShoes.NONE) {
      drawNearShoes(state.shoes, centerX, centerY, outlineColor, strokeW)
    }

    // 9. Contorno principal de toda la silueta
    drawPath(fullBodySilhouette, outlineColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

    // 10. Hocico característico grande, romboidal y redondeado (marrón oscuro)
    drawSnoutMuzzle(centerX, centerY, outlineColor, strokeW)

    // 11. Oreja delantera (primer plano)
    drawFrontEar(centerX, centerY, c, outlineColor, strokeW)

    // 12. Ojo dulce / relajado (estilo dibujo animado)
    drawSweetEye(centerX, centerY, state.isHappy, outlineColor)

    // 13. Gafas (si tiene)
    if (state.glasses != CapybaraGlasses.NONE) {
      drawGlasses(state.glasses, centerX, centerY)
    }

    // 14. Gorro / Accesorio superior
    if (state.hat != CapybaraHat.NONE) {
      drawHat(state.hat, centerX, centerY)
    }

    // 15. Efecto de felicidad (corazones/estrellitas si está alegre)
    if (state.isHappy || state.happinessCount > 1) {
      drawHappySparks(centerX, centerY)
    }
  }
}

/**
 * Genera el camino vectorial de la silueta completa del capibara sentado mirando a la derecha.
 */
private fun createCapybaraSilhouettePath(cx: Float, cy: Float): Path {
  return Path().apply {
    // Empezamos en la parte superior de la cabeza (frente)
    moveTo(cx - 30f, cy - 95f)
    
    // Curva superior de la cabeza hacia la frente y hocico
    cubicTo(cx - 10f, cy - 98f, cx + 25f, cy - 92f, cx + 46f, cy - 70f)
    
    // Punta del hocico ancho y romo (frente frontal del capibara)
    cubicTo(cx + 68f, cy - 58f, cx + 72f, cy - 35f, cx + 68f, cy - 20f)
    
    // Barbilla inferior y transición directa al pecho/garganta sin cuello visible
    cubicTo(cx + 62f, cy - 5f, cx + 48f, cy + 2f, cx + 42f, cy + 18f)
    
    // Pecho hacia abajo
    cubicTo(cx + 40f, cy + 30f, cx + 38f, cy + 50f, cx + 36f, cy + 70f)
    
    // Suelo bajo las patas
    lineTo(cx + 10f, cy + 92f)
    lineTo(cx - 55f, cy + 92f)
    
    // Curva del anca/trasero sentado apoyado
    cubicTo(cx - 85f, cy + 92f, cx - 108f, cy + 78f, cx - 106f, cy + 42f)
    
    // Espalda ancha y curva ascendente (cuerpo barril)
    cubicTo(cx - 102f, cy + 8f, cx - 78f, cy - 35f, cx - 60f, cy - 65f)
    
    // Conexión directa de la espalda con la parte trasera de la cabeza
    cubicTo(cx - 50f, cy - 80f, cx - 42f, cy - 92f, cx - 30f, cy - 95f)
    
    close()
  }
}

// Oreja trasera (pequeña y al fondo)
private fun DrawScope.drawBackEar(
  cx: Float,
  cy: Float,
  c: CapybaraColor,
  outline: Color,
  strokeW: Float
) {
  val earCenter = Offset(cx - 5f, cy - 96f)
  val earPath = Path().apply {
    moveTo(earCenter.x - 8f, earCenter.y + 4f)
    cubicTo(earCenter.x - 10f, earCenter.y - 14f, earCenter.x + 8f, earCenter.y - 18f, earCenter.x + 10f, earCenter.y)
    close()
  }
  drawPath(earPath, c.shadowColor)
  drawPath(earPath, outline, style = Stroke(width = strokeW))
}

// Oreja delantera (primer plano)
private fun DrawScope.drawFrontEar(
  cx: Float,
  cy: Float,
  c: CapybaraColor,
  outline: Color,
  strokeW: Float
) {
  val earCenter = Offset(cx - 32f, cy - 88f)
  val earPath = Path().apply {
    moveTo(earCenter.x - 12f, earCenter.y + 6f)
    cubicTo(earCenter.x - 16f, earCenter.y - 18f, earCenter.x + 12f, earCenter.y - 20f, earCenter.x + 14f, earCenter.y + 2f)
    close()
  }
  drawPath(earPath, c.baseColor)
  
  // Interior de la oreja suave
  val innerEar = Path().apply {
    moveTo(earCenter.x - 6f, earCenter.y + 2f)
    cubicTo(earCenter.x - 8f, earCenter.y - 11f, earCenter.x + 6f, earCenter.y - 12f, earCenter.x + 7f, earCenter.y)
    close()
  }
  drawPath(innerEar, Color(0xFF5D4037).copy(alpha = 0.5f))
  drawPath(earPath, outline, style = Stroke(width = strokeW))
}

// Pata delantera lejana
private fun DrawScope.drawFarFrontLeg(
  cx: Float,
  cy: Float,
  c: CapybaraColor,
  outline: Color,
  strokeW: Float
) {
  val legPath = Path().apply {
    moveTo(cx + 12f, cy + 42f)
    lineTo(cx + 22f, cy + 42f)
    lineTo(cx + 20f, cy + 90f)
    lineTo(cx + 5f, cy + 90f)
    close()
  }
  drawPath(legPath, c.shadowColor)
  drawPath(legPath, outline, style = Stroke(width = strokeW))
}

// Pata delantera cercana con dedos
private fun DrawScope.drawNearFrontLeg(
  cx: Float,
  cy: Float,
  c: CapybaraColor,
  outline: Color,
  strokeW: Float
) {
  val legPath = Path().apply {
    moveTo(cx + 24f, cy + 32f)
    cubicTo(cx + 34f, cy + 32f, cx + 38f, cy + 50f, cx + 36f, cy + 90f)
    lineTo(cx + 16f, cy + 90f)
    cubicTo(cx + 18f, cy + 60f, cx + 18f, cy + 45f, cx + 24f, cy + 32f)
    close()
  }
  drawPath(legPath, c.baseColor)
  drawPath(legPath, outline, style = Stroke(width = strokeW))

  // Deditos de la pata delantera
  drawLine(outline, Offset(cx + 23f, cy + 82f), Offset(cx + 23f, cy + 90f), strokeWidth = 2.5f, cap = StrokeCap.Round)
  drawLine(outline, Offset(cx + 30f, cy + 82f), Offset(cx + 30f, cy + 90f), strokeWidth = 2.5f, cap = StrokeCap.Round)
}

// Muslo y pata trasera sentada
private fun DrawScope.drawHindLegFlank(
  cx: Float,
  cy: Float,
  c: CapybaraColor,
  outline: Color,
  strokeW: Float
) {
  // Curva del muslo sentado
  val thighCurve = Path().apply {
    moveTo(cx - 72f, cy + 22f)
    cubicTo(cx - 45f, cy + 30f, cx - 35f, cy + 65f, cx - 55f, cy + 88f)
  }
  drawPath(thighCurve, outline, style = Stroke(width = strokeW, cap = StrokeCap.Round))

  // Pie trasero apoyado horizontalmente hacia adelante
  val rearPaw = Path().apply {
    moveTo(cx - 58f, cy + 78f)
    cubicTo(cx - 30f, cy + 78f, cx - 18f, cy + 84f, cx - 18f, cy + 92f)
    lineTo(cx - 58f, cy + 92f)
    close()
  }
  drawPath(rearPaw, c.baseColor)
  drawPath(rearPaw, outline, style = Stroke(width = strokeW))

  // Deditos pata trasera
  drawLine(outline, Offset(cx - 26f, cy + 84f), Offset(cx - 26f, cy + 92f), strokeWidth = 2.5f, cap = StrokeCap.Round)
  drawLine(outline, Offset(cx - 34f, cy + 84f), Offset(cx - 34f, cy + 92f), strokeWidth = 2.5f, cap = StrokeCap.Round)
}

// Hocico característico romboidal / redondeado más oscuro que el pelaje
private fun DrawScope.drawSnoutMuzzle(
  cx: Float,
  cy: Float,
  outline: Color,
  strokeW: Float
) {
  val snoutPath = Path().apply {
    moveTo(cx + 36f, cy - 65f)
    cubicTo(cx + 56f, cy - 58f, cx + 70f, cy - 38f, cx + 68f, cy - 20f)
    cubicTo(cx + 62f, cy - 5f, cx + 48f, cy + 2f, cx + 38f, cy - 2f)
    cubicTo(cx + 30f, cy - 10f, cx + 24f, cy - 38f, cx + 36f, cy - 65f)
    close()
  }
  // Relleno marrón oscuro característico
  drawPath(snoutPath, Color(0xFF6D402B))
  drawPath(snoutPath, outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))

  // Fosa nasal curva
  val nostril = Path().apply {
    moveTo(cx + 52f, cy - 42f)
    cubicTo(cx + 58f, cy - 46f, cx + 64f, cy - 42f, cx + 60f, cy - 38f)
  }
  drawPath(nostril, outline, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

  // Boquita relajada
  val mouth = Path().apply {
    moveTo(cx + 42f, cy - 14f)
    cubicTo(cx + 52f, cy - 10f, cx + 60f, cy - 14f, cx + 64f, cy - 20f)
  }
  drawPath(mouth, outline, style = Stroke(width = 3f, cap = StrokeCap.Round))
}

// Ojo dulce y sereno
private fun DrawScope.drawSweetEye(
  cx: Float,
  cy: Float,
  isHappy: Boolean,
  outline: Color
) {
  val eyePos = Offset(cx + 10f, cy - 62f)

  if (isHappy) {
    // Ojo cerrado de felicidad ^ relajado
    val happyEye = Path().apply {
      moveTo(eyePos.x - 10f, eyePos.y + 2f)
      cubicTo(eyePos.x - 2f, eyePos.y - 8f, eyePos.x + 6f, eyePos.y - 8f, eyePos.x + 12f, eyePos.y + 2f)
    }
    drawPath(happyEye, outline, style = Stroke(width = 3.8f, cap = StrokeCap.Round))
  } else {
    // Ojo relajado dulce con párpado horizontal tranquilo
    val relaxedEye = Path().apply {
      moveTo(eyePos.x - 10f, eyePos.y - 1f)
      cubicTo(eyePos.x - 2f, eyePos.y + 5f, eyePos.x + 8f, eyePos.y + 4f, eyePos.x + 12f, eyePos.y - 2f)
    }
    drawPath(relaxedEye, outline, style = Stroke(width = 3.8f, cap = StrokeCap.Round))
    // Pestañita sutil
    drawLine(outline, Offset(eyePos.x + 10f, eyePos.y - 1f), Offset(eyePos.x + 15f, eyePos.y - 5f), strokeWidth = 2.5f, cap = StrokeCap.Round)
  }
}

// -------------------------------------------------------------
// VESTIMENTA COMPLETA QUE CUBRE TODA LA SILUETA DEL CAPIBARA
// -------------------------------------------------------------
private fun DrawScope.drawFullSilhouetteClothing(
  shirt: CapybaraShirt,
  bodyPath: Path,
  cx: Float,
  cy: Float,
  strokeW: Float,
  outlineColor: Color
) {
  clipPath(bodyPath) {
    when (shirt) {
      CapybaraShirt.NONE -> {}

      CapybaraShirt.STRIPED -> {
        // Camiseta marinera que cubre todo el torso
        drawRect(Color.White, topLeft = Offset(cx - 120f, cy - 40f), size = Size(240f, 150f))
        for (i in 0..7) {
          val y = cy - 35f + (i * 16f)
          drawRect(Color(0xFF1E88E5), topLeft = Offset(cx - 120f, y), size = Size(240f, 8f))
        }
        // Cuello rojo vivo
        drawOval(Color(0xFFE53935), topLeft = Offset(cx - 15f, cy - 45f), size = Size(55f, 16f))
      }

      CapybaraShirt.HEARTS -> {
        // Vestido rosa que cubre todo el cuerpo
        drawRect(Color(0xFFFF4081), topLeft = Offset(cx - 120f, cy - 40f), size = Size(240f, 150f))
        val hearts = listOf(
          Offset(cx - 70f, cy),
          Offset(cx - 40f, cy - 20f),
          Offset(cx - 20f, cy + 20f),
          Offset(cx + 10f, cy - 10f),
          Offset(cx + 25f, cy + 30f),
          Offset(cx - 60f, cy + 40f),
          Offset(cx - 10f, cy + 60f)
        )
        hearts.forEach { drawMiniHeart(it, Color.White, 7f) }
        // Volante inferior
        drawRect(Color(0xFFFF80AB), topLeft = Offset(cx - 120f, cy + 72f), size = Size(240f, 25f))
      }

      CapybaraShirt.RAINBOW_SWEATER -> {
        // Suéter con franjas arcoíris por todo el cuerpo
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
          drawRect(color, topLeft = Offset(cx - 120f, cy - 40f + index * 18f), size = Size(240f, 18f))
        }
      }

      CapybaraShirt.SUPERHERO -> {
        // Traje de superhéroe azul completo
        drawRect(Color(0xFF1565C0), topLeft = Offset(cx - 120f, cy - 40f), size = Size(240f, 150f))
        // Cinturón amarillo dorado
        drawRect(Color(0xFFFFD700), topLeft = Offset(cx - 120f, cy + 35f), size = Size(240f, 10f))
        // Estrella dorada en el pecho
        drawStar(Offset(cx + 10f, cy + 5f), 18f, Color(0xFFFFD700))
      }

      CapybaraShirt.HAWAIIAN -> {
        // Camisa hawaiana turquesa
        drawRect(Color(0xFF00B4D8), topLeft = Offset(cx - 120f, cy - 40f), size = Size(240f, 150f))
        val flowerPoints = listOf(
          Offset(cx - 70f, cy + 10f),
          Offset(cx - 35f, cy - 15f),
          Offset(cx - 20f, cy + 35f),
          Offset(cx + 15f, cy),
          Offset(cx + 25f, cy + 45f),
          Offset(cx - 65f, cy + 50f)
        )
        flowerPoints.forEach { drawMiniFlower(it, Color(0xFFFF5400), Color(0xFFFFDD00)) }
      }

      CapybaraShirt.DINOSAUR -> {
        // Disfraz verde de dinosaurio
        drawRect(Color(0xFF43A047), topLeft = Offset(cx - 120f, cy - 40f), size = Size(240f, 150f))
        drawOval(Color(0xFF81C784), topLeft = Offset(cx - 10f, cy + 10f), size = Size(45f, 65f))
      }
    }
  }

  // Púas de dinosaurio en la espalda (fuera del clip para que sobresalgan por el lomo)
  if (shirt == CapybaraShirt.DINOSAUR) {
    for (i in 0..3) {
      val spikePath = Path().apply {
        val sX = cx - 55f - (i * 14f)
        val sY = cy - 60f + (i * 24f)
        moveTo(sX, sY)
        lineTo(sX - 16f, sY - 6f)
        lineTo(sX - 10f, sY + 12f)
        close()
      }
      drawPath(spikePath, Color(0xFFFFEB3B))
      drawPath(spikePath, outlineColor, style = Stroke(width = 2.8f))
    }
  }
}

// Capa de superhéroe trasera
private fun DrawScope.drawCapeBack(cx: Float, cy: Float, outline: Color, strokeW: Float) {
  val capePath = Path().apply {
    moveTo(cx - 35f, cy - 55f)
    cubicTo(cx - 120f, cy - 10f, cx - 135f, cy + 60f, cx - 95f, cy + 90f)
    cubicTo(cx - 70f, cy + 95f, cx - 50f, cy + 50f, cx - 45f, cy - 35f)
    close()
  }
  drawPath(capePath, Color(0xFFD32F2F))
  drawPath(capePath, outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))
}

// -------------------------------------------------------------
// GAFAS
// -------------------------------------------------------------
private fun DrawScope.drawGlasses(glasses: CapybaraGlasses, cx: Float, cy: Float) {
  val eyePos = Offset(cx + 10f, cy - 62f)

  when (glasses) {
    CapybaraGlasses.NONE -> {}

    CapybaraGlasses.SUNGLASSES -> {
      val lens = Path().apply {
        addRoundRect(RoundRect(Rect(eyePos.x - 16f, eyePos.y - 12f, eyePos.x + 18f, eyePos.y + 14f), CornerRadius(8f, 8f)))
      }
      drawPath(lens, Color(0xFF212121))
      // Reflejos blancos
      drawLine(Color.White.copy(alpha = 0.6f), Offset(eyePos.x - 8f, eyePos.y - 8f), Offset(eyePos.x, eyePos.y + 8f), strokeWidth = 2.5f)
      drawPath(lens, Color.Black, style = Stroke(width = 3f))
      // Patilla hacia la oreja
      drawLine(Color.Black, Offset(eyePos.x - 16f, eyePos.y - 2f), Offset(cx - 30f, cy - 80f), strokeWidth = 3f)
    }

    CapybaraGlasses.HEART_GLASSES -> {
      drawHeartGlassesLens(Offset(eyePos.x + 2f, eyePos.y))
      drawLine(Color(0xFFFF1493), Offset(eyePos.x - 14f, eyePos.y), Offset(cx - 30f, cy - 80f), strokeWidth = 3.5f)
    }

    CapybaraGlasses.GOLD_ROUND -> {
      drawCircle(Color(0xFFFFD700).copy(alpha = 0.35f), radius = 16f, center = eyePos)
      drawCircle(Color(0xFFFFD700), radius = 16f, center = eyePos, style = Stroke(width = 3.5f))
      drawLine(Color(0xFFFFD700), Offset(eyePos.x - 16f, eyePos.y), Offset(cx - 30f, cy - 80f), strokeWidth = 3f)
    }

    CapybaraGlasses.STAR_GLASSES -> {
      drawStar(eyePos, 20f, Color(0xFFFFEB3B).copy(alpha = 0.85f))
      drawStar(eyePos, 20f, Color(0xFFF57F17), strokeWidth = 3f)
      drawLine(Color(0xFFF57F17), Offset(eyePos.x - 16f, eyePos.y), Offset(cx - 30f, cy - 80f), strokeWidth = 3.5f)
    }

    CapybaraGlasses.SNORKEL -> {
      val maskRect = Rect(eyePos.x - 18f, eyePos.y - 14f, eyePos.x + 26f, eyePos.y + 16f)
      drawRoundRect(
        color = Color(0xFF00E5FF).copy(alpha = 0.45f),
        topLeft = Offset(maskRect.left, maskRect.top),
        size = Size(maskRect.width, maskRect.height),
        cornerRadius = CornerRadius(12f, 12f)
      )
      drawRoundRect(
        color = Color(0xFFFF3D00),
        topLeft = Offset(maskRect.left, maskRect.top),
        size = Size(maskRect.width, maskRect.height),
        cornerRadius = CornerRadius(12f, 12f),
        style = Stroke(width = 3.5f)
      )
      // Tubo de snorkel hacia arriba
      val tubePath = Path().apply {
        moveTo(eyePos.x + 26f, eyePos.y + 5f)
        cubicTo(eyePos.x + 40f, eyePos.y + 5f, eyePos.x + 42f, eyePos.y - 45f, eyePos.x + 36f, eyePos.y - 55f)
      }
      drawPath(tubePath, Color(0xFFFFEA00), style = Stroke(width = 6f, cap = StrokeCap.Round))
      drawPath(tubePath, Color(0xFF2C1810), style = Stroke(width = 1.5f))
    }
  }
}

private fun DrawScope.drawHeartGlassesLens(center: Offset) {
  val path = Path().apply {
    val s = 1.2f
    moveTo(center.x, center.y + 10f * s)
    cubicTo(center.x - 14f * s, center.y + 2f * s, center.x - 16f * s, center.y - 12f * s, center.x, center.y - 3f * s)
    cubicTo(center.x + 16f * s, center.y - 12f * s, center.x + 14f * s, center.y + 2f * s, center.x, center.y + 10f * s)
    close()
  }
  drawPath(path, Color(0xFFFF4081).copy(alpha = 0.45f))
  drawPath(path, Color(0xFFFF1493), style = Stroke(width = 3.5f))
}

// -------------------------------------------------------------
// GORROS Y ACCESORIOS SUPERIORES
// -------------------------------------------------------------
private fun DrawScope.drawHat(hat: CapybaraHat, cx: Float, cy: Float) {
  val headTop = Offset(cx - 5f, cy - 96f)
  val outline = Color(0xFF24140E)

  when (hat) {
    CapybaraHat.NONE -> {}

    CapybaraHat.ORANGE -> {
      // Mandarina kawaii en la cabeza (el toque clásico capibara)
      val orangeCenter = Offset(headTop.x, headTop.y - 16f)
      drawCircle(Color(0x33000000), radius = 18f, center = Offset(orangeCenter.x, orangeCenter.y + 6f))
      drawCircle(Color(0xFFFF9100), radius = 20f, center = orangeCenter)
      drawCircle(Color(0xFFFFAB40), radius = 15f, center = Offset(orangeCenter.x - 3f, orangeCenter.y - 3f))
      drawCircle(outline, radius = 20f, center = orangeCenter, style = Stroke(width = 3f))
      // Tallo y hoja
      drawLine(Color(0xFF5D4037), orangeCenter, Offset(orangeCenter.x, orangeCenter.y - 28f), strokeWidth = 3.5f, cap = StrokeCap.Round)
      val leaf = Path().apply {
        moveTo(orangeCenter.x, orangeCenter.y - 25f)
        quadraticBezierTo(orangeCenter.x + 16f, orangeCenter.y - 32f, orangeCenter.x + 20f, orangeCenter.y - 22f)
        quadraticBezierTo(orangeCenter.x + 10f, orangeCenter.y - 18f, orangeCenter.x, orangeCenter.y - 25f)
        close()
      }
      drawPath(leaf, Color(0xFF4CAF50))
      drawPath(leaf, outline, style = Stroke(width = 2.2f))
    }

    CapybaraHat.FLOWER -> {
      // Flor hawaiana tropical rosada
      val flowerPos = Offset(headTop.x - 22f, headTop.y + 2f)
      for (i in 0 until 5) {
        val angle = (i * 72f) * (Math.PI / 180f)
        val pX = flowerPos.x + cos(angle).toFloat() * 15f
        val pY = flowerPos.y + sin(angle).toFloat() * 15f
        drawCircle(Color(0xFFFF4081), radius = 11f, center = Offset(pX, pY))
        drawCircle(outline, radius = 11f, center = Offset(pX, pY), style = Stroke(width = 2.2f))
      }
      drawCircle(Color(0xFFFFEB3B), radius = 8f, center = flowerPos)
      drawCircle(outline, radius = 8f, center = flowerPos, style = Stroke(width = 2.2f))
    }

    CapybaraHat.CROWN -> {
      // Corona dorada de princesa real
      val crownPath = Path().apply {
        moveTo(headTop.x - 28f, headTop.y + 5f)
        lineTo(headTop.x - 35f, headTop.y - 32f)
        lineTo(headTop.x - 12f, headTop.y - 14f)
        lineTo(headTop.x, headTop.y - 40f)
        lineTo(headTop.x + 12f, headTop.y - 14f)
        lineTo(headTop.x + 35f, headTop.y - 32f)
        lineTo(headTop.x + 28f, headTop.y + 5f)
        close()
      }
      drawPath(crownPath, Color(0xFFFFD700))
      drawPath(crownPath, outline, style = Stroke(width = 3.5f, join = StrokeJoin.Round))
      drawCircle(Color(0xFFE91E63), radius = 4.5f, center = Offset(headTop.x - 35f, headTop.y - 32f))
      drawCircle(Color(0xFF00E5FF), radius = 5.5f, center = Offset(headTop.x, headTop.y - 40f))
      drawCircle(Color(0xFF9C27B0), radius = 4.5f, center = Offset(headTop.x + 35f, headTop.y - 32f))
    }

    CapybaraHat.PARTY_HAT -> {
      // Gorro de fiesta cónico
      val conePath = Path().apply {
        moveTo(headTop.x - 24f, headTop.y + 5f)
        lineTo(headTop.x, headTop.y - 50f)
        lineTo(headTop.x + 24f, headTop.y + 5f)
        close()
      }
      drawPath(conePath, Color(0xFF00E676))
      clipPath(conePath) {
        drawLine(Color(0xFFFF1744), Offset(headTop.x - 35f, headTop.y - 8f), Offset(headTop.x + 35f, headTop.y - 26f), strokeWidth = 7f)
        drawLine(Color(0xFFFFD600), Offset(headTop.x - 35f, headTop.y - 26f), Offset(headTop.x + 35f, headTop.y - 44f), strokeWidth = 7f)
      }
      drawPath(conePath, outline, style = Stroke(width = 3.5f, join = StrokeJoin.Round))
      drawCircle(Color(0xFFFF1744), radius = 9f, center = Offset(headTop.x, headTop.y - 50f))
    }

    CapybaraHat.CAP -> {
      // Gorra deportiva roja mirando al frente
      val capDome = Path().apply {
        moveTo(headTop.x - 32f, headTop.y + 4f)
        cubicTo(headTop.x - 28f, headTop.y - 25f, headTop.x + 28f, headTop.y - 25f, headTop.x + 32f, headTop.y + 4f)
        close()
      }
      drawPath(capDome, Color(0xFFE53935))
      drawPath(capDome, outline, style = Stroke(width = 3.5f))
      // Visera
      val visor = Path().apply {
        moveTo(headTop.x - 10f, headTop.y + 4f)
        quadraticBezierTo(headTop.x + 40f, headTop.y - 2f, headTop.x + 52f, headTop.y + 12f)
        quadraticBezierTo(headTop.x + 25f, headTop.y + 10f, headTop.x - 10f, headTop.y + 4f)
        close()
      }
      drawPath(visor, Color(0xFFC62828))
      drawPath(visor, outline, style = Stroke(width = 3f))
      drawCircle(Color(0xFFFFD54F), radius = 4f, center = Offset(headTop.x, headTop.y - 20f))
    }

    CapybaraHat.WIZARD -> {
      // Gorro de mago púrpura
      val brim = Path().apply {
        addOval(Rect(headTop.x - 42f, headTop.y - 6f, headTop.x + 42f, headTop.y + 10f))
      }
      val cone = Path().apply {
        moveTo(headTop.x - 30f, headTop.y)
        cubicTo(headTop.x - 18f, headTop.y - 40f, headTop.x - 10f, headTop.y - 58f, headTop.x + 20f, headTop.y - 68f)
        cubicTo(headTop.x + 8f, headTop.y - 50f, headTop.x + 20f, headTop.y - 30f, headTop.x + 30f, headTop.y)
        close()
      }
      drawPath(cone, Color(0xFF6200EA))
      drawPath(brim, Color(0xFF4A148C))
      drawPath(cone, outline, style = Stroke(width = 3.5f))
      drawPath(brim, outline, style = Stroke(width = 3.5f))
      drawCircle(Color(0xFFFFD600), radius = 8f, center = Offset(headTop.x + 2f, headTop.y - 28f))
      drawCircle(Color(0xFF6200EA), radius = 6f, center = Offset(headTop.x + 6f, headTop.y - 30f))
    }

    CapybaraHat.BERET -> {
      // Boina de artista
      val beretPath = Path().apply {
        addOval(Rect(headTop.x - 38f, headTop.y - 20f, headTop.x + 38f, headTop.y + 4f))
      }
      drawPath(beretPath, Color(0xFFD81B60))
      drawPath(beretPath, outline, style = Stroke(width = 3.5f))
      drawLine(Color(0xFF880E4F), Offset(headTop.x, headTop.y - 20f), Offset(headTop.x, headTop.y - 26f), strokeWidth = 3.5f, cap = StrokeCap.Round)
    }
  }
}

// -------------------------------------------------------------
// ZAPATOS / BOTITAS
// -------------------------------------------------------------
private fun DrawScope.drawFarFrontShoe(
  shoes: CapybaraShoes,
  cx: Float,
  cy: Float,
  outline: Color,
  strokeW: Float
) {
  val shoeColor = getShoeColor(shoes)
  drawSingleShoe(Offset(cx + 6f, cy + 82f), shoeColor, outline, shoes, strokeW)
}

private fun DrawScope.drawNearShoes(
  shoes: CapybaraShoes,
  cx: Float,
  cy: Float,
  outline: Color,
  strokeW: Float
) {
  val shoeColor = getShoeColor(shoes)
  // Pie delantero cercano
  drawSingleShoe(Offset(cx + 18f, cy + 82f), shoeColor, outline, shoes, strokeW)
  // Pie trasero apoyado sentado
  drawSingleShoe(Offset(cx - 48f, cy + 82f), shoeColor, outline, shoes, strokeW, width = 34f)
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

private fun DrawScope.drawSingleShoe(
  pos: Offset,
  color: Color,
  outline: Color,
  shoes: CapybaraShoes,
  strokeW: Float,
  width: Float = 24f
) {
  val shoeBounds = Rect(pos.x, pos.y, pos.x + width, pos.y + 14f)
  val path = Path().apply {
    addRoundRect(
      RoundRect(
        shoeBounds,
        topLeft = CornerRadius(6f, 6f),
        topRight = CornerRadius(10f, 10f),
        bottomLeft = CornerRadius(8f, 8f),
        bottomRight = CornerRadius(8f, 8f)
      )
    )
  }
  drawPath(path, color)
  drawPath(path, outline, style = Stroke(width = strokeW))

  if (shoes == CapybaraShoes.SNEAKERS) {
    drawRect(Color.White, topLeft = Offset(pos.x, pos.y + 10f), size = Size(width, 4f))
  } else if (shoes == CapybaraShoes.ROLLER_SKATES) {
    drawCircle(Color(0xFF00E5FF), radius = 4f, center = Offset(pos.x + 4f, pos.y + 17f))
    drawCircle(Color(0xFFFFEA00), radius = 4f, center = Offset(pos.x + width - 4f, pos.y + 17f))
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

private fun DrawScope.drawHappySparks(cx: Float, cy: Float) {
  drawMiniHeart(Offset(cx - 75f, cy - 85f), Color(0xFFFF4081), 12f)
  drawMiniHeart(Offset(cx + 65f, cy - 95f), Color(0xFFFF4081), 10f)
  drawStar(Offset(cx - 85f, cy - 35f), 10f, Color(0xFFFFD600))
  drawStar(Offset(cx + 80f, cy - 45f), 12f, Color(0xFFFFD600))
}
