package ru.practicum;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class StatsClientConfig {
    @Value("${stats-service.url}")
    private String serverUrl;

    @Bean("statsRestTemplate")
    public RestTemplate statsRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Bean
    public StatsClient statsClient(@Qualifier("statsRestTemplate") RestTemplate restTemplate) {
        return new StatsClient(restTemplate);
    }
}

