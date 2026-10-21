package lt.rildev.movieappclientbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import lt.rildev.movieappclientbe.config.MovieBackendProperties;

@SpringBootApplication
@EnableConfigurationProperties(MovieBackendProperties.class)
public class MovieAppClientBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieAppClientBeApplication.class, args);
    }

}
