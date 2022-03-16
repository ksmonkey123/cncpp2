package ch.awae.cncpp

import java.util.logging.Logger

abstract class LoggedComponent {
    protected val LOG = Logger.getLogger(javaClass.name)!!

    fun logInitialized() {
        LOG.info("${javaClass.name} initialized")
    }
}