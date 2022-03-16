package ch.awae.cncpp.logic

import ch.awae.cncpp.LoggedComponent
import ch.awae.cncpp.file.FileMapping
import ch.awae.cncpp.file.FileRepository
import ch.awae.cncpp.fx.RootController
import ch.awae.cncpp.fx.modal.FileLocationService
import ch.awae.cncpp.fx.modal.PopupService
import ch.awae.cncpp.util.splitLines
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files

@Service
class ProcessingService(
    private val rootController: RootController,
    private val fileLocationService: FileLocationService,
    private val popupService: PopupService,
    private val fileRepository: FileRepository,
) : LoggedComponent() {

    val commonHeader = """
        M400
        M420 S
        M300 S440 P500
        G4 S20
        M300 S660 P500
        M3
        G4 S5
    """.trimIndent().splitLines()

    val footer = """
        M400
        G00 Z40
        M5
        G4 S2
        M300 S880 P250
    """.trimIndent().splitLines()

    fun process(params: ProcessingParameters, fileList: List<FileMapping>) {
        LOG.info("starting processing G-Code")
        LOG.info("parameters: $params")
        LOG.info("files: ${fileList.map { it.uuid }}")

        val header = createHeader(params)

        val body = fileList.flatMap { fileRepository.getFileContent(it) }
            .filter { it.startsWith("G0") }
            .map(::stripFeedrate)
            .filter { it.length > 3 }
            .map { addCustomFeedrate(it, params) }

        LOG.info("processed body - ${body.size} lines")

        val lines = listOf(header, body, footer).flatten()

        LOG.info("finished content processing - ${lines.size} lines")

        fileLocationService.askForSavePath("output", ".gcode")
            ?.let { File(it) }
            ?.let { Files.write(it.toPath(), lines) }
            ?.also {
                popupService.info("Processing Completed")
                rootController.activeTab = RootController.RootTab.FILE_LIST
            }
            ?: popupService.warning("Processing Aborted")
    }

    private fun stripFeedrate(line: String): String {
        return line.split(" ")
            .filterNot { it.startsWith("F") }
            .joinToString(separator = " ")
    }

    private fun addCustomFeedrate(line: String, params: ProcessingParameters): String {
        val speed = if (line.startsWith("G00")) params.travelSpeed else params.workSpeed
        return "$line F$speed"

    }

    private fun createHeader(params: ProcessingParameters): List<String> {
        val lines = mutableListOf<String>()

        with(params.homingParameters) {
            when (this) {
                null -> lines += "G28 Z"
                else -> {
                    lines += "G28"
                    if (levelBed) {
                        lines += "G29"
                        lines += "M400"
                        lines += "M500"
                    }
                    lines += "G00 X${zeroOffset.x} ${zeroOffset.y}"
                    lines += "G92 X0 Y0"
                }
            }
        }
        lines += commonHeader
        return lines
    }
}