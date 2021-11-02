package org.dice.FactCheck.preprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.dice.FactCheck.preprocess.config")
public class PreprocessApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreprocessApplication.class, args);
	}

}
