package io.io.tarobot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@RequiredArgsConstructor
public class OpenAIConfiguration {

    private final OpenAIConfigurationProperties openAiConfig;

    @Bean
    public RestClient openAiRestClient() {  // Убираем аргумент RestClient, чтобы не было зависимости
        return RestClient.builder()
                .uriBuilderFactory(new DefaultUriBuilderFactory(openAiConfig.getBaseUrl()))
                .requestInitializer(new OpenAIClientAuthInitializer(openAiConfig.getToken()))
                .build();
    }
}
