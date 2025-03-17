package io.io.tarobot.integration.openai.chatgpt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PromptLoader {

    public String loadPrompt(String fileName) {
        ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
        if (!resource.exists()) {
            log.error("Файл промта не найден: {}", fileName);
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("Ошибка загрузки промта: {}", fileName, e);
            return "";
        }
    }
}
