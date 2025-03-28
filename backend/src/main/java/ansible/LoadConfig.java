package ansible;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadConfig {
    @Getter
    private static String uploadsDir;

    @Value("${upload.path}")
    public void setUploadsDir(String dir) {
        uploadsDir = dir;
    }

}
