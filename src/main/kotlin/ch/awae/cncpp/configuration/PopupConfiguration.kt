package ch.awae.cncpp.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("popup")
class PopupConfiguration {

    lateinit var info: String
    lateinit var warning: String

}