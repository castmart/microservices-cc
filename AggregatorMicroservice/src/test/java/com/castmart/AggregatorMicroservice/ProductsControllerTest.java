package com.castmart.AggregatorMicroservice;

import com.castmart.AggregatorMicroservice.controller.ProductsController;
import com.castmart.AggregatorMicroservice.model.DayStatistic;
import com.castmart.AggregatorMicroservice.model.Product;
import com.castmart.AggregatorMicroservice.persistence.ProductJPARepository;
import com.castmart.AggregatorMicroservice.service.StatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductsController.class)
public class ProductsControllerTest {

	@Autowired
	private MockMvc mvc;

	// Controller dependencies.
	@MockBean
	ProductJPARepository repository;

	@MockBean
	StatisticsService statsService;

	@Test
	public void givenNoProductsAtDB_then_getAnEmptyJsonArray()throws Exception {
		List<Product> productList = new ArrayList<>();
		// Mock repo.
		when(repository.findAll()).thenReturn(productList);

		mvc.perform( MockMvcRequestBuilders
				.get("/products")
				.accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().json("[]") );
	}

	@Test
	public void givenOneProduct_then_getAJsonStringArrayWithProduct()throws Exception {
		String id = UUID.randomUUID().toString();
		Product product = new Product();
		product.setId(id);
		product.setName("Test product");
		product.setDescription("Test description");
		product.setAvailable(true);
		product.setProvider("Test Provider");
		product.setMeasurementUnits("units");
		List<Product> productList = new ArrayList<>();
		productList.add(product);
		// Mock repo.
		when(repository.findAll()).thenReturn(productList);

		mvc.perform( MockMvcRequestBuilders
				.get("/products")
				.accept("application/json"))
			.andExpect(status().isOk())
			.andExpect(content().json("[{" +
					"\"id\": \""+id+"\"," +
					"\"name\": \"Test product\"," +
					"\"description\": \"Test description\"," +
					"\"provider\": \"Test Provider\"," +
					"\"available\": true," +
					"\"measurementUnits\": \"units\"" +
					"}]") );
	}

	@Test
	public void givenGoodProductId_then_getProductJson() throws Exception {
		String id = UUID.randomUUID().toString();
		Product product = new Product();
		product.setId(id);
		product.setName("Test product");
		product.setDescription("Test description");
		product.setAvailable(true);
		product.setProvider("Test Provider");
		product.setMeasurementUnits("units");

		when(repository.findById(anyString())).thenReturn(Optional.of(product));

		mvc.perform( MockMvcRequestBuilders.get("/products/"+id))
				.andExpect(status().isOk())
				.andExpect(content().json("{" +
						"\"id\": \""+id+"\"," +
						"\"name\": \"Test product\"," +
						"\"description\": \"Test description\"," +
						"\"provider\": \"Test Provider\"," +
						"\"available\": true," +
						"\"measurementUnits\": \"units\"" +
						"}"));
	}

	@Test
	public void givenBadProductId_then_getErrorResponse() throws Exception {
		String badUUID = "123456789";
		mvc.perform( MockMvcRequestBuilders.get("/products/"+badUUID))
				.andExpect(status().isBadRequest())
				.andExpect(content().json("{\n" +
						"  \"error\": \"Invalid uuid\",\n" +
						"  \"description\": \"The provided id is not a valid UUID.\"\n" +
						"}"));
	}

	@Test
	public void givenValidProductAsPayloadWithoutId_then_PersistItAtDB() throws Exception {
		ObjectMapper om = new ObjectMapper();

		Product product = new Product();
		product.setName("Post test");
		product.setDescription("Post test");
		product.setProvider("Post test");
		product.setMeasurementUnits("units");
		product.setAvailable(true);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		when(repository.save(captor.capture())).thenReturn(product);
		String payload = "{" +
				"    \"name\": \"Post test\"," +
				"    \"description\": \"Post test\"," +
				"    \"provider\": \"Post test\"," +
				"    \"available\": true," +
				"    \"measurementUnits\": \"units\"" +
				"}";

		MvcResult result = mvc.perform( MockMvcRequestBuilders
				.post("/products")
				.content(payload)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();

		// Captured
		Product captured = captor.getValue();

		String content = result.getResponse().getContentAsString();
		Product response = om.readValue(content.replace("\"id\":null,", ""), Product.class);

		Assert.assertNotNull(captured.getId());
		Assert.assertEquals(captured.getName(), response.getName());
		Assert.assertEquals(captured.getDescription(), response.getName());
		Assert.assertEquals(captured.getProvider(), response.getProvider());
		Assert.assertEquals(captured.getAvailable(), response.getAvailable());
		Assert.assertEquals(captured.getMeasurementUnits(), response.getMeasurementUnits());
	}

	@Test
	public void givenValidProductAsPayloadWitId_then_PersistItAtDB() throws Exception {
		ObjectMapper om = new ObjectMapper();

		Product product = new Product();
		product.setId(UUID.randomUUID().toString());
		product.setName("Post test");
		product.setDescription("Post test");
		product.setProvider("Post test");
		product.setMeasurementUnits("units");
		product.setAvailable(true);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		when(repository.save(captor.capture())).thenReturn(product);
		String payload = "{" +
				"    \"id\": \""+product.getId()+"\"," +
				"    \"name\": \"Post test\"," +
				"    \"description\": \"Post test\"," +
				"    \"provider\": \"Post test\"," +
				"    \"available\": true," +
				"    \"measurementUnits\": \"units\"" +
				"}";

		MvcResult result = mvc.perform( MockMvcRequestBuilders
				.post("/products")
				.content(payload)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		// Captured
		Product captured = captor.getValue();

		String content = result.getResponse().getContentAsString();
		Product response = om.readValue(content, Product.class);

		Assert.assertEquals(captured.getId(), response.getId());
		Assert.assertEquals(captured.getName(), response.getName());
		Assert.assertEquals(captured.getDescription(), response.getName());
		Assert.assertEquals(captured.getProvider(), response.getProvider());
		Assert.assertEquals(captured.getAvailable(), response.getAvailable());
		Assert.assertEquals(captured.getMeasurementUnits(), response.getMeasurementUnits());
	}

	@Test
	public void givenBadDayFormat_then_getErrorResponse() throws Exception {
		when(statsService.getStatisticOfDay(anyString())).thenThrow(new ParseException("Bad date format", 1));

		mvc.perform( MockMvcRequestBuilders
			.get("/products/statistic/2019-0820").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().json("{" +
						"  \"error\": \"Invalid Date format\"," +
						"  \"description\": \"The correct format is yyyy-MM-dd.\"" +
						"}"));
	}

	@Test
	public void givenGoodDayFormat_then_getMockStatisticOfDay() throws Exception {
		ObjectMapper om = new ObjectMapper();
		String day = "2019-08-20";
		DayStatistic stats = new DayStatistic();
		stats.setDay(day);
		stats.setProductsCreated(1000);
		stats.setProductsUpdated(500);

		when(statsService.getStatisticOfDay(anyString())).thenReturn(stats);

		mvc.perform( MockMvcRequestBuilders
				.get("/products/statistic/"+day).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json(om.writeValueAsString(stats)));
	}

}
