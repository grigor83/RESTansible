package ansible;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static String extractResource(String filename) throws IOException {
		// Create a temp directory inside system temp
		File tempDir = new File(System.getProperty("java.io.tmpdir"), "ansible");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		// Define destination file path
		File tempFile = new File(tempDir, filename);

		// Load the resource file from classpath
		Resource resource = new ClassPathResource("ansible/" + filename);
		// Copy resource to temp file
		try (InputStream in = resource.getInputStream(); OutputStream out = new FileOutputStream(tempFile)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}

		return tempFile.getAbsolutePath();  // Return the real file path
	}

	public static String convertToWslPath(String windowsPath) {
		return "/mnt/" + windowsPath.substring(0, 1).toLowerCase() +
				windowsPath.substring(2).replace("\\", "/");
	}

}
