package com.example.allomaison.Delayed;

import com.example.allomaison.Services.TaskService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class InMemoryDelayedQueueService implements DelayedQueueService {

    private final Queue<CompletionRequest> queue = new ConcurrentLinkedQueue<>();

    private final TaskService taskService; // or other service to update task status

    @Override
    public void enqueueCompletionRequest(Long taskId, Long providerId, Duration delay) {
        Instant readyAt = Instant.now().plus(delay);
        queue.add(new CompletionRequest(taskId, providerId, readyAt));
    }

    @Scheduled(fixedRate = 600_000)
    public void processQueue() {
        Instant now = Instant.now();
        Iterator<CompletionRequest> iterator = queue.iterator();

        while (iterator.hasNext()) {
            CompletionRequest request = iterator.next();
            if (request.getReadyAt().isBefore(now)) {
                boolean success = taskService.updateTaskStatus(request.getTaskId(), com.example.allomaison.Entities.Task.Status.COMPLETED);
                // 可选：日志、错误重试等
                iterator.remove();
            }
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("Delayed queue scheduler initialized.");
    }
}
