package ansible;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

	public static String copyResourceFile(String filename) throws IOException {
		String uploadsDir = LoadConfig.getUploadsDir();
		if (uploadsDir == null) {
			throw new IllegalStateException("Uploads directory is not set!");
		}

		Path filePath = Paths.get(uploadsDir, filename);
		// Kreiraj upload folder ako ne postoji
		Files.createDirectories(filePath.getParent());
		Files.writeString(filePath, readFileFromResources(filename), StandardCharsets.UTF_8);
		return filePath.toFile().getAbsolutePath();  // Return the real file path
	}

	public static String convertToWslPath(String windowsPath) {
		return "/mnt/" + windowsPath.substring(0, 1).toLowerCase() +
				windowsPath.substring(2).replace("\\", "/");
	}

}
