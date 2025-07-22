package com.twillio.twillio.service;

import com.twilio.rest.taskrouter.v1.workspace.Worker;
import com.twillio.twillio.configuration.TwilioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentService {

    @Autowired
    private TwilioConfig twilioConfig;

    // Get all available agents
    public List<Worker> getAvailableAgents() {
        return List.of();
    }

    // Update agent status
    public void updateAgentStatus(String workerSid, String activitySid) {
        String workspaceSid = twilioConfig.getAccountSid(); // Make sure this is set in your TwilioConfig
        Worker.updater(workspaceSid, workerSid)
                .setActivitySid(activitySid)
                .update();
    }

}
