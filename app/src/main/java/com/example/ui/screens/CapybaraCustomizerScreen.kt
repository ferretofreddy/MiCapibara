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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.CartoonSounds
import com.example.model.CapybaraBackground
import com.example.model.CapybaraColor
import com.example.model.CapybaraGlasses
import com.example.model.CapybaraHat
import com.example.model.CapybaraShirt
import com.example.model.CapybaraShoes
import com.example.model.CapybaraState
import com.example.model.CustomizationCategory
import com.example.ui.components.CapybaraCanvas
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
  var selectedCategory by remember { mutableStateOf(CustomizationCategory.COLOR) }
  var isPetting by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  var reactionJob by remember { mutableStateOf<Job?>(null) }

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
    isPetting = true
    onStateChange(
      state.copy(
        isHappy = true,
        happinessCount = state.happinessCount + 1
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
    val randomColor = CapybaraColor.values().random()
    val randomHat = CapybaraHat.values().random()
    val randomShirt = CapybaraShirt.values().random()
    val randomGlasses = CapybaraGlasses.values().random()
    val randomShoes = CapybaraShoes.values().random()
    val randomBg = CapybaraBackground.values().random()
    val cuteNames = listOf("Capi", "Pelusa", "Brisa", "Choco", "Galleta", "Copito", "Lulu", "Pompón", "Chispita")
    
    val randomized = state.copy(
      color = randomColor,
      hat = randomHat,
      shirt = randomShirt,
      glasses = randomGlasses,
      shoes = randomShoes,
      background = randomBg,
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

        // Cartel flotante con el nombre del capibara
        Surface(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 10.dp),
          color = Color.White.copy(alpha = 0.92f),
          shape = RoundedCornerShape(20.dp),
          shadowElevation = 4.dp
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "✨ ${state.name} ✨",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 16.sp,
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
              onColorSelect = { triggerReaction(state.copy(color = it), isColor = true) }
            )
          }

          CustomizationCategory.HAT -> {
            HatSelectorSection(
              selectedHat = state.hat,
              onHatSelect = { triggerReaction(state.copy(hat = it), isColor = false) }
            )
          }

          CustomizationCategory.SHIRT -> {
            ShirtSelectorSection(
              selectedShirt = state.shirt,
              onShirtSelect = { triggerReaction(state.copy(shirt = it), isColor = false) }
            )
          }

          CustomizationCategory.GLASSES -> {
            GlassesSelectorSection(
              selectedGlasses = state.glasses,
              onGlassesSelect = { triggerReaction(state.copy(glasses = it), isColor = false) }
            )
          }

          CustomizationCategory.SHOES -> {
            ShoesSelectorSection(
              selectedShoes = state.shoes,
              onShoesSelect = { triggerReaction(state.copy(shoes = it), isColor = false) }
            )
          }

          CustomizationCategory.BACKGROUND -> {
            BackgroundSelectorSection(
              selectedBg = state.background,
              onBgSelect = { triggerReaction(state.copy(background = it), isColor = false) }
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
}

// -------------------------------------------------------------
// COMPONENTES DE SELECCIÓN POR CATEGORÍA
// -------------------------------------------------------------

@Composable
private fun ColorSelectorSection(
  selectedColor: CapybaraColor,
  onColorSelect: (CapybaraColor) -> Unit
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

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clickable { onColorSelect(colorItem) }
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(62.dp)
              .shadow(if (isSelected) 6.dp else 2.dp, CircleShape)
              .clip(CircleShape)
              .background(colorItem.baseColor)
              .border(
                width = if (isSelected) 4.dp else 2.dp,
                color = if (isSelected) Color(0xFFFF6D00) else Color.White,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
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
            text = colorItem.label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFF6D00) else Color(0xFF4E342E),
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
  onHatSelect: (CapybaraHat) -> Unit
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
          onClick = { onHatSelect(hat) }
        )
      }
    }
  }
}

@Composable
private fun ShirtSelectorSection(
  selectedShirt: CapybaraShirt,
  onShirtSelect: (CapybaraShirt) -> Unit
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
          onClick = { onShirtSelect(shirt) }
        )
      }
    }
  }
}

@Composable
private fun GlassesSelectorSection(
  selectedGlasses: CapybaraGlasses,
  onGlassesSelect: (CapybaraGlasses) -> Unit
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
          onClick = { onGlassesSelect(glasses) }
        )
      }
    }
  }
}

@Composable
private fun ShoesSelectorSection(
  selectedShoes: CapybaraShoes,
  onShoesSelect: (CapybaraShoes) -> Unit
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
          onClick = { onShoesSelect(shoes) }
        )
      }
    }
  }
}

@Composable
private fun BackgroundSelectorSection(
  selectedBg: CapybaraBackground,
  onBgSelect: (CapybaraBackground) -> Unit
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

        Surface(
          onClick = { onBgSelect(bg) },
          shape = RoundedCornerShape(18.dp),
          color = if (isSelected) Color(0xFFFFF3E0) else Color.White,
          shadowElevation = if (isSelected) 5.dp else 2.dp,
          modifier = Modifier
            .width(135.dp)
            .height(115.dp)
            .border(
              width = if (isSelected) 3.5.dp else 1.dp,
              color = if (isSelected) Color(0xFFFF6D00) else Color(0xFFE0E0E0),
              shape = RoundedCornerShape(18.dp)
            )
        ) {
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
              color = if (isSelected) Color(0xFFFF6D00) else Color(0xFF3E2723),
              textAlign = TextAlign.Center,
              maxLines = 1
            )
            Text(
              text = when (bg) {
                CapybaraBackground.BEACH -> "Sol y mar"
                CapybaraBackground.FOREST -> "Árboles y setas"
                CapybaraBackground.MEADOW -> "Arcoíris y flores"
                CapybaraBackground.SUNSET -> "Atardecer mágico"
              },
              fontSize = 10.sp,
              color = Color(0xFF757575),
              textAlign = TextAlign.Center,
              maxLines = 1
            )
          }
        }
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
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(18.dp),
    color = if (isSelected) Color(0xFFFFF3E0) else Color.White,
    shadowElevation = if (isSelected) 5.dp else 2.dp,
    modifier = Modifier
      .width(112.dp)
      .height(115.dp)
      .border(
        width = if (isSelected) 3.5.dp else 1.dp,
        color = if (isSelected) Color(0xFFFF6D00) else Color(0xFFE0E0E0),
        shape = RoundedCornerShape(18.dp)
      )
  ) {
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
        color = if (isSelected) Color(0xFFFF6D00) else Color(0xFF3E2723),
        textAlign = TextAlign.Center,
        maxLines = 2,
        lineHeight = 14.sp
      )
    }
  }
}
