package oscargrc.zoo.services.storage

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import oscargrc.zoo.config.AppConfig
import oscargrc.zoo.dto.json.AnimalDto
import oscargrc.zoo.errors.AnimalError
import oscargrc.zoo.mappers.toDto
import oscargrc.zoo.mappers.toAnimal
import oscargrc.zoo.models.Animal
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Instant


private val logger = KotlinLogging.logger {}

/***
 * Clase encargada del almacenamiento en Json , si quisieramos otro tipo de import o export se pondrian aqui
 */
class StorageAnimalesImpl(
    private val appConfig: AppConfig
) : StorageAnimales {

    init {
        // Creamos el directorio de imagenes si no existe
        logger.debug { "Creando directorio de imagenes si no existe" }
        Files.createDirectories(Paths.get(appConfig.imagesDirectory))
    }

    override fun storeDataJson(file: File, data: List<Animal>): Result<Long, AnimalError> {
        logger.debug { "Guardando datos en fichero $file" }
        return try {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(data.toDto())
            file.writeText(jsonString)
            Ok(data.size.toLong())
        } catch (e: Exception) {
            Err(AnimalError.SaveJson("Error al escribir el JSON: ${e.message}"))
        }

    }

    override fun loadDataJson(file: File): Result<List<Animal>, AnimalError> {
        logger.debug { "Cargando datos en fichero $file" }
        val gson = GsonBuilder().setPrettyPrinting().create()
        val importType = object : TypeToken<List<AnimalDto>>() {}.type
        return try {
            val jsonString = file.readText()
            val data = gson.fromJson<List<AnimalDto>>(jsonString, importType)
            Ok(data.toAnimal())
        } catch (e: Exception) {
            Err(AnimalError.LoadJson("Error al parsear el JSON: ${e.message}"))
        }

    }

    private fun getImagenName(newFileImage: File): String {
        val name = newFileImage.name
        val extension = name.substring(name.lastIndexOf(".") + 1)
        return "${Instant.now().toEpochMilli()}.$extension"
    }

    override fun saveImage(fileName: File): Result<File, AnimalError> {
        logger.debug { "Guardando imagen $fileName" }
        return try {
            val newFileImage = File(appConfig.imagesDirectory + getImagenName(fileName))
            Files.copy(fileName.toPath(), newFileImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
            Ok(newFileImage)
        } catch (e: Exception) {
            Err(AnimalError.SaveImage("Error al guardar la imagen: ${e.message}"))
        }
    }

    override fun loadImage(fileName: String): Result<File, AnimalError> {
        logger.debug { "Cargando imagen $fileName" }
        val file = File(appConfig.imagesDirectory + fileName)
        return if (file.exists()) {
            Ok(file)
        } else {
            Err(AnimalError.LoadImage("La imagen no existe: ${file.name}"))
        }
    }

    override fun deleteImage(fileImage: File): Result<Unit, AnimalError> {
        logger.debug { "Borrando imagen $fileImage" }
        Files.deleteIfExists(fileImage.toPath())
        return Ok(Unit)
    }

    override fun deleteAllImages(): Result<Long, AnimalError> {
        logger.debug { "Borrando todas las imagenes" }
        return try {
            Ok(Files.walk(Paths.get(appConfig.imagesDirectory))
                .filter { Files.isRegularFile(it) }
                .map { Files.deleteIfExists(it) }
                .count())
        } catch (e: Exception) {
            Err(AnimalError.DeleteImage("Error al borrar todas las imagenes: ${e.message}"))
        }
    }

    override fun updateImage(imageFile: File, newFileImage: File): Result<File, AnimalError> {
        logger.debug { "Actualizando imagen $imageFile" }
        return try {
            val newUpdateImage = File(appConfig.imagesDirectory + imageFile.name)
            Files.copy(newFileImage.toPath(), newUpdateImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
            Ok(newUpdateImage)
        } catch (e: Exception) {
            Err(AnimalError.SaveImage("Error al guardar la imagen: ${e.message}"))
        }
    }

}

