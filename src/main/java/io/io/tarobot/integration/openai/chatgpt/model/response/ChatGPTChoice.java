package io.io.tarobot.integration.openai.chatgpt.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTChoice {
    private int index;
    private ChatGPTMessage message;
    private String finish_reason;
}
