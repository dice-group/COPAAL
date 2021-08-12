package org.dice.fact_check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.dice.fact_check.corraborative.config")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
