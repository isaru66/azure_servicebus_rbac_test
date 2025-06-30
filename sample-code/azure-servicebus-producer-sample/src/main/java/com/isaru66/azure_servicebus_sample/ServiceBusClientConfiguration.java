package com.isaru66.azure_servicebus_sample;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.servicebus.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration(proxyBeanMethods = false)
public class ServiceBusClientConfiguration {
    @Value("${spring.cloud.azure.servicebus.namespace}")
    private String SERVICE_BUS_FQDN = "sbns-isaru66-servicebus.servicebus.windows.net";

    @Value("${spring.cloud.azure.servicebus.entity-name}")
    private String QUEUE_NAME = "queue-1";

    private static final Logger logger = LoggerFactory.getLogger(ServiceBusClientConfiguration.class);

    @Bean
    ServiceBusClientBuilder serviceBusClientBuilder() {
        logger.info("Creating ServiceBusSenderClient for Namespace: {}", SERVICE_BUS_FQDN);
        return new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(SERVICE_BUS_FQDN)
                .credential(new DefaultAzureCredentialBuilder().build());
    }

    @Bean
    ServiceBusSenderClient serviceBusSenderClient(ServiceBusClientBuilder builder) {
        logger.info("Creating ServiceBusSenderClient for queue: {}", QUEUE_NAME);
        return builder
                .sender()
                .queueName(QUEUE_NAME)
                .buildClient();
    }

    @Bean
    ServiceBusProcessorClient serviceBusProcessorClient(ServiceBusClientBuilder builder) {
        return builder.processor()
                .queueName(QUEUE_NAME)
                .processMessage(ServiceBusClientConfiguration::processMessage)
                .processError(ServiceBusClientConfiguration::processError)
                .buildProcessorClient();
    }

    private static void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        System.out.printf("Processing message. Id: %s, Sequence #: %s. Contents: %s%n",
                message.getMessageId(), message.getSequenceNumber(), message.getBody());
    }

    private static void processError(ServiceBusErrorContext context) {
        System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                context.getFullyQualifiedNamespace(), context.getEntityPath());
    }
}