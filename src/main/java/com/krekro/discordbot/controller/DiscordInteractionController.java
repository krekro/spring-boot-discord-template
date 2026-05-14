package com.krekro.discordbot.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krekro.discordbot.dto.DiscordInteraction;
import com.krekro.discordbot.service.DiscordResponseService;
import com.krekro.discordbot.service.DiscordVerificationService;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/interactions")
public class DiscordInteractionController {

    private final DiscordVerificationService verificationService;
    private final DiscordResponseService responseService;
    private final ThreadPoolTaskExecutor botExecutor;
    private final ObjectMapper objectMapper;

    public DiscordInteractionController(DiscordVerificationService verificationService,
            DiscordResponseService responseService,
            ThreadPoolTaskExecutor botExecutor,
            ObjectMapper objectMapper) {
        this.verificationService = verificationService;
        this.responseService = responseService;
        this.botExecutor = botExecutor;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> handleInteraction(
            @RequestHeader("X-Signature-Ed25519") String signature,
            @RequestHeader("X-Signature-Timestamp") String timestamp,
            @RequestBody String rawBody) {

        // 1. Mandatory Crypto Validation
        if (!verificationService.isValidRequest(signature, timestamp, rawBody)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            // Map raw body to type-safe Java Record
            DiscordInteraction interaction = objectMapper.readValue(rawBody, DiscordInteraction.class);

            // 2. Handle PING (Type 1)
            if (interaction.type() == 1) {
                return ResponseEntity.ok(Map.of("type", 1));
            }

            // 3. Handle Slash Commands (Type 2)
            if (interaction.type() == 2) {
                // Return Deferred Status (Type 5) within 3-second limit
                ResponseEntity<Map<String, Object>> deferredResponse = ResponseEntity.ok(Map.of("type", 5));

                // Delegate heavy operations to our async managed thread pool
                botExecutor.execute(() -> {
                    processCommandAsync(interaction);
                });

                return deferredResponse;
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    private void processCommandAsync(DiscordInteraction interaction) {
        try {
            String commandName = interaction.data().name();

            if ("ping".equals(commandName)) {
                Thread.sleep(2000); // Simulate processing delay safely
                responseService.patchOriginalResponse(
                        interaction.applicationId(),
                        interaction.token(),
                        "🏓 Pong from a strictly typed Java engine!");
            } else {
                responseService.patchOriginalResponse(
                        interaction.applicationId(),
                        interaction.token(),
                        "Unknown command execution target.");
            }
        } catch (Exception e) {
            responseService.patchOriginalResponse(
                    interaction.applicationId(),
                    interaction.token(),
                    "❌ Execution pipeline crashed.");
        }
    }
}
