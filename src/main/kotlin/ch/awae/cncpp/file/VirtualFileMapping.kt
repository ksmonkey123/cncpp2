package ch.awae.cncpp.file

import java.util.*

internal data class VirtualFileMapping(override val uuid : UUID) : FileMapping {
    override val path = "#" + hashCode()
    override val reloadable: Boolean
        get() = false
}