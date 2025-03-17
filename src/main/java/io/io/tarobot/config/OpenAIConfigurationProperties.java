package io.io.tarobot.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "open-ai")
public class OpenAIConfigurationProperties {

    private String token;
    private String baseUrl;
    private ChatGpt chatGpt;
    private Dalle dalle;

    @Data
    public static class ChatGpt {
        private String chatUrl;
        private String threads;
    }

    @Data
    public static class Dalle {
        private String createImageUrl;
    }
}
