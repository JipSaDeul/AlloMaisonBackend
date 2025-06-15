package com.example.allomaison.Delayed;

public interface DelayedQueueService {
    void enqueueCompletionRequest(Long taskId, Long providerId, java.time.Duration delay);
}
