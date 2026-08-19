package com.example.data

import com.example.model.CapybaraMood
import kotlin.math.max
import kotlin.math.min

/**
 * Gestor de cálculo y decaimiento del sistema de necesidades vitales del capibara
 * (Hambre 🍉, Higiene 🛁, Energía 🛏️, Diversión 🎈).
 */
object NeedsManager {
  // Tasas de decaimiento por hora
  private const val HUNGER_DECAY_PER_HOUR = 8
  private const val HYGIENE_DECAY_PER_HOUR = 6
  private const val ENERGY_DECAY_PER_HOUR = 5
  private const val FUN_DECAY_PER_HOUR = 10

  /**
   * Aplica decaimiento natural de necesidades al abrir la app o tras inactividad.
   */
  fun calculateDecayedNeeds(
    hunger: Int,
    hygiene: Int,
    energy: Int,
    funLevel: Int,
    lastUpdateMillis: Long,
    currentMillis: Long = System.currentTimeMillis()
  ): DecayedNeedsResult {
    if (lastUpdateMillis <= 0L) {
      return DecayedNeedsResult(hunger, hygiene, energy, funLevel, currentMillis)
    }

    val elapsedHours = ((currentMillis - lastUpdateMillis).coerceAtLeast(0L) / (1000.0 * 60.0 * 60.0)).toFloat()

    // Si transcurrió menos de 1 minuto, no decae
    if (elapsedHours < (1f / 60f)) {
      return DecayedNeedsResult(hunger, hygiene, energy, funLevel, currentMillis)
    }

    // Limitamos a un máximo de 24 horas de decaimiento continuo
    val effectiveHours = min(elapsedHours, 24f)

    val newHunger = max(10, (hunger - (effectiveHours * HUNGER_DECAY_PER_HOUR).toInt()))
    val newHygiene = max(10, (hygiene - (effectiveHours * HYGIENE_DECAY_PER_HOUR).toInt()))
    val newEnergy = max(10, (energy - (effectiveHours * ENERGY_DECAY_PER_HOUR).toInt()))
    val newFun = max(10, (funLevel - (effectiveHours * FUN_DECAY_PER_HOUR).toInt()))

    return DecayedNeedsResult(
      hunger = newHunger.coerceIn(0, 100),
      hygiene = newHygiene.coerceIn(0, 100),
      energy = newEnergy.coerceIn(0, 100),
      funLevel = newFun.coerceIn(0, 100),
      newTimestamp = currentMillis
    )
  }

  /**
   * Determina la expresión / estado anímico según los valores de necesidades actuales.
   */
  fun calculateMood(hunger: Int, hygiene: Int, energy: Int, funLevel: Int): CapybaraMood {
    return when {
      hunger < 25 && hygiene < 25 -> CapybaraMood.SICK
      hunger < 35 -> CapybaraMood.HUNGRY
      hygiene < 35 -> CapybaraMood.DIRTY
      energy < 35 -> CapybaraMood.TIRED
      funLevel < 35 -> CapybaraMood.SAD
      else -> CapybaraMood.HAPPY
    }
  }
}

data class DecayedNeedsResult(
  val hunger: Int,
  val hygiene: Int,
  val energy: Int,
  val funLevel: Int,
  val newTimestamp: Long
)
