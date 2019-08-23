package com.castmart.AggregatorMicroservice;

import com.castmart.AggregatorMicroservice.model.Product;
import com.castmart.AggregatorMicroservice.persistence.ProductJPARepository;
import com.castmart.AggregatorMicroservice.service.ProductProcessorListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ProductProcessorListenerTest {

    private static final String goodId = "0002853e-bddd-4663-b7a6-2ff12ee807fc";
    private static final String badId = "000285bddd63a6f12ee807fc";

    private static final String goodJsonProduct = "{" +
            "  \"id\": \""+goodId+"\"," +
            "  \"name\": \"Sony  Mobile\"," +
            "  \"description\": \"smart phone\"," +
            "  \"provider\": \"Sony\"," +
            "  \"available\": true," +
            "  \"measurementUnits\": \"PC\"" +
            "}";

    private static final String badJsonProduct = "{" +
            "  \"id\": \""+badId+"\"," +
            "  \"name\": \"Sony  Mobile\"," +
            "  \"description\": \"smart phone\"," +
            "  \"provider\": \"Sony\"," +
            "  \"available\": true," +
            "  \"measurementUnits\": \"PC\"" +
            "}";

    private static final String badJsonFormatProduct = "{" +
            "  \"id\": \""+badId+"\"," +
            "  \"name\": \"Sony  Mobile\"," +
            "  \"description\": \"smart phone\"," +
            "  \"provider\": \"Sony\"," +
            "  \"available\": true," +
            "  \"measurementUnits\": \"PC\", ," + // Here is the bad format
            "}";

    ProductProcessorListener listener;
    ProductJPARepository jpaRepo;

    @Before
    public void init() {
        jpaRepo = mock(ProductJPARepository.class);
        listener = new ProductProcessorListener(jpaRepo);
    }

    @Test
    public void givenGoodJsonString_then_storeValidProductAtDB() throws JMSException, IOException {
        // To check conversion of string to
        ArgumentCaptor<Product> productArgument = ArgumentCaptor.forClass(Product.class);
        // Behave as edition.
        when(jpaRepo.existsById(anyString())).thenReturn(true);
        // Capture the constructed Product.
        when(jpaRepo.save(productArgument.capture())).thenReturn(new Product());

        // Mock jms message (we have no JMS session/connection)
        TextMessage txtMessage = mock(TextMessage.class);
        when(txtMessage.getText()).thenReturn(goodJsonProduct);

        listener.productProcessor(txtMessage);

        Product capturedProduct = productArgument.getValue();
        Assert.assertEquals(capturedProduct.getId(), goodId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenBadTypeOfMessage_then_notSaveItInDB() throws IllegalArgumentException, JMSException, IOException {

        // To check conversion of string to
        ArgumentCaptor<Product> productArgument = ArgumentCaptor.forClass(Product.class);
        // Behave as creation.
        when(jpaRepo.existsById(anyString())).thenReturn(false);
        // Capture the constructed Product.
        when(jpaRepo.save(productArgument.capture())).thenReturn(new Product());
        // Mock jms message (we have no JMS session/connection)
        TextMessage txtMessage = mock(TextMessage.class);
        when(txtMessage.getText()).thenReturn(badJsonProduct);

        listener.productProcessor(txtMessage);

        Product capturedProduct = productArgument.getValue();
        Assert.assertEquals(capturedProduct.getId(), goodId);
    }
}
