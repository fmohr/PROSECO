package de.upb.crc901.proseco.view.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 *
 * Spring Boot web application starter
 *
 *
 * @author kadirayk
 *
 */
@SpringBootApplication
public class PROSECOServer {

	private static final int MAX_MB_UPLOAD = 150;

	/**
	 *
	 * @return {@link CommonsMultipartResolver}
	 */
	@Bean
	@Order(0)
	public CommonsMultipartResolver multipartResolver() {
		final CommonsMultipartResolver multipart = new CommonsMultipartResolver();
		multipart.setMaxUploadSize(MAX_MB_UPLOAD * 1024 * 1024L);
		multipart.setMaxUploadSizePerFile(MAX_MB_UPLOAD * 1024 * 1024L);
		return multipart;
	}

	/**
	 * launches PROSECOServer
	 *
	 * @param args arguments to pass to SpringApplication
	 */
	public void launch(final String... args) {
		SpringApplication.run(PROSECOServer.class, args);
	}

	/**
	 * main method to start PROSECOServer
	 *
	 * @param args main method arguments
	 */
	public static void main(final String[] args) {
		new PROSECOServer().launch();
	}
}
