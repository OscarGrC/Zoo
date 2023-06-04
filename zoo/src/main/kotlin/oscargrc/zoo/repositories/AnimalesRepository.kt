package oscargrc.zoo.repositories

import oscargrc.zoo.models.Animal

interface AnimalesRepository {
    fun findAll(): List<Animal>
    fun findById(id: Long): Animal?
    fun save(animal: Animal): Animal
    fun deleteById(id: Long)
    fun deleteAll()
    fun saveAll(animals: List<Animal>): List<Animal>
}