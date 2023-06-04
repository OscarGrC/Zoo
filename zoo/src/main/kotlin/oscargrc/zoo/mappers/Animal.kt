package oscargrc.zoo.mappers

import database.AnimalTable
import oscargrc.zoo.dto.json.AnimalDto
import oscargrc.zoo.models.Animal
import oscargrc.zoo.viewmodels.MainViewModel.AnimalState
import java.time.LocalDate

/***
 * Mappers de Animala dto y al reves asi como sus listas
 */
fun AnimalDto.toAnimal(): Animal {
    return Animal(
   id,nombre,nombreCientifico,clase,habitat,LocalDate.parse(fechaNacimiento),dieta,peso.toDouble(),
        tamanio.toDouble(),imagen)
}

fun List<AnimalDto>.toAnimal(): List<Animal> {
    var salida = mutableListOf<Animal>()
    this.map {it.toAnimal() }
    return salida
}

fun Animal.toDto(): AnimalDto {
    return AnimalDto(
        id = id,nombre = nombre,nombreCientifico = nombreCientifico,clase = clase,habitat= habitat,
        fechaNacimiento= fechaNacimiento.toString(),dieta = dieta,peso= peso.toString(),tamanio=tamanio.toString()
        ,imagen= imagen)
}

fun List<Animal>.toDto(): List<AnimalDto> {
    return map { it.toDto() }
}

fun AnimalTable.toClass(): Animal {
    return Animal(
        id,
        nombre,
        nombreCientifico,
        clase,
        habitat,
        LocalDate.parse(fechaNacimiento),
        dieta,
        peso,
        tamanio,
        imagen
    )

}
fun List<AnimalTable>.toAninal():List<Animal> {
    var salida = mutableListOf<Animal>()
    this.forEach { salida+=it.toClass() }
    return salida
}


fun AnimalState.toModel(): Animal {
    return Animal(
        id = if (numero.value.trim().isBlank()) oscargrc.zoo.models.Animal.NEW_ANIMAL else numero.value.toLong(),
        nombre = nombre.value,
        nombreCientifico = nombreCientifico.value.trim(),
        clase = clase.value.trim(),
        habitat = habitat.value,
        fechaNacimiento = fechaNacimiento.value,
        dieta = dieta.value,
        peso = peso.value.trim().replace(",", ".").toDouble(),
        tamanio = tamanio.value.trim().replace(",", ".").toDouble(),
        imagen = imagen.value.url ?: "sin-imagen.png"
    )
}



