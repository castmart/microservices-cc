package com.castmart.AggregatorMicroservice.service;

import com.castmart.AggregatorMicroservice.model.Product;
import com.castmart.AggregatorMicroservice.persistence.ProductJPARepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class ProductProcessorListener {

    private static Logger LOG = LoggerFactory.getLogger(ProductProcessorListener.class);

    ProductJPARepository jpaRepo;

    @Autowired
    public ProductProcessorListener(ProductJPARepository repository) {
        this.jpaRepo = repository;
    }

    @JmsListener(destination = "${queue.product}")
    public void productProcessor(Message msg) throws
            JMSException, IOException, IllegalArgumentException {
        if (msg instanceof TextMessage) {
            final TextMessage txtMessage = (TextMessage) msg;
            ObjectMapper om = new ObjectMapper();
            try {
                long start = System.currentTimeMillis();
                Product product = null;
                try {
                    product = om.readValue(txtMessage.getText(), Product.class);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Bad input string");
                }

                if (jpaRepo.existsById(product.getId())) {
                    product.setEditionTimestamp(new Date());
                    LOG.info("Edition...");
                } else {
                    product.setCreationTimestamp(new Date());
                    LOG.info("Creation...");
                }
                jpaRepo.save(product);
                LOG.info("Product saved in " + (System.currentTimeMillis() - start) + " millis");
            } catch(Exception e) {
                LOG.error(e.getMessage());
                if (e instanceof IllegalArgumentException)
                    throw new IllegalArgumentException("Bad input String");
                else
                    throw new IOException("Exception: " + e.getMessage());

                // We might send an alert to the user to make him check the info from file.
            }

        } else {
            // We might log this message to syslog and take action for possible bad direction.
            LOG.error("The message received at the queue is not of text type");
        }
    }
}
