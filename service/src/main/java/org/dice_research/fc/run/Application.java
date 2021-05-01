package org.dice_research.fc.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Launches the application as a REST service
 *
 */
@SpringBootApplication
@ComponentScan("org.dice_research.fc.config")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
