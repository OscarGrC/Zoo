package oscargrc.zoo.controllers

import com.github.michaelbull.result.*
import oscargrc.zoo.errors.AnimalError
import oscargrc.zoo.routes.RoutesManager
import oscargrc.zoo.viewmodels.MainViewModel
import oscargrc.zoo.viewmodels.MainViewModel.TipoOperacion.EDITAR
import oscargrc.zoo.viewmodels.MainViewModel.TipoOperacion.NUEVO
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate


private val logger = KotlinLogging.logger {}

/***
 * @author:OscarGrC
 * Clase encargada de gestionar la vista donde editamos los Animales.
 * Tiene un bind para cargar los datos de MasterView aunque queda pendiente solucionar fallo
 */
class DetalleViewController : KoinComponent {
    // Inyectamos nuestro ViewModel
    val viewModel: MainViewModel by inject()

    // Botones
    @FXML
    private lateinit var btnGuardar: Button

    @FXML
    private lateinit var btnLimpiar: Button

    @FXML
    private lateinit var btnCancelar: Button

    // Formulario del alumno
    @FXML
    private lateinit var textAnimalNumero: TextField

    @FXML
    private lateinit var textAnimalNombreCientifico: TextField

    @FXML
    private lateinit var textAnimalNombre: TextField

    @FXML
    private lateinit var textAnimalHabitat: TextField

    @FXML
    private lateinit var textAnimalPeso: TextField

    @FXML
    private lateinit var textAnimalTamanio: TextField

    @FXML
    private lateinit var textAnimalDieta: TextField

    @FXML
    private lateinit var textAnimalClase: TextField

    @FXML
    private lateinit var dateAnimalFechaNacimiento: DatePicker

    @FXML
    private lateinit var imageAnimal: ImageView

    /***
     * La funcion initialize es la que se usa para inicializar los bindings (uniones)y los eventos
     */
    @FXML
    fun initialize() {
        logger.debug { "Inicializando DetalleViewController FXML en Modo: ${viewModel.state.tipoOperacion}" }
        textAnimalNumero.isEditable = false // No se puede editar el número
        initBindings()
        initEventos()
    }

    /***
     * funcion que inicializa los eventos aqui decimos que accion se desencadena cuando se genera un cambio
     */
    private fun initEventos() {
        // Botones
        btnGuardar.setOnAction {
            onGuardarAction()
        }
        btnLimpiar.setOnAction {
            onLimpiarAction()
        }
        btnCancelar.setOnAction {
            onCancelarAction()
        }
        imageAnimal.setOnMouseClicked {
            onImageAction()
        }
    }

    /***
     * funcion que pide una imagen para cargarla y unirla a el animal
     */
    private fun onImageAction() {
        logger.debug { "onImageAction" }

        FileChooser().run {
            title = "Selecciona una imagen"
            extensionFilters.addAll(FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            viewModel.updateImageAnimalOperacion(it)
        }

    }

    /***
     * Los Bind o uniones aqui le decimos al programa que los cambios se propagen automaticamente al MasterView
     * esto esta mal tendremos que crear una clase previa donde guardar los valores de Master, pasar una copia
     * y solo cuando de a guardar sobreescribir los valores nuevos. En caso contrario recuperar valores originales
     */
    private fun initBindings() {

        dateAnimalFechaNacimiento.valueProperty().bindBidirectional(viewModel.state.animalSeleccionado.fechaNacimiento)
        // Formulario, ahora si es doble binding, porque va y viene
        textAnimalNumero.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.numero)
        textAnimalNombreCientifico.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.nombreCientifico)
        textAnimalNombre.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.nombre)
        textAnimalHabitat.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.habitat)
        dateAnimalFechaNacimiento.valueProperty().bindBidirectional(viewModel.state.animalSeleccionado.fechaNacimiento)
        textAnimalDieta.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.dieta)
        textAnimalPeso.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.peso)
        textAnimalTamanio.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.tamanio)
        textAnimalClase.textProperty().bindBidirectional(viewModel.state.animalSeleccionado.clase)
        imageAnimal.imageProperty().bindBidirectional(viewModel.state.animalSeleccionado.imagen)
        dateAnimalFechaNacimiento.style = "-fx-opacity: 1"
        dateAnimalFechaNacimiento.editor.style = "-fx-opacity: 1"
    }

    /***
     * funcion encargada de guardar el animal ya sea nuevo o cambiado.
     */
    private fun onGuardarAction() {
        logger.debug { "onGuardarActio" }
        when (viewModel.state.tipoOperacion) {
            NUEVO -> {
                validateForm().andThen {
                    viewModel.crearAnimal()
                }.onSuccess {
                    logger.debug { "Animal creado correctamente" }
                    showAlertOperacion(
                        AlertType.INFORMATION,
                        "Animal creado",
                        "Animal creado correctamente:\n${it.nombreCompleto}"
                    )
                    cerrarVentana()
                }.onFailure {
                    logger.error { "Error al salvar Animal: ${it.message}" }
                    showAlertOperacion(
                        AlertType.ERROR,
                        "Error al salvar Animal",
                        "Se ha producido un error al salvar el Animal:\n${it.message}"
                    )
                }
            }

            EDITAR -> {
                validateForm().andThen {
                    viewModel.editarAnimal()
                }.onSuccess {
                    logger.debug { "Animal editado correctamente" }
                    showAlertOperacion(
                        AlertType.INFORMATION,
                        "Animal editado",
                        "Animal editado correctamente:\n${it.nombreCompleto}"
                    )
                    cerrarVentana()
                }.onFailure {
                    logger.error { "Error al actualizar Animal: ${it.message}" }
                    showAlertOperacion(
                        AlertType.ERROR,
                        "Error al actualizar Animal",
                        "Se ha producido un error al actualizar el Animal:\n${it.message}"
                    )
                }
            }
        }

    }

    private fun cerrarVentana() {
        btnCancelar.scene.window.hide()
    }

    private fun onCancelarAction() {
        logger.debug { "onCancelarAction" }
        cerrarVentana()
    }

    private fun onLimpiarAction() {
        logger.debug { "onLimpiarAction" }
        viewModel.state.animalOperacion.limpiar()
    }

    /***
     * Unas validaciones minimas de los campos para procurar no introducir errores en la bbdd
     */
    private fun validateForm(): Result<Unit, AnimalError> {
        logger.debug { "validateForm" }

        if (textAnimalNombre.text.isNullOrEmpty()) {
            return Err(AnimalError.ValidationProblem("El nombre no puede estar vacío"))
        }
        if (textAnimalNombreCientifico.text.isNullOrEmpty()) {
            return Err(AnimalError.ValidationProblem("Nombre cientifico no puede estar vacío"))
        }
        if (textAnimalDieta.text.isNullOrEmpty()) {
            return Err(AnimalError.ValidationProblem("La dieta no puede estar vacía"))
        }
        if (textAnimalHabitat.text.isNullOrEmpty()) {
            return Err(AnimalError.ValidationProblem("El habitat no puede estar vacío"))
        }
        if (textAnimalPeso.text.isNullOrEmpty()) {
            return Err(AnimalError.ValidationProblem("El peso no puede estar vacío"))
        }
        if (dateAnimalFechaNacimiento.value == null || dateAnimalFechaNacimiento.value.isAfter(LocalDate.now())) {
            return Err(AnimalError.ValidationProblem("Fecha de nacimiento no puede estar vacía y debe ser anterior a hoy"))
        }
        return Ok(Unit)
    }

    private fun showAlertOperacion(
        alerta: AlertType = AlertType.CONFIRMATION,
        title: String = "",
        mensaje: String = ""
    ) {
        val alert = Alert(alerta)
        alert.title = title
        alert.contentText = mensaje
        alert.showAndWait()
    }


}



