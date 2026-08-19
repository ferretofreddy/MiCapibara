package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundPlayer
import com.example.data.NeedsManager
import com.example.data.ProgressStore
import com.example.model.CapybaraMood
import com.example.model.CapybaraState
import com.example.ui.components.CelebrationConfetti
import com.example.ui.components.SpriteResources
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pantalla principal "El Hogar" del capibara.
 * Muestra la sala de estar como fondo, el capibara interactivo en el centro,
 * las 4 barras de necesidades superiores y la botonera de acciones inferiores.
 */
@Composable
fun HomeScreen(
  state: CapybaraState,
  onStateChange: (CapybaraState) -> Unit,
  onOpenCustomizer: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val progressStore = remember { ProgressStore(context) }
  val coroutineScope = rememberCoroutineScope()

  var totalPets by remember { mutableIntStateOf(progressStore.getTotalPets()) }
  var currentLevel by remember { mutableIntStateOf(progressStore.getLevel()) }
  var coins by remember { mutableIntStateOf(progressStore.getCoins()) }
  var xp by remember { mutableIntStateOf(progressStore.getXp()) }

  var hunger by remember { mutableIntStateOf(progressStore.getHunger()) }
  var hygiene by remember { mutableIntStateOf(progressStore.getHygiene()) }
  var energy by remember { mutableIntStateOf(progressStore.getEnergy()) }
  var funLevel by remember { mutableIntStateOf(progressStore.getFun()) }

  var showLevelUpDialog by remember { mutableStateOf(false) }
  var newLevelReached by remember { mutableIntStateOf(1) }
  var bonusCoinsEarned by remember { mutableIntStateOf(10) }

  var isHappyShowing by remember { mutableStateOf(false) }
  var isPetBounce by remember { mutableStateOf(false) }
  var happyTimerJob by remember { mutableStateOf<Job?>(null) }

  var placeholderMessage by remember { mutableStateOf<String?>(null) }
  var placeholderJob by remember { mutableStateOf<Job?>(null) }

  // Aplicar decaimiento de necesidades al abrir la pantalla
  LaunchedEffect(Unit) {
    val decayed = progressStore.applyDecay()
    hunger = decayed.hunger
    hygiene = decayed.hygiene
    energy = decayed.energy
    funLevel = decayed.funLevel
    coins = progressStore.getCoins()
    xp = progressStore.getXp()
    totalPets = progressStore.getTotalPets()
    currentLevel = progressStore.getLevel()

    onStateChange(
      state.copy(
        coins = coins,
        xp = xp,
        hunger = hunger,
        hygiene = hygiene,
        energy = energy,
        funLevel = funLevel
      )
    )
  }

  // Estado de ánimo según necesidades
  val currentMood = remember(hunger, hygiene, energy, funLevel) {
    NeedsManager.calculateMood(hunger, hygiene, energy, funLevel)
  }

  // Animación de respiración continua
  val infiniteTransition = rememberInfiniteTransition(label = "home_capy_breath")
  val breathOffsetY by infiniteTransition.animateFloat(
    initialValue = -4f,
    targetValue = 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "capy_breath"
  )

  // Animación de rebote al acariciar
  val petScale by animateFloatAsState(
    targetValue = if (isPetBounce) 1.12f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "pet_scale_spring"
  )

  // Manejador de caricias al tocar el capibara
  fun onPetCapybara() {
    SoundPlayer.playPop()
    val levelResult = progressStore.incrementPet()
    totalPets = levelResult.totalPets
    currentLevel = levelResult.currentLevel
    coins = levelResult.totalCoins
    xp = levelResult.currentXp

    // Aumentar un poco la diversión por caricias
    val newFun = (funLevel + 3).coerceIn(0, 100)
    funLevel = newFun
    progressStore.setNeeds(hunger, hygiene, energy, newFun)

    if (levelResult.leveledUp) {
      SoundPlayer.playDing()
      newLevelReached = levelResult.currentLevel
      bonusCoinsEarned = levelResult.bonusCoins
      showLevelUpDialog = true
    }

    isPetBounce = true
    isHappyShowing = true

    onStateChange(
      state.copy(
        isHappy = true,
        coins = coins,
        xp = xp,
        funLevel = newFun,
        happinessCount = state.happinessCount + 1
      )
    )

    happyTimerJob?.cancel()
    happyTimerJob = coroutineScope.launch {
      delay(200)
      isPetBounce = false
      // Mostrar sprite feliz por 1.5 segundos exactos
      delay(1300)
      isHappyShowing = false
      onStateChange(state.copy(isHappy = false))
    }
  }

  fun showPlaceholderBubble(buttonName: String) {
    SoundPlayer.playPop()
    placeholderJob?.cancel()
    placeholderMessage = "¡En camino! 🚧 ($buttonName)"
    placeholderJob = coroutineScope.launch {
      delay(2000)
      placeholderMessage = null
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen")
  ) {
    // 1. FONDO: Sala de estar a pantalla completa
    Image(
      painter = painterResource(id = SpriteResources.SCENE_LIVINGROOM),
      contentDescription = "Hogar del Capibara - Sala de estar",
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Sombra suave superior e inferior para mejorar contraste de UI
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .align(Alignment.TopCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color(0x99000000), Color.Transparent)
          )
        )
    )
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xAA000000))
          )
        )
    )

    // 2. CONTENIDO PRINCIPAL: Barras superiores, Capibara central y Botones inferiores
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // -------------------------------------------------------------
      // PANEL SUPERIOR: Nombre, Nivel, Monedas y 4 Necesidades
      // -------------------------------------------------------------
      TopStatusHeader(
        name = state.name,
        level = currentLevel,
        coins = coins,
        hunger = hunger,
        hygiene = hygiene,
        energy = energy,
        funLevel = funLevel
      )

      // -------------------------------------------------------------
      // CAPIBARA CENTRAL INTERACTIVO
      // -------------------------------------------------------------
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        InteractiveCapybaraHero(
          state = state,
          isHappyShowing = isHappyShowing,
          currentMood = currentMood,
          breathOffsetY = breathOffsetY,
          petScale = petScale,
          onPet = { onPetCapybara() }
        )
      }

      // -------------------------------------------------------------
      // BOTONERA INFERIOR: 6 Botones Redondeados
      // -------------------------------------------------------------
      BottomActionBar(
        onKitchenClick = { showPlaceholderBubble("Cocina") },
        onBathClick = { showPlaceholderBubble("Baño") },
        onSleepClick = { showPlaceholderBubble("Dormir") },
        onPlayClick = { showPlaceholderBubble("Jugar") },
        onShopClick = { showPlaceholderBubble("Tienda") },
        onDressClick = onOpenCustomizer
      )
    }

    // -------------------------------------------------------------
    // BURBUJA FLOTANTE PLACEHOLDER "¡En camino! 🚧"
    // -------------------------------------------------------------
    AnimatedVisibility(
      visible = placeholderMessage != null,
      enter = fadeIn() + slideInVertically(initialOffsetY = { -50 }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { -50 }),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .statusBarsPadding()
        .padding(top = 110.dp)
    ) {
      placeholderMessage?.let { msg ->
        Surface(
          color = Color(0xFF2C1810).copy(alpha = 0.95f),
          shape = RoundedCornerShape(24.dp),
          shadowElevation = 8.dp,
          modifier = Modifier.padding(horizontal = 24.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(text = "🚧", fontSize = 20.sp)
            Text(
              text = msg,
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    // -------------------------------------------------------------
    // DIÁLOGO DE CELEBRACIÓN DE SUBIDA DE NIVEL
    // -------------------------------------------------------------
    if (showLevelUpDialog) {
      Dialog(onDismissRequest = { showLevelUpDialog = false }) {
        Box(
          modifier = Modifier
            .fillMaxWidth(0.94f)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(28.dp))
        ) {
          CelebrationConfetti(modifier = Modifier.matchParentSize())

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Surface(
              color = Color(0xFFFFD54F),
              shape = CircleShape,
              modifier = Modifier.size(70.dp),
              shadowElevation = 4.dp
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "👑", fontSize = 36.sp)
              }
            }

            Text(
              text = "¡Nivel $newLevelReached Alcanzado!",
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF4A2810),
              textAlign = TextAlign.Center
            )

            Text(
              text = "¡Tu capibara te adora y ha crecido! 💕\n¡Has ganado +$bonusCoinsEarned Monedas de Bonus! 🪙✨",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF5D4037),
              textAlign = TextAlign.Center
            )

            Button(
              onClick = { showLevelUpDialog = false },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.padding(top = 6.dp)
            ) {
              Text(
                text = "¡Genial! 💖",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// COMPONENTES AUXILIARES DEL HOGAR
// -------------------------------------------------------------

@Composable
private fun TopStatusHeader(
  name: String,
  level: Int,
  coins: Int,
  hunger: Int,
  hygiene: Int,
  energy: Int,
  funLevel: Int
) {
  Surface(
    color = Color.White.copy(alpha = 0.93f),
    shape = RoundedCornerShape(24.dp),
    shadowElevation = 6.dp,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Fila superior: Nombre, Nivel y Monedas
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "🐾 $name",
            fontWeight = FontWeight.Black,
            fontSize = 17.sp,
            color = Color(0xFF4A2810)
          )
          Surface(
            color = Color(0xFFFF6D00),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = "Nvl $level",
              color = Color.White,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        // Monedas
        Surface(
          color = Color(0xFFFFF3D0),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(text = "🪙", fontSize = 16.sp)
            Text(
              text = "$coins",
              fontWeight = FontWeight.Black,
              fontSize = 14.sp,
              color = Color(0xFF5D4037)
            )
          }
        }
      }

      // Fila de las 4 Necesidades con iconos y barras
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        NeedBarItem(
          emoji = "🍉",
          label = "Hambre",
          value = hunger,
          color = Color(0xFFE53935),
          modifier = Modifier.weight(1f).testTag("hunger_bar")
        )
        NeedBarItem(
          emoji = "🛁",
          label = "Higiene",
          value = hygiene,
          color = Color(0xFF039BE5),
          modifier = Modifier.weight(1f).testTag("hygiene_bar")
        )
        NeedBarItem(
          emoji = "🛏️",
          label = "Energía",
          value = energy,
          color = Color(0xFF8E24AA),
          modifier = Modifier.weight(1f).testTag("energy_bar")
        )
        NeedBarItem(
          emoji = "🎈",
          label = "Diversión",
          value = funLevel,
          color = Color(0xFFFF8F00),
          modifier = Modifier.weight(1f).testTag("fun_bar")
        )
      }
    }
  }
}

@Composable
private fun NeedBarItem(
  emoji: String,
  label: String,
  value: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  val progressFraction = (value.toFloat() / 100f).coerceIn(0f, 1f)

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(text = emoji, fontSize = 13.sp)
      Text(
        text = "$value%",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF424242)
      )
    }

    LinearProgressIndicator(
      progress = { progressFraction },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = color,
      trackColor = color.copy(alpha = 0.25f)
    )
  }
}

@Composable
private fun InteractiveCapybaraHero(
  state: CapybaraState,
  isHappyShowing: Boolean,
  currentMood: CapybaraMood,
  breathOffsetY: Float,
  petScale: Float,
  onPet: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }

  // Recurso del sprite: CAPYBARA_HAPPY si se está acariciando o normal según color
  val spriteRes = if (isHappyShowing) {
    SpriteResources.CAPYBARA_HAPPY
  } else {
    SpriteResources.getCapybaraDrawable(state.color)
  }

  Box(
    modifier = Modifier
      .size(310.dp)
      .scale(petScale)
      .offset(y = breathOffsetY.dp)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onPet
      )
      .testTag("pet_capybara"),
    contentAlignment = Alignment.Center
  ) {
    // Sombra en el piso
    Canvas(
      modifier = Modifier
        .size(260.dp, 50.dp)
        .align(Alignment.BottomCenter)
        .offset(y = (-10).dp)
    ) {
      drawOval(
        color = Color(0x35000000),
        topLeft = Offset.Zero,
        size = Size(size.width, size.height)
      )
    }

    // Imagen del Sprite PNG
    Image(
      painter = painterResource(id = spriteRes),
      contentDescription = "Capibara en el Hogar",
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(280.dp)
    )

    // Capa de expresiones y efectos según necesidades / estado emocional
    Canvas(
      modifier = Modifier
        .matchParentSize()
    ) {
      if (isHappyShowing) {
        drawHappyHeartsAndStars()
      } else {
        when (currentMood) {
          CapybaraMood.SICK -> drawSickOverlay()
          CapybaraMood.HUNGRY -> drawHungryOverlay()
          CapybaraMood.DIRTY -> drawDirtyMudOverlay()
          CapybaraMood.TIRED -> drawTiredSleepOverlay()
          CapybaraMood.SAD -> drawSadTearsOverlay()
          CapybaraMood.HAPPY -> {
            // Brillos sutiles de felicidad
            drawSubtleHappyGlow()
          }
        }
      }
    }

    // Indicador "¡Tócame! 💕" flotante
    Surface(
      color = Color(0xFFFF4081).copy(alpha = 0.92f),
      shape = RoundedCornerShape(16.dp),
      shadowElevation = 4.dp,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 12.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
        Text(
          text = "¡Acariciar!",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}

// -------------------------------------------------------------
// DIBUJO DE EXPRESIONES Y EFECTOS SOBRE EL SPRITE
// -------------------------------------------------------------

private fun DrawScope.drawHappyHeartsAndStars() {
  val hearts = listOf(
    Offset(size.width * 0.25f, size.height * 0.22f),
    Offset(size.width * 0.75f, size.height * 0.20f),
    Offset(size.width * 0.50f, size.height * 0.12f),
    Offset(size.width * 0.18f, size.height * 0.40f),
    Offset(size.width * 0.82f, size.height * 0.38f)
  )
  hearts.forEach { pos ->
    drawHeartParticle(pos, Color(0xFFFF4081), 12f)
  }
}

private fun DrawScope.drawHeartParticle(center: Offset, color: Color, r: Float) {
  val path = androidx.compose.ui.graphics.Path().apply {
    moveTo(center.x, center.y + r)
    cubicTo(center.x - r * 1.3f, center.y - r * 0.2f, center.x - r * 1.3f, center.y - r * 1.2f, center.x, center.y - r * 0.5f)
    cubicTo(center.x + r * 1.3f, center.y - r * 1.2f, center.x + r * 1.3f, center.y - r * 0.2f, center.x, center.y + r)
    close()
  }
  drawPath(path, color)
}

private fun DrawScope.drawSubtleHappyGlow() {
  // Mejillas sonrojadas
  drawOval(
    color = Color(0x66FF8A80),
    topLeft = Offset(size.width * 0.28f, size.height * 0.52f),
    size = Size(28f, 16f)
  )
  drawOval(
    color = Color(0x66FF8A80),
    topLeft = Offset(size.width * 0.64f, size.height * 0.52f),
    size = Size(28f, 16f)
  )
}

private fun DrawScope.drawDirtyMudOverlay() {
  // Manchas de lodo suaves alrededor del capibara
  val mudColor = Color(0xAA5D4037)
  val mudSpots = listOf(
    Offset(size.width * 0.35f, size.height * 0.45f) to 14f,
    Offset(size.width * 0.65f, size.height * 0.48f) to 18f,
    Offset(size.width * 0.48f, size.height * 0.62f) to 16f,
    Offset(size.width * 0.28f, size.height * 0.68f) to 12f,
    Offset(size.width * 0.72f, size.height * 0.65f) to 15f
  )
  mudSpots.forEach { (pos, radius) ->
    drawCircle(mudColor, radius = radius, center = pos)
  }
}

private fun DrawScope.drawTiredSleepOverlay() {
  // Zzz flotantes
  drawOval(
    color = Color(0x55000000),
    topLeft = Offset(size.width * 0.32f, size.height * 0.40f),
    size = Size(24f, 10f)
  )
  drawOval(
    color = Color(0x55000000),
    topLeft = Offset(size.width * 0.60f, size.height * 0.40f),
    size = Size(24f, 10f)
  )
}

private fun DrawScope.drawHungryOverlay() {
  // Espirales de apetito y gotita
  drawCircle(
    color = Color(0x8829B6F6),
    radius = 7f,
    center = Offset(size.width * 0.72f, size.height * 0.42f)
  )
}

private fun DrawScope.drawSadTearsOverlay() {
  // Lagrimita tierna
  val tearColor = Color(0xCC42A5F5)
  drawCircle(tearColor, radius = 6f, center = Offset(size.width * 0.32f, size.height * 0.50f))
  drawCircle(tearColor, radius = 6f, center = Offset(size.width * 0.68f, size.height * 0.50f))
}

private fun DrawScope.drawSickOverlay() {
  // Tono mareado y curita
  drawCircle(
    color = Color(0x5581C784),
    radius = 16f,
    center = Offset(size.width * 0.75f, size.height * 0.30f)
  )
}

// -------------------------------------------------------------
// BOTONERA INFERIOR (6 BOTONES GRANDES)
// -------------------------------------------------------------

data class HomeActionItem(
  val label: String,
  val iconEmoji: String,
  val containerColor: Color,
  val testTag: String,
  val onClick: () -> Unit
)

@Composable
private fun BottomActionBar(
  onKitchenClick: () -> Unit,
  onBathClick: () -> Unit,
  onSleepClick: () -> Unit,
  onPlayClick: () -> Unit,
  onShopClick: () -> Unit,
  onDressClick: () -> Unit
) {
  val actions = listOf(
    HomeActionItem("Cocina", "🍉", Color(0xFFFF5252), "btn_kitchen", onKitchenClick),
    HomeActionItem("Baño", "🛁", Color(0xFF29B6F6), "btn_bath", onBathClick),
    HomeActionItem("Dormir", "🛏️", Color(0xFFAB47BC), "btn_sleep", onSleepClick),
    HomeActionItem("Jugar", "🎈", Color(0xFFFFB300), "btn_play", onPlayClick),
    HomeActionItem("Tienda", "🏪", Color(0xFF26A69A), "btn_shop", onShopClick),
    HomeActionItem("Vestir", "👕", Color(0xFF4CAF50), "btn_dress", onDressClick)
  )

  Surface(
    color = Color.White.copy(alpha = 0.94f),
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    shadowElevation = 12.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Distribución en 2 filas de 3 botones grandes redondeados
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        for (i in 0..2) {
          val item = actions[i]
          HomeActionButton(item = item, modifier = Modifier.weight(1f))
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        for (i in 3..5) {
          val item = actions[i]
          HomeActionButton(item = item, modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun HomeActionButton(
  item: HomeActionItem,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = item.onClick,
    shape = RoundedCornerShape(20.dp),
    color = item.containerColor,
    shadowElevation = 4.dp,
    modifier = modifier
      .height(60.dp)
      .testTag(item.testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = item.iconEmoji,
        fontSize = 22.sp
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = item.label,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp,
        color = Color.White
      )
    }
  }
}
