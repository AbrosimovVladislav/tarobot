package io.io.tarobot.integration.openai.chatgpt.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextContentItem {
    private String type;
    private String text;
}
