package oscargrc.zoo.services.storage

import com.github.michaelbull.result.Result
import oscargrc.zoo.errors.AnimalError
import oscargrc.zoo.models.Animal
import java.io.File

interface StorageAnimales {
    fun storeDataJson(file: File, data: List<Animal>): Result<Long, AnimalError>
    fun loadDataJson(file: File): Result<List<Animal>, AnimalError>
    fun saveImage(fileName: File): Result<File, AnimalError>
    fun loadImage(fileName: String): Result<File, AnimalError>
    fun deleteImage(fileName: File): Result<Unit, AnimalError>
    fun deleteAllImages(): Result<Long, AnimalError>
    fun updateImage(imagenName: File, newFileImage: File): Result<File, AnimalError>
}