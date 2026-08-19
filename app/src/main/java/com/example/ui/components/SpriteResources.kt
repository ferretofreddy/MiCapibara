package com.example.ui.components

import androidx.annotation.DrawableRes
import com.example.R
import com.example.model.CapybaraBackground
import com.example.model.CapybaraColor

/**
 * Helper y mapeo centralizado de recursos gráficos (sprites, fondos y escenas)
 * ubicados en res/drawable/.
 */
object SpriteResources {

  // Constantes de escenas principales
  @get:DrawableRes
  val SCENE_LIVINGROOM: Int get() = R.drawable.scene_livingroom

  @get:DrawableRes
  val SCENE_KITCHEN: Int get() = R.drawable.scene_kitchen

  @get:DrawableRes
  val SCENE_BATH: Int get() = R.drawable.scene_bath

  @get:DrawableRes
  val SCENE_BEDROOM: Int get() = R.drawable.scene_bedroom

  // Constantes de comidas y pociones
  @get:DrawableRes
  val FOOD_CAKE: Int get() = R.drawable.food_cake

  @get:DrawableRes
  val FOOD_COOKIE: Int get() = R.drawable.food_cookie

  @get:DrawableRes
  val FOOD_DONUT: Int get() = R.drawable.food_donut

  @get:DrawableRes
  val FOOD_ICECREAM: Int get() = R.drawable.food_icecream

  @get:DrawableRes
  val FOOD_PIZZA: Int get() = R.drawable.food_pizza

  @get:DrawableRes
  val POTION_HEAL: Int get() = R.drawable.potion_heal

  @get:DrawableRes
  val CAPYBARA_HAPPY: Int get() = R.drawable.capybara_happy

  /**
   * Obtiene el recurso drawable para un [CapybaraColor].
   * Mapeo explícito para variaciones de nombres y convenios.
   */
  @DrawableRes
  fun getCapybaraDrawable(color: CapybaraColor): Int = color.getDrawableRes()

  /**
   * Obtiene el recurso drawable para un [CapybaraBackground].
   */
  @DrawableRes
  fun getBackgroundDrawable(background: CapybaraBackground): Int = background.getDrawableRes()
}

/**
 * Mapeo explícito de [CapybaraColor] a drawable de recurso.
 * - ROSE_PASTEL → capybara_rose
 * - SKY_BLUE → capybara_sky
 * - FLAMINGO_PINK → capybara_flamingo
 * - JUNGLE_GREEN → capybara_jungle
 * - CLASSIC → capybara_classic
 * - CHOCOLATE → capybara_chocolate
 * - MINT → capybara_mint
 * - LAVENDER → capybara_lavender
 * - SUNNY → capybara_sunny
 * - CARAMEL → capybara_caramel
 */
@DrawableRes
fun CapybaraColor.getDrawableRes(): Int = when (this) {
  CapybaraColor.CLASSIC -> R.drawable.capybara_classic
  CapybaraColor.CHOCOLATE -> R.drawable.capybara_chocolate
  CapybaraColor.ROSE_PASTEL -> R.drawable.capybara_rose
  CapybaraColor.MINT -> R.drawable.capybara_mint
  CapybaraColor.LAVENDER -> R.drawable.capybara_lavender
  CapybaraColor.SUNNY -> R.drawable.capybara_sunny
  CapybaraColor.SKY_BLUE -> R.drawable.capybara_sky
  CapybaraColor.CARAMEL -> R.drawable.capybara_caramel
  CapybaraColor.FLAMINGO_PINK -> R.drawable.capybara_flamingo
  CapybaraColor.JUNGLE_GREEN -> R.drawable.capybara_jungle
}

/**
 * Mapeo de [CapybaraBackground] a drawable correspondiente.
 * - BEACH → bg_beach
 * - FOREST → bg_forest
 * - MEADOW → bg_meadow
 * - SUNSET → bg_sunset
 * - RAINFOREST → bg_jungle
 * - SPACE → bg_space
 */
@DrawableRes
fun CapybaraBackground.getDrawableRes(): Int = when (this) {
  CapybaraBackground.BEACH -> R.drawable.bg_beach
  CapybaraBackground.FOREST -> R.drawable.bg_forest
  CapybaraBackground.MEADOW -> R.drawable.bg_meadow
  CapybaraBackground.SUNSET -> R.drawable.bg_sunset
  CapybaraBackground.RAINFOREST -> R.drawable.bg_jungle
  CapybaraBackground.SPACE -> R.drawable.bg_space
}
