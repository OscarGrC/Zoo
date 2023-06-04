package oscargrc.zoo.routes

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import mu.KotlinLogging
import java.io.InputStream
import java.net.URL
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Clase que gestiona las rutas de la aplicación encargado de direccionar el cambio de paginas en vez de directamente
 * al saltar el evento
 */
object RoutesManager {
    // Necesitamos siempre saber
    private lateinit var mainStage: Stage
    private lateinit var _activeStage: Stage
    val activeStage: Stage
        get() = _activeStage
    lateinit var app: Application
    private var scenesMap: HashMap<String, Pane> = HashMap()
    private val iconoName ="icons/app-icon.png"

    enum class View(val fxml: String) {
        MAIN("views/master-view.fxml"),
        ACERCA_DE("views/AcercaDe-view.fxml"),
        DETAILS("views/detalle-view.fxml"),
    }


    init {
        logger.debug { "Inicializando RoutesManager" }
        Locale.setDefault(Locale("es", "ES"))
    }

    fun initMainStage(stage: Stage) {
        logger.debug { "Inicializando MainStage" }

        val fxmlLoader = FXMLLoader(getResource(View.MAIN.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val scene = Scene(parentRoot, 905.0, 629.0)
        stage.title = "ZooMatic 9000"
        stage.isResizable = false
        stage.icons.add(Image(getResourceAsStream(iconoName)))
        stage.setOnCloseRequest {

        }
        stage.scene = scene
        mainStage = stage
        _activeStage = stage
        mainStage.show()

    }

    fun initAcercaDeStage() {
        logger.debug { "Inicializando AcercaDeStage" }

        val fxmlLoader = FXMLLoader(getResource(View.ACERCA_DE.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val scene = Scene(parentRoot, 407.0, 364.0)
        val stage = Stage()
        stage.title = "Acerca De Mi"
        stage.scene = scene
        stage.icons.add(Image(getResourceAsStream(iconoName)))
        stage.initOwner(mainStage)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.isResizable = false
        stage.show()
    }

    // Abrimos one como una nueva ventana
    fun initDetalle() {
        logger.debug { "Inicializando Edicion" }

        val fxmlLoader = FXMLLoader(getResource(View.DETAILS.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val scene = Scene(parentRoot, 375.0, 520.0)
        val stage = Stage()
        stage.title = "Detalle de Animal"
        stage.scene = scene
        stage.icons.add(Image(getResourceAsStream(iconoName)))
        stage.initOwner(mainStage)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.isResizable = false
        stage.show()
    }

    fun getResource(resource: String): URL {
        return app::class.java.getResource(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso: $resource")
    }

    fun getResourceAsStream(resource: String): InputStream {
        return app::class.java.getResourceAsStream(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso como stream: $resource")
    }
}

