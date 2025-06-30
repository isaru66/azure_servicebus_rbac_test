package com.isaru66.azure_servicebus_sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.azure.messaging.servicebus.ServiceBusProcessorClient;

@SpringBootApplication
public class AzureServicebusSampleApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AzureServicebusSampleApplication.class);
    
    private final ServiceBusProcessorClient processorClient;

    public AzureServicebusSampleApplication(ServiceBusProcessorClient processorClient) {
        this.processorClient = processorClient;
    }

		public static void main(String[] args) {
        logger.info("STARTING THE APPLICATION");
        SpringApplication.run(AzureServicebusSampleApplication.class, args);
        logger.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Service Bus Processor Client...");
        processorClient.start();
        logger.info("Service Bus Processor Client started. Listening for messages...");
        
        // Keep the application running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Service Bus Processor Client...");
            processorClient.close();
            logger.info("Service Bus Processor Client closed.");
        }));
        
        // Keep the main thread alive
        Thread.currentThread().join();
    }
}
