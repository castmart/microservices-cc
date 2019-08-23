package com.castmart.importer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@EnableJms
public class ImporterMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImporterMicroserviceApplication.class, args);
	}

	/**
	 * Method to configure thread pool to apply "Back pressure". When the queue capacity has been exceeded then the rest
	 * service will notify the client with an status code like 503.
	 * @return A thread executor with 4-16 threads and a max capacity of 20,000 tasks to attend.
	 */
	@Bean
	public Executor taskExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(2000);
		executor.setThreadNamePrefix("ProductAsyncProcessor-");
		executor.initialize();

		//ExecutorService executor = Executors.newFixedThreadPool(20); // Use this executor to not use queue size.
		return executor;
	}

	@Bean
	public Docket swaggerConfig() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.castmart.importer.controller"))  // // Generate API of EndPoints which is available inside defined package
				.paths(PathSelectors.any())   // for all EndPoints
				.build()
				.pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class);
	}
}
