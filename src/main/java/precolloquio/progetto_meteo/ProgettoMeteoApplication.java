package precolloquio.progetto_meteo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProgettoMeteoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgettoMeteoApplication.class, args);
	}

}
