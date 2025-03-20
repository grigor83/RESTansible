package ansible;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class RestAnsibleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestAnsibleApplication.class, args);
	}



	public static String readFileFromResources(String filename) throws IOException {
		// Prednosti korišćenja ClassPathResource: Radi i kada se aplikacija build-a u .jar ili .war.
		// Ne zavisi od putanja na operativnom sistemu.
		Resource resource = new ClassPathResource("ansible/" + filename);
		try (InputStream inputStream = resource.getInputStream()) {
			byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
			return new String(bytes, StandardCharsets.UTF_8);
		}
	}

}
