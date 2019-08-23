package com.castmart.importer.service;

import com.castmart.importer.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;


@Service
public class ProductSender {

    private static final Logger LOG = LoggerFactory.getLogger(ProductSender.class);

    private static final int FIELDS_PER_LINE = 6;

    JmsTemplate jmsTemplate;

    MappingJackson2MessageConverter converter;

    @Value("${queue.product}")
    String destinationQueue;

    @Autowired
    public ProductSender(JmsTemplate jmsTemplate, MappingJackson2MessageConverter converter) {
        this.jmsTemplate = jmsTemplate;
        this.converter = converter;
    }

    @PostConstruct
    public void init() {
        jmsTemplate.setMessageConverter(converter);
        LOG.info("Product sender created");
    }

    @Async
    public CompletableFuture<Boolean> constructAndSendMessage(String productString) {
        if (productString == null) return CompletableFuture.completedFuture(false);

        String[] elements = productString.split("[,]");

        if (elements != null && elements.length == FIELDS_PER_LINE) {
            Product product = new Product(
                    elements[0], // UUID
                    elements[1], // Name
                    elements[2], // Description
                    elements[3], // Provider
                    elements[4] != null ? (elements[4].equalsIgnoreCase("true") ? true : false): false,
                    elements[5]); // MeasurementUnits
            //sendMessage(product);
            jmsTemplate.convertAndSend(destinationQueue, product);
            return CompletableFuture.completedFuture(true);
        } else {
            return CompletableFuture.completedFuture(false);
        }
    }

    public void sendMessage(Product product) {
        jmsTemplate.convertAndSend(destinationQueue, product);
    }

    protected void setQueue(String queue) {
        this.destinationQueue = queue;
    }
}
