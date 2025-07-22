package com.twillio.twillio.service;

import com.twilio.base.ResourceSet;
import com.twilio.rest.api.v2010.account.Queue;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.CallUpdater;
import com.twillio.twillio.configuration.TwilioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class QueueService {
    private static final Logger logger = Logger.getLogger(QueueService.class.getName());

    @Autowired
    private TwilioConfig twilioConfig;

    public List<Queue> getCallsInQueue() {
        List<Queue> result = new ArrayList<>();
        logger.info("Attempting to fetch queues from Twilio...");
        logger.info("Using Account SID: " + (twilioConfig.getAccountSid() != null ? "[HIDDEN]" : "NULL"));
        
        try {
            ResourceSet<Queue> queues = Queue.reader().read();
            logger.info("Successfully connected to Twilio API");
            
            for (Queue queue : queues) {
                logger.info("Found queue - SID: " + queue.getSid() + ", Name: " + queue.getFriendlyName());
                if (queue.getSid() != null) {
                    result.add(queue);
                }
            }
            logger.info("Total queues found: " + result.size());
        } catch (Exception e) {
            logger.severe("Error fetching queues: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch queues: " + e.getMessage(), e);
        }
        return result;
    }

    public void dequeueCall(String callSid, String agentPhoneNumber) {
        try {
            logger.info("Attempting to dequeue call with SID: " + callSid);
            // Try the most basic approach
            Call call = Call.fetcher(callSid).fetch();            
            if (call != null) {
                logger.info("Call found - SID: " + call.getSid() + ", Status: " + call.getStatus());
                // Use the simplest CallUpdater approach
                CallUpdater updater = new CallUpdater(callSid);
                updater.setUrl("https://your-server.com/connect-agent?agent=" + agentPhoneNumber);
                updater.update();
                logger.info("Call dequeued successfully");
            } else {
                logger.warning("Call not found with SID: " + callSid);
            }
        } catch (Exception e) {
            logger.severe("Error dequeuing call: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to dequeue call: " + e.getMessage(), e);
        }
    }

    public Call.Status getCallStatus(String callSid) {
        try {
            logger.info("Attempting to fetch call status with SID: " + callSid);
            // Try the most basic approach
            Call call = Call.fetcher(callSid).fetch();
            if (call != null) {
                logger.info("Call found - SID: " + call.getSid() + ", Status: " + call.getStatus());
                return call.getStatus();
            } else {
                logger.warning("Call not found with SID: " + callSid);
                return null;
            }
        } catch (Exception e) {
            logger.severe("Error fetching call status: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch call status: " + e.getMessage(), e);
        }
    }
}