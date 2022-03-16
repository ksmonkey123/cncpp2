package ch.awae.cncpp.fx

import ch.awae.cncpp.LoggedComponent
import ch.awae.cncpp.file.FileMapping
import ch.awae.cncpp.file.FileRepository
import ch.awae.cncpp.fx.modal.ErrorReportService
import ch.awae.cncpp.fx.modal.FileLocationService
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.util.Callback
import org.springframework.stereotype.Controller

@Controller
class FileListController(
    private val rootController: RootController,
    private val fileViewController: FileViewController,
    private val fileLocationService: FileLocationService,
    private val errorReportService: ErrorReportService,
    private val fileRepository: FileRepository
) : LoggedComponent(), FxController {

    @FXML
    lateinit var fileList: ListView<FileMapping>

    @FXML
    lateinit var nextButton: Button

    val files: ObservableList<FileMapping> = FXCollections.observableArrayList()

    override fun initialize() {
        nextButton.isDisable = files.isEmpty()
        fileList.items = files
        fileList.cellFactory = Callback { FileListCell() }
        files.addListener(ListChangeListener { nextButton.isDisable = it.list.isEmpty() })
        logInitialized()
    }

    fun onAddFile(actionEvent: ActionEvent) {
        fileLocationService.askForFileToOpen()
            ?.let { errorReportService.reporting { fileRepository.loadFile(it) } }
            ?.let(::addFile)
    }

    private fun addFile(fileMapping: FileMapping) {
        LOG.info("adding file ${fileMapping.uuid}")
        files.add(fileMapping)
    }

    private fun onRemoveFile(item: FileMapping) {
        LOG.info("remove file ${item.uuid}")
        files.remove(item)
        fileRepository.discardFile(item)
    }

    private fun onShowFile(item: FileMapping) {
        LOG.info("show file ${item.uuid}")
        fileViewController.loadFile(item)
        rootController.activeTab = RootController.RootTab.FILE_VIEW
    }

    fun onCreateFile(actionEvent: ActionEvent) {
        addFile(fileRepository.createVirtualFile())
    }

    fun onNext(actionEvent: ActionEvent) {
        rootController.activeTab = RootController.RootTab.OPTIONS
    }

    private inner class FileListCell : ListCell<FileMapping>() {
        val box: BorderPane
        val label: Label
        val button: Button

        init {
            prefWidthProperty().bind(fileList.widthProperty().subtract(2))
            maxWidth = Control.USE_PREF_SIZE

            label = Label("(empty)").apply {
                textOverrun = OverrunStyle.LEADING_ELLIPSIS
                onMouseClicked = EventHandler {
                    if (it.clickCount == 2 && it.button == MouseButton.PRIMARY) {
                        onShowFile(item)
                    }
                }
            }

            button = Button("X").apply {
                onAction = EventHandler { onRemoveFile(item) }
            }

            box = BorderPane().apply {
                center = HBox().apply {
                    alignment = Pos.CENTER_LEFT
                    children.add(label)
                }
                right = button
            }
        }

        override fun updateItem(item: FileMapping?, empty: Boolean) {
            super.updateItem(item, empty)
            when {
                empty -> graphic = null
                else -> {
                    label.text = item?.path
                    graphic = box
                }
            }
        }
    }

}
