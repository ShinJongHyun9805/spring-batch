package springbatch.utilize;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class UtilizeApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtilizeApplication.class, args);
	}

}
