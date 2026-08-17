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
  val bellyColor: Color,
  val unlockLevel: Int = 1
) {
  CLASSIC(
    label = "Canela Clásico",
    baseColor = Color(0xFFC98651),
    shadowColor = Color(0xFFA26131),
    highlightColor = Color(0xFFDF9E6A),
    bellyColor = Color(0xFFDEB088),
    unlockLevel = 1
  ),
  CHOCOLATE(
    label = "Chocolate",
    baseColor = Color(0xFF8A532C),
    shadowColor = Color(0xFF65391A),
    highlightColor = Color(0xFFA86E44),
    bellyColor = Color(0xFFB07F5A),
    unlockLevel = 1
  ),
  ROSE_PASTEL(
    label = "Rosa Algodón",
    baseColor = Color(0xFFFF94B8),
    shadowColor = Color(0xFFE26792),
    highlightColor = Color(0xFFFFBCD0),
    bellyColor = Color(0xFFFFD1E0),
    unlockLevel = 1
  ),
  MINT(
    label = "Menta Dulce",
    baseColor = Color(0xFF5FC9A7),
    shadowColor = Color(0xFF3B9B7B),
    highlightColor = Color(0xFF8DE0C6),
    bellyColor = Color(0xFFB4EFE0),
    unlockLevel = 1
  ),
  LAVENDER(
    label = "Lavanda Mágica",
    baseColor = Color(0xFFB58FE7),
    shadowColor = Color(0xFF885FC0),
    highlightColor = Color(0xFFD3B6F7),
    bellyColor = Color(0xFFE5D5FC),
    unlockLevel = 1
  ),
  SUNNY(
    label = "Sol Dorado",
    baseColor = Color(0xFFFFCA3A),
    shadowColor = Color(0xFFD69C13),
    highlightColor = Color(0xFFFFE07A),
    bellyColor = Color(0xFFFFF0B3),
    unlockLevel = 1
  ),
  SKY_BLUE(
    label = "Celeste Cielo",
    baseColor = Color(0xFF68C5F7),
    shadowColor = Color(0xFF389CD3),
    highlightColor = Color(0xFFA4DEFF),
    bellyColor = Color(0xFFCCEFFF),
    unlockLevel = 1
  ),
  CARAMEL(
    label = "Caramelo Suave",
    baseColor = Color(0xFFE5A663),
    shadowColor = Color(0xFFBF7E39),
    highlightColor = Color(0xFFF7C894),
    bellyColor = Color(0xFFFCE1C3),
    unlockLevel = 1
  ),
  FLAMINGO_PINK(
    label = "Rosa Flamenco",
    baseColor = Color(0xFFFF5252),
    shadowColor = Color(0xFFD32F2F),
    highlightColor = Color(0xFFFF8A80),
    bellyColor = Color(0xFFFFCDD2),
    unlockLevel = 2
  ),
  JUNGLE_GREEN(
    label = "Verde Selva",
    baseColor = Color(0xFF2E7D32),
    shadowColor = Color(0xFF1B5E20),
    highlightColor = Color(0xFF4CAF50),
    bellyColor = Color(0xFFA5D6A7),
    unlockLevel = 3
  )
}

/**
 * Tipos de gorros y accesorios para la cabeza
 */
enum class CapybaraHat(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  NONE("Sin Gorro", "❌", "Ningún accesorio", unlockLevel = 1),
  ORANGE("Mandarina Kawaii", "🍊", "La clásica fruta en la cabeza", unlockLevel = 1),
  FLOWER("Flor Tropical", "🌸", "Flor hawaiana brillante", unlockLevel = 1),
  CROWN("Corona de Princesa", "👑", "Corona dorada con gemas brillantes", unlockLevel = 1),
  PARTY_HAT("Gorro de Fiesta", "🥳", "Gorro festivo de cumpleaños", unlockLevel = 1),
  CAP("Gorra Deportiva", "🧢", "Gorra roja con visera hacia adelante", unlockLevel = 1),
  WIZARD("Gorro Mágico", "🧙", "Gorro de mago morado con luna", unlockLevel = 1),
  BERET("Boina de Artista", "🎨", "Boina francesa con estilo", unlockLevel = 1),
  COWBOY("Sombrero Vaquero", "🤠", "Auténtico sombrero del oeste", unlockLevel = 2),
  STAR_TIARA("Tiara de Estrellas", "✨", "Brillante tiara con destellos", unlockLevel = 3)
}

/**
 * Tipos de camisetas y vestimentas para el torso
 */
enum class CapybaraShirt(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  NONE("Sin Ropa", "❌", "Sin prenda", unlockLevel = 1),
  STRIPED("Rayas Marineras", "⚓", "Camiseta a rayas azules y blancas", unlockLevel = 1),
  HEARTS("Vestido Corazones", "💖", "Vestido rosa con tiernos corazones", unlockLevel = 1),
  RAINBOW_SWEATER("Suéter Arcoíris", "🌈", "Suéter calentito multicolor", unlockLevel = 1),
  SUPERHERO("Capa de Súper", "🦸", "Capa de superhéroe con estrella", unlockLevel = 1),
  HAWAIIAN("Camisa Hawaiana", "🌺", "Camisa playera con flores", unlockLevel = 1),
  DINOSAUR("Traje Dinosaurio", "🦖", "Disfraz verde con púas suaves", unlockLevel = 1)
}

/**
 * Tipos de gafas y accesorios oculares
 */
enum class CapybaraGlasses(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  NONE("Sin Gafas", "❌", "Ojos al natural", unlockLevel = 1),
  SUNGLASSES("Gafas de Sol", "😎", "Gafas oscuras super cool", unlockLevel = 1),
  HEART_GLASSES("Gafas de Corazón", "💕", "Montura rosa en forma de corazón", unlockLevel = 1),
  GOLD_ROUND("Gafas Redondas", "👓", "Gafas doradas de intelectual", unlockLevel = 1),
  STAR_GLASSES("Gafas de Estrella", "⭐", "Gafas amarillas con forma de estrella", unlockLevel = 1),
  SNORKEL("Gafas de Buceo", "🤿", "Máscara de bucear con tubo", unlockLevel = 1),
  BUTTERFLY_GLASSES("Gafas de Mariposa", "🦋", "Gafas con alas de mariposa", unlockLevel = 2),
  MOON_GLASSES("Gafas Lunares", "🌙", "Gafas místicas en media luna", unlockLevel = 4)
}

/**
 * Tipos de calzado / zapatitos para las patitas
 */
enum class CapybaraShoes(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  NONE("Sin Zapatos", "❌", "Patitas descalzas", unlockLevel = 1),
  RAIN_BOOTS("Botas de Lluvia", "🥾", "Botitas amarillas impermeables", unlockLevel = 1),
  SNEAKERS("Zapatillas Tenis", "👟", "Zapatillas deportivas azules", unlockLevel = 1),
  RED_BOOTS("Botitas Rojas", "👢", "Botas rojas brillantes", unlockLevel = 1),
  GOLD_SHOES("Zapatos de Oro", "✨", "Zapatitos dorados de princesa", unlockLevel = 1),
  ROLLER_SKATES("Patines con Ruedas", "🛼", "Patines rosas con ruedas de colores", unlockLevel = 1),
  BEACH_SANDALS("Sandalias de Playa", "🩴", "Frescas sandalias veraniegas", unlockLevel = 2),
  SPACE_BOOTS("Botas Espaciales", "🚀", "Botas futuristas de astronauta", unlockLevel = 5)
}

/**
 * Bebidas refrescantes para acompañar al capibara
 */
enum class CapybaraDrink(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  NONE("Sin Bebida", "❌", "Sin bebida", unlockLevel = 1),
  ORANGE_JUICE("Jugo de Naranja", "🍊", "Delicioso jugo natural de naranja", unlockLevel = 1),
  STRAWBERRY_SMOOTHIE("Batido de Fresa", "🍓", "Dulce batido cremoso de fresa", unlockLevel = 2),
  CHOCOLATE_MILK("Leche con Chocolate", "🍫", "Rica leche chocolatada fría", unlockLevel = 3),
  LEMONADE("Limonada", "🍋", "Fresca limonada con hielo y menta", unlockLevel = 4),
  TROPICAL_COCO("Batido Tropical", "🥥", "Coco refrescante con pajilla y sombrillita", unlockLevel = 5)
}

/**
 * Medios de transporte y vehículos divertidos
 */
enum class CapybaraVehicle(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  NONE("Ninguno", "❌", "Sin vehículo", unlockLevel = 1),
  BICYCLE("Bicicleta", "🚲", "Bicicleta clásica de paseo", unlockLevel = 2),
  SCOOTER("Patinete", "🛴", "Patinete ágil y veloz", unlockLevel = 2),
  MOTORCYCLE("Moto", "🛵", "Motoneta vintage con estilo", unlockLevel = 4),
  CAR("Coche", "🚗", "Mini auto descapotable divertido", unlockLevel = 5)
}

/**
 * Fondos y paisajes disponibles
 */
enum class CapybaraBackground(
  val label: String,
  val iconEmoji: String,
  val description: String,
  val unlockLevel: Int = 1
) {
  BEACH("Playa Soleada", "🏖️", "Arena dorada, mar azul, sol y palmera", unlockLevel = 1),
  FOREST("Bosque Mágico", "🌲", "Árboles verdes, setas y flores silvestres", unlockLevel = 1),
  MEADOW("Pradera Feliz", "🌈", "Colinas verdes, arcoíris y nubes", unlockLevel = 1),
  SUNSET("Puesta de Sol", "🌅", "Cielo cálido al atardecer, montañas y estrellas", unlockLevel = 1),
  RAINFOREST("Selva Tropical", "🌴", "Selva exuberante y exótica", unlockLevel = 3),
  SPACE("Espacio Estrellado", "🌌", "Cosmos infinito y planetas", unlockLevel = 5)
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
  DRINK("Bebidas", "🥤"),
  VEHICLE("Transporte", "🛵"),
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
  val drink: CapybaraDrink = CapybaraDrink.NONE,
  val vehicle: CapybaraVehicle = CapybaraVehicle.NONE,
  val background: CapybaraBackground = CapybaraBackground.MEADOW,
  val isHappy: Boolean = false,
  val happinessCount: Int = 1
)
