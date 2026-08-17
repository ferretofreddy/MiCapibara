package com.example.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Almacenamiento local persistente con SharedPreferences y control de versión de esquema (schemaVersion)
 * para garantizar compatibilidad hacia adelante y hacia atrás sin fallos ni crasheos.
 */
class ProgressStore(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  companion object {
    private const val PREFS_NAME = "capybara_progress_prefs"
    private const val KEY_TOTAL_PETS = "key_total_pets"
    private const val KEY_LEVEL = "key_level"
    private const val KEY_SCHEMA_VERSION = "key_schema_version"

    const val CURRENT_SCHEMA_VERSION = 1

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
   * Verifica la versión de esquema y aplica migraciones automáticas seguras si la versión es antigua.
   */
  private fun ensureSchemaMigration() {
    val savedVersion = prefs.getInt(KEY_SCHEMA_VERSION, 0)
    if (savedVersion < CURRENT_SCHEMA_VERSION) {
      val existingPets = prefs.getInt(KEY_TOTAL_PETS, 0).coerceAtLeast(0)
      val calculatedLvl = calculateLevel(existingPets)
      prefs.edit()
        .putInt(KEY_TOTAL_PETS, existingPets)
        .putInt(KEY_LEVEL, calculatedLvl)
        .putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
        .apply()
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

  /**
   * Incrementa una caricia, guarda en disco y devuelve el nuevo nivel alcanzado si subió de nivel, o null si se mantiene.
   */
  fun incrementPet(): LevelUpResult {
    val currentPets = getTotalPets()
    val newPets = currentPets + 1
    val oldLevel = calculateLevel(currentPets)
    val newLevel = calculateLevel(newPets)
    val leveledUp = newLevel > oldLevel

    prefs.edit()
      .putInt(KEY_TOTAL_PETS, newPets)
      .putInt(KEY_LEVEL, newLevel)
      .putInt(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION)
      .apply()

    return LevelUpResult(
      totalPets = newPets,
      currentLevel = newLevel,
      leveledUp = leveledUp
    )
  }
}

data class LevelUpResult(
  val totalPets: Int,
  val currentLevel: Int,
  val leveledUp: Boolean
)
