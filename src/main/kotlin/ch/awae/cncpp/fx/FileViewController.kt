package ch.awae.cncpp.fx

import ch.awae.cncpp.LoggedComponent
import ch.awae.cncpp.file.FileMapping
import ch.awae.cncpp.file.FileRepository
import ch.awae.cncpp.fx.modal.ErrorReportService
import ch.awae.cncpp.util.splitLines
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import org.springframework.stereotype.Controller

@Controller
class FileViewController(
    private val rootController: RootController,
    private val errorReportService: ErrorReportService,
    private val fileRepository: FileRepository
) : LoggedComponent() {

    @FXML
    lateinit var textArea: TextArea

    @FXML
    lateinit var reloadButton: Button

    lateinit var currentFile: FileMapping

    fun loadFile(fileMapping: FileMapping) {
        LOG.info("loading file ${fileMapping.uuid}")
        currentFile = fileMapping
        textArea.text = fileRepository.getFileContent(fileMapping).joinToString(separator = "\n")
        reloadButton.isVisible = fileMapping.reloadable
    }

    fun onBack(actionEvent: ActionEvent) {
        rootController.activeTab = RootController.RootTab.FILE_LIST
    }

    fun onReload(actionEvent: ActionEvent) {
        LOG.info("reload file ${currentFile.uuid} from disk")
        errorReportService.reporting {
            fileRepository.reloadFileContents(currentFile)
            loadFile(currentFile)
        }
    }

    fun onSave(actionEvent: ActionEvent) {
        LOG.info("saving file content for file ${currentFile.uuid}")
        fileRepository.updateFileContent(currentFile, textArea.text.splitLines())
    }

}
