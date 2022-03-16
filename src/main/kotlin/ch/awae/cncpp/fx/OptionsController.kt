package ch.awae.cncpp.fx

import ch.awae.cncpp.LoggedComponent
import ch.awae.cncpp.fx.modal.ErrorReportService
import ch.awae.cncpp.logic.*
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import org.springframework.stereotype.Controller

@Controller
class OptionsController(
    private val rootController: RootController,
    private val fileListController: FileListController,
    private val errorReportService: ErrorReportService,
    private val processingService: ProcessingService
) : LoggedComponent(), FxController {

    @FXML
    lateinit var homingMode: ComboBox<HomingMode>

    @FXML
    lateinit var updateBedMesh: CheckBox

    @FXML
    lateinit var workSpeed: TextField

    @FXML
    lateinit var travelSpeed: TextField

    @FXML
    lateinit var zeroX: TextField

    @FXML
    lateinit var zeroY: TextField

    @FXML
    lateinit var homingExtras: VBox

    private val numericRegex = Regex("\\d+")

    override fun initialize() {
        homingMode.value = HomingMode.HOME_ALL
        homingMode.valueProperty().addListener { _, _, newValue ->
            homingExtras.isDisable = newValue != HomingMode.HOME_ALL
        }

        listOf(workSpeed, travelSpeed, zeroX, zeroY).forEach(::addNumericEnforcer)
    }

    private fun addNumericEnforcer(field: TextField) {
        field.textProperty().addListener { _, old, new ->
            if (!new.matches(numericRegex)) {
                field.text = old
            }
        }
    }

    fun onBack(actionEvent: ActionEvent) {
        rootController.activeTab = RootController.RootTab.FILE_LIST
    }

    fun onProcess(actionEvent: ActionEvent) {
        errorReportService.reporting {
            processingService.process(collectParameters(), fileListController.files)
        }
    }

    private fun collectParameters(): ProcessingParameters {
        return ProcessingParameters(
            homingParameters = when (homingMode.value!!) {
                HomingMode.HOME_ALL -> HomingParameters(
                    levelBed = updateBedMesh.isSelected,
                    zeroOffset = Point(zeroX.text.toInt(), zeroY.text.toInt())
                )
                HomingMode.HOME_Z -> null
            },
            workSpeed = workSpeed.text.toInt(),
            travelSpeed = travelSpeed.text.toInt()
        )
    }

}