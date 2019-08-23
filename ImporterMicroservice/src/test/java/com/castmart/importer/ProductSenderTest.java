package com.castmart.importer;

import com.castmart.importer.model.Product;
import com.castmart.importer.service.ProductSender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class ProductSenderTest {

    ProductSender sender;
    JmsTemplate jmsTemplate;
    MappingJackson2MessageConverter converter;

    @Before
    public void init() {
        jmsTemplate = mock(JmsTemplate.class);
        converter = mock(MappingJackson2MessageConverter.class);
        sender = new ProductSender(jmsTemplate, converter);
    }

    @Test
    public void givenBadProductLine_then_falseAtCompletableFuture() throws Exception {
        String badCsvLine = "1234-1241341-124142, 123, -1 , false";
        CompletableFuture<Boolean> result = sender.constructAndSendMessage(badCsvLine);
        Assert.assertFalse(result.get());
    }

    @Test
    public void givenBadProductLineWithMoreFields_then_falseAtCompletableFuture() throws Exception {
        String badCsvLine = "1234-1241341-124142, 123, -1 , false,1234-1241341-124142, 123, -1 , false";
        CompletableFuture<Boolean> result = sender.constructAndSendMessage(badCsvLine);
        Assert.assertFalse(result.get());
    }

    @Test
    public void givenGoodProductLine_then_interceptProduct_and_trueAtCompletableFuture() throws Exception {
        String goodCsvLine = "13dd36da-8396-412f-b61f-252e9ae123ec,Samsung Galaxy Mobile,smart phone,Samsung Galaxy,true,PC";
        CompletableFuture<Boolean> result = sender.constructAndSendMessage(goodCsvLine);
        Assert.assertTrue(result.get());
    }
}
