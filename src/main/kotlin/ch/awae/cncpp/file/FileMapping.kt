package ch.awae.cncpp.file

import java.util.*

interface FileMapping {
    val uuid: UUID
    val path: String
    val reloadable: Boolean
}