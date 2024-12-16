package de.kamillionlabs.hateoflux.integrationtest;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Younes El Ouarti
 */

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "de.kamillionlabs.hateoflux")
class IntegrationTestConfiguration {

}
