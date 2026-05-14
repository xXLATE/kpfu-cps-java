package com.example.demo.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.config.TelegramProperties;

@Service
public class TelegramNotificationService {
    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final TelegramProperties telegramProperties;
    private final HttpClient httpClient;

    public TelegramNotificationService(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5));
        if (telegramProperties.proxyEnabled()) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(
                    telegramProperties.getProxyHost(),
                    telegramProperties.getProxyPort())));
            log.info(
                    "Telegram proxy is enabled: {}:{}",
                    telegramProperties.getProxyHost(),
                    telegramProperties.getProxyPort());
        }
        this.httpClient = builder.build();
    }

    public boolean isEnabled() {
        return telegramProperties.enabled();
    }

    public void sendMessage(String text) {
        if (!isEnabled()) {
            log.debug("Telegram notifications are disabled");
            return;
        }

        String body = "chat_id=" + encode(telegramProperties.getChatId())
                + "&text=" + encode(text);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + telegramProperties.getBotToken() + "/sendMessage"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                log.warn("Telegram message was not sent. Status: {}, body: {}",
                        response.statusCode(),
                        response.body());
            }
        } catch (IOException exception) {
            log.warn("Telegram message was not sent because of IO error: {}", exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("Telegram message sending was interrupted");
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
