package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.ProgressStore
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ShopScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  private lateinit var progressStore: ProgressStore

  @Before
  fun setUp() {
    val context = composeTestRule.activity
    val prefs = context.getSharedPreferences("capybara_progress_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()
    progressStore = ProgressStore(context)
    // Otorgar 20 monedas para las pruebas de compra
    progressStore.addCoins(20)
  }

  @Test
  fun testNavigateToShopAndBuyFood() {
    composeTestRule.waitForIdle()

    // Abrir la tienda desde la pantalla principal
    composeTestRule.onNodeWithTag("btn_shop").performClick()
    composeTestRule.waitForIdle()

    // Verificar que el grid y los items de la tienda están presentes
    composeTestRule.onNodeWithTag("food_shop_grid").assertIsDisplayed()
    composeTestRule.onNodeWithText("Manzana").assertIsDisplayed()

    // Comprar Manzana (cuesta 5 monedas)
    composeTestRule.onNodeWithTag("buy_fruit_apple").performClick()
    composeTestRule.waitForIdle()

    // Verificar que se descontaron las monedas
    assertEquals(15, progressStore.getCoins())
    val inventory = progressStore.getFoodInventory()
    assertEquals(1, inventory.size)
    assertEquals("fruit_apple", inventory[0])
  }
}


