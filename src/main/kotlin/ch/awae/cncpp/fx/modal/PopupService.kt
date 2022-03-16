package ch.awae.cncpp.fx.modal

import ch.awae.cncpp.LoggedComponent
import ch.awae.cncpp.configuration.PopupConfiguration
import ch.awae.cncpp.util.unwrap
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import org.springframework.stereotype.Service

@Service
class PopupService(private val popupConfiguration: PopupConfiguration) : LoggedComponent() {

    fun info(content: String) {
        showPopup(PopupType.INFORMATION, content)
    }

    fun warning(content: String) {
        showPopup(PopupType.WARNING, content)
    }

    private fun showPopup(type: PopupType, content: String): ButtonType? {
        LOG.info("showing $type popup: $content")
        val alert = Alert(type.type).apply {
            title = type.titleSource(popupConfiguration)
            headerText = null
            contentText = content
        }
        return alert.showAndWait().unwrap().also {
            LOG.info("popup closed")
        }
    }

    enum class PopupType(val type: AlertType, val titleSource: (PopupConfiguration) -> String) {
        INFORMATION(AlertType.INFORMATION, { it.info }),
        WARNING(AlertType.WARNING, { it.warning }),
    }

}
