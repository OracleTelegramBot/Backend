package dev.sammy_ulfh.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class KpiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpiApplication.class, args);
	}

}
