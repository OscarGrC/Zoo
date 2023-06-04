package oscargrc.zoo.controllers

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import oscargrc.zoo.models.Animal
import oscargrc.zoo.routes.RoutesManager
import oscargrc.zoo.viewmodels.MainViewModel
import oscargrc.zoo.viewmodels.MainViewModel.TipoOperacion
import javafx.fxml.FXML
import javafx.scene.Cursor.DEFAULT
import javafx.scene.Cursor.WAIT
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


private val logger = KotlinLogging.logger {}

/***
 * @author:OscarGrC
 * Esta es la clase encargada de manejar la vista principal de la app
 */
class MasterViewController : KoinComponent {

    val viewModel: MainViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuImportar: MenuItem

    @FXML
    private lateinit var menuExportar: MenuItem


    @FXML
    private lateinit var menuAcercaDe: MenuItem

    // Botones
    @FXML
    private lateinit var btnNuevo: Button

    @FXML
    private lateinit var btnEditar: Button

    @FXML
    private lateinit var btnEliminar: Button

    //Combo
    @FXML
    private lateinit var comboTipo: ComboBox<String>

    // Tabla
    @FXML
    private lateinit var tableAnimales: TableView<Animal>
    @FXML
    private lateinit var tableColumnNumero: TableColumn<Animal, Long>
    @FXML
    private lateinit var tableColumNombre: TableColumn<Animal, String>
    @FXML
    private lateinit var tableColumNombreCientifico: TableColumn<Animal, String>
    @FXML
    private lateinit var tableColumHabitat: TableColumn<Animal, String>
    @FXML
    private lateinit var tableColumDieta: TableColumn<Animal, String>
    @FXML
    private lateinit var tableColumPeso: TableColumn<Animal, Double>
    @FXML
    private lateinit var tableColumTamanio: TableColumn<Animal, Double>
    @FXML
    private lateinit var tableColumClase: TableColumn<Animal, String>

    // Buscador
    @FXML
    private lateinit var textBuscador: TextField


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
    private lateinit var textAnimalClase: TextField

    @FXML
    private lateinit var textAnimalPeso: TextField

    @FXML
    private lateinit var textAnimalTamanio: TextField

    @FXML
    private lateinit var textAnimalDieta: TextField

    @FXML
    private lateinit var dateAnimalFechaNacimiento: DatePicker

    @FXML
    private lateinit var imageAnimal: ImageView

    // Metodo para inicializar
    @FXML
    fun initialize() {
        logger.debug { "Inicializando ExpedientesDeViewController FXML" }
        initBindings()
        initEventos()
    }
// metodo que carga los bind
    private fun initBindings() {
        logger.debug { "Inicializando bindings" }

        // comboBox
        comboTipo.items = viewModel.state.typesAnimales
        comboTipo.selectionModel.selectFirst()

        // Tabla
        tableAnimales.items = viewModel.state.animales
        // columnas, con el nombre de la propiedad del objeto hará binding

        tableColumnNumero.cellValueFactory = PropertyValueFactory("id")
        tableColumNombre.cellValueFactory = PropertyValueFactory("nombre")
        tableColumNombreCientifico.cellValueFactory = PropertyValueFactory("nombreCientifico")
        tableColumHabitat.cellValueFactory = PropertyValueFactory("habitat")
        tableColumDieta.cellValueFactory = PropertyValueFactory("dieta")
        tableColumPeso.cellValueFactory = PropertyValueFactory("peso")
        tableColumTamanio.cellValueFactory = PropertyValueFactory("tamanio")
        tableColumClase.cellValueFactory = PropertyValueFactory("clase")
        dateAnimalFechaNacimiento.valueProperty().bindBidirectional(viewModel.state.animalSeleccionado.fechaNacimiento)

        // Formulario, solo queremos enlazar el alumno, no tocar el estado, por eso es bind
        textAnimalNumero.textProperty().bind(viewModel.state.animalSeleccionado.numero)
        textAnimalNombreCientifico.textProperty().bind(viewModel.state.animalSeleccionado.nombreCientifico)
        textAnimalNombre.textProperty().bind(viewModel.state.animalSeleccionado.nombre)
        textAnimalHabitat.textProperty().bind(viewModel.state.animalSeleccionado.habitat)
        dateAnimalFechaNacimiento.valueProperty().bind(viewModel.state.animalSeleccionado.fechaNacimiento)
        textAnimalDieta.textProperty().bind(viewModel.state.animalSeleccionado.dieta)
        textAnimalPeso.textProperty().bind(viewModel.state.animalSeleccionado.peso)
        textAnimalTamanio.textProperty().bind(viewModel.state.animalSeleccionado.tamanio)
        textAnimalClase.textProperty().bind(viewModel.state.animalSeleccionado.clase)
        imageAnimal.imageProperty().bind(viewModel.state.animalSeleccionado.imagen)

        dateAnimalFechaNacimiento.style = "-fx-opacity: 1"
        dateAnimalFechaNacimiento.editor.style = "-fx-opacity: 1"
    }
// fun que inicia los eventos
    private fun initEventos() {
        logger.debug { "Inicializando eventos" }

        // menús
        menuImportar.setOnAction {
            onImportarAction()
        }

        menuExportar.setOnAction {
            onExportarAction()
        }


        menuAcercaDe.setOnAction {
            onAcercaDeAction()
        }

        // Botones
        btnNuevo.setOnAction {
            onNuevoAction()
        }

        btnEditar.setOnAction {
            onEditarAction()
        }

        btnEliminar.setOnAction {
            onEliminarAction()
        }

        // Combo
        comboTipo.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onComboSelected(it) }
        }

        // Tabla
        tableAnimales.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onTablaSelected(it) }
        }
        textBuscador.setOnKeyReleased { newValue ->
            newValue?.let { onKeyReleasedAction() }
        }
    }

    private fun onKeyReleasedAction() {
        logger.debug { "onKeyReleasedAction" }
        filterDataTable()
    }

    private fun filterDataTable() {
        logger.debug { "filterDataTable" }
        // filtramos por el tipo seleccionado en la tabla
        tableAnimales.items = viewModel.animalesFilteredList(comboTipo.value, textBuscador.text.trim())
    }

    private fun onTablaSelected(newValue: Animal) {
        logger.debug { "onTablaSelected: $newValue" }
        viewModel.updateAnimalSeleccionado(newValue)
    }

    private fun onComboSelected(newValue: String) {
        logger.debug { "onComboSelected: $newValue" }
        filterDataTable()
    }

    fun onEliminarAction() {
        logger.debug { "onEliminarAction" }
        if (tableAnimales.selectionModel.selectedItem == null) {
            return
        }
        val alert = Alert(AlertType.CONFIRMATION)
        alert.title = "¿Eliminar Animal/?"
        alert.contentText =
            "¿Desea eliminar este animal?\nEsta acción no se puede deshacer y se eliminarán todos los datos asociados."
        val result = alert.showAndWait()
        if (result.get() == ButtonType.OK) {
            viewModel.eliminarAnimal().onSuccess {
                logger.debug { "Animal eliminado correctamente" }
                showAlertOperacion(
                    alerta = AlertType.INFORMATION,
                    "Animal eliminado",
                    "Se ha eliminado el animal correctamente del sistema"
                )
                tableAnimales.selectionModel.clearSelection()
            }.onFailure {
                logger.error { "Error al eliminar el animal: ${it.message}" }
                showAlertOperacion(alerta = AlertType.ERROR, "Error al eliminar animal", it.message)
            }
        } else {
            alert.close()
        }

    }

    private fun onEditarAction() {
        logger.debug { "onEditarAction" }
        if (tableAnimales.selectionModel.selectedItem == null) {
            return
        }
        viewModel.state.tipoOperacion = TipoOperacion.EDITAR
        RoutesManager.initDetalle()

    }

    private fun onNuevoAction() {
        logger.debug { "onNuevoAction" }
        viewModel.state.tipoOperacion = TipoOperacion.NUEVO
        RoutesManager.initDetalle()
    }

    private fun onAcercaDeAction() {
        logger.debug { "onAcercaDeAction" }
        RoutesManager.initAcercaDeStage()
    }

    /***
     * funcion que exporta a Json usando Gson
     */
    private fun onExportarAction() {
        logger.debug { "onExportarAction" }
        FileChooser().run {
            title = "Exportar animales"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
            showSaveDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onSaveAction: $it" }
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.saveAnimalToJson(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos exportados",
                        mensaje = "Se han exportado los Animales: ${viewModel.state.animales.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al exportar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

    /***
     * funcion que carga de Json usando Gson
     */
    private fun onImportarAction() {
        logger.debug { "onImportarAction" }
        FileChooser().run {
            title = "Importar expedientes"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onAbrirAction: $it" }
            showAlertOperacion(
                AlertType.INFORMATION,
                "Importando datos",
                "Importando datos Se sustituye la imagen por una imagen por defecto."
            )
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.loadAnimalesFromJson(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos importados",
                        mensaje = "Se ha importado tus animales.\n Animales importados: ${viewModel.state.animales.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al importar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
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
