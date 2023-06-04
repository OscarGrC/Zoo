package oscargrc.zoo

import oscargrc.zoo.di.AppDIModule
import oscargrc.zoo.routes.RoutesManager
import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

/***
 * La clase principal. Aqui inizializamos la app y lanzamos koin para la inyeccion
 */
class ZooApplication : Application(), KoinComponent {
    init {
        // creamos Koin
        startKoin {
            printLogger() // Logger de Koin
            modules(AppDIModule) // Módulos de Koin
        }
    }
    override fun start(stage: Stage) {
        RoutesManager.apply {
            app = this@ZooApplication
        }.run { initMainStage(stage) }
    }
}
fun main() {
    Application.launch(ZooApplication::class.java)
}