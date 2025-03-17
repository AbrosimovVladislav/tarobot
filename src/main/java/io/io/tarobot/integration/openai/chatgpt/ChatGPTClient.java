package io.io.tarobot.integration.openai.chatgpt;

import io.io.tarobot.config.OpenAIConfigurationProperties;
import io.io.tarobot.integration.openai.chatgpt.model.request.ChatGPTRequest;
import io.io.tarobot.integration.openai.chatgpt.model.response.ChatGPTResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatGPTClient {

    @Qualifier("openAiRestClient") // Явно указываем, какой RestClient используем
    private final RestClient openAiRestClient;
    private final OpenAIConfigurationProperties openAiConfig;

    public ChatGPTResponse callChat(ChatGPTRequest request) {
        return openAiRestClient.post()
                .uri(openAiConfig.getChatGpt().getChatUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ChatGPTResponse.class);
    }
}
