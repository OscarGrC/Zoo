package oscargrc.zoo.controllers

import com.vaadin.open.Open
import javafx.fxml.FXML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/***
 * @author:OscarGrC
 * CLase que se encarga de la gestion de la Vista de la pantalla Acerda De.
 */
class AcercaDeViewController {
    @FXML
    private lateinit var linkGitHub: javafx.scene.control.Hyperlink


    @FXML
    fun initialize() {
        logger.info { "Inicializando AcercaDeViewController FXML" }
        linkGitHub.setOnAction {
            val url = "https://github.com/OscarGrC"
            Open.open(url)
        }
    }
}
