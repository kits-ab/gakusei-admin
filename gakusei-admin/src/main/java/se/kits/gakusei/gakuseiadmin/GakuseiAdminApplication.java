package se.kits.gakusei.gakuseiadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"se.kits.gakusei","se.kits.gakusei.gakuseiadmin"})
public class GakuseiAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(GakuseiAdminApplication.class, args);
	}
}
