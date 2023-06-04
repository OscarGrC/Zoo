package oscargrc.zoo.dto.json

/***
 * la clase AnimalDto que usamos para exportar a Json
 */
data class AnimalDto(
    val id: Long,
    val nombre: String,
    val nombreCientifico: String,
    val clase: String,
    val habitat: String,
    val fechaNacimiento: String,
    val dieta: String,
    val peso: String,
    val tamanio: String,
    val imagen: String,
)