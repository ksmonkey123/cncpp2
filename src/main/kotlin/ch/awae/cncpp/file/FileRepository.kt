package ch.awae.cncpp.file

import ch.awae.cncpp.LoggedComponent
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.file.Files
import java.util.*

@Repository
class FileRepository : LoggedComponent() {

    private val fileContents = HashMap<UUID, List<String>>()

    fun createVirtualFile(): FileMapping {
        val mapping = VirtualFileMapping(UUID.randomUUID())
        fileContents[mapping.uuid] = ArrayList()
        LOG.info("created empty virtual file ${mapping.uuid}")
        return mapping
    }

    fun loadFile(file: File): FileMapping {
        LOG.info("loading file: ${file.absolutePath}")
        val mapping = FilesystemBackedFileMapping(file, UUID.randomUUID())
        loadFileContent(file, mapping.uuid)
        return mapping
    }

    private fun loadFileContent(file: File, uuid: UUID) {
        fileContents[uuid] = Files.readAllLines(file.toPath()).also {
            LOG.info("loaded file $uuid from ${file.absolutePath}: ${it.size} lines")
        }
    }

    fun discardFile(mapping: FileMapping) {
        LOG.info("discarding file ${mapping.uuid}")
        fileContents.remove(mapping.uuid)
    }

    fun getFileContent(mapping: FileMapping): List<String> {
        return fileContents[mapping.uuid] ?: throw NoSuchElementException("no such element: $mapping")
    }

    fun reloadFileContents(mapping: FileMapping) {
        when (mapping) {
            is FilesystemBackedFileMapping -> loadFileContent(mapping.file, mapping.uuid)
            else -> throw IllegalArgumentException("file cannot be reloaded: $mapping")
        }
    }

    fun updateFileContent(mapping: FileMapping, lines: List<String>) {
        LOG.info("replacing file content for file ${mapping.uuid}")
        fileContents[mapping.uuid] = lines
    }
}
