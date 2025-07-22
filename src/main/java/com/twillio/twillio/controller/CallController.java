package com.twillio.twillio.controller;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Enqueue;
import com.twilio.twiml.voice.Redirect;
import com.twilio.twiml.voice.Say;
import com.twillio.twillio.configuration.TwilioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CallController {

    @Autowired
    private TwilioConfig twilioConfig;

    @PostMapping(value = "/incoming-call", 
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_XML_VALUE)
    public String handleIncomingCall(@RequestParam MultiValueMap<String, String> params) {
        String from = params.getFirst("From");
        String to = params.getFirst("To");
        String callSid = params.getFirst("CallSid");

        System.out.println("Incoming call from: " + from + " to: " + to + " (CallSid: " + callSid + ")");

        // Create enqueue TwiML with action URL for when a call is dequeued
        Enqueue enqueue = new Enqueue.Builder()
                .name(twilioConfig.getQueueName())
                .waitUrl("http://localhost:8088/api/hold-music")
                .action("http://localhost:8088/api/queue-status")
                .method(com.twilio.http.HttpMethod.POST)  // Fixed method call
                .build();

        VoiceResponse response = new VoiceResponse.Builder()
                .enqueue(enqueue)
                .build();

        String twiml = response.toXml();
        System.out.println("Sending TwiML: " + twiml);
        
        return twiml;
    }

    @PostMapping(value = "/queue-status",
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_XML_VALUE)
    public String handleQueueStatus(@RequestParam MultiValueMap<String, String> params) {
        String callSid = params.getFirst("CallSid");
        String queueResult = params.getFirst("QueueResult");
        
        System.out.println("Queue status for call " + callSid + ": " + queueResult);
        
        // This endpoint is called when the call is dequeued or if there's an error
        // In a real application, you would update your database or notify your system here
        
        // For now, we'll just return a simple message
        VoiceResponse response = new VoiceResponse.Builder()
                .say(new Say.Builder("Connecting you to an agent.").build())
                .redirect(new Redirect.Builder("/api/agent/connect?CallSid=" + callSid).build())
                .build();
                
        return response.toXml();
    }

    @GetMapping(value = "/hold-music", 
               produces = MediaType.APPLICATION_XML_VALUE)
    public String holdMusic() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Response>" +
                "    <Play>https://demo.twilio.com/docs/classic.mp3</Play>" +
                "</Response>";
    }
}
