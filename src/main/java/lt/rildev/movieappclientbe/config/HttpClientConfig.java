package lt.rildev.movieappclientbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    RestClient movieBackendClient(RestClient.Builder builder,
                                  MovieBackendProperties properties) {
        return builder.baseUrl(properties.backendUrl()).build();
    }
}
