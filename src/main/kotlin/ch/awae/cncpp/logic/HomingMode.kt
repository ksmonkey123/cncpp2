package ch.awae.cncpp.logic

enum class HomingMode (private val text: String) {
    HOME_ALL("Home All"),
    HOME_Z("Home Z only");

    override fun toString() = text
}
