package io.io.tarobot.integration.openai.chatgpt.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequestMessage {

    private String role;
    private String content;
}
