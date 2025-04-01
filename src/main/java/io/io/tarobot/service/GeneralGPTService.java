package io.io.tarobot.service;

import io.io.tarobot.integration.openai.chatgpt.ChatGPTClient;
import io.io.tarobot.integration.openai.chatgpt.model.request.ChatGPTRequest;
import io.io.tarobot.integration.openai.chatgpt.model.request.ChatGPTWebRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralGPTService {

    private final ChatGPTClient chatGPTClient;

    public String ask(long chatId, String userMessage) {
        log.info("ask[GeneralGPTService] initiated");

        String response = chatGPTClient.callChat(ChatGPTWebRequest.createWebRequest("", userMessage))
                .getChoices().get(0)
                .getMessage()
                .getContent();

        log.info("ask[GeneralGPTService] chat gpt response received");
        return response;
    }

}
