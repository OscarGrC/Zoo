package oscargrc.zoo.repositories

import database.AnimalTable
import oscargrc.zoo.mappers.toAninal
import oscargrc.zoo.mappers.toClass
import oscargrc.zoo.models.Animal
import oscargrc.zoo.services.database.SqlDeLightClient
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/***
 * @author:OscarGrC
 * Implementacion de Interfaz AnimalesRepository encargada de comunicarse con la bbdd
 */
class AnimalesRepositoryImpl(
    private val databaseClient: SqlDeLightClient
) : AnimalesRepository {

    val database = databaseClient.database


    override fun findAll(): List<Animal> {
        logger.debug { "findAll" }
     var salida: List<AnimalTable> = database.selectAll().executeAsList()
        return salida.toAninal()
    }

    override fun findById(id: Long): Animal? {
        logger.debug { "findById: $id" }

        return database.selectById(id).executeAsOneOrNull()?.toClass()
    }

    override fun save(animal: Animal): Animal {
        logger.debug { "save: $animal" }
        return if (animal.isNewAnimal()) {
            create(animal)
        } else {
            update(animal)
        }
    }

    private fun create(animal: Animal): Animal {
        logger.debug { "create: $animal" }

        database.transaction {
            database.insert(
                nombre = animal.nombre,
                nombreCientifico = animal.nombreCientifico,
                clase = animal.clase,
                habitat = animal.habitat,
                fechaNacimiento = animal.fechaNacimiento.toString(),
                dieta = animal.dieta,
                peso = animal.peso,
                tamanio = animal.tamanio,
                imagen = animal.imagen
            )
        }
        return database.selectLastInserted().executeAsOne().toClass()
    }

    private fun update(animal: Animal): Animal {
        logger.debug { "update: $animal" }
        database.update(
            id = animal.id,
            nombre = animal.nombre,
            nombreCientifico = animal.nombreCientifico,
            clase = animal.clase,
            habitat = animal.habitat,
            fechaNacimiento = animal.fechaNacimiento.toString(),
            dieta = animal.dieta,
            peso = animal.peso,
            tamanio = animal.tamanio,
            imagen = animal.imagen
        )
        return animal
    }

    override fun deleteById(id: Long) {
        logger.debug { "deleteById: $id" }
        return database.delete(id)
    }

    override fun deleteAll() {
        logger.debug { "deleteAll" }
        return database.deleteAll()
    }

    override fun saveAll(animals: List<Animal>): List<Animal> {
        logger.debug { "saveAll: $animals" }
        return animals.map { save(it) }
    }
}