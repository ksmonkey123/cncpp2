package ch.awae.cncpp

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.util.Callback
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Cncpp2Application : Application() {

    lateinit var rootNode : Parent

    override fun start(stage: Stage) {
        stage.scene = Scene(rootNode)
        stage.show()
        stage.title = "CNC G-Code Post-Processor 2"
    }

    override fun init() {
        val app = SpringApplication(javaClass)
        app.webApplicationType = WebApplicationType.NONE
        val context = app.run()
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/Root.fxml"))
        fxmlLoader.controllerFactory = Callback { context.getBean(it) }
        rootNode = fxmlLoader.load()
    }

}

fun main(args: Array<String>) {
    Application.launch(Cncpp2Application::class.java, *args)
}
