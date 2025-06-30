
package com.isaru66.azure_servicebus_sample.controllers;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class DefaultController {
    private static final List<String> COLOR_LIST = List.of("red", "blue", "green", "yellow");

    private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);

    
    @Value("${spring.cloud.azure.servicebus.entity-name}")
    private String ENTITY_NAME = "queue-1";

    @Value("${spring.cloud.azure.servicebus.entity-type}")
    private String SERVICE_BUS_ENTITY_TYPE = "queue"; // can be "queue" or "topic"

    private final ServiceBusSenderClient senderClient;

    public DefaultController(ServiceBusSenderClient senderClient) {
        this.senderClient = senderClient;
    }

    @GetMapping("/")
    public String greet() throws Exception {
        // send one message to the queue
        String dateString = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String randomColor = COLOR_LIST
                .get((int) (Math.random() * 4));

        String messageString = String.format("Message at %s with color %s", dateString, randomColor);
        ServiceBusMessage message = new ServiceBusMessage(
                messageString);

        // Optional: Add custom properties for topic filtering
        message.getApplicationProperties().put("MessageColor",
                randomColor);
        message.getApplicationProperties().put("Source", "azure-servicebus-producer-sample");
        message.getApplicationProperties().put("Timestamp", System.currentTimeMillis());

        // Send the message to the Service Bus queue
        senderClient.sendMessage(message);
        logger.info("Sent a message to " + SERVICE_BUS_ENTITY_TYPE + " entityname: "+ ENTITY_NAME +", Message [" + messageString + "]\n");

        return "Sent message: " + messageString;
    }
}
