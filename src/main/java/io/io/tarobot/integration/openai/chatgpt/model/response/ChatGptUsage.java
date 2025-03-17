package io.io.tarobot.integration.openai.chatgpt.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptUsage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
}
