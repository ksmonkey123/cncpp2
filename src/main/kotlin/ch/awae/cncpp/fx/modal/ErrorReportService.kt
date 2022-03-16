package ch.awae.cncpp.fx.modal

import ch.awae.cncpp.LoggedComponent
import javafx.scene.control.Alert
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import org.springframework.stereotype.Service
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import kotlin.collections.ArrayList

@Service
class ErrorReportService : LoggedComponent() {

    fun report(ex: Throwable) {
        LOG.info("reporting error: $ex")
        ex.printStackTrace(System.err)

        val exceptionText = ex.let {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            it.printStackTrace(pw)
            reshuffle(sw.toString())
        }

        val textArea = TextArea(exceptionText).apply {
            isEditable = false
            isWrapText = true
            maxHeight = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
            GridPane.setVgrow(this, Priority.ALWAYS)
            GridPane.setHgrow(this, Priority.ALWAYS)
        }

        val expContent = GridPane().apply {
            maxWidth = Double.MAX_VALUE
            add(textArea, 0, 0)
        }

        Alert(Alert.AlertType.ERROR).apply {
            title = ex.javaClass.name
            headerText = "An error occurred!"
            contentText = ex.toString()
            dialogPane.expandableContent = expContent
            showAndWait()
        }

        LOG.info("error report closed")
    }


    private fun reshuffle(exceptionText: String): String {
        // reshuffle Caused-By clauses
        val blocks: Deque<List<String>> = ArrayDeque()
        var list: MutableList<String> = ArrayList()
        val lines = exceptionText.split("\n").toTypedArray()
        var first = true
        for (line in lines) {
            when {
                first -> {
                    list.add(line)
                    first = false
                }
                line.startsWith("Caused by: ") -> {
                    blocks.push(list)
                    first = true
                    list = ArrayList()
                    list.add(line.substring(11))
                }
                else -> {
                    list.add(line)
                }
            }
        }
        blocks.push(list)

        // print the blocks in reverse order
        var firstLine = true
        val sb = StringBuilder()
        for (block in blocks) {
            var firstOfBlock = true
            for (line in block) {
                if (firstOfBlock && !firstLine) {
                    sb.append("Caused: ")
                }
                firstLine = false
                firstOfBlock = false
                sb.append(line).append("\n")
            }
        }
        return sb.toString()
    }

    fun <T> reporting(body: () -> T): T? {
        return try {
            body()
        } catch (throwable: Throwable) {
            report(throwable)
            null
        }
    }

}