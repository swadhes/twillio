package com.twillio.twillio.controller;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Number;
import com.twilio.twiml.voice.Say;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @PostMapping("/connect")
    public String connectCallToAgent(
            @RequestParam("CallSid") String callSid,
            @RequestParam("QueueSid") String queueSid) {

        System.out.println("Connecting call " + callSid + " from queue " + queueSid);

        // In a real application, you would look up the next available agent here
        // For now, we'll use a default agent number
        String agentNumber = "+919999999999"; // Replace with actual agent number

        // Create TwiML to dial the agent
        Number number = new Number.Builder(agentNumber).build();
        Dial dial = new Dial.Builder()
                .action("/api/agent/call-end")
                .callerId("+917276751376") // Your Twilio number
                .number(number)
                .build();

        VoiceResponse response = new VoiceResponse.Builder()
                .dial(dial)
                .build();

        return response.toXml();
    }

    @PostMapping("/call-end")
    public String handleCallEnd(
            @RequestParam("DialCallStatus") String status) {

        System.out.println("Call ended with status: " + status);

        // You can add post-call logic here
        VoiceResponse response = new VoiceResponse.Builder()
                .say(new Say.Builder("Thank you for your call. Goodbye!").build())
                .build();

        return response.toXml();
    }
}
