package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.CartoonSounds
import com.example.data.ProgressStore
import com.example.model.CapybaraBackground
import com.example.model.CapybaraColor
import com.example.model.CapybaraDrink
import com.example.model.CapybaraGlasses
import com.example.model.CapybaraHat
import com.example.model.CapybaraShirt
import com.example.model.CapybaraShoes
import com.example.model.CapybaraState
import com.example.model.CapybaraVehicle
import com.example.model.CustomizationCategory
import com.example.ui.components.CapybaraCanvas
import com.example.ui.components.CelebrationConfetti
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapybaraCustomizerScreen(
  state: CapybaraState,
  onStateChange: (CapybaraState) -> Unit,
  onDone: () -> Unit
) {
  val context = LocalContext.current
  val progressStore = remember { ProgressStore(context) }
  var totalPets by remember { mutableIntStateOf(progressStore.getTotalPets()) }
  var currentLevel by remember { mutableIntStateOf(progressStore.getLevel()) }
  var showLevelUpDialog by remember { mutableStateOf(false) }
  var newLevelReached by remember { mutableIntStateOf(1) }
  var lockedToastMessage by remember { mutableStateOf<String?>(null) }
  var lockedToastJob by remember { mutableStateOf<Job?>(null) }

  var selectedCategory by remember { mutableStateOf(CustomizationCategory.COLOR) }
  var isPetting by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  var reactionJob by remember { mutableStateOf<Job?>(null) }

  fun showLockedNotification(reqLevel: Int) {
    lockedToastJob?.cancel()
    lockedToastMessage = "🔒 ¡Se desbloquea en el Nivel $reqLevel!\nAcaricia a tu capibara para desbloquearlo 💕"
    lockedToastJob = coroutineScope.launch {
      delay(2500)
      lockedToastMessage = null
    }
  }

  val petScale by animateFloatAsState(
    targetValue = if (isPetting) 1.08f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "pet_bounce"
  )

  fun triggerReaction(newState: CapybaraState, isColor: Boolean = false) {
    if (isColor) {
      CartoonSounds.playDing()
    } else {
      CartoonSounds.playPop()
    }
    isPetting = true
    onStateChange(
      newState.copy(
        isHappy = true,
        happinessCount = state.happinessCount + 1
      )
    )
    reactionJob?.cancel()
    reactionJob = coroutineScope.launch {
      delay(1000)
      isPetting = false
      onStateChange(newState.copy(isHappy = false))
    }
  }

  fun triggerPetting() {
    CartoonSounds.playPop()
    val levelResult = progressStore.incrementPet()
    totalPets = levelResult.totalPets
    currentLevel = levelResult.currentLevel

    if (levelResult.leveledUp) {
      CartoonSounds.playDing()
      newLevelReached = levelResult.currentLevel
      showLevelUpDialog = true
    }

    isPetting = true
    onStateChange(
      state.copy(
        isHappy = true,
        happinessCount = state.happinessCount + 1,
        coins = levelResult.totalCoins,
        xp = levelResult.currentXp
      )
    )
    reactionJob?.cancel()
    reactionJob = coroutineScope.launch {
      delay(1000)
      isPetting = false
      onStateChange(state.copy(isHappy = false))
    }
  }

  fun randomizeAll() {
    CartoonSounds.playDing()
    val availableColors = CapybaraColor.values().filter { it.unlockLevel <= currentLevel }
    val availableHats = CapybaraHat.values().filter { it.unlockLevel <= currentLevel }
    val availableShirts = CapybaraShirt.values().filter { it.unlockLevel <= currentLevel }
    val availableGlasses = CapybaraGlasses.values().filter { it.unlockLevel <= currentLevel }
    val availableShoes = CapybaraShoes.values().filter { it.unlockLevel <= currentLevel }
    val availableBgs = CapybaraBackground.values().filter { it.unlockLevel <= currentLevel }
    val availableDrinks = CapybaraDrink.values().filter { it.unlockLevel <= currentLevel }
    val availableVehicles = CapybaraVehicle.values().filter { it.unlockLevel <= currentLevel }

    val randomColor = (if (availableColors.isNotEmpty()) availableColors else CapybaraColor.values().toList()).random()
    val randomHat = (if (availableHats.isNotEmpty()) availableHats else CapybaraHat.values().toList()).random()
    val randomShirt = (if (availableShirts.isNotEmpty()) availableShirts else CapybaraShirt.values().toList()).random()
    val randomGlasses = (if (availableGlasses.isNotEmpty()) availableGlasses else CapybaraGlasses.values().toList()).random()
    val randomShoes = (if (availableShoes.isNotEmpty()) availableShoes else CapybaraShoes.values().toList()).random()
    val randomBg = (if (availableBgs.isNotEmpty()) availableBgs else CapybaraBackground.values().toList()).random()
    val randomDrink = (if (availableDrinks.isNotEmpty()) availableDrinks else CapybaraDrink.values().toList()).random()
    val randomVehicle = (if (availableVehicles.isNotEmpty()) availableVehicles else CapybaraVehicle.values().toList()).random()

    val cuteNames = listOf("Capi", "Pelusa", "Brisa", "Choco", "Galleta", "Copito", "Lulu", "Pompón", "Chispita")
    
    val randomized = state.copy(
      color = randomColor,
      hat = randomHat,
      shirt = randomShirt,
      glasses = randomGlasses,
      shoes = randomShoes,
      background = randomBg,
      drink = randomDrink,
      vehicle = randomVehicle,
      name = cuteNames.random(),
      isHappy = true,
      happinessCount = state.happinessCount + 1
    )
    isPetting = true
    onStateChange(randomized)
    reactionJob?.cancel()
    reactionJob = coroutineScope.launch {
      delay(1000)
      isPetting = false
      onStateChange(randomized.copy(isHappy = false))
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "🐾 Mi Capibara",
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              color = Color(0xFF4A2810)
            )
            Surface(
              color = Color(0xFFFFD54F),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = "✨",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 14.sp
              )
            }
          }
        },
        actions = {
          Button(
            onClick = { randomizeAll() },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFFF6D00)
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            modifier = Modifier
              .testTag("randomize_button")
              .padding(end = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Casino,
              contentDescription = "Aleatorio",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "¡Sorpresa!",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0xFFFFF9E6)
        )
      )
    },
    bottomBar = {
      // Botón principal "¡LISTO!"
      Surface(
        color = Color(0xFFFFF9E6),
        shadowElevation = 12.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Button(
            onClick = onDone,
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp)
              .testTag("done_button")
              .shadow(8.dp, RoundedCornerShape(28.dp)),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF00C853)
            ),
            shape = RoundedCornerShape(28.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "¡LISTO! Mostrar Capibara",
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
          }
        }
      }
    },
    containerColor = Color(0xFFFFFDF5)
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. ÁREA DE VISTA PREVIA DEL CAPIBARA
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(24.dp))
          .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(24.dp))
      ) {
        // Lienzo animado
        Box(
          modifier = Modifier
            .fillMaxSize()
            .scale(petScale)
        ) {
          CapybaraCanvas(
            state = state,
            onPet = { triggerPetting() }
          )
        }

        // Cartel flotante con el nombre e indicador de nivel y progreso por caricias
        val nextThreshold = ProgressStore.getNextLevelThreshold(currentLevel)
        val prevThreshold = ProgressStore.getPrevLevelThreshold(currentLevel)
        val progressFraction = if (nextThreshold != null) {
          val span = nextThreshold - prevThreshold
          val currentInSpan = (totalPets - prevThreshold).coerceIn(0, span)
          (currentInSpan.toFloat() / span.toFloat()).coerceIn(0f, 1f)
        } else {
          1f
        }
        val remainingToNext = if (nextThreshold != null) (nextThreshold - totalPets).coerceAtLeast(0) else 0

        Column(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Insignia del nombre del capibara
          Surface(
            color = Color.White.copy(alpha = 0.94f),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "✨ ${state.name} ✨",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color(0xFF4A2810)
              )
              if (state.isHappy) {
                Icon(
                  imageVector = Icons.Default.Favorite,
                  contentDescription = null,
                  tint = Color(0xFFFF4081),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          // Barra de progreso y nivel de caricias
          Surface(
            color = Color(0xFFFFF9E6).copy(alpha = 0.95f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 3.dp,
            modifier = Modifier.testTag("level_progress_indicator")
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  color = Color(0xFFFF6D00),
                  shape = CircleShape
                ) {
                  Text(
                    text = "Nvl $currentLevel",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                  )
                }

                Text(
                  text = if (nextThreshold != null) {
                    "🐾 $totalPets caricias (Faltan $remainingToNext 💖)"
                  } else {
                    "👑 ¡Nivel Máximo! ($totalPets 💖)"
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF5D4037)
                )
              }

              // Barrita visual de progreso
              LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                  .width(140.dp)
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFFFF4081),
                trackColor = Color(0xFFFFE082)
              )
            }
          }
        }

        // Indicador interactivo para niños: "¡Tócame!"
        Surface(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp)
            .clickable { triggerPetting() },
          color = Color(0xFFFF4081),
          shape = RoundedCornerShape(16.dp),
          shadowElevation = 4.dp
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "💖 ¡Acariciar!",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      // 2. SELECTOR DE CATEGORÍAS (Barra con botones grandes y táctiles)
      Surface(
        color = Color(0xFFFFF3D6),
        modifier = Modifier.fillMaxWidth()
      ) {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(CustomizationCategory.values()) { category ->
            val isSelected = category == selectedCategory
            val bgColor by animateColorAsState(
              targetValue = if (isSelected) Color(0xFFFF6D00) else Color.White,
              label = "tab_color"
            )
            val textColor = if (isSelected) Color.White else Color(0xFF5D4037)

            Surface(
              onClick = { selectedCategory = category },
              shape = RoundedCornerShape(20.dp),
              color = bgColor,
              shadowElevation = if (isSelected) 4.dp else 1.dp,
              modifier = Modifier
                .testTag("category_tab_${category.name.lowercase()}")
                .height(48.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = category.iconEmoji,
                  fontSize = 18.sp
                )
                Text(
                  text = category.label,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                  fontSize = 14.sp,
                  color = textColor
                )
              }
            }
          }
        }
      }

      // 3. PANEL DE OPCIONES DE LA CATEGORÍA SELECCIONADA
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(170.dp)
          .background(Color(0xFFFFFDF5))
          .padding(top = 8.dp, bottom = 4.dp)
      ) {
        when (selectedCategory) {
          CustomizationCategory.COLOR -> {
            ColorSelectorSection(
              selectedColor = state.color,
              currentLevel = currentLevel,
              onColorSelect = { triggerReaction(state.copy(color = it), isColor = true) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.HAT -> {
            HatSelectorSection(
              selectedHat = state.hat,
              currentLevel = currentLevel,
              onHatSelect = { triggerReaction(state.copy(hat = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.SHIRT -> {
            ShirtSelectorSection(
              selectedShirt = state.shirt,
              currentLevel = currentLevel,
              onShirtSelect = { triggerReaction(state.copy(shirt = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.GLASSES -> {
            GlassesSelectorSection(
              selectedGlasses = state.glasses,
              currentLevel = currentLevel,
              onGlassesSelect = { triggerReaction(state.copy(glasses = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.SHOES -> {
            ShoesSelectorSection(
              selectedShoes = state.shoes,
              currentLevel = currentLevel,
              onShoesSelect = { triggerReaction(state.copy(shoes = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.BACKGROUND -> {
            BackgroundSelectorSection(
              selectedBg = state.background,
              currentLevel = currentLevel,
              onBgSelect = { triggerReaction(state.copy(background = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.DRINK -> {
            DrinkSelectorSection(
              selectedDrink = state.drink,
              currentLevel = currentLevel,
              onDrinkSelect = { triggerReaction(state.copy(drink = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.VEHICLE -> {
            VehicleSelectorSection(
              selectedVehicle = state.vehicle,
              currentLevel = currentLevel,
              onVehicleSelect = { triggerReaction(state.copy(vehicle = it), isColor = false) },
              onLockedClick = { showLockedNotification(it) }
            )
          }

          CustomizationCategory.NAME -> {
            NameSelectorSection(
              currentName = state.name,
              onNameChange = { onStateChange(state.copy(name = it)) }
            )
          }
        }
      }
    }
  }

  // Notificación flotante de accesorio bloqueado
  AnimatedVisibility(
    visible = lockedToastMessage != null,
    enter = fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { -40 }),
    exit = fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { -40 }),
    modifier = Modifier
      .statusBarsPadding()
      .padding(top = 16.dp)
  ) {
    lockedToastMessage?.let { msg ->
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
      ) {
        Surface(
          color = Color(0xFF2C1810),
          shape = RoundedCornerShape(20.dp),
          shadowElevation = 8.dp,
          modifier = Modifier.padding(horizontal = 24.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(text = "🔒", fontSize = 20.sp)
            Text(
              text = msg,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }
  }

  // 4. DIÁLOGO DE CELEBRACIÓN DE SUBIDA DE NIVEL CON CONFETI Y SONIDO
  if (showLevelUpDialog) {
    Dialog(onDismissRequest = { showLevelUpDialog = false }) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.94f)
          .clip(RoundedCornerShape(28.dp))
          .background(Color.White)
          .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(28.dp))
      ) {
        // Confeti festivo dentro del diálogo
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
              Text(text = "⭐", fontSize = 36.sp)
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
            text = "¡Tu capibara está súper feliz con tus caricias! 💕\n¡Has ganado +10 Monedas de Bonus! 🪙✨",
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
              text = "¡Genial! ✨",
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

// -------------------------------------------------------------
// COMPONENTES DE SELECCIÓN POR CATEGORÍA
// -------------------------------------------------------------

@Composable
private fun ColorSelectorSection(
  selectedColor: CapybaraColor,
  currentLevel: Int,
  onColorSelect: (CapybaraColor) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Elige el color de tu Capibara:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 14.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(CapybaraColor.values()) { colorItem ->
        val isSelected = colorItem == selectedColor
        val isLocked = colorItem.unlockLevel > currentLevel

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clickable {
              if (isLocked) {
                onLockedClick(colorItem.unlockLevel)
              } else {
                onColorSelect(colorItem)
              }
            }
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(62.dp)
              .shadow(if (isSelected) 6.dp else 2.dp, CircleShape)
              .clip(CircleShape)
              .background(if (isLocked) colorItem.baseColor.copy(alpha = 0.45f) else colorItem.baseColor)
              .border(
                width = if (isSelected) 4.dp else 2.dp,
                color = if (isSelected) Color(0xFFFF6D00) else if (isLocked) Color(0xFFBDBDBD) else Color.White,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isLocked) {
              Surface(
                color = Color(0x99000000),
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(text = "🔒", fontSize = 16.sp)
                }
              }
            } else if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Seleccionado",
                tint = Color.White,
                modifier = Modifier
                  .size(28.dp)
                  .shadow(2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isLocked) "🔒 Nvl ${colorItem.unlockLevel}" else colorItem.label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6D00) else if (isLocked) Color(0xFF9E9E9E) else Color(0xFF4E342E),
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
private fun HatSelectorSection(
  selectedHat: CapybaraHat,
  currentLevel: Int,
  onHatSelect: (CapybaraHat) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige un gorro o adorno para la cabeza:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraHat.values()) { hat ->
        ItemCardOption(
          title = hat.label,
          emoji = hat.iconEmoji,
          isSelected = hat == selectedHat,
          isLocked = hat.unlockLevel > currentLevel,
          unlockLevel = hat.unlockLevel,
          onClick = { onHatSelect(hat) },
          onLockedClick = { onLockedClick(hat.unlockLevel) }
        )
      }
    }
  }
}

@Composable
private fun ShirtSelectorSection(
  selectedShirt: CapybaraShirt,
  currentLevel: Int,
  onShirtSelect: (CapybaraShirt) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige la vestimenta de tu Capibara:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraShirt.values()) { shirt ->
        ItemCardOption(
          title = shirt.label,
          emoji = shirt.iconEmoji,
          isSelected = shirt == selectedShirt,
          isLocked = shirt.unlockLevel > currentLevel,
          unlockLevel = shirt.unlockLevel,
          onClick = { onShirtSelect(shirt) },
          onLockedClick = { onLockedClick(shirt.unlockLevel) }
        )
      }
    }
  }
}

@Composable
private fun GlassesSelectorSection(
  selectedGlasses: CapybaraGlasses,
  currentLevel: Int,
  onGlassesSelect: (CapybaraGlasses) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige unas gafas divertidas:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraGlasses.values()) { glasses ->
        ItemCardOption(
          title = glasses.label,
          emoji = glasses.iconEmoji,
          isSelected = glasses == selectedGlasses,
          isLocked = glasses.unlockLevel > currentLevel,
          unlockLevel = glasses.unlockLevel,
          onClick = { onGlassesSelect(glasses) },
          onLockedClick = { onLockedClick(glasses.unlockLevel) }
        )
      }
    }
  }
}

@Composable
private fun ShoesSelectorSection(
  selectedShoes: CapybaraShoes,
  currentLevel: Int,
  onShoesSelect: (CapybaraShoes) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige calzado para sus patitas:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraShoes.values()) { shoes ->
        ItemCardOption(
          title = shoes.label,
          emoji = shoes.iconEmoji,
          isSelected = shoes == selectedShoes,
          isLocked = shoes.unlockLevel > currentLevel,
          unlockLevel = shoes.unlockLevel,
          onClick = { onShoesSelect(shoes) },
          onLockedClick = { onLockedClick(shoes.unlockLevel) }
        )
      }
    }
  }
}

@Composable
private fun BackgroundSelectorSection(
  selectedBg: CapybaraBackground,
  currentLevel: Int,
  onBgSelect: (CapybaraBackground) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige el paisaje de fondo:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraBackground.values()) { bg ->
        val isSelected = bg == selectedBg
        val isLocked = bg.unlockLevel > currentLevel

        Surface(
          onClick = {
            if (isLocked) {
              onLockedClick(bg.unlockLevel)
            } else {
              onBgSelect(bg)
            }
          },
          shape = RoundedCornerShape(18.dp),
          color = if (isSelected) Color(0xFFFFF3E0) else if (isLocked) Color(0xFFF5F5F5) else Color.White,
          shadowElevation = if (isSelected) 5.dp else 2.dp,
          modifier = Modifier
            .width(135.dp)
            .height(115.dp)
            .border(
              width = if (isSelected) 3.5.dp else 1.dp,
              color = if (isSelected) Color(0xFFFF6D00) else if (isLocked) Color(0xFFE0E0E0) else Color(0xFFE0E0E0),
              shape = RoundedCornerShape(18.dp)
            )
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = bg.iconEmoji,
                fontSize = 32.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = bg.label,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color(0xFFFF6D00) else if (isLocked) Color(0xFF757575) else Color(0xFF3E2723),
                textAlign = TextAlign.Center,
                maxLines = 1
              )
              Text(
                text = if (isLocked) "Nivel ${bg.unlockLevel}" else when (bg) {
                  CapybaraBackground.BEACH -> "Sol y mar"
                  CapybaraBackground.FOREST -> "Árboles y setas"
                  CapybaraBackground.MEADOW -> "Arcoíris y flores"
                  CapybaraBackground.SUNSET -> "Atardecer mágico"
                  CapybaraBackground.RAINFOREST -> "Selva exótica"
                  CapybaraBackground.SPACE -> "Cosmos y estrellas"
                },
                fontSize = 10.sp,
                color = if (isLocked) Color(0xFFFF6D00) else Color(0xFF757575),
                fontWeight = if (isLocked) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1
              )
            }

            if (isLocked) {
              Surface(
                color = Color(0xCC212121),
                shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 12.dp),
                modifier = Modifier.align(Alignment.TopStart)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  Text(text = "🔒", fontSize = 10.sp)
                  Text(text = "Nvl ${bg.unlockLevel}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DrinkSelectorSection(
  selectedDrink: CapybaraDrink,
  currentLevel: Int,
  onDrinkSelect: (CapybaraDrink) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige una bebida refrescante:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraDrink.values()) { drink ->
        ItemCardOption(
          title = drink.label,
          emoji = drink.iconEmoji,
          isSelected = drink == selectedDrink,
          isLocked = drink.unlockLevel > currentLevel,
          unlockLevel = drink.unlockLevel,
          onClick = { onDrinkSelect(drink) },
          onLockedClick = { onLockedClick(drink.unlockLevel) }
        )
      }
    }
  }
}

@Composable
private fun VehicleSelectorSection(
  selectedVehicle: CapybaraVehicle,
  currentLevel: Int,
  onVehicleSelect: (CapybaraVehicle) -> Unit,
  onLockedClick: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = "Elige un medio de transporte:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
    )

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(CapybaraVehicle.values()) { vehicle ->
        ItemCardOption(
          title = vehicle.label,
          emoji = vehicle.iconEmoji,
          isSelected = vehicle == selectedVehicle,
          isLocked = vehicle.unlockLevel > currentLevel,
          unlockLevel = vehicle.unlockLevel,
          onClick = { onVehicleSelect(vehicle) },
          onLockedClick = { onLockedClick(vehicle.unlockLevel) }
        )
      }
    }
  }
}

@Composable
private fun NameSelectorSection(
  currentName: String,
  onNameChange: (String) -> Unit
) {
  val quickNames = listOf("Capi", "Pelusa", "Brisa", "Choco", "Galleta", "Copito", "Lulu", "Princesa", "Coco")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp)
  ) {
    Text(
      text = "Ponle un nombre lindo a tu Capibara:",
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = Color(0xFF6D4C41),
      modifier = Modifier.padding(bottom = 6.dp)
    )

    OutlinedTextField(
      value = currentName,
      onValueChange = { if (it.length <= 18) onNameChange(it) },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      placeholder = { Text("Escribe un nombre...") },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFFF6D00),
        unfocusedBorderColor = Color(0xFFFFB74D),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
      )
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(quickNames) { nameOption ->
        Surface(
          onClick = { onNameChange(nameOption) },
          shape = RoundedCornerShape(14.dp),
          color = if (currentName == nameOption) Color(0xFFFF6D00) else Color(0xFFFFE082),
          modifier = Modifier.height(34.dp)
        ) {
          Text(
            text = nameOption,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (currentName == nameOption) Color.White else Color(0xFF4E342E)
          )
        }
      }
    }
  }
}

@Composable
private fun ItemCardOption(
  title: String,
  emoji: String,
  isSelected: Boolean,
  isLocked: Boolean = false,
  unlockLevel: Int = 1,
  onClick: () -> Unit,
  onLockedClick: () -> Unit = {}
) {
  Surface(
    onClick = {
      if (isLocked) {
        onLockedClick()
      } else {
        onClick()
      }
    },
    shape = RoundedCornerShape(18.dp),
    color = if (isSelected) Color(0xFFFFF3E0) else if (isLocked) Color(0xFFF5F5F5) else Color.White,
    shadowElevation = if (isSelected) 5.dp else 2.dp,
    modifier = Modifier
      .width(112.dp)
      .height(115.dp)
      .border(
        width = if (isSelected) 3.5.dp else 1.dp,
        color = if (isSelected) Color(0xFFFF6D00) else if (isLocked) Color(0xFFE0E0E0) else Color(0xFFE0E0E0),
        shape = RoundedCornerShape(18.dp)
      )
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = emoji,
          fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = title,
          fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
          fontSize = 12.sp,
          color = if (isSelected) Color(0xFFFF6D00) else if (isLocked) Color(0xFF9E9E9E) else Color(0xFF3E2723),
          textAlign = TextAlign.Center,
          maxLines = 2,
          lineHeight = 14.sp
        )
      }

      if (isLocked) {
        Surface(
          color = Color(0xCC212121),
          shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 12.dp),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
          ) {
            Text(text = "🔒", fontSize = 10.sp)
            Text(text = "Nvl $unlockLevel", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}
