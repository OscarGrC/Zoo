package oscargrc.zoo.models

import java.time.LocalDate

/***
 * @author:OscarGrC
 * Data class modelo Animal
 */
data class Animal(
    val id: Long = NEW_ANIMAL,
    val nombre:String,
    val nombreCientifico: String,
    val clase: String,
    val habitat: String,
    val fechaNacimiento: LocalDate,
    val dieta: String,
    val peso: Double,
    val tamanio: Double,
    val imagen: String
)
{
    companion object {
        const val NEW_ANIMAL = -1L
    }

    val nombreCompleto: String
        get() = "$nombreCientifico, $nombre"

    fun isNewAnimal(): Boolean = id == NEW_ANIMAL

}