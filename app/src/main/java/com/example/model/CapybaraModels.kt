package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores para el cuerpo del capibara
 */
enum class CapybaraColor(
  val label: String,
  val baseColor: Color,
  val shadowColor: Color,
  val highlightColor: Color,
  val bellyColor: Color
) {
  CLASSIC(
    label = "Canela Clásico",
    baseColor = Color(0xFFC98651),
    shadowColor = Color(0xFFA26131),
    highlightColor = Color(0xFFDF9E6A),
    bellyColor = Color(0xFFDEB088)
  ),
  CHOCOLATE(
    label = "Chocolate",
    baseColor = Color(0xFF8A532C),
    shadowColor = Color(0xFF65391A),
    highlightColor = Color(0xFFA86E44),
    bellyColor = Color(0xFFB07F5A)
  ),
  ROSE_PASTEL(
    label = "Rosa Algodón",
    baseColor = Color(0xFFFF94B8),
    shadowColor = Color(0xFFE26792),
    highlightColor = Color(0xFFFFBCD0),
    bellyColor = Color(0xFFFFD1E0)
  ),
  MINT(
    label = "Menta Dulce",
    baseColor = Color(0xFF5FC9A7),
    shadowColor = Color(0xFF3B9B7B),
    highlightColor = Color(0xFF8DE0C6),
    bellyColor = Color(0xFFB4EFE0)
  ),
  LAVENDER(
    label = "Lavanda Mágica",
    baseColor = Color(0xFFB58FE7),
    shadowColor = Color(0xFF885FC0),
    highlightColor = Color(0xFFD3B6F7),
    bellyColor = Color(0xFFE5D5FC)
  ),
  SUNNY(
    label = "Sol Dorado",
    baseColor = Color(0xFFFFCA3A),
    shadowColor = Color(0xFFD69C13),
    highlightColor = Color(0xFFFFE07A),
    bellyColor = Color(0xFFFFF0B3)
  ),
  SKY_BLUE(
    label = "Celeste Cielo",
    baseColor = Color(0xFF68C5F7),
    shadowColor = Color(0xFF389CD3),
    highlightColor = Color(0xFFA4DEFF),
    bellyColor = Color(0xFFCCEFFF)
  ),
  CARAMEL(
    label = "Caramelo Suave",
    baseColor = Color(0xFFE5A663),
    shadowColor = Color(0xFFBF7E39),
    highlightColor = Color(0xFFF7C894),
    bellyColor = Color(0xFFFCE1C3)
  )
}

/**
 * Tipos de gorros y accesorios para la cabeza
 */
enum class CapybaraHat(
  val label: String,
  val iconEmoji: String,
  val description: String
) {
  NONE("Sin Gorro", "❌", "Ningún accesorio"),
  ORANGE("Mandarina Kawaii", "🍊", "La clásica fruta en la cabeza"),
  FLOWER("Flor Tropical", "🌸", "Flor hawaiana brillante"),
  CROWN("Corona de Princesa", "👑", "Corona dorada con gemas brillantes"),
  PARTY_HAT("Gorro de Fiesta", "🥳", "Gorro festivo de cumpleaños"),
  CAP("Gorra Deportiva", "🧢", "Gorra roja con visera hacia adelante"),
  WIZARD("Gorro Mágico", "🧙", "Gorro de mago morado con luna"),
  BERET("Boina de Artista", "🎨", "Boina francesa con estilo")
}

/**
 * Tipos de camisetas y vestimentas para el torso
 */
enum class CapybaraShirt(
  val label: String,
  val iconEmoji: String,
  val description: String
) {
  NONE("Sin Ropa", "❌", "Sin prenda"),
  STRIPED("Rayas Marineras", "⚓", "Camiseta a rayas azules y blancas"),
  HEARTS("Vestido Corazones", "💖", "Vestido rosa con tiernos corazones"),
  RAINBOW_SWEATER("Suéter Arcoíris", "🌈", "Suéter calentito multicolor"),
  SUPERHERO("Capa de Súper", "🦸", "Capa de superhéroe con estrella"),
  HAWAIIAN("Camisa Hawaiana", "🌺", "Camisa playera con flores"),
  DINOSAUR("Traje Dinosaurio", "🦖", "Disfraz verde con púas suaves")
}

/**
 * Tipos de gafas y accesorios oculares
 */
enum class CapybaraGlasses(
  val label: String,
  val iconEmoji: String,
  val description: String
) {
  NONE("Sin Gafas", "❌", "Ojos al natural"),
  SUNGLASSES("Gafas de Sol", "😎", "Gafas oscuras super cool"),
  HEART_GLASSES("Gafas de Corazón", "💕", "Montura rosa en forma de corazón"),
  GOLD_ROUND("Gafas Redondas", "👓", "Gafas doradas de intelectual"),
  STAR_GLASSES("Gafas de Estrella", "⭐", "Gafas amarillas con forma de estrella"),
  SNORKEL("Gafas de Buceo", "🤿", "Máscara de bucear con tubo")
}

/**
 * Tipos de calzado / zapatitos para las patitas
 */
enum class CapybaraShoes(
  val label: String,
  val iconEmoji: String,
  val description: String
) {
  NONE("Sin Zapatos", "❌", "Patitas descalzas"),
  RAIN_BOOTS("Botas de Lluvia", "🥾", "Botitas amarillas impermeables"),
  SNEAKERS("Zapatillas Tenis", "👟", "Zapatillas deportivas azules"),
  RED_BOOTS("Botitas Rojas", "👢", "Botas rojas brillantes"),
  GOLD_SHOES("Zapatos de Oro", "✨", "Zapatitos dorados de princesa"),
  ROLLER_SKATES("Patines con Ruedas", "🛼", "Patines rosas con ruedas de colores")
}

/**
 * Fondos y paisajes disponibles
 */
enum class CapybaraBackground(
  val label: String,
  val iconEmoji: String,
  val description: String
) {
  BEACH("Playa Soleada", "🏖️", "Arena dorada, mar azul, sol y palmera"),
  FOREST("Bosque Mágico", "🌲", "Árboles verdes, setas y flores silvestres"),
  MEADOW("Pradera Feliz", "🌈", "Colinas verdes, arcoíris y nubes"),
  SUNSET("Puesta de Sol", "🌅", "Cielo cálido al atardecer, montañas y estrellas")
}

/**
 * Categorías de personalización para el selector
 */
enum class CustomizationCategory(
  val label: String,
  val iconEmoji: String
) {
  COLOR("Color", "🎨"),
  HAT("Gorros", "👒"),
  SHIRT("Ropa", "👕"),
  GLASSES("Gafas", "🕶️"),
  SHOES("Zapatos", "👟"),
  BACKGROUND("Fondo", "🌄"),
  NAME("Nombre", "🏷️")
}

/**
 * Estado completo del capibara personalizado
 */
data class CapybaraState(
  val name: String = "Capi",
  val color: CapybaraColor = CapybaraColor.CLASSIC,
  val hat: CapybaraHat = CapybaraHat.ORANGE,
  val shirt: CapybaraShirt = CapybaraShirt.STRIPED,
  val glasses: CapybaraGlasses = CapybaraGlasses.NONE,
  val shoes: CapybaraShoes = CapybaraShoes.RAIN_BOOTS,
  val background: CapybaraBackground = CapybaraBackground.MEADOW,
  val isHappy: Boolean = false,
  val happinessCount: Int = 1
)
