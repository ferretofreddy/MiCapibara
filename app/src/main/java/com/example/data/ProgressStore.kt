package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Almacenamiento local persistente con SharedPreferences y control de versión de esquema (schemaVersion)
 * para garantizar compatibilidad hacia adelante y hacia atrás sin fallos ni crasheos.
 *
 * Versión 1: totalPets, level
 * Versión 2: hunger, hygiene, energy, funLevel, lastUpdateTime
 * Versión 3: coins, xp, foodInventory
 */
class ProgressStore(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  companion object {
    private const val PREFS_NAME = "capybara_progress_prefs"
    private const val KEY_TOTAL_PETS = "key_total_pets"
    private const val KEY_LEVEL = "key_level"
    private const val KEY_SCHEMA_VERSION = "key_schema_version"
    private const val KEY_COINS = "key_coins"
    private const val KEY_XP = "key_xp"
    private const val KEY_HUNGER = "key_hunger"
    private const val KEY_HYGIENE = "key_hygiene"
    private const val KEY_ENERGY = "key_energy"
    private const val KEY_FUN = "key_fun"
    private const val KEY_LAST_UPDATE_TIME = "key_last_update_time"
    private const val KEY_FOOD_INVENTORY = "key_food_inventory"
    private const val KEY_FOOD_INVENTORY_CSV = "key_food_inventory_csv"

    const val CURRENT_SCHEMA_VERSION = 3

    val LEVEL_THRESHOLDS = listOf(
      1 to 0,    // Nivel 1: 0 - 9 caricias
      2 to 10,   // Nivel 2: 10 - 24 caricias
      3 to 25,   // Nivel 3: 25 - 49 caricias
      4 to 50,   // Nivel 4: 50 - 99 caricias
      5 to 100   // Nivel 5: 100+ caricias (Nivel Máximo)
    )

    fun calculateLevel(totalPets: Int): Int {
      return when {
        totalPets >= 100 -> 5
        totalPets >= 50 -> 4
        totalPets >= 25 -> 3
        totalPets >= 10 -> 2
        else -> 1
      }
    }

    fun getNextLevelThreshold(currentLevel: Int): Int? {
      return when (currentLevel) {
        1 -> 10
        2 -> 25
        3 -> 50
        4 -> 100
        else -> null // Nivel máximo alcanzado
      }
    }

    fun getPrevLevelThreshold(currentLevel: Int): Int {
      return when (currentLevel) {
        2 -> 10
        3 -> 25
        4 -> 50
        5 -> 100
        else -> 0
      }
    }
  }

  init {
    ensureSchemaMigration()
  }

  /**
   * Verifica la versión de esquema y aplica migraciones automáticas seguras.
   * Migración desde v1 o v2 conserva caricias, nivel y necesidades; coins/xp inician en 0, inventario vacío.
   */
  private fun ensureSchemaMigration() {
    val savedVersion = prefs.getInt(KEY_SCHEMA_VERSION, 0)
    if (savedVersion < CURRENT_SCHEMA_VERSION) {
      val editor = prefs.edit()

      if (savedVersion < 1) {
        val existingPets = prefs.getInt(KEY_TOTAL_PETS, 0).coerceAtLeast(0)
        val calculatedLvl = calculateLevel(existingPets)
        editor.putInt(KEY_TOTAL_PETS, existingPets)
        editor.putInt(KEY_LEVEL, calculatedLvl)
      }

      if (savedVersion < 2) {
        if (!prefs.contains(KEY_HUNGER)) editor.putInt(KEY_HUNGER, 100)
        if (!prefs.contains(KEY_HYGIENE)) editor.putInt(KEY_HYGIENE, 100)
        if (!prefs.contains(KEY_ENERGY)) editor.putInt(KEY_ENERGY, 100)
        if (!prefs.contains(KEY_FUN)) editor.putInt(KEY_FUN, 100)
        if (!prefs.contains(KEY_LAST_UPDATE_TIME)) {
          editor.putLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
        }
      }

      if (savedVersion < 3) {
        if (!prefs.contains(KEY_COINS)) editor.putInt(KEY_COINS, 0)
        if (!prefs.contains(KEY_XP)) editor.putInt(KEY_XP, prefs.getInt(KEY_TOTAL_PETS, 0))
        if (!prefs.contains(KEY_FOOD_INVENTORY)) editor.putStringSet(KEY_FOOD_INVENTORY, emptySet())
      }

      editor.putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
      editor.apply()
    }
  }

  fun getTotalPets(): Int {
    return prefs.getInt(KEY_TOTAL_PETS, 0).coerceAtLeast(0)
  }

  fun getLevel(): Int {
    val storedLvl = prefs.getInt(KEY_LEVEL, 1)
    val calculatedLvl = calculateLevel(getTotalPets())
    return storedLvl.coerceAtLeast(calculatedLvl).coerceIn(1, 5)
  }

  fun getCoins(): Int {
    return prefs.getInt(KEY_COINS, 0).coerceAtLeast(0)
  }

  fun addCoins(amount: Int): Int {
    val newCoins = (getCoins() + amount).coerceAtLeast(0)
    prefs.edit().putInt(KEY_COINS, newCoins).commit()
    return newCoins
  }

  fun spendCoins(amount: Int): Boolean {
    val current = getCoins()
    if (current < amount) return false
    val newCoins = current - amount
    prefs.edit().putInt(KEY_COINS, newCoins).commit()
    return true
  }

  fun getXp(): Int {
    return prefs.getInt(KEY_XP, 0).coerceAtLeast(0)
  }

  fun getHunger(): Int = prefs.getInt(KEY_HUNGER, 100).coerceIn(0, 100)
  fun getHygiene(): Int = prefs.getInt(KEY_HYGIENE, 100).coerceIn(0, 100)
  fun getEnergy(): Int = prefs.getInt(KEY_ENERGY, 100).coerceIn(0, 100)
  fun getFun(): Int = prefs.getInt(KEY_FUN, 100).coerceIn(0, 100)

  fun getLastUpdateTime(): Long = prefs.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())

  fun setNeeds(hunger: Int, hygiene: Int, energy: Int, funLevel: Int) {
    prefs.edit()
      .putInt(KEY_HUNGER, hunger.coerceIn(0, 100))
      .putInt(KEY_HYGIENE, hygiene.coerceIn(0, 100))
      .putInt(KEY_ENERGY, energy.coerceIn(0, 100))
      .putInt(KEY_FUN, funLevel.coerceIn(0, 100))
      .putLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
      .apply()
  }

  /**
   * Ejecuta el decaimiento de necesidades basado en el tiempo transcurrido y actualiza la persistencia.
   */
  fun applyDecay(): DecayedNeedsResult {
    val decayed = NeedsManager.calculateDecayedNeeds(
      hunger = getHunger(),
      hygiene = getHygiene(),
      energy = getEnergy(),
      funLevel = getFun(),
      lastUpdateMillis = getLastUpdateTime()
    )
    setNeeds(
      hunger = decayed.hunger,
      hygiene = decayed.hygiene,
      energy = decayed.energy,
      funLevel = decayed.funLevel
    )
    return decayed
  }

  fun getFoodInventory(): List<String> {
    val csv = prefs.getString(KEY_FOOD_INVENTORY_CSV, null)
    if (csv != null) {
      return if (csv.isBlank()) emptyList() else csv.split(",").filter { it.isNotBlank() }
    }
    return prefs.getStringSet(KEY_FOOD_INVENTORY, emptySet())?.toList() ?: emptyList()
  }

  fun addFoodItem(itemKey: String) {
    val current = getFoodInventory().toMutableList()
    current.add(itemKey)
    val csv = current.joinToString(",")
    prefs.edit()
      .putString(KEY_FOOD_INVENTORY_CSV, csv)
      .putStringSet(KEY_FOOD_INVENTORY, current.toSet())
      .commit()
  }

  fun removeFoodItem(itemKey: String): Boolean {
    val current = getFoodInventory().toMutableList()
    val removed = current.remove(itemKey)
    if (removed) {
      val csv = current.joinToString(",")
      prefs.edit()
        .putString(KEY_FOOD_INVENTORY_CSV, csv)
        .putStringSet(KEY_FOOD_INVENTORY, current.toSet())
        .commit()
    }
    return removed
  }

  /**
   * Incrementa una caricia, suma XP, calcula subida de nivel,
   * añade +10 monedas de bono en caso de level up y guarda todo en disco.
   */
  fun incrementPet(): LevelUpResult {
    val currentPets = getTotalPets()
    val newPets = currentPets + 1
    val oldLevel = calculateLevel(currentPets)
    val newLevel = calculateLevel(newPets)
    val leveledUp = newLevel > oldLevel

    var currentCoins = getCoins()
    var bonusCoins = 0
    if (leveledUp) {
      bonusCoins = 10
      currentCoins += bonusCoins
    }
    val currentXp = getXp() + 1

    prefs.edit()
      .putInt(KEY_TOTAL_PETS, newPets)
      .putInt(KEY_LEVEL, newLevel)
      .putInt(KEY_COINS, currentCoins)
      .putInt(KEY_XP, currentXp)
      .putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
      .apply()

    return LevelUpResult(
      totalPets = newPets,
      currentLevel = newLevel,
      leveledUp = leveledUp,
      bonusCoins = bonusCoins,
      totalCoins = currentCoins,
      currentXp = currentXp
    )
  }
}

data class LevelUpResult(
  val totalPets: Int,
  val currentLevel: Int,
  val leveledUp: Boolean,
  val bonusCoins: Int = 0,
  val totalCoins: Int = 0,
  val currentXp: Int = 0
)
