package com.castmart.AggregatorMicroservice.controller;

import com.castmart.AggregatorMicroservice.model.DayStatistic;
import com.castmart.AggregatorMicroservice.model.Product;
import com.castmart.AggregatorMicroservice.persistence.ProductJPARepository;
import com.castmart.AggregatorMicroservice.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping( path = "products")
public class ProductsController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductsController.class);

    ProductJPARepository jpaRepo;

    StatisticsService statsService;

    @Autowired
    public ProductsController(StatisticsService service, ProductJPARepository repository) {
        this.jpaRepo = repository;
        this.statsService = service;
        LOG.info("ProductsController created...");
    }

    @ApiOperation(value = "Endpoint to list the products at the database. It uses simple pagination with the params \"page\" and \"pageSize\". " +
            "If no query elements are provided then the first 100 elements at database are returned ")
    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<List<Product>> productsFromDb(@RequestParam(name="page", required = false) Integer page,
                                                        @RequestParam(name="pageSize", required = false) Integer pageSize) {

        PageRequest requestParams;

        if (page == null || pageSize == null) {
            requestParams = new PageRequest(0, 100);
        } else {
            requestParams = new PageRequest(page, pageSize);
        }

        Page<Product> resultPage = jpaRepo.findAll(requestParams);
        return ResponseEntity.ok(resultPage.getContent());
    }

    @ApiOperation(value = "Endpoint to create/edit a product. If the provided product has an id then it is an update if not then it is a product creation.")
    @PostMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<Product> createProduct(@RequestBody Product p) {
        if (p.getId() == null) {
            p.setId(UUID.randomUUID().toString());
            p.setCreationTimestamp(null);
            p.setEditionTimestamp(null);
        } else {
            p.setEditionTimestamp(new Date());
        }
        return ResponseEntity.ok(jpaRepo.save(p));
    }

    @ApiOperation(value = "Endpoint to get a product by providing the corresponding ID (valid UUID).")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If the product is found"),
            @ApiResponse(code = 400, message = "If the ID is not a valid UUID"),
            @ApiResponse(code = 404, message = "If the product is not found")

    })
    @GetMapping(path = "{productId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Product> productById(@PathVariable("productId") String productId) {
            UUID id = UUID.fromString(productId);
            Optional<Product> product = jpaRepo.findById(productId);
            if (product.isPresent())
                return ResponseEntity.ok(product.get());
            else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ApiOperation(value = "Endpoint to get the statistics of the specified day, the day format should be yyyy-MM-dd.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "If there is a valid result"),
            @ApiResponse(code = 400, message = "If the specified date is not in the proper format")
    })
    @GetMapping(path="statistic/{day}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<DayStatistic> getStatistics(@PathVariable("day") String day) throws ParseException {
        return ResponseEntity.ok(statsService.getStatisticOfDay(day));

    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> badDate(ParseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid Date format", "The correct format is yyyy-MM-dd."));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> badUUID(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid uuid", "The provided id is not a valid UUID."));
    }

    static class ErrorDTO implements Serializable {
        private String error;
        private String description;

        public ErrorDTO() {}

        public ErrorDTO(String error, String description) {
            this.setError(error);
            this.setDescription(description);
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
