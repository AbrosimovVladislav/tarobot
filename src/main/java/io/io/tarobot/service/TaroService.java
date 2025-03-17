package io.io.tarobot.service;

import io.io.tarobot.integration.openai.chatgpt.ChatGPTClient;
import io.io.tarobot.integration.openai.chatgpt.PromptLoader;
import io.io.tarobot.integration.openai.chatgpt.model.request.ChatGPTRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaroService {

    private final ChatGPTClient chatGPTClient;
    private final PromptLoader promptLoader;

    /**
     * Генерирует расклад Таро по запросу пользователя.
     */
    public String makeTarotPrediction(String userMessage) {
        // Загружаем system-промт и подставляем стиль блогера
        String systemMessage = promptLoader.loadPrompt("tarot_system.txt");

        // Загружаем user-промт и подставляем вопрос пользователя
        String userPrompt = String.format(promptLoader.loadPrompt("tarot_user.txt"), userMessage);

        String response = chatGPTClient.callChat(ChatGPTRequest.createRequest(systemMessage, userPrompt))
                .getChoices().get(0)
                .getMessage()
                .getContent();

        log.info("Tarot prediction generated successfully");
        return response;
    }

    /**
     * Генерирует уточняющий вопрос по запросу пользователя.
     */
    public String makeClarifyingQuestion(String userMessage) {
        String systemMessage = promptLoader.loadPrompt("clarifying_system.txt");
        String userPrompt = String.format("Помоги человеку уточнить его вопрос: \"%s\".", userMessage);

        String response = chatGPTClient.callChat(ChatGPTRequest.createRequest(systemMessage, userPrompt))
                .getChoices().get(0)
                .getMessage()
                .getContent();

        log.info("Clarifying question generated successfully");
        return response;
    }
}
