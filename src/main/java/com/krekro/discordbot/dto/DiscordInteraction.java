package com.krekro.discordbot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DiscordInteraction(
        int type,
        DiscordData data,
        @JsonProperty("application_id") String applicationId,
        String token) {
}