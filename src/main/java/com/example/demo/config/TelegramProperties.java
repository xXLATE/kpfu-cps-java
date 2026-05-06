package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.telegram")
public class TelegramProperties {
    private String botToken = "";
    private String chatId = "";
    private long statusDelayMs = 300000;

    public boolean enabled() {
        return botToken != null && !botToken.isBlank()
                && chatId != null && !chatId.isBlank();
    }
}
