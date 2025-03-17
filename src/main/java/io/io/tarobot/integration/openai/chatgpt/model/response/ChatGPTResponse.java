package io.io.tarobot.integration.openai.chatgpt.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTResponse {
    private String id;
    private String object;
    private Instant created;
    private String model;
    private List<ChatGPTChoice> choices;
    private ChatGptUsage usage;
}
