package org.dice.FactCheck.updater;

import org.dice.FactCheck.updater.service.IRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
/*@EnableJpaRepositories("org.dice.FactCheck.updater.repository")
@EntityScan("org.dice.FactCheck.updater.model")*/
public class UpdaterApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(UpdaterApplication.class, args);
		var runner = ctx.getBean(IRunner.class);
		runner.run();
	}
}
