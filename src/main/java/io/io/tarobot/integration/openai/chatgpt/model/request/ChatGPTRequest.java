package io.io.tarobot.integration.openai.chatgpt.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequest {

    private String model;
    private List<ChatGPTRequestMessage> messages;
    private Double temperature;
    private Double top_p;
    private Integer max_tokens;

    /**
     * Универсальный метод для создания запроса к ChatGPT.
     *
     * @param systemMessage Инструкция для модели (контекст, стиль, правила).
     * @param userMessage   Вопрос пользователя.
     * @return Экземпляр ChatGPTRequest.
     */
    public static ChatGPTRequest createRequest(String systemMessage, String userMessage) {
        return new ChatGPTRequest(
//                "gpt-4o-mini",
                "gpt-4o",
                List.of(
                        new ChatGPTRequestMessage("system", systemMessage),
                        new ChatGPTRequestMessage("user", userMessage)
                ),
                0.7,   // temperature
                0.8,   // top_p
                500    // max_tokens
        );
    }
}
