package io.io.tarobot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestInitializer;

@RequiredArgsConstructor
public class OpenAIClientAuthInitializer implements ClientHttpRequestInitializer {

    private final String token;

    @Override
    public void initialize(ClientHttpRequest request) {
        request.getHeaders().setBearerAuth(token);
    }
}
