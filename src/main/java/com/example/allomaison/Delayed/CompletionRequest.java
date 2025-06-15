package com.example.allomaison.Delayed;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CompletionRequest {
    private Long taskId;
    private Long providerId;
    private Instant readyAt; // when the request should be processed
}
