package com.castmart.AggregatorMicroservice.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.ConnectionFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    String brockerUrl;

    @Value("${spring.activemq.user}")
    String user;

    @Value("${spring.activemq.password}")
    String password;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        ExecutorService executor = Executors.newFixedThreadPool(20);


        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(brockerUrl);

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        cachingConnectionFactory.setCacheConsumers(true);


        factory.setTaskExecutor(executor);
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setConcurrency("20");

        return factory;
    }
}
