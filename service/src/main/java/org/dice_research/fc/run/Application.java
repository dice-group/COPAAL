package org.dice_research.fc.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Launches the application as a REST service
 *
 */
@SpringBootApplication
@EnableJpaRepositories("org.dice_research.fc.paths.repository")
@EntityScan("org.dice_research.fc.paths.model")
@ComponentScan("org.dice_research.fc.config")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
