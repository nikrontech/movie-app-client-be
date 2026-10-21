package lt.rildev.movieappclientbe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "movie-app")
public record MovieBackendProperties(String backendUrl) {
}
