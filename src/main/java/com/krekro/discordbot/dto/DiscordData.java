package com.krekro.discordbot.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DiscordData(
        String id,
        String name,
        Map<String, Object> options) {
}