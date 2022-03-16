package ch.awae.cncpp.fx

import ch.awae.cncpp.LoggedComponent
import javafx.fxml.FXML
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import org.springframework.stereotype.Controller

@Controller
class RootController : LoggedComponent() {

    @FXML
    lateinit var tabPane: TabPane
    @FXML
    lateinit var fileListTab: Tab
    @FXML
    lateinit var fileViewTab: Tab
    @FXML
    lateinit var optionsTab: Tab

    var activeTab: RootTab = RootTab.FILE_LIST
        set(value) {
            LOG.info("switching tab: $field => $value")
            field = value
            tabPane.selectionModel.select(getTab(value))
        }

    private fun getTab(view: RootTab) = when (view) {
        RootTab.FILE_LIST -> fileListTab
        RootTab.FILE_VIEW -> fileViewTab
        RootTab.OPTIONS -> optionsTab
    }

    enum class RootTab {
        FILE_LIST, FILE_VIEW, OPTIONS
    }

}
