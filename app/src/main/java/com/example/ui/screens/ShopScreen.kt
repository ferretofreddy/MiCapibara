package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundPlayer
import com.example.data.ProgressStore
import com.example.model.CapybaraState
import com.example.ui.components.SpriteResources
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Modelo de datos para los artículos de la tienda
 */
data class ShopItem(
  val id: String,
  val name: String,
  val category: String,
  val price: Int,
  @DrawableRes val drawableRes: Int
)

/**
 * Catálogo completo de alimentos disponibles en la tienda
 */
val SHOP_FOOD_ITEMS = listOf(
  // Frutas (5 Monedas)
  ShopItem("fruit_apple", "Manzana", "Fruta", 5, R.drawable.fruit_apple),
  ShopItem("fruit_banana", "Plátano", "Fruta", 5, R.drawable.fruit_banana),
  ShopItem("fruit_coconut", "Coco", "Fruta", 5, R.drawable.fruit_coconut),
  ShopItem("fruit_orange", "Naranja", "Fruta", 5, R.drawable.fruit_orange),
  ShopItem("fruit_peach", "Durazno", "Fruta", 5, R.drawable.fruit_peach),
  ShopItem("fruit_pineapple", "Piña", "Fruta", 5, R.drawable.fruit_pineapple),
  ShopItem("fruit_strawberry", "Fresa", "Fruta", 5, R.drawable.fruit_strawberry),
  ShopItem("fruit_watermelon", "Sandía", "Fruta", 5, R.drawable.fruit_watermelon),

  // Comidas Chatarra (10 Monedas)
  ShopItem("food_pizza", "Pizza", "Comida", 10, R.drawable.food_pizza),
  ShopItem("food_donut", "Dona", "Dulce", 10, R.drawable.food_donut),
  ShopItem("food_icecream", "Helado", "Dulce", 10, R.drawable.food_icecream),
  ShopItem("food_cookie", "Galleta", "Dulce", 10, R.drawable.food_cookie),
  ShopItem("food_cake", "Pastel", "Dulce", 10, R.drawable.food_cake),

  // Poción Curativa (15 Monedas)
  ShopItem("potion_heal", "Poción Curativa", "Mágica", 15, R.drawable.potion_heal)
)

/**
 * Pantalla de la Tienda de Alimentos del Hogar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
  state: CapybaraState,
  onStateChange: (CapybaraState) -> Unit,
  onBack: () -> Unit,
  progressStore: ProgressStore? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val actualStore = remember(progressStore) { progressStore ?: ProgressStore(context) }
  val coroutineScope = rememberCoroutineScope()

  var coins by remember(actualStore) { mutableIntStateOf(actualStore.getCoins()) }
  var inventory by remember(actualStore) { mutableStateOf(actualStore.getFoodInventory()) }

  LaunchedEffect(actualStore) {
    coins = actualStore.getCoins()
    inventory = actualStore.getFoodInventory()
  }

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Alimentos, 1: Accesorios
  var bubbleMessage by remember { mutableStateOf<String?>(null) }
  var bubbleIsError by remember { mutableStateOf(false) }
  var bubbleJob by remember { mutableStateOf<Job?>(null) }

  fun showBubble(message: String, isError: Boolean = false) {
    SoundPlayer.playPop()
    bubbleJob?.cancel()
    bubbleMessage = message
    bubbleIsError = isError
    bubbleJob = coroutineScope.launch {
      delay(2200)
      bubbleMessage = null
    }
  }

  fun handleBuyFood(item: ShopItem) {
    val currentCoins = actualStore.getCoins()
    if (currentCoins >= item.price) {
      val success = actualStore.spendCoins(item.price)
      if (success) {
        actualStore.addFoodItem(item.id)
        coins = actualStore.getCoins()
        val updatedInventory = actualStore.getFoodInventory()
        inventory = updatedInventory

        onStateChange(
          state.copy(
            coins = coins,
            foodInventory = updatedInventory
          )
        )
        showBubble("¡Comprado! 🛒", isError = false)
      } else {
        showBubble("Faltan monedas 🪙 — ¡juega para ganarlas!", isError = true)
      }
    } else {
      showBubble("Faltan monedas 🪙 — ¡juega para ganarlas!", isError = true)
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Tienda 🏪",
              fontWeight = FontWeight.Black,
              fontSize = 20.sp,
              color = Color(0xFF4A2810)
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("shop_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver al Hogar",
              tint = Color(0xFF4A2810)
            )
          }
        },
        actions = {
          // Contador de monedas en vivo
          Surface(
            color = Color(0xFFFFF3D0),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
            shadowElevation = 2.dp,
            modifier = Modifier
              .padding(end = 12.dp)
              .testTag("shop_coins_badge")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(text = "🪙", fontSize = 16.sp)
              Text(
                text = "$coins",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color(0xFF5D4037)
              )
            }
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = Color(0xFFFFF8E7)
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFFDF7))
        .padding(paddingValues)
    ) {
      Column(
        modifier = Modifier.fillMaxSize()
      ) {
        // -------------------------------------------------------------
        // SECCIÓN "MI DESPENSA 🧺" (Fila horizontal de sprites adquiridos)
        // -------------------------------------------------------------
        PantrySection(inventory = inventory)

        // -------------------------------------------------------------
        // PESTAÑAS (ALIMENTOS / ACCESORIOS)
        // -------------------------------------------------------------
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFFFFF8E7),
          contentColor = Color(0xFFFF6D00),
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = Color(0xFFFF6D00),
              height = 3.dp
            )
          }
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Text(
                text = "Alimentos 🍉",
                fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.Bold,
                fontSize = 15.sp,
                color = if (selectedTab == 0) Color(0xFFE65100) else Color(0xFF8D6E63)
              )
            }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = {
              selectedTab = 1
              showBubble("¡Pronto! 🚧 La sección de accesorios llegará muy pronto.")
            },
            text = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = "Accesorios 👓",
                  fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (selectedTab == 1) Color(0xFFE65100) else Color(0xFF8D6E63)
                )
                Surface(
                  color = Color(0xFFFFB300),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text(
                    text = "Pronto",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
            }
          )
        }

        // -------------------------------------------------------------
        // CONTENIDO SEGÚN LA PESTAÑA SELECCIONADA
        // -------------------------------------------------------------
        if (selectedTab == 0) {
          // GRID DE ALIMENTOS
          FoodShopGrid(
            items = SHOP_FOOD_ITEMS,
            coins = coins,
            onBuy = { handleBuyFood(it) }
          )
        } else {
          // PESTAÑA DE ACCESORIOS (PLACEHOLDER INFORMATIVO)
          AccessoriesComingSoonPane(
            onBackToFood = { selectedTab = 0 }
          )
        }
      }

      // -------------------------------------------------------------
      // BURBUJA DE NOTIFICACIÓN FLOTANTE (Compra exitosa / Falta monedas)
      // -------------------------------------------------------------
      AnimatedVisibility(
        visible = bubbleMessage != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -60 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -60 }),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        bubbleMessage?.let { msg ->
          Surface(
            color = if (bubbleIsError) Color(0xFFD32F2F) else Color(0xFF2E7D32),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = if (bubbleIsError) "⚠️" else "✨",
                fontSize = 18.sp
              )
              Text(
                text = msg,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Sección horizontal superior "Mi despensa 🧺"
 */
@Composable
private fun PantrySection(
  inventory: List<String>,
  modifier: Modifier = Modifier
) {
  Surface(
    color = Color(0xFFFFF3E0),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, Color(0xFFFFCC80)),
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(text = "🧺", fontSize = 16.sp)
          Text(
            text = "Mi despensa",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = Color(0xFF5D4037)
          )
        }
        Surface(
          color = Color(0xFFFF9800),
          shape = CircleShape
        ) {
          Text(
            text = "${inventory.size} items",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      if (inventory.isEmpty()) {
        Text(
          text = "Tu despensa está vacía. ¡Compra rica comida para tu capibara abajo!",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF8D6E63),
          modifier = Modifier.padding(vertical = 4.dp)
        )
      } else {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp),
          modifier = Modifier.fillMaxWidth().testTag("pantry_items_row")
        ) {
          items(inventory) { foodId ->
            val drawableRes = SpriteResources.getFoodDrawable(foodId)
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = BorderStroke(1.dp, Color(0xFFFFE082)),
              shadowElevation = 1.dp,
              modifier = Modifier.size(46.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(4.dp)
              ) {
                Image(
                  painter = painterResource(id = drawableRes),
                  contentDescription = foodId,
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.size(34.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Grid de tarjetas de alimentos
 */
@Composable
private fun FoodShopGrid(
  items: List<ShopItem>,
  coins: Int,
  onBuy: (ShopItem) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier
      .fillMaxSize()
      .testTag("food_shop_grid")
  ) {
    items(items, key = { it.id }) { item ->
      FoodShopCard(
        item = item,
        canAfford = coins >= item.price,
        onBuy = { onBuy(item) }
      )
    }

    item(span = { GridItemSpan(2) }) {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

/**
 * Tarjeta individual de comida en la tienda
 */
@Composable
private fun FoodShopCard(
  item: ShopItem,
  canAfford: Boolean,
  onBuy: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, if (canAfford) Color(0xFFFFE082) else Color(0xFFEEEEEE)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Badge de categoría
      Surface(
        color = when (item.category) {
          "Fruta" -> Color(0xFFE8F5E9)
          "Mágica" -> Color(0xFFF3E5F5)
          else -> Color(0xFFFFF3E0)
        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.align(Alignment.Start)
      ) {
        Text(
          text = item.category,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = when (item.category) {
            "Fruta" -> Color(0xFF2E7D32)
            "Mágica" -> Color(0xFF7B1FA2)
            else -> Color(0xFFE65100)
          },
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Imagen del Sprite
      Box(
        modifier = Modifier
          .size(76.dp)
          .clip(CircleShape)
          .background(Color(0xFFFFFDE7)),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = item.drawableRes),
          contentDescription = item.name,
          contentScale = ContentScale.Fit,
          modifier = Modifier.size(56.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Nombre del alimento
      Text(
        text = item.name,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = Color(0xFF3E2723),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Precio en monedas
      Surface(
        color = Color(0xFFFFF8E1),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFD54F))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(text = "🪙", fontSize = 13.sp)
          Text(
            text = "${item.price}",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF5D4037)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Botón "Comprar"
      Button(
        onClick = onBuy,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (canAfford) Color(0xFFFF6D00) else Color(0xFFBDBDBD)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .testTag("buy_${item.id}")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Comprar",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White
          )
        }
      }
    }
  }
}

/**
 * Pantalla informativa para la pestaña de Accesorios (Pronto)
 */
@Composable
private fun AccessoriesComingSoonPane(
  onBackToFood: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Surface(
      color = Color(0xFFFFF3E0),
      shape = CircleShape,
      modifier = Modifier.size(80.dp),
      shadowElevation = 2.dp
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(text = "🚧", fontSize = 42.sp)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "¡Accesorios en camino!",
      fontWeight = FontWeight.Black,
      fontSize = 20.sp,
      color = Color(0xFF4A2810),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Pronto podrás comprar sombreros especiales, lentes, atuendos y fondos temáticos para tu capibara.",
      fontSize = 14.sp,
      fontWeight = FontWeight.Medium,
      color = Color(0xFF795548),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = onBackToFood,
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
    ) {
      Text(
        text = "Ver Alimentos 🍉",
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }
  }
}
