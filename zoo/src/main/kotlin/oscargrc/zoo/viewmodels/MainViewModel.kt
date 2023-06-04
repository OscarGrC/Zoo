package oscargrc.zoo.viewmodels

import com.github.michaelbull.result.*
import oscargrc.zoo.errors.AnimalError
import oscargrc.zoo.locale.round
import oscargrc.zoo.locale.toLocalNumber
import oscargrc.zoo.mappers.toModel
import oscargrc.zoo.models.Animal
import oscargrc.zoo.repositories.AnimalesRepository
import oscargrc.zoo.routes.RoutesManager
import oscargrc.zoo.services.storage.StorageAnimales
import oscargrc.zoo.validators.validate
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.image.Image
import mu.KotlinLogging
import java.io.File
import java.time.LocalDate
import kotlin.properties.Delegates
private val defaultImagen = "images/sin-imagen.png"
private val logger = KotlinLogging.logger {}

/***
 * La clase principal de la app/ aqui tenemos que inyectar el repositorio y el estoraje. En el modelo MVC seria nuestro
 * controlador es la clase encargada de comunicar la fachada con el repositorio
 */
class MainViewModel(private val repository: AnimalesRepository, private val storage: StorageAnimales) {

    val state = MainState()

    init {
        logger.debug { "Inicializando MainState" }
        loadAnimalFromRepository()
        loadTypes()
    }

    private fun loadTypes() {
        logger.debug { "Cargando tipos de animales" }
        state.typesAnimales.clear()
        state.typesAnimales.addAll(TipoFiltro.TODOS.value, TipoFiltro.MAMIFEROS.value, TipoFiltro.PECES.value,
            TipoFiltro.REPTILES.value,TipoFiltro.AVES.value,TipoFiltro.EURASIA.value,TipoFiltro.PESADOS.value)
    }

    private fun loadAnimalFromRepository() {
        logger.debug { "Cargando animales del repositorio" }
        state.animales.setAll(repository.findAll())
        updateActualState()
    }

    private fun updateActualState() {
        logger.debug { "Actualizando estado de Aplicacion" }
        state.animalSeleccionado.limpiar()
        state.animalOperacion.limpiar()
    }

    fun animalesFilteredList(tipo: String, nombreCompleto: String): FilteredList<Animal> {
        logger.debug { "Filtrando lista de Animales: $tipo, $nombreCompleto" }

        return state.animales
            .filtered { animal ->
                when (tipo) {
                    TipoFiltro.MAMIFEROS.value -> animal.clase=="Mamifero"
                    TipoFiltro.PECES.value -> animal.clase=="Pez"
                    TipoFiltro.AVES.value -> animal.clase=="Ave"
                    TipoFiltro.REPTILES.value -> animal.clase=="Reptil"
                    TipoFiltro.EURASIA.value -> animal.habitat=="Eurasia"
                    TipoFiltro.PESADOS.value -> animal.peso > 150.0
                    else -> true
                }
            }.filtered { animal ->
                animal.nombreCompleto.contains(nombreCompleto, true)
            }

    }

    fun saveAnimalToJson(file: File): Result<Long, AnimalError> {
        logger.debug { "Guardando Animales en JSON" }
        return storage.storeDataJson(file, state.animales)
    }

    fun loadAnimalesFromJson(file: File, withImages: Boolean = false): Result<List<Animal>, AnimalError> {
        logger.debug { "Cargando Animales en JSON" }
        return storage.deleteAllImages().andThen {
            storage.loadDataJson(file).onSuccess {
                repository.deleteAll()
                repository.saveAll(
                    if (withImages)
                        it
                    else
                        it.map { a -> a.copy(id = Animal.NEW_ANIMAL, imagen = TipoImagen.SIN_IMAGEN.value) }
                )
                loadAnimalFromRepository()
            }
        }
    }

    fun updateAnimalSeleccionado(animal: Animal) {
        logger.debug { "Actualizando estado de Animal: $animal" }

        state.animalSeleccionado.apply {
            numero.value = animal.id.toString()
            nombre.value = animal.nombre
            nombreCientifico.value = animal.nombreCientifico
            clase.value = animal.clase
            habitat.value = animal.habitat
            fechaNacimiento.value = animal.fechaNacimiento
            dieta.value = animal.dieta
            peso.value = animal.peso.round(2).toLocalNumber()
            tamanio.value = animal.tamanio.round(2).toLocalNumber()
            storage.loadImage(animal.imagen).onSuccess {
                imagen.value = Image(it.absoluteFile.toURI().toString())
                fileImage = it
            }.onFailure {
                imagen.value = Image(RoutesManager.getResourceAsStream(defaultImagen))
                fileImage = File(RoutesManager.getResource(defaultImagen).toURI())
            }

        }
    }

    fun crearAnimal(): Result<Animal, AnimalError> {
        logger.debug { "Creando Animal" }
        val newAnimalTemp = state.animalOperacion.copy()
        var newAnimal = newAnimalTemp.toModel().copy(id = Animal.NEW_ANIMAL)
        return newAnimal.validate()
            .andThen {
                newAnimalTemp.fileImage?.let { newFileImage ->
                    storage.saveImage(newFileImage).onSuccess {
                        newAnimal = newAnimal.copy(imagen = it.name)
                    }
                }
                val new = repository.save(newAnimal)
                state.animales.add(new)
                updateActualState()
                Ok(new)
            }
    }


    fun editarAnimal(): Result<Animal, AnimalError> {
        logger.debug { "Editando Animal" }
        val updatedAnimalTemp = state.animalOperacion.copy()
        val fileImageTemp = state.animalSeleccionado.fileImage
        var updatedAnimal = state.animalOperacion.toModel().copy(imagen = fileImageTemp!!.name)
        return updatedAnimal.validate()
            .andThen {
                updatedAnimalTemp.fileImage?.let { newFileImage ->
                    if (updatedAnimal.imagen == TipoImagen.SIN_IMAGEN.value || updatedAnimal.imagen == TipoImagen.EMPTY.value) {
                        storage.saveImage(newFileImage).onSuccess {
                            updatedAnimal = updatedAnimal.copy(imagen = it.name)
                        }
                    } else {
                        storage.updateImage(fileImageTemp, newFileImage)
                    }
                }
                val updated = repository.save(updatedAnimal)
                val index = state.animales.indexOfFirst { a -> a.id == updated.id }
                state.animales[index] = updated
                updateActualState()
                Ok(updated)
            }
    }

    fun eliminarAnimal(): Result<Unit, AnimalError> {
        logger.debug { "Eliminando Animal" }
        val animal = state.animalSeleccionado.copy()
        val myId = animal.numero.value.toLong()

        animal.fileImage?.let {
            if (it.name != TipoImagen.SIN_IMAGEN.value) {
                storage.deleteImage(it)
            }
        }
        repository.deleteById(myId)
        state.animales.removeIf { it.id == myId }
        updateActualState()
        return Ok(Unit)
    }

    fun updateImageAnimalOperacion(fileImage: File) {
        logger.debug { "Actualizando imagen: $fileImage" }
        state.animalOperacion.imagen.value = Image(fileImage.toURI().toString())
        state.animalOperacion.fileImage = fileImage
    }

    enum class TipoFiltro(val value: String) {
        TODOS("Todos/as"), MAMIFEROS("Mamiferos"), PECES("Peces"),
        REPTILES("Reptiles"), AVES("Aves"),EURASIA("Eurasia"),PESADOS("Mas 150Kg")
    }

    enum class TipoOperacion(val value: String) {
        NUEVO("Nuevo"), EDITAR("Editar")
    }

    enum class TipoImagen(val value: String) {
        SIN_IMAGEN("sin-imagen.png"), EMPTY("")
    }

    data class MainState(
        val typesAnimales: ObservableList<String> = FXCollections.observableArrayList<String>(),
        val animales: ObservableList<Animal> = FXCollections.observableArrayList<Animal>(),
        var animalSeleccionado: AnimalState = AnimalState(),
        val animalOperacion: AnimalState = AnimalState(),
    ) {
        var tipoOperacion: TipoOperacion by Delegates.observable(TipoOperacion.NUEVO) { _, _, newValue ->
            if (newValue == TipoOperacion.EDITAR) {
                logger.debug { "Copiando estado de Animal Seleccionado a Operacion" }

                animalOperacion.numero.value = animalSeleccionado.numero.value
                animalOperacion.nombre.value = animalSeleccionado.nombre.value
                animalOperacion.nombreCientifico.value = animalSeleccionado.nombreCientifico.value
                animalOperacion.clase.value = animalSeleccionado.clase.value
                animalOperacion.habitat.value = animalSeleccionado.habitat.value
                animalOperacion.fechaNacimiento.value = animalSeleccionado.fechaNacimiento.value
                animalOperacion.dieta.value = animalSeleccionado.dieta.value
                animalOperacion.peso.value = animalSeleccionado.peso.value
                animalOperacion.tamanio.value = animalSeleccionado.tamanio.value
                animalOperacion.imagen.value = animalSeleccionado.imagen.value
            } else {
                logger.debug { "Limpiando estado de Animal Operacion" }
                animalOperacion.limpiar()
            }
        }
    }

    data class AnimalState(
        val numero: SimpleStringProperty = SimpleStringProperty(""),
        val nombre: SimpleStringProperty = SimpleStringProperty(""),
        val nombreCientifico: SimpleStringProperty = SimpleStringProperty(""),
        val clase: SimpleStringProperty = SimpleStringProperty(""),
        val habitat: SimpleStringProperty = SimpleStringProperty(""),
        val fechaNacimiento: SimpleObjectProperty<LocalDate> = SimpleObjectProperty(LocalDate.now()),
        val dieta: SimpleStringProperty = SimpleStringProperty(""),
        val peso: SimpleStringProperty = SimpleStringProperty(""),
        val tamanio: SimpleStringProperty = SimpleStringProperty(""),
        val imagen: SimpleObjectProperty<Image> = SimpleObjectProperty(Image(RoutesManager.getResourceAsStream(
            defaultImagen))),
        var fileImage: File? = null
    ) {
        fun limpiar() {
            numero.value = " "
            nombre.value = " "
            nombreCientifico.value = " "
            clase.value = " "
            habitat.value= " "
            fechaNacimiento.value = LocalDate.now()
            dieta.value = " "
            peso.value = " "
            tamanio.value = " "
            imagen.value = Image(RoutesManager.getResourceAsStream(defaultImagen))
            fileImage = null
        }
    }


}

