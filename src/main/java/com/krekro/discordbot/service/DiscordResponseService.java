package com.krekro.discordbot.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DiscordResponseService {

    private final WebClient discordWebClient;

    public DiscordResponseService(WebClient discordWebClient) {
        this.discordWebClient = discordWebClient;
    }

    public void patchOriginalResponse(String applicationId, String interactionToken, String markdownContent) {
        Map<String, Object> payload = Map.of("content", markdownContent);

        discordWebClient.patch()
                .uri("/webhooks/{appId}/{token}/messages/@original", applicationId, interactionToken)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        success -> System.out.println("Dispatched response update to Discord."),
                        error -> System.err.println("Failed to patch interaction: " + error.getMessage()));
    }
}
