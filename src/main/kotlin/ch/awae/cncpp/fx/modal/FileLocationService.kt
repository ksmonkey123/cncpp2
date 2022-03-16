package ch.awae.cncpp.fx.modal

import ch.awae.cncpp.LoggedComponent
import javafx.stage.FileChooser
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileLocationService : LoggedComponent() {

    private val fileFilter = FileChooser.ExtensionFilter("G-Code File", "*.gcode")

    fun askForSavePath(name: String, suffix: String): String? {
        LOG.info("prompting for save location")

        val fileChooser = FileChooser().apply {
            title = "Save file"
            initialFileName = name
        }

        return fileChooser.showSaveDialog(null)?.absolutePath
            ?.let { forceSuffix(it, suffix) }
            .also {
                when (it) {
                    null -> LOG.info("save file prompt aborted")
                    else -> LOG.info("save location: $it")
                }
            }
    }

    private fun forceSuffix(path: String, suffix: String) = when {
        path.endsWith(suffix) -> path
        else -> path + suffix
    }

    fun askForFileToOpen(): File? {
        LOG.info("prompting for file to open")
        val chooser = FileChooser().apply {
            title = "Open file"
            initialFileName = null
            extensionFilters.add(fileFilter)
            selectedExtensionFilter = fileFilter
        }

        return chooser.showOpenDialog(null)
            .also {
                when (it) {
                    null -> LOG.info("open file prompt aborted")
                    else -> LOG.info("selected file: ${it.absolutePath}")
                }
            }
    }

}
