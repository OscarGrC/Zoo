package oscargrc.zoo.repositories

import app.cash.sqldelight.Query
import database.AnimalTable
import database.ZooQueries
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import oscargrc.zoo.config.AppConfig
import oscargrc.zoo.models.Animal
import oscargrc.zoo.services.database.SqlDeLightClient
import java.time.LocalDate
import javax.security.auth.callback.Callback

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnimalesRepositoryImplTest {
    @Mock
    val mockDatabase = mock(SqlDeLightClient::class.java)
    @Mock
    val mockZooQueries = mock(ZooQueries::class.java)

    private  var repositorio =AnimalesRepositoryImpl(mockDatabase)
    private val listaAnimales = listOf<Animal>(
        Animal(
            1L, "Laika", "Canis lupus familiaris",
            "Mamifero", "Eurasia", LocalDate.parse("1954-02-02"), "Omnívoro", 10.2,
            80.1, "1685562882347.jpg"
        ),
        Animal(
            2L, "Ham", "Pan troglodytes", "Mamifero", "Africa",
            LocalDate.parse("1991-02-02"), "Omnívoro", 65.2, 130.0, "1685562938283.jpg"
        ),
        Animal(
            3L, "Koko", "Troglodytes gorilla", "Mamifero", "Africa",
            LocalDate.parse("2020-03-03"), "Omnívoro", 150.32, 165.12, "1685601967067.jpg"
        ),
        Animal(
            4L, "Dolly", "Ovis orientalis aries", "Mamifero", "Escocia",
            LocalDate.parse("2022-04-04"), "Herbivoro", 65.5, 70.0, "1685601795955.jpg"
        ),
        Animal(
            5L, "April", "Giraffa camelopardalis", "Mamifero", "Africa",
            LocalDate.parse("2021-05-05"), "Herbivoro", 1442.53, 498.76, "1682763355435.jpg"
        ),
        Animal(
            6L, "Allons-y", "Chelonia mydas", "Reptil", "Oceano",
            LocalDate.parse("1920-09-26"), "Herbivoro", 193.35, 154.2, "1685601474977.jpg"
        )
    )
    val animalTest =   Animal(
        7L, "Allonso", "Chelonia mydas", "Reptil", "Oceano",
        LocalDate.parse("1920-09-26"), "Herbivoro", 193.35, 154.2, "1685601474977.jpg"
    )

    @BeforeEach
    fun setUp() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this)
        Mockito.`when`(mockDatabase.database).thenReturn(mockZooQueries)

        // Configurar el comportamiento del mock mockZooQueries
        Mockito.`when`(mockZooQueries.selectAll()).thenReturn(listaAnimales)
        Mockito.`when`(mockZooQueries.selectById(1L)).thenReturn(listaAnimales[0])
        Mockito.`when`(mockZooQueries.selectById(33L)).thenReturn(null)

    }

    @Test
    fun findAll() {
        val consulta = repositorio.findAll()
        assertAll(
            { assertEquals(consulta, listaAnimales) },
        )

    }


    @Test
    fun findById() {
        val consulta = repositorio.findById(1L)
        val consulta2 = repositorio.findById(33L)
        assertAll(
            { assertEquals(consulta, listaAnimales[0]) },
            { assertEquals(consulta2, null) }
        )


    }

    @Test
    fun save() {
        repositorio.save(animalTest)
        val consulta = repositorio.findById(6L)
        assertAll(
            { assertEquals(consulta, animalTest) }
        )


    }

    @Test
    fun deleteById() {
        repositorio.deleteById(1L)
        val consulta = repositorio.findById(1L)
        assertAll(
            { assertEquals(consulta, null) }
        )
    }

    @Test
    fun deleteAll() {
        repositorio.deleteAll()
        val consulta = repositorio.findAll()
        assertAll(
            { assertEquals(consulta.size,0) }
        )
    }




}