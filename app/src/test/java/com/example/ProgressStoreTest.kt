package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NeedsManager
import com.example.data.ProgressStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProgressStoreTest {

  private lateinit var progressStore: ProgressStore

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("capybara_progress_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()
    progressStore = ProgressStore(context)
  }

  @Test
  fun testInitialValuesAndLevelCalculation() {
    assertEquals(0, progressStore.getTotalPets())
    assertEquals(1, progressStore.getLevel())
    assertEquals(0, progressStore.getCoins())
    assertEquals(0, progressStore.getXp())
    assertEquals(100, progressStore.getHunger())
    assertEquals(100, progressStore.getHygiene())
  }

  @Test
  fun testLevelUpGivesCoinsBonus() {
    var lastResult = progressStore.incrementPet()
    for (i in 2..10) {
      lastResult = progressStore.incrementPet()
    }
    assertEquals(10, lastResult.totalPets)
    assertEquals(2, lastResult.currentLevel)
    assertTrue(lastResult.leveledUp)
    assertEquals(10, lastResult.bonusCoins)
    assertEquals(10, progressStore.getCoins())
  }

  @Test
  fun testNeedsDecayCalculation() {
    val oneHourAgo = System.currentTimeMillis() - (3600 * 1000L)
    val result = NeedsManager.calculateDecayedNeeds(
      hunger = 100,
      hygiene = 100,
      energy = 100,
      funLevel = 100,
      lastUpdateMillis = oneHourAgo
    )
    assertTrue(result.hunger < 100)
    assertTrue(result.hygiene < 100)
    assertTrue(result.energy < 100)
    assertTrue(result.funLevel < 100)
  }
}
