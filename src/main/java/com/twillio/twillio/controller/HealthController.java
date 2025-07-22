package com.twillio.twillio.controller;

import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Queue;
import com.twillio.twillio.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private QueueService queueService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Twilio Integration Service");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/queue-status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Queue> queues = queueService.getCallsInQueue();
            response.put("status", "success");
            response.put("queues", queues);
            response.put("totalQueues", queues.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error fetching queue status: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
