package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.CartoonSounds
import com.example.model.CapybaraState
import com.example.ui.components.CapybaraCanvas
import com.example.ui.components.CelebrationConfetti
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla completa de exhibición del Capibara terminado con confeti y modo interactivo.
 */
@Composable
fun CapybaraShowcaseScreen(
  state: CapybaraState,
  onEditAgain: () -> Unit,
  onResetNew: () -> Unit
) {
  var petCount by remember { mutableIntStateOf(state.happinessCount) }
  var isBouncing by remember { mutableStateOf(false) }
  var showPhotoCard by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  val scaleAnim by animateFloatAsState(
    targetValue = if (isBouncing) 1.08f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "showcase_bounce"
  )

  fun petCapybara() {
    CartoonSounds.playPop()
    petCount++
    isBouncing = true
    coroutineScope.launch {
      delay(900)
      isBouncing = false
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // 1. LIENZO A PANTALLA COMPLETA
    Box(
      modifier = Modifier
        .fillMaxSize()
        .scale(scaleAnim)
    ) {
      CapybaraCanvas(
        state = state.copy(isHappy = isBouncing || petCount > 3),
        onPet = { petCapybara() }
      )
    }

    // 2. EFECTO DE CONFETI CELEBRATORIO
    CelebrationConfetti(modifier = Modifier.fillMaxSize())

    // 3. BARRA SUPERIOR CON TÍTULO CELEBRATORIO
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter)
        .statusBarsPadding()
        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Surface(
        color = Color.White.copy(alpha = 0.94f),
        shape = RoundedCornerShape(26.dp),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFFFD54F)),
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "🎉",
              fontSize = 22.sp
            )
            Text(
              text = "¡Conoce a ${state.name}!",
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              color = Color(0xFF4A2810)
            )
            Text(
              text = "✨",
              fontSize = 22.sp
            )
          }

          Text(
            text = "¡El capibara más lindo del mundo!",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFF6D00)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Indicador de caricias
      Surface(
        onClick = { petCapybara() },
        color = Color(0xFFFF4081).copy(alpha = 0.95f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "¡$petCount caricias de amor!",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White
          )
        }
      }
    }

    // 4. BOTONES DE ACCIÓN INFERIORES
    Surface(
      color = Color.White.copy(alpha = 0.95f),
      shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
      shadowElevation = 16.dp,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Botón para foto de recuerdo
        Button(
          onClick = { showPhotoCard = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("photo_card_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6D00)
          ),
          shape = RoundedCornerShape(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "📸 Foto de Recuerdo",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Botón para seguir vistiendo
          Button(
            onClick = onEditAgain,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("edit_again_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF00B0FF)
            ),
            shape = RoundedCornerShape(22.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Seguir Vistiendo",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Botón para nuevo capibara
          OutlinedButton(
            onClick = onResetNew,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("new_capy_button"),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = Color(0xFF4A2810)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB74D)),
            shape = RoundedCornerShape(22.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Nuevo Capibara",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 5. DIÁLOGO POLAROID DE FOTO DE RECUERDO
    if (showPhotoCard) {
      Dialog(onDismissRequest = { showPhotoCard = false }) {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = Color.White,
          shadowElevation = 16.dp,
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Cabecera del diálogo con botón de cerrar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "✨ Tarjeta Postal ✨",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color(0xFF4E342E)
              )
              IconButton(onClick = { showPhotoCard = false }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Cerrar",
                  tint = Color(0xFF8D6E63)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Marco polaroid con el capibara
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF8E1))
                .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
            ) {
              CapybaraCanvas(
                state = state.copy(isHappy = true)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "🐾 ${state.name} 🐾",
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              color = Color(0xFFFF6D00)
            )

            Text(
              text = "¡Un gran amigo para siempre! 💖",
              fontSize = 14.sp,
              color = Color(0xFF6D4C41),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = { showPhotoCard = false },
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C853)
              ),
              shape = RoundedCornerShape(20.dp)
            ) {
              Text(
                text = "¡Qué lindo!",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }
    }
  }
}
