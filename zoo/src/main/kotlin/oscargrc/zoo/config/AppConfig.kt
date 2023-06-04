package oscargrc.zoo.config

import mu.KotlinLogging
import java.io.File
import java.io.InputStream
import java.util.*

private val logger = KotlinLogging.logger {}
private const val CONFIG_FILE_NAME = "application.properties"

/***
 * @author:OscarGrC
 * Clase AppConfig que carga fichero properties y carga los valores por defecto que queremos aplicar
 */
class AppConfig {
    init {
        logger.debug { "Cargando configuración" }
    }
    val APP_PATH = System.getProperty("user.dir")
    val imagesDirectory by lazy {
        val path = readProperty("app.images") ?: "imagenes"
        "$APP_PATH${File.separator}$path/"
    }
    val databaseUrl: String by lazy {
        readProperty("app.database.url") ?: "jdbc:sqlite:database.db"
    }
    val databaseInit: Boolean by lazy {
        readProperty("app.database.init")?.toBoolean() ?: false
    }
    val databaseRemoveData: Boolean by lazy {
        readProperty("app.database.removedata")?.toBoolean() ?: false
    }

    /***
     * Funcion que lee del archivo applicacion.properties la configuracion
     */
    private fun readProperty(propiedad: String): String? {
        logger.debug { "Leyendo propiedades en AppConfig" }
        val properties = Properties()
        val inputStream: InputStream = ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME)
            ?: throw Exception("ERROR LECTURA")
        properties.load(inputStream)
        return properties.getProperty(propiedad)
    }
}