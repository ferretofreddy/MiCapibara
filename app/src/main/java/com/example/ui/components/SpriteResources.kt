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
  val FRUIT_APPLE: Int get() = R.drawable.fruit_apple

  @get:DrawableRes
  val FRUIT_BANANA: Int get() = R.drawable.fruit_banana

  @get:DrawableRes
  val FRUIT_COCONUT: Int get() = R.drawable.fruit_coconut

  @get:DrawableRes
  val FRUIT_ORANGE: Int get() = R.drawable.fruit_orange

  @get:DrawableRes
  val FRUIT_PEACH: Int get() = R.drawable.fruit_peach

  @get:DrawableRes
  val FRUIT_PINEAPPLE: Int get() = R.drawable.fruit_pineapple

  @get:DrawableRes
  val FRUIT_STRAWBERRY: Int get() = R.drawable.fruit_strawberry

  @get:DrawableRes
  val FRUIT_WATERMELON: Int get() = R.drawable.fruit_watermelon

  @get:DrawableRes
  val CAPYBARA_HAPPY: Int get() = R.drawable.capybara_happy

  @DrawableRes
  fun getFoodDrawable(foodId: String): Int {
    return when (foodId) {
      "fruit_apple" -> R.drawable.fruit_apple
      "fruit_banana" -> R.drawable.fruit_banana
      "fruit_coconut" -> R.drawable.fruit_coconut
      "fruit_orange" -> R.drawable.fruit_orange
      "fruit_peach" -> R.drawable.fruit_peach
      "fruit_pineapple" -> R.drawable.fruit_pineapple
      "fruit_strawberry" -> R.drawable.fruit_strawberry
      "fruit_watermelon" -> R.drawable.fruit_watermelon
      "food_pizza" -> R.drawable.food_pizza
      "food_donut" -> R.drawable.food_donut
      "food_icecream" -> R.drawable.food_icecream
      "food_cookie" -> R.drawable.food_cookie
      "food_cake" -> R.drawable.food_cake
      "potion_heal" -> R.drawable.potion_heal
      else -> R.drawable.fruit_apple
    }
  }

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
