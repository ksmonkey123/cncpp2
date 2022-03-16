package ch.awae.cncpp.file

import java.io.File
import java.util.*

internal data class FilesystemBackedFileMapping(val file: File, override val uuid: UUID) : FileMapping {
    override val path: String
        get() = file.path
    override val reloadable: Boolean
        get() = true
}