package io.io.tarobot.integration.openai.chatgpt.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTWebRequest {

    private String model;
    private List<ChatGPTRequestMessage> messages;

    /**
     * Метод для создания запроса с web search preview моделью и опциями поиска.
     *
     * @param userMessage Вопрос пользователя.
     * @return Экземпляр ChatGPTRequest с включённым веб-поиском.
     */
    public static ChatGPTWebRequest createWebRequest(String systemMessage, String userMessage) {
        return new ChatGPTWebRequest(
                "gpt-4o-search-preview",
                List.of(new ChatGPTRequestMessage("user", userMessage))
        );
    }
}
