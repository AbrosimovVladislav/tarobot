package io.io.tarobot.integration.openai.chatgpt.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTMessage {
    private String role;
    private String content;
}
